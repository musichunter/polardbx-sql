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

package org.apache.calcite.rel.ddl;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.rel.core.DDL;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;

import java.util.List;

public final class CreateMaterializedView extends DDL {

    private final String schemaName;

    private final String viewName;

    private final List<String> columnList;

    private final SqlNode definition;

    public final boolean bRefresh;

    public CreateMaterializedView(RelOptCluster cluster, String schemaName, String viewName,
                                  List<String> columnList,
                                  SqlNode definition, RelNode input,
                                  boolean bRefresh) {
        super(cluster, cluster.traitSet(), input);
        this.schemaName = schemaName;
        this.viewName = viewName;
        this.columnList = columnList;
        this.definition = definition;
        this.bRefresh = bRefresh;
    }

    @Override
    public CreateMaterializedView copy(
        RelTraitSet traitSet, List<RelNode> inputs) {
        return new CreateMaterializedView(
            this.getCluster(), schemaName, viewName, columnList, definition, inputs.get(0), bRefresh);
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getViewName() {
        return viewName;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public SqlNode getDefinition() {
        return definition;
    }

    @Override
    public RelWriter explainTermsForDisplay(RelWriter pw) {

        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("CREATE MATERIALIZED VIEW ");
        sqlBuilder.append("`").append(schemaName).append("`.`").append(viewName).append("`");

        if (columnList != null && !columnList.isEmpty()) {
            sqlBuilder.append("(").append(String.join(",", columnList)).append(")");
        }

        sqlBuilder.append(" AS ").append(definition);

        return pw.item("sql", sqlBuilder.toString());
    }

    @Override
    public SqlKind kind() {
        return SqlKind.CREATE_MATERIALIZED_VIEW;
    }

    @Override
    public List<RelNode> getInputs() {
        return ImmutableList.of(input);
    }

    @Override
    public SqlNodeList getHints() {
        return hints;
    }

    @Override
    public RelNode setHints(SqlNodeList hints) {
        this.hints = hints;
        return this;
    }

    @Override
    public void replaceInput(
        int ordinalInParent,
        RelNode rel) {
        assert ordinalInParent == 0;
        this.input = rel;
        recomputeDigest();
    }
}
