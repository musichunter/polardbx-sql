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

package com.alibaba.polardbx.sequence.impl;

import com.alibaba.polardbx.common.utils.logger.Logger;
import com.alibaba.polardbx.common.utils.logger.LoggerFactory;
import com.alibaba.polardbx.gms.util.MetaDbUtil;
import com.alibaba.polardbx.sequence.exception.SequenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.alibaba.polardbx.common.constants.SequenceAttribute.CYCLE;
import static com.alibaba.polardbx.common.constants.SequenceAttribute.NOCYCLE;
import static com.alibaba.polardbx.gms.metadb.GmsSystemTables.SEQUENCE_OPT;

/**
 * A simple sequence DAO to maintain sequence value.
 *
 * @author chensr 2016/04/12 11:15:46
 * @since 5.0.0
 */
public class SimpleSequenceDao extends FunctionalSequenceDao {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSequenceDao.class);

    protected static final String UPDATE_SIMPLE_SEQ_VALUE = "UPDATE " + SEQUENCE_OPT
        + " SET value = ? + increment_by WHERE name = ? AND value <= ? AND schema_name = ?";

    public SimpleSequenceDao() {
    }

    public long nextValue(String seqName, int size) {
        String errPrefix = "Failed to get next value for sequence " + seqName + " - ";

        // Do some protection first.
        size = size <= 0 ? 1 : size;

        long newValue;
        try (Connection metaDbConn = MetaDbUtil.getConnection();
            PreparedStatement ps = metaDbConn.prepareStatement(getNextValueSql(size),
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, seqName);
            ps.setString(2, schemaName);

            int updateCount = ps.executeUpdate();
            if (updateCount == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        newValue = rs.getLong(1);
                        if (newValue == 0) {
                            String errMsg = errPrefix + "unexpected generated key '0'.";
                            logger.error(errMsg);
                            throw new SequenceException(errMsg);
                        }
                    } else {
                        String errMsg = errPrefix + "requested sequence value(s) with size " + size
                            + " exceeds maximum value allowed.";
                        logger.error(errMsg);
                        throw new SequenceException(errMsg);
                    }
                }
            } else {
                String errMsg = errPrefix + "nothing updated.";
                logger.error(errMsg);
                throw new SequenceException(errMsg);
            }
            return newValue;
        } catch (SQLException e) {
            String errMsg = errPrefix + e.getMessage();
            logger.error(errMsg, e);
            throw new SequenceException(e, errMsg);
        }
    }

    public void updateValue(String seqName, long value, long maxValue) {
        if (value > maxValue) {
            String errMsg =
                String.format("Attempting to update sequence value %s that exceeds maximum value allowed %s.", value,
                    maxValue);
            logger.error(errMsg);
            throw new SequenceException(errMsg);
        }
        try (Connection metaDbConn = MetaDbUtil.getConnection();
            PreparedStatement stmt = metaDbConn.prepareStatement(UPDATE_SIMPLE_SEQ_VALUE)) {

            stmt.setLong(1, value);
            stmt.setString(2, seqName);
            stmt.setLong(3, value);
            stmt.setString(4, schemaName);

            // Update the sequence value only when new value > current value.
            // Don't care about return code.
            stmt.executeUpdate();
        } catch (SQLException e) {
            String errMsg = "Failed to update sequence value " + value + ".";
            logger.error(errMsg, e);
            throw new SequenceException(e, errMsg);
        }
    }

    protected String getNextValueSql(int size) {
        final String incrementValue = "increment_by * (" + size + " - 1)";
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(SEQUENCE_OPT);
        // Check if next value will exceed maximum value allowed.
        sb.append(" SET value = IF(value + ").append(incrementValue).append(" > max_value,");
        // If it exceeds maximum value, then check if cycle is enabled.
        sb.append(" IF(cycle & ").append(CYCLE).append(" = ").append(NOCYCLE).append(",");
        // If no cycle, then no generated key and keep database value as is.
        sb.append(" LAST_INSERT_ID(0) + value,");
        // If cycle, then check if we have enough values.
        sb.append(" IF(start_with + ").append(incrementValue).append(" > max_value, LAST_INSERT_ID(0) + value,");
        // If enough, then return next value after recycled.
        sb.append(" LAST_INSERT_ID(start_with + ").append(incrementValue).append(") + increment_by)),");
        // If next value won't exceed maximum value, then return normal value.
        sb.append(" LAST_INSERT_ID(value + ").append(incrementValue).append(") + increment_by)");
        sb.append(" WHERE name = ? AND schema_name = ?");
        return sb.toString();
    }

}
