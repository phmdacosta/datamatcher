package com.phmc.datamatcher.builder;

public interface IMatcher<T> {
    boolean matches(T o1, T o2);
}
