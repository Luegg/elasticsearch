/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */
package org.elasticsearch.xpack.sql.plan.physical;

import org.elasticsearch.xpack.ql.tree.NodeInfo;
import org.elasticsearch.xpack.ql.tree.Source;

import java.util.Objects;

public class LimitExec extends UnaryExec implements Unexecutable {

    private final Integer limit;
    private final int offset;

    public LimitExec(Source source, PhysicalPlan child, Integer limit, int offset) {
        super(source, child);
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    protected NodeInfo<LimitExec> info() {
        return NodeInfo.create(this, LimitExec::new, child(), limit, offset);
    }

    @Override
    protected LimitExec replaceChild(PhysicalPlan newChild) {
        return new LimitExec(source(), newChild, limit, offset);
    }

    public Integer limit() {
        return limit;
    }

    public int offset() {
        return offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, offset, child());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        LimitExec other = (LimitExec) obj;
        return Objects.equals(limit, other.limit)
                && Objects.equals(offset, other.offset)
                && Objects.equals(child(), other.child());
    }
}
