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

package com.alibaba.polardbx.druid.sql.ast.statement;

import com.alibaba.polardbx.druid.sql.ast.SQLName;
import com.alibaba.polardbx.druid.sql.ast.SQLObjectImpl;
import com.alibaba.polardbx.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyanxin.
 *
 * @author luoyanxin
 */
public class DrdsMergePartition extends SQLObjectImpl implements SQLAlterTableItem, SQLAlterTableGroupItem {
    private final List<SQLName> partitions = new ArrayList<SQLName>(4);
    private SQLName targetPartitionName;
    private boolean subPartitionsMerge;

    public SQLName getTargetPartitionName() {
        return targetPartitionName;
    }

    public void setTargetPartitionName(SQLName targetPartitionName) {
        this.targetPartitionName = targetPartitionName;
    }

    public List<SQLName> getPartitions() {
        return partitions;
    }

    public void addPartition(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.partitions.add(x);
    }

    public boolean isSubPartitionsMerge() {
        return subPartitionsMerge;
    }

    public void setSubPartitionsMerge(boolean subPartitionsMerge) {
        this.subPartitionsMerge = subPartitionsMerge;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}