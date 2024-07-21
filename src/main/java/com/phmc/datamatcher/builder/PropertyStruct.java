package com.phmc.datamatcher.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PropertyStruct implements Serializable {
    private final String name;
    private final Class<?> type;
    private final List<String> annotationNames;

    public PropertyStruct(String name, Class<?> type) {
        this(name, type, new ArrayList<>());
    }

    public PropertyStruct(String name, Class<?> type, List<String> annotationNames) {
        this.name = name;
        this.type = type;
        this.annotationNames = annotationNames;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return type.getSimpleName();
    }

    public Class<?> getType() {
        return type;
    }

    public List<String> getAnnotationNames() {
        return annotationNames;
    }

    public void addAnnotation(String annotation) {
        if (annotation == null) return;
        this.annotationNames.add(annotation);
    }
}
