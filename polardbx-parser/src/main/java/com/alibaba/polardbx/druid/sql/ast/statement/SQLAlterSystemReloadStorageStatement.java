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

import com.alibaba.polardbx.druid.sql.ast.SQLExpr;
import com.alibaba.polardbx.druid.sql.ast.SQLStatementImpl;
import com.alibaba.polardbx.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenghui.lch
 */
public class SQLAlterSystemReloadStorageStatement extends SQLStatementImpl implements SQLAlterStatement {

    /**
     * the list of dn id
     */
    private List<SQLExpr> storageList = new ArrayList<>();

    public SQLAlterSystemReloadStorageStatement() {
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, storageList);
        }
        visitor.endVisit(this);
    }

    public List<SQLExpr> getStorageList() {
        return storageList;
    }

    public void setStorageList(List<SQLExpr> storageList) {
        this.storageList = storageList;
    }
}
