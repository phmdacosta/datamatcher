package com.phmc.datamanager.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class WrapperObj {
    private Integer intProp;
    private Short shortProp;
    private Long longProp;
    private Float floatProp;
    private Double doubleProp;
    private Boolean booleanProp;
    private Character charProp;
    private Byte byteProp;
    private Date dateProp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WrapperObj)) return false;
        WrapperObj that = (WrapperObj) o;
        return Objects.equals(getIntProp(), that.getIntProp()) && Objects.equals(getShortProp(), that.getShortProp()) && Objects.equals(getLongProp(), that.getLongProp()) && Objects.equals(getFloatProp(), that.getFloatProp()) && Objects.equals(getDoubleProp(), that.getDoubleProp()) && Objects.equals(getBooleanProp(), that.getBooleanProp()) && Objects.equals(getCharProp(), that.getCharProp()) && Objects.equals(getByteProp(), that.getByteProp()) && Objects.equals(getDateProp(), that.getDateProp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIntProp(), getShortProp(), getLongProp(), getFloatProp(), getDoubleProp(), getBooleanProp(), getCharProp(), getByteProp(), getDateProp());
    }
}
