/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.parser;

import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.jvm.classfile.ClassFile;
import com.huawei.jade.fel.tool.annotation.Attribute;
import com.huawei.jade.fel.tool.annotation.ToolMethod;
import com.huawei.jade.maven.complie.entity.ParameterEntity;
import com.huawei.jade.maven.complie.entity.ToolEntity;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 基于 {@link net.bytebuddy.ByteBuddy} 对于 {@link ToolMethod} 注解的工具解析器。
 *
 * @author 杭潇
 * @author 易文渊
 * @since 2024-06-12
 */
public class ByteBuddyToolParser implements ToolParser {
    private static final String DOT = ".";

    private final TypePool typePool;
    private final String rootPath;

    public ByteBuddyToolParser(ClassLoader classLoader, String rootPath) {
        this.typePool = TypePool.Default.of(classLoader);
        this.rootPath = Paths.get(rootPath).normalize().toString();
    }

    @Override
    public List<ToolEntity> parseTool(File classFile) {
        String normalizedPath;
        try {
            normalizedPath = classFile.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("Failed get class canonical path.");
        }
        if (!normalizedPath.startsWith(rootPath)) {
            throw new IllegalStateException("The class not in root directory.");
        }
        String classFullName = normalizedPath.substring(rootPath.length() + 1,
                normalizedPath.length() - ClassFile.FILE_EXTENSION.length()).replace(File.separator, DOT);
        return this.parseMethodAnnotations(this.typePool.describe(classFullName).resolve());
    }

    private List<ToolEntity> parseMethodAnnotations(TypeDescription typeDescription) {
        List<ToolEntity> tools = new ArrayList<>();
        for (MethodDescription.InDefinedShape methodDescription : typeDescription.getDeclaredMethods()) {
            AnnotationDescription.Loadable<ToolMethod> toolAnnotation =
                    methodDescription.getDeclaredAnnotations().ofType(ToolMethod.class);
            if (toolAnnotation == null) {
                continue;
            }
            ToolMethod toolMethod = toolAnnotation.load();
            ToolEntity tool = new ToolEntity();
            tool.setNamespace(toolMethod.namespace());
            tool.setName(toolMethod.name());
            tool.setDescription(toolMethod.description());
            tool.setReturnType(Validation.notNull(JacksonTypeParser.getParameterSchema(
                    methodDescription.getReturnType()), "The return type cannot be null.").toString());
            tool.setExtraParameters(Arrays.asList(toolMethod.extraParams()));
            tool.setReturnConvertor(toolMethod.returnConverter());
            tool.setExtensions(parseAttributes(toolMethod));

            AnnotationDescription.Loadable<Property> propertyAnnotation =
                    methodDescription.getDeclaredAnnotations().ofType(Property.class);
            if (propertyAnnotation != null) {
                tool.setReturnDescription(propertyAnnotation.load().description());
            }
            tool.setParameterEntities(this.parseParameterAnnotations(methodDescription));
            this.parseRunnable(typeDescription, tool, methodDescription);
            tools.add(tool);
        }
        return tools;
    }

    private void parseRunnable(TypeDescription typeDescription, ToolEntity tool,
            MethodDescription.InDefinedShape methodDescription) {
        AnnotationDescription.Loadable<Fitable> fitableAnnotation =
                methodDescription.getDeclaredAnnotations().ofType(Fitable.class);
        if (fitableAnnotation == null) {
            throw new IllegalArgumentException("The tool method must contained fitable.");
        }
        String fitableId = fitableAnnotation.load().value();
        tool.setFitableId(fitableId);
        for (TypeDescription.Generic interfaceType : typeDescription.getInterfaces()) {
            TypeDescription interfaceDescription = interfaceType.asErasure();
            Optional<String> genericable = this.parseGenericable(methodDescription, interfaceDescription);
            if (genericable.isPresent()) {
                tool.setGenericableId(genericable.get());
                return;
            }
        }
        throw new IllegalArgumentException("The tool method must contained genericable.");
    }

    private List<ParameterEntity> parseParameterAnnotations(MethodDescription.InDefinedShape methodDescription) {
        List<ParameterEntity> parameterEntities = new ArrayList<>();
        for (ParameterDescription.InDefinedShape parameterDescription : methodDescription.getParameters()) {
            ParameterEntity entity = new ParameterEntity();
            entity.setType(Objects.requireNonNull(JacksonTypeParser.getParameterSchema(parameterDescription.getType()))
                    .toString());
            entity.setName(parameterDescription.getName());

            AnnotationDescription.Loadable<Property> paramAnnotation =
                    parameterDescription.getDeclaredAnnotations().ofType(Property.class);
            if (paramAnnotation != null) {
                Property property = paramAnnotation.load();
                entity.setDescription(property.description());
                entity.setRequired(property.required());
                entity.setDefaultValue(property.defaultValue());
            }

            parameterEntities.add(entity);
        }
        return parameterEntities;
    }

    private Optional<String> parseGenericable(MethodDescription.InDefinedShape methodDescription,
            TypeDescription interfaceDescription) {
        for (MethodDescription.InDefinedShape interfaceMethod : interfaceDescription.getDeclaredMethods()) {
            if (methodDescription.getName().equals(interfaceMethod.getName()) && methodDescription.getDescriptor()
                    .equals(interfaceMethod.getDescriptor())) {
                AnnotationDescription.Loadable<Genericable> genericableAnnotation =
                        interfaceMethod.getDeclaredAnnotations().ofType(Genericable.class);
                if (genericableAnnotation != null) {
                    return Optional.of(genericableAnnotation.load().value());
                }
            }
        }
        return Optional.empty();
    }

    private Map<String, String> parseAttributes(ToolMethod toolMethod) {
        return Arrays.stream(toolMethod.extensions()).collect(Collectors.toMap(Attribute::key, Attribute::value));
    }
}
