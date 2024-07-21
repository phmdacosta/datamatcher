package com.phmc.datamatcher.builder;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

public class MatcherBuilder extends ClassBuilder {
    private final Class<?> classInterface;
    private final Class<?> returnType;
//    private final Class<?>[] parameterTypes;
    private boolean defaultInterface;
    private final Set<String> excludedProperties;

    public MatcherBuilder(Class<?> clazz) {
        this(clazz, IMatcher.class);
        this.defaultInterface = true;
    }

    private MatcherBuilder(Class<?> clazz, Class<?> matcherInterface) {
        this.classInterface = matcherInterface;
        this.returnType = clazz;
        this.defaultInterface = false;
        this.excludedProperties = new HashSet<>();
    }

    public void addExcludeProperty(String property) {
        this.excludedProperties.add(property);
    }

    public void addAllExcludedProperties(Collection<String> properties) {
        this.excludedProperties.addAll(properties);
    }

    @Override
    protected void prepareBuild() {
        String packagePath = defaultInterface ? returnType.getPackage().getName() : classInterface.getPackage().getName();
        setPackageName(packagePath);
        String implClassName = (defaultInterface ? returnType.getSimpleName().concat("Matcher") : classInterface.getSimpleName()).concat("Impl");
        setClassName(implClassName);
        addImport(returnType);
        addInterface(IMatcher.class, new Class[]{returnType});
        if (!defaultInterface) addImport(classInterface);
        int paramSize = 2;
        List<ParameterStruct> paramStructs = new ArrayList<>();
        String[] paramNames = new String[paramSize];
        for (int i = 0; i < paramSize; i++) {
            String paramName = "o"+(i+1);
            paramStructs.add(new ParameterStruct(paramName, returnType));
            paramNames[i] = paramName;
        }
        for (Method interfaceMethod : IMatcher.class.getMethods()) {
            MethodStruct methodStruct = new MethodStruct(AccessType.PUBLIC, interfaceMethod.getName(), interfaceMethod.getReturnType(), paramStructs);
            methodStruct.setBody(buildMethodBody(paramNames));
            addMethodStruct(methodStruct);
            addMethodAnnotation(methodStruct.getName(), Override.class);
        }
    }

    private String buildMethodBody(String[] paramNames) {
        StringBuilder _sb = new StringBuilder();
        _sb.append("if (").append(paramNames[0]).append(" == null && ").append(paramNames[1]).append(" == null)").append(" { return true; } ");
        _sb.append("if (").append(paramNames[0]).append(" == null) ").append(" { return false; } ");
        _sb.append("if (").append(paramNames[1]).append(" == null) ").append(" { return false; } ");
        _sb.append("return ");
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(returnType);
            PropertyDescriptor[] propDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propDescriptors.length; i++) {
                String propName = propDescriptors[i].getName();
                if (excludedProperties.contains(propName)) continue;
                String getter = propDescriptors[i].getReadMethod().getName();
                if (i > 0) {
                    _sb.append(" && ");
                }
                _sb.append("Objects.equals(")
                        .append(paramNames[0]).append(".").append(getter).append("(), ")
                        .append(paramNames[1]).append(".").append(getter).append("()); ");
            }
        } catch (IntrospectionException e) {
            _sb.append(" ").append(paramNames[0]).append(".equals(").append(paramNames[1]).append("); ");
        }

        return _sb.toString();
    }
}
