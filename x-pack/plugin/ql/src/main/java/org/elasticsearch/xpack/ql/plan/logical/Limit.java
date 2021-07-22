/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.ql.plan.logical;

import org.elasticsearch.xpack.ql.expression.Expression;
import org.elasticsearch.xpack.ql.expression.Literal;
import org.elasticsearch.xpack.ql.tree.NodeInfo;
import org.elasticsearch.xpack.ql.tree.Source;

import java.util.Objects;

public class Limit extends UnaryPlan {

    private final Expression offset;
    private final Expression limit;

    public Limit(Source source, Expression limit, LogicalPlan child) {
        this(source, limit, Literal.ZERO, child);
    }

    public Limit(Source source, Expression limit, Expression offset, LogicalPlan child) {
        super(source, child);
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    protected NodeInfo<Limit> info() {
        return NodeInfo.create(this, Limit::new, limit, offset, child());
    }

    @Override
    protected Limit replaceChild(LogicalPlan newChild) {
        return new Limit(source(), limit, offset, newChild);
    }

    public Expression offset() {
        return offset;
    }

    public Expression limit() {
        return limit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Objects.hash(limit, child()), offset);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            Limit other = (Limit) obj;
            return Objects.equals(limit, other.limit)
                && Objects.equals(offset, other.offset)
                && Objects.equals(child(), other.child());
        }
    }

    @Override
    public boolean expressionsResolved() {
        return limit.resolved() && offset.resolved();
    }

}
