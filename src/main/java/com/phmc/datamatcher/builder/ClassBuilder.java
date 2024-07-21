package com.phmc.datamatcher.builder;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;

public abstract class ClassBuilder {
    private String packageName;
    private String className;
    private String superClassName;
    private List<String> interfaceNames;
    private List<String> classAnnotations;
    private Map<String, PropertyStruct> properties;
    private Map<String, String> propAnnotNames;
    private Map<String, MethodStruct> methods;
    private Map<String, ParameterStruct> methodParameters;
    private Map<String, String> methodAnnotNames;
    private Set<Class<?>> imports;

    protected String getPackageName() {
        return packageName;
    }

    protected String getClassName() {
        return className;
    }

    protected String getSuperClassName() {
        return superClassName;
    }

    protected List<String> getInterfaceNames() {
        if (interfaceNames == null) return new ArrayList<>();
        return Collections.unmodifiableList(interfaceNames);
    }

    protected List<String> getClassAnnotations() {
        if (classAnnotations == null) return new ArrayList<>();
        return Collections.unmodifiableList(classAnnotations);
    }

    protected Map<String, PropertyStruct> getProperties() {
        if (properties == null) return new HashMap<>();
        return Collections.unmodifiableMap(properties);
    }

    protected Map<String, String> getPropAnnotNames() {
        if (propAnnotNames == null) return new HashMap<>();
        return Collections.unmodifiableMap(propAnnotNames);
    }

    protected Map<String, MethodStruct> getMethods() {
        if (methods == null) return new HashMap<>();
        return Collections.unmodifiableMap(methods);
    }

    protected Map<String, ParameterStruct> getMethodParameters() {
        if (methodParameters == null) return new HashMap<>();
        return Collections.unmodifiableMap(methodParameters);
    }

    protected Map<String, String> getMethodAnnotNames() {
        if (methodAnnotNames == null) return new HashMap<>();
        return Collections.unmodifiableMap(methodAnnotNames);
    }

    protected Set<Class<?>> getImports() {
        if (imports == null) return new HashSet<>();
        return Collections.unmodifiableSet(imports);
    }

    public ImplementationClass build() {
        prepareBuild();

        StringBuilder sb = new StringBuilder();
        buildPackage(sb, this.getPackageName());
        buildImports(sb, this.getImports().toArray(new Class[0]));
        // Build Class
        for (String annot : this.getClassAnnotations()) {
            buildAnnotation(sb, annot);
        }
        buildClassDeclaration(sb, this.getClassName(), this.getSuperClassName(), this.getInterfaceNames().toArray(new String[0]));

        // Build properties
        if (this.getPropAnnotNames().isEmpty()) {
            buildPropertiesVanilla(sb);
        } else {
            buildPropertyWithAnnotation(sb);
        }

        // Build methods
        String[] methodKeys = this.getMethods().keySet().toArray(new String[0]);
        String[] methodAnnotKeys = this.getMethodAnnotNames().keySet().toArray(new String[0]);
        String[] methodParamKeys = this.getMethodParameters().keySet().toArray(new String[0]);
        int biggestSize = methodParamKeys.length;
        if (methodAnnotKeys.length > biggestSize)
            biggestSize = methodAnnotKeys.length;
        else if (methodKeys.length > biggestSize)
            biggestSize = methodKeys.length;

        String previousMethodKey = null;
        List<String> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        for (int i = 0; i < biggestSize; i++) {
            String currentMethodKey = null;
            // Build annotations
            if (i < methodAnnotKeys.length) {
                String annotKey = methodAnnotKeys[i];
                buildAnnotation(sb, this.getMethodAnnotNames().get(annotKey));
                currentMethodKey = extractNameOf(annotKey);
            }
            // Get parameters
            if (i < methodParamKeys.length) {
                String paramKey = methodParamKeys[i];
                ParameterStruct paramStruct = this.getMethodParameters().get(paramKey);
                paramTypes.add(paramStruct.getTypeName());
                paramNames.add(paramStruct.getName());
                if (currentMethodKey == null)
                    currentMethodKey = extractNameOf(paramKey);
            }

            if (i == methodParamKeys.length-1) {
                // Build method declaration
                if (currentMethodKey == null) currentMethodKey = methodKeys[i];
                if (currentMethodKey != null && !currentMethodKey.equals(previousMethodKey)) {
                    MethodStruct methodStruct = this.getMethods().get(currentMethodKey);
                    buildMethodDeclaration(sb, methodStruct.getAccessType(), methodStruct.getName(),
                            methodStruct.getReturnTypeName(), paramTypes.toArray(new String[0]), paramNames.toArray(new String[0]));
                    sb.append(" ").append(methodStruct.getBody());
                    // Closing method
                    buildQuoteClosing(sb);
                    previousMethodKey = currentMethodKey;
                }
            }
        }
        // Closing Class
        buildQuoteClosing(sb);
        System.out.println(sb);
        return new ImplementationClass(this.getPackageName().concat(".").concat(this.getClassName()), sb.toString());
    }

    protected abstract void prepareBuild();

    protected void buildPackage(StringBuilder sb, String packageName) {
        prepare(sb).append(" ").append("package ").append(packageName).append("; ");
    }

    protected void buildImports(StringBuilder sb, Class<?> ... classes) {
        final StringBuilder _sb = new StringBuilder();
        for (int i = 0; i < classes.length; i++) {
            Class<?> c = classes[i];
            if (i == 0 || !c.getName().equals(classes[i-1].getName())) {
                _sb.append("import ").append(c.getName()).append("; ");
            }
        }
        prepare(sb).append(" ").append(_sb);
    }

    protected void buildClassDeclaration(StringBuilder sb, String className, String superClassName, String[] interfaceNames) {
        final StringBuilder _sb = new StringBuilder();
        _sb.append("public class ").append(className);
        if (StringUtils.isNotBlank(superClassName)) {
            _sb.append(" extends ").append(superClassName);
        }
        if (interfaceNames != null) {
            for (int i = 0; i < interfaceNames.length; i++) {
                String interfaceName = interfaceNames[i];
                if (StringUtils.isNotBlank(interfaceName)) {
                    if (i == 0) {
                        _sb.append(" implements ");
                    }
                    _sb.append(interfaceName).append(",");
                }
            }
            _sb.deleteCharAt(_sb.length() - 1);
        }
        _sb.append(" { ");
        prepare(sb).append(" ").append(_sb);
    }

    protected void buildPropertyDeclaration(StringBuilder sb, String propertyTypeName, String propertyName) {
        String _s = "private " + propertyTypeName + " " + propertyName + ";";
        prepare(sb).append(" ").append(_s);
    }

    protected void buildAnnotation(StringBuilder sb, String name) {
        String _s = "@" + name;
        prepare(sb).append(" ").append(_s);
    }

    protected void buildMethodDeclaration(StringBuilder sb, AccessType accessType, String methodName, String returnTypeName, String[] paramTypeNames, String[] paramNames) {
        final StringBuilder _sb = new StringBuilder();
        switch (accessType) {
            case PRIVATE:
                _sb.append("private ");
                break;
            case PROTECTED:
                _sb.append("protected ");
                break;
            case PUBLIC:
            default:
                _sb.append("public ");
        }
        _sb.append(returnTypeName).append(" ").append(methodName).append(" ( ");
        if (paramTypeNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                String paramType = paramTypeNames[i];
                String paramName = paramNames[i];
                if (StringUtils.isNotBlank(paramType) && StringUtils.isNotBlank(paramName)) {
                    _sb.append(paramType).append(" ").append(paramName).append(",");
                }
            }
        }
        prepare(sb).append(" ").append(_sb.deleteCharAt(_sb.length() - 1)).append(") { ");
    }

    protected void buildQuoteClosing(StringBuilder sb) {
        final StringBuilder _sb = prepare(sb);
        _sb.append(" } ");
    }

    protected void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    protected void setClassName(String className) {
        this.className = className;
    }

    protected void addImport(Class<?> importClass) {
        if (this.imports == null) this.imports = new HashSet<>();
        if (importClass.getPackage() != null) this.imports.add(importClass);
    }

    protected void addAllImports(Class<?>[] importClasses) {
        if (this.imports == null) this.imports = new HashSet<>();
        for (Class<?> importClass : importClasses) {
            if (importClass.getPackage() != null) this.imports.add(importClass);
        }
    }

    protected void addClassAnnotation(Class<? extends Annotation> classAnnotation) {
        if (classAnnotation == null) return;
        if (this.classAnnotations == null) this.classAnnotations = new ArrayList<>();
        this.classAnnotations.add(classAnnotation.getSimpleName());
        addImport(classAnnotation);
    }

    protected void setInterfaceNames(List<String> interfaceNames) {
        this.interfaceNames = interfaceNames;
    }

    protected void addInterface(Class<?> interfaceClass) {
        if (interfaceClass == null) return;
        if (this.interfaceNames == null) this.interfaceNames = new ArrayList<>();
        this.interfaceNames.add(interfaceClass.getSimpleName());
        addImport(interfaceClass);
    }

    protected void addInterface(Class<?> interfaceClass, Class<?>[] genericTypes) {
        if (interfaceClass == null) return;
        if (this.interfaceNames == null) this.interfaceNames = new ArrayList<>();
        String generics = "";
        for (Class<?> genericType : genericTypes) {
            generics = generics.concat(genericType.getSimpleName());
        }
        if (StringUtils.isNotBlank(generics)) generics = "<" + generics.substring(0, generics.length() - 1) + ">";
        this.interfaceNames.add(interfaceClass.getSimpleName() + generics);
        addImport(interfaceClass);
        addAllImports(genericTypes);
    }

    protected void setSuperClass(Class<?> superClass) {
        if (superClass == null) return;
        this.superClassName = superClass.getSimpleName();
        addImport(superClass);
    }

    protected void setSuperClass(Class<?> superClass, Class<?>[] genericTypes) {
        if (superClass == null) return;
        String generics = "";
        for (Class<?> genericType : genericTypes) {
            generics = generics.concat(genericType.getSimpleName());
        }
        if (StringUtils.isNotBlank(generics)) generics = "<" + generics.substring(0, generics.length() - 1) + ">";
        this.superClassName = superClass.getSimpleName() + generics;
        addImport(superClass);
        addAllImports(genericTypes);
    }

    protected void addMethodStruct(MethodStruct methodStruct) {
        if (this.methods == null) this.methods = new HashMap<>();
        this.methods.put(methodStruct.getName(), methodStruct);
        addAllImports(methodStruct.getParameters().stream().map(ParameterStruct::getType).toArray(Class[]::new));
        if (this.methodParameters == null) this.methodParameters = new HashMap<>();
        int newParamIdx = getNewKeyIndex(this.methodParameters.keySet());
        for (ParameterStruct param : methodStruct.getParameters()) {
            this.methodParameters.put(generateAnnotationMapKey(methodStruct.getName(), newParamIdx), param);
            newParamIdx++;
        }
    }

    protected void addMethodAnnotation(String methodName, Class<? extends Annotation> annotation) {
        MethodStruct methodStruct = this.methods.get(methodName);
        if (methodStruct == null) return;
        methodStruct.addAnnotationName(annotation.getSimpleName());
        addImport(annotation);
        if (this.methodAnnotNames == null) this.methodAnnotNames = new HashMap<>();
        int newIdx = getNewKeyIndex(this.methodAnnotNames.keySet());
        this.methodAnnotNames.put(generateAnnotationMapKey(methodName, newIdx), annotation.getSimpleName());
    }

    protected void addAllMethodAnnotations(String methodName, Class<? extends Annotation>[] annotations) {
        if (this.methodAnnotNames == null) this.methodAnnotNames = new HashMap<>();
        addAllImports(annotations);
        int newIdx = getNewKeyIndex(this.methodAnnotNames.keySet());
        for (int i = 0; i < annotations.length; i++) {
            String annotKey = generateAnnotationMapKey(methodName, newIdx+i);
            this.methodAnnotNames.put(annotKey, annotations[i].getSimpleName());
        }
    }

    protected void addPropertyStructs(PropertyStruct propertyStruct) {
        if (this.properties == null) this.properties = new HashMap<>();
        this.properties.put(propertyStruct.getName(), propertyStruct);

    }

    protected void addPropertyAnnotation(String propName, Class<? extends Annotation> annotation) {
        MethodStruct methodStruct = this.methods.get(propName);
        if (methodStruct == null) return;
        methodStruct.addAnnotationName(annotation.getSimpleName());
        addImport(annotation);
        if (this.propAnnotNames == null) this.propAnnotNames = new HashMap<>();
        int newIdx = getNewKeyIndex(this.methodAnnotNames.keySet());
        this.propAnnotNames.put(generateAnnotationMapKey(propName, newIdx), annotation.getSimpleName());
    }

    protected void addAllPropertyAnnotations(String propName, Class<? extends Annotation>[] annotations) {
        if (this.propAnnotNames == null) this.propAnnotNames = new HashMap<>();
        addAllImports(annotations);
        int newIdx = getNewKeyIndex(this.propAnnotNames.keySet());
        for (int i = 0; i < annotations.length; i++) {
            String annotKey = generateAnnotationMapKey(propName, newIdx+i);
            this.propAnnotNames.put(annotKey, annotations[i].getSimpleName());
        }
    }

    private void buildPropertiesVanilla(StringBuilder sb) {
        for (Map.Entry<String, PropertyStruct> entryProp :this.getProperties().entrySet()) {
            buildPropertyDeclaration(sb, entryProp.getValue().getTypeName(), entryProp.getValue().getName());
        }
    }

    private void buildPropertyWithAnnotation(StringBuilder sb) {
        String[] propAnnotArr = this.getPropAnnotNames().keySet().toArray(new String[0]);
        PropertyStruct previousPropStruct = null;
        for (String propAnnotKey : propAnnotArr) {
            String propKey = extractNameOf(propAnnotKey);
            PropertyStruct currentPropStruct = this.getProperties().get(propKey);
            if (currentPropStruct == null) {
                continue;
            }
            String annotName = this.getPropAnnotNames().get(propAnnotKey);
            buildAnnotation(sb, annotName);
            if (previousPropStruct != null && !previousPropStruct.equals(currentPropStruct)) {
                buildPropertyDeclaration(sb, currentPropStruct.getTypeName(), currentPropStruct.getName());
            }
            previousPropStruct = currentPropStruct;
        }
    }

    private StringBuilder prepare(StringBuilder sb) {
        if (sb == null) sb = new StringBuilder();
        return sb;
    }

    private String generateAnnotationMapKey(String name, int index) {
        return name + "_" + index;
    }

    private String extractNameOf(String key) {
        if (StringUtils.isBlank(key)) return "";
        String[] s = key.split("_");
        return s[0];
    }

    private int extractLastIndexOf(Set<String> keys) {
        if (keys.isEmpty()) return 0;
        SortedSet<String> sortedKeys = new TreeSet<>(keys);
        String lastKey = sortedKeys.last();
        if (StringUtils.isBlank(lastKey)) return 0;
        String[] s = lastKey.split("_");
        return Integer.parseInt(s[s.length - 1]);
    }

    private int getNewKeyIndex(Set<String> keys) {
        return extractLastIndexOf(keys) + 1;
    }

    protected enum AccessType {
        PUBLIC, PRIVATE, PROTECTED
    }
}
