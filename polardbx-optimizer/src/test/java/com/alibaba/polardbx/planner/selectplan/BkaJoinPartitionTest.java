package com.alibaba.polardbx.planner.selectplan;

import com.alibaba.polardbx.planner.common.PlanTestCommon;
import org.junit.runners.Parameterized;

import java.util.List;

/**
 * @author dylan
 */
public class BkaJoinPartitionTest extends PlanTestCommon {

    public BkaJoinPartitionTest(String caseName, int sqlIndex, String sql, String expectedPlan,
                                String lineNum) {
        super(caseName, sqlIndex, sql, expectedPlan, lineNum);
    }

    @Override
    protected void initBasePlannerTestEnv() {
        this.useNewPartDb = true;
    }

    @Parameterized.Parameters(name = "{0}:{1}")
    public static List<Object[]> prepare() {
        return loadSqls(BkaJoinPartitionTest.class);
    }

}

