package com.phmc.datamanager.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class PrimitiveObj {
    private int intProp;
    private short shortProp;
    private long longProp;
    private float floatProp;
    private double doubleProp;
    private boolean booleanProp;
    private char charProp;
    private byte byteProp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrimitiveObj)) return false;
        PrimitiveObj that = (PrimitiveObj) o;
        return getIntProp() == that.getIntProp() && getShortProp() == that.getShortProp() && getLongProp() == that.getLongProp() && Float.compare(that.getFloatProp(), getFloatProp()) == 0 && Double.compare(that.getDoubleProp(), getDoubleProp()) == 0 && isBooleanProp() == that.isBooleanProp() && getCharProp() == that.getCharProp() && getByteProp() == that.getByteProp();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIntProp(), getShortProp(), getLongProp(), getFloatProp(), getDoubleProp(), isBooleanProp(), getCharProp(), getByteProp());
    }
}
