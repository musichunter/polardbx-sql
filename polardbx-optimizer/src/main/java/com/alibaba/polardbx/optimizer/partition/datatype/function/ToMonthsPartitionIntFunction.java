/*
 * Copyright [2013-2021], Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.polardbx.optimizer.partition.datatype.function;

import com.alibaba.polardbx.common.utils.time.MySQLTimeTypeUtil;
import com.alibaba.polardbx.common.utils.time.calculator.MySQLIntervalType;
import com.alibaba.polardbx.common.utils.time.calculator.PartitionFunctionTimeCaculator;
import com.alibaba.polardbx.common.utils.time.core.MysqlDateTime;
import com.alibaba.polardbx.common.utils.time.parser.TimeParserFlags;
import com.alibaba.polardbx.optimizer.core.TddlOperatorTable;
import com.alibaba.polardbx.optimizer.core.datatype.DataType;
import com.alibaba.polardbx.optimizer.core.field.SessionProperties;
import com.alibaba.polardbx.optimizer.partition.datatype.PartitionField;
import org.apache.calcite.sql.SqlOperator;

import java.sql.Types;
import java.util.List;

import static com.alibaba.polardbx.optimizer.partition.datatype.function.Monotonicity.MONOTONIC_INCREASING_NOT_NULL;
import static com.alibaba.polardbx.optimizer.partition.datatype.function.Monotonicity.NON_MONOTONIC;

/**
 * Created by zhuqiwei.
 */
public class ToMonthsPartitionIntFunction extends PartitionIntFunction {

    public ToMonthsPartitionIntFunction(List<DataType> operandTypes, DataType resultType) {
        super(operandTypes, resultType);
    }

    @Override
    public Monotonicity getMonotonicity(DataType<?> fieldType) {
        if (fieldType.getSqlType() == Types.DATE || fieldType.getSqlType() == MySQLTimeTypeUtil.DATETIME_SQL_TYPE) {
            return MONOTONIC_INCREASING_NOT_NULL;
        } else {
            return NON_MONOTONIC;
        }
    }

    @Override
    public MySQLIntervalType getIntervalType() {
        return MySQLIntervalType.INTERVAL_MONTH;
    }

    @Override
    public long evalInt(PartitionField partitionField, SessionProperties sessionProperties) {
        MysqlDateTime t =
            partitionField.datetimeValue(TimeParserFlags.FLAG_TIME_NO_ZERO_DATE, SessionProperties.empty());
        if (t == null) {
            return 0L;
        } else {
            return PartitionFunctionTimeCaculator.calToMonths(t.getYear(), t.getMonth(), t.getDay());
        }
    }

    @Override
    public long evalIntEndpoint(PartitionField partitionField, SessionProperties sessionProperties,
                                boolean[] endpoints) {
        MysqlDateTime t =
            partitionField.datetimeValue(TimeParserFlags.FLAG_TIME_NO_ZERO_DATE, SessionProperties.empty());
        if (t == null) {
            return SINGED_MIN_LONG;
        }
        long months = PartitionFunctionTimeCaculator.calToMonths(t.getYear(), t.getMonth(), t.getDay());

        // Set to NULL if invalid date, but keep the value
        boolean isNonZeroDate = t.getYear() != 0 || t.getMonth() != 0 || t.getDay() != 0;
        boolean invalid = MySQLTimeTypeUtil.isDateInvalid(t, isNonZeroDate,
            TimeParserFlags.FLAG_TIME_NO_ZERO_IN_DATE | TimeParserFlags.FLAG_TIME_NO_ZERO_DATE);
        if (invalid) {
            endpoints[1] = true;
            return months;
        }

        /* *
         *  handle the situation:
         *
         *  if sqlType is date
         *      case 1. col < 'DateWhichIsFirstDayInMonth'    ->    to_months(col) < to_months('DateWhichIsFirstDayInMonth')
         *      case 2. col > 'DateWhichIsLastDayInMonth'  ->    to_months(col) > to_months('DateWhichIsLastDayInMonth')
         *      other wise, we use <= or >=
         *  if sqlType is datetime
         *      case 1. col < 'DateWhichIsFirstDayInMonth 00:00:00'   ->   to_months(col) < to_months('DateWhichIsFirstDayInMonth 00:00:00')
         *      case 2. col > 'DateWhichIsLastDayInMonth 23:59:59'   ->    to_months(col) > to_months('DateWhichIsLastDayInMonth 23:59:59')
         *      otherwise, we use <= or >=
         *
         * */
        int sqlType = partitionField.dataType().getSqlType();
        boolean leftEndpoint = endpoints[0];
        boolean isFirstDayInMonth = (t.getDay() == 1);
        boolean isLastDayInMonth;

        long dayNumber = PartitionFunctionTimeCaculator.calDayNumber(t.getYear(), t.getMonth(), t.getDay());
        MysqlDateTime nextDay = new MysqlDateTime();
        PartitionFunctionTimeCaculator.getDateFromDayNumber(dayNumber + 1, nextDay);
        isLastDayInMonth = (nextDay.getMonth() != t.getMonth());

        boolean isStrict = false;
        if (sqlType == Types.DATE) {
            isStrict = (!leftEndpoint && isFirstDayInMonth || leftEndpoint && isLastDayInMonth);
        } else if (sqlType == MySQLTimeTypeUtil.DATETIME_SQL_TYPE) {
            isStrict =
                (!leftEndpoint && isFirstDayInMonth && t.getHour() == 0 && t.getMinute() == 0 && t.getSecond() == 0
                    && t.getSecondPart() == 0)
                    || (leftEndpoint && isLastDayInMonth && t.getHour() == 23 && t.getMinute() == 59
                    && t.getSecond() == 59);
        }

        if (!isStrict) {
            endpoints[1] = true;
        }

        return months;
    }

    @Override
    public String[] getFunctionNames() {
        return new String[] {"ToMonthsPartitionInt"};
    }

    @Override
    public SqlOperator getSqlOperator() {
        return TddlOperatorTable.TO_MONTHS;
    }
}
