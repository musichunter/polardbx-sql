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

package com.alibaba.polardbx.executor.handler.subhandler;

import com.alibaba.polardbx.common.utils.logger.Logger;
import com.alibaba.polardbx.common.utils.logger.LoggerFactory;
import com.alibaba.polardbx.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.polardbx.druid.sql.ast.statement.SQLCreateProcedureStatement;
import com.alibaba.polardbx.druid.sql.parser.SQLParserFeature;
import com.alibaba.polardbx.executor.cursor.Cursor;
import com.alibaba.polardbx.executor.cursor.impl.ArrayResultCursor;
import com.alibaba.polardbx.executor.ddl.job.task.basic.pl.PlConstants;
import com.alibaba.polardbx.executor.handler.VirtualViewHandler;
import com.alibaba.polardbx.gms.metadb.GmsSystemTables;
import com.alibaba.polardbx.gms.util.MetaDbUtil;
import com.alibaba.polardbx.optimizer.context.ExecutionContext;
import com.alibaba.polardbx.optimizer.parse.FastsqlUtils;
import com.alibaba.polardbx.optimizer.view.InformationSchemaCheckRoutines;
import com.alibaba.polardbx.optimizer.view.InformationSchemaRoutines;
import com.alibaba.polardbx.optimizer.view.VirtualView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InformationSchemaCheckRoutinesHandler extends BaseVirtualViewSubClassHandler {
    private static final Logger logger = LoggerFactory.getLogger(InformationSchemaCheckRoutinesHandler.class);

    public InformationSchemaCheckRoutinesHandler(VirtualViewHandler virtualViewHandler) {
        super(virtualViewHandler);
    }

    @Override
    public boolean isSupport(VirtualView virtualView) {
        return virtualView instanceof InformationSchemaCheckRoutines;
    }

    @Override
    public Cursor handle(VirtualView virtualView, ExecutionContext executionContext, ArrayResultCursor cursor) {

        try (Connection metaDbConn = MetaDbUtil.getConnection();
            PreparedStatement statement = metaDbConn.prepareStatement(
                "SELECT ROUTINE_SCHEMA,ROUTINE_NAME,ROUTINE_TYPE,ROUTINE_DEFINITION  FROM " + GmsSystemTables.ROUTINES);
            ResultSet rs = statement.executeQuery();) {
            while (rs.next()) {
                String schema = rs.getString("ROUTINE_SCHEMA");
                String name = rs.getString("ROUTINE_NAME");
                String type = rs.getString("ROUTINE_TYPE");
                String definition = rs.getString("ROUTINE_DEFINITION");
                boolean parseValid = checkParseContent(type, definition);
                cursor.addRow(new Object[] {
                    schema,
                    name,
                    type,
                    definition,
                    parseValid ? "success" : "failed: error happened when parse content"
                });
            }
        } catch (SQLException ex) {
            logger.error("get information schema routines failed!", ex);
        }
        return cursor;
    }

    private boolean checkParseContent(String type, String content) {
        try {
            if (PlConstants.PROCEDURE.equalsIgnoreCase(type)) {
                SQLCreateProcedureStatement statement = (SQLCreateProcedureStatement) FastsqlUtils.parseSql(content,
                    SQLParserFeature.IgnoreNameQuotes).get(0);
            } else if (PlConstants.FUNCTION.equalsIgnoreCase(type)) {
                SQLCreateFunctionStatement statement = (SQLCreateFunctionStatement) FastsqlUtils.parseSql(content,
                    SQLParserFeature.IgnoreNameQuotes).get(0);
            }
        } catch (Throwable e) {
            logger.error("parse routine error, content is " + content, e);
            return false;
        }
        return true;
    }
}
