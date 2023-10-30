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

package com.alibaba.polardbx.optimizer.core.rel.ddl;

import com.alibaba.polardbx.druid.sql.ast.SQLName;
import com.alibaba.polardbx.druid.sql.ast.expr.SQLPropertyExpr;
import org.apache.calcite.rel.ddl.CreateProcedure;
import org.apache.calcite.sql.SqlCreateProcedure;

public class LogicalCreateProcedure extends BaseDdlOperation {

    private SqlCreateProcedure sqlCreateProcedure;

    public LogicalCreateProcedure(CreateProcedure createProcedure) {
        super(createProcedure);
        resetSchemaIfNeed(((SqlCreateProcedure) relDdl.sqlNode).getProcedureName());
        this.sqlCreateProcedure = (SqlCreateProcedure) relDdl.sqlNode;
    }

    public static LogicalCreateProcedure create(CreateProcedure createProcedure) {
        return new LogicalCreateProcedure(createProcedure);
    }

    public SqlCreateProcedure getSqlCreateProcedure() {
        return sqlCreateProcedure;
    }

    private void resetSchemaIfNeed(SQLName procedureName) {
        if (procedureName instanceof SQLPropertyExpr) {
            setSchemaName(((SQLPropertyExpr) procedureName).getOwnerName());
        }
    }

    @Override
    public boolean isSupportedByFileStorage() {
        return true;
    }

    @Override
    public boolean isSupportedByBindFileStorage() {
        return true;
    }
}