package com.phmc.datamatcher.builder;

import java.io.Serializable;
import java.util.Objects;

public class ParameterStruct implements Serializable {
    private final String name;
    private final Class<?> typeClass;

    public ParameterStruct(String name, Class<?> type) {
        this.name = name;
        this.typeClass = type;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeClass.getSimpleName();
    }

    public Class<?> getType() {
        return this.typeClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterStruct)) return false;
        ParameterStruct that = (ParameterStruct) o;
        return Objects.equals(getName(), that.getName())
                && Objects.equals(getTypeName(), that.getTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTypeName());
    }

    public String toString() {
        return "(" + this.getName() + ',' + this.getTypeName() + ')';
    }

    public String toString(String format) {
        return String.format(format, this.getName(), this.getTypeName());
    }
}
