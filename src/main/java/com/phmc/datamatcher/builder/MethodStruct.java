package com.phmc.datamatcher.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodStruct {
    private final String name;
    private final ClassBuilder.AccessType accessType;
    private final Class<?> returnType;
    private final List<ParameterStruct> parameters;
    private final List<String> annotationNames;
    private String body;
    private boolean isAbstract;

    public MethodStruct(ClassBuilder.AccessType accessType, String name) {
        this(accessType, name, Void.class, new ArrayList<>());
    }

    public MethodStruct(ClassBuilder.AccessType accessType, String name, Class<?> returnType) {
        this(accessType, name, returnType, new ArrayList<>());
    }

    public MethodStruct(ClassBuilder.AccessType accessType, String name, Class<?> returnType, List<ParameterStruct> parameter) {
        this(accessType, name, returnType, parameter, new ArrayList<>());
    }

    public MethodStruct(ClassBuilder.AccessType accessType, String name, Class<?> returnType, List<ParameterStruct> parameters, List<String> annotationNames) {
        this.accessType = accessType;
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.annotationNames = annotationNames;
        if (returnType.isAssignableFrom(Void.class)) this.body = "";
    }

    public String getName() {
        return name;
    }

    public ClassBuilder.AccessType getAccessType() {
        return accessType;
    }

    public String getReturnTypeName() {
        return returnType.getSimpleName();
    }

    public List<ParameterStruct> getParameters() {
        return parameters;
    }

    public void addParameter(ParameterStruct parameter) {
        if (parameter == null) return;
        this.parameters.add(parameter);
    }

    public List<String> getAnnotationNames() {
        return annotationNames;
    }

    public void addAnnotationName(String annotationName) {
        if (annotationName == null) return;
        this.annotationNames.add(annotationName);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodStruct)) return false;
        MethodStruct that = (MethodStruct) o;
        return Objects.equals(getName(), that.getName())
                && Objects.equals(getReturnTypeName(), that.getReturnTypeName())
                && Objects.equals(getParameters(), that.getParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getReturnTypeName(), getParameters());
    }
}
