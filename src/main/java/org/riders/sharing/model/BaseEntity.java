package org.riders.sharing.model;

public abstract class BaseEntity {
    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
