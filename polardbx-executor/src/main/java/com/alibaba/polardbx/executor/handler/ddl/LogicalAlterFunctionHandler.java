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

package com.alibaba.polardbx.executor.handler.ddl;

import com.alibaba.polardbx.druid.sql.SQLUtils;
import com.alibaba.polardbx.druid.sql.ast.SQLName;
import com.alibaba.polardbx.executor.ddl.job.task.basic.pl.udf.AlterFunctionModifyMetaTask;
import com.alibaba.polardbx.executor.ddl.job.task.basic.pl.udf.AlterProcedureTask;
import com.alibaba.polardbx.executor.ddl.newengine.job.DdlJob;
import com.alibaba.polardbx.executor.ddl.newengine.job.DdlTask;
import com.alibaba.polardbx.executor.ddl.newengine.job.ExecutableDdlJob;
import com.alibaba.polardbx.executor.pl.PLUtils;
import com.alibaba.polardbx.executor.spi.IRepository;
import com.alibaba.polardbx.optimizer.context.ExecutionContext;
import com.alibaba.polardbx.optimizer.core.rel.ddl.BaseDdlOperation;
import com.alibaba.polardbx.optimizer.core.rel.ddl.LogicalAlterFunction;
import com.alibaba.polardbx.optimizer.core.rel.ddl.LogicalAlterProcedure;
import org.apache.calcite.sql.SqlAlterFunction;
import org.apache.calcite.sql.SqlAlterProcedure;

import java.util.ArrayList;
import java.util.List;

public class LogicalAlterFunctionHandler extends LogicalCommonDdlHandler {
    public LogicalAlterFunctionHandler(IRepository repo) {
        super(repo);
    }

    @Override
    public DdlJob buildDdlJob(BaseDdlOperation logicalDdlPlan, ExecutionContext executionContext) {
        ExecutableDdlJob executableDdlJob = new ExecutableDdlJob();

        List<DdlTask> taskList = new ArrayList<>();
        taskList.add(getAlterFunctionTask(logicalDdlPlan, executionContext));
        executableDdlJob.addSequentialTasks(taskList);

        return executableDdlJob;
    }

    private AlterFunctionModifyMetaTask getAlterFunctionTask(BaseDdlOperation logicalDdlPlan,
                                                             ExecutionContext executionContext) {
        SqlAlterFunction alterFunction = ((LogicalAlterFunction) logicalDdlPlan).getSqlAlterFunction();
        return new AlterFunctionModifyMetaTask(logicalDdlPlan.getSchemaName(), null,
            alterFunction.getFunctionName(),
            alterFunction.getText());
    }
}
