/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */
package org.elasticsearch.xpack.sql.planner;

import org.elasticsearch.xpack.ql.common.Failure;
import org.elasticsearch.xpack.ql.plan.logical.LogicalPlan;
import org.elasticsearch.xpack.ql.tree.Node;
import org.elasticsearch.xpack.sql.plan.physical.PhysicalPlan;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class Planner {

    private final Mapper mapper = new Mapper();
    private final QueryFolder folder = new QueryFolder();

    public PhysicalPlan plan(LogicalPlan plan) {
        return plan(plan, true, Integer.MAX_VALUE);
    }

    public PhysicalPlan plan(LogicalPlan plan, boolean verify, int pageSize) {
        return foldPlan(mapPlan(plan, verify), verify, pageSize);
    }

    // first, map the logical plan
    public PhysicalPlan mapPlan(LogicalPlan plan, boolean verify) {
        return verify ? verifyMappingPlan(mapper.map(plan)) : mapper.map(plan);
    }

    // second, pack it up
    public PhysicalPlan foldPlan(PhysicalPlan mapped, boolean verify, int pageSize) {
        return verify ? verifyExecutingPlan(folder.fold(mapped), pageSize) : folder.fold(mapped);
    }

    // verify the mapped plan
    public PhysicalPlan verifyMappingPlan(PhysicalPlan plan) {
        List<Failure> failures = Verifier.verifyMappingPlan(plan);
        if (failures.isEmpty() == false) {
            throw new PlanningException(failures);
        }
        return plan;
    }

    public Map<Node<?>, String> verifyMappingPlanFailures(PhysicalPlan plan) {
        List<Failure> failures = Verifier.verifyMappingPlan(plan);
        return failures.stream().collect(toMap(Failure::node, Failure::message));
    }

    public PhysicalPlan verifyExecutingPlan(PhysicalPlan plan, int pageSize) {
        List<Failure> failures = Verifier.verifyExecutingPlan(plan, pageSize);
        if (failures.isEmpty() == false) {
            throw new PlanningException(failures);
        }
        return plan;
    }

    public Map<Node<?>, String> verifyExecutingPlanFailures(PhysicalPlan plan, int pageSize) {
        List<Failure> failures = Verifier.verifyExecutingPlan(plan, pageSize);
        return failures.stream().collect(toMap(Failure::node, Failure::message));
    }
}
