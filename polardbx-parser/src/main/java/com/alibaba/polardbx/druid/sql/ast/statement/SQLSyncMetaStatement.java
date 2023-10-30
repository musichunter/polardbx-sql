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
import com.alibaba.polardbx.druid.sql.ast.SQLName;
import com.alibaba.polardbx.druid.sql.ast.SQLStatementImpl;
import com.alibaba.polardbx.druid.sql.ast.SqlType;
import com.alibaba.polardbx.druid.sql.visitor.SQLASTVisitor;

public class SQLSyncMetaStatement extends SQLStatementImpl {
    private Boolean restrict;
    private Boolean ignore;

    private SQLName from;
    private SQLExpr like;

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, from);
            acceptChild(v, like);
        }
        v.endVisit(this);
    }

    public Boolean getRestrict() {
        return restrict;
    }

    public void setRestrict(Boolean restrict) {
        this.restrict = restrict;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    public SQLName getFrom() {
        return from;
    }

    public void setFrom(SQLName from) {
        this.from = from;
    }

    public SQLExpr getLike() {
        return like;
    }

    public void setLike(SQLExpr like) {
        this.like = like;
    }

    @Override
    public SqlType getSqlType() {
        return null;
    }
}
