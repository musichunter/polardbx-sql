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

package com.alibaba.polardbx.gms.metadb.table;

import com.alibaba.polardbx.common.utils.Pair;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents status of column schema change
 */
public enum ColumnStatus {

    ABSENT(0),
    PUBLIC(1),
    WRITE_ONLY(2),
    WRITE_REORG(3),
    MULTI_WRITE_SOURCE(4),
    MULTI_WRITE_TARGET(5);

    private final int value;

    ColumnStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ColumnStatus convert(int value) {
        switch (value) {
        case 0:
            return ABSENT;
        case 1:
            return PUBLIC;
        case 2:
            return WRITE_ONLY;
        case 3:
            return WRITE_REORG;
        case 4:
            return MULTI_WRITE_SOURCE;
        case 5:
            return MULTI_WRITE_TARGET;
        default:
            return null;
        }
    }

    /**
     * Schema change for add a column
     */
    public static List<Pair<ColumnStatus, ColumnStatus>> schemaChangeForAddColumn() {
        return enumerateStatusChange(addColumnStatusList());
    }

    /**
     * Schema change for drop a column
     */
    public static List<Pair<ColumnStatus, ColumnStatus>> schemaChangeForDropColumn() {
        return enumerateStatusChange(Lists.reverse(addColumnStatusList()));
    }

    private static List<ColumnStatus> addColumnStatusList() {
        return Arrays.asList(ABSENT, WRITE_ONLY, WRITE_REORG, PUBLIC);
    }

    private static List<Pair<ColumnStatus, ColumnStatus>> enumerateStatusChange(List<ColumnStatus> statuses) {
        assert statuses.size() > 1;
        List<Pair<ColumnStatus, ColumnStatus>> result = new ArrayList<>();
        for (int i = 1; i < statuses.size(); i++) {
            result.add(Pair.of(statuses.get(i - 1), statuses.get(i)));
        }
        return result;
    }
}
