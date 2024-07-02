/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.parser;

import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.annotation.Property;
import com.huawei.jade.carver.tool.annotation.ToolMethod;
import com.huawei.jade.maven.complie.entity.MethodEntity;
import com.huawei.jade.maven.complie.entity.ParameterEntity;
import com.huawei.jade.maven.complie.entity.ToolEntity;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 基于 {@link net.bytebuddy.ByteBuddy} 对于 {@link ToolMethod} 注解的工具解析器。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-12
 */
public class ByteBuddyToolParser implements ToolParser {
    /**
     * {@link ToolParser} 的 ByteBuddy 的单例。
     */
    public static final ToolParser INSTANCE = new ByteBuddyToolParser();
    private static final int CLASSES_LENGTH = "classes.".length();
    private static final String CLASSES = "classes";
    private static final int CLASS_LENGTH = ".class".length();
    private static final String DOT = ".";

    private ByteBuddyToolParser() {}

    @Override
    public ToolEntity parseTool(Path classFilePath) {
        ToolEntity toolEntity = new ToolEntity();
        try {
            Path absolutePath = classFilePath.toAbsolutePath().normalize();
            Path directoryPath = classFilePath.getParent();
            String className = this.getClassName(classFilePath);

            String basePath =
                    absolutePath.toString().substring(0, absolutePath.toString().indexOf(CLASSES) + CLASSES_LENGTH);

            List<URL> urlList = new ArrayList<>();
            urlList.add(directoryPath.toUri().toURL());
            urlList.add(Paths.get(basePath).toUri().toURL());

            try (URLClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[0]),
                    ByteBuddyToolParser.class.getClassLoader())) {
                TypePool typePool = TypePool.Default.of(classLoader);
                this.extractMethodAnnotations(typePool.describe(className).resolve(), toolEntity);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse the class file.", e);
        }
        return toolEntity;
    }

    private String getClassName(Path classFilePath) {
        String pathStr = classFilePath.toString().replace(File.separator, DOT);
        return pathStr.substring(pathStr.indexOf(CLASSES + DOT) + CLASSES_LENGTH, pathStr.length() - CLASS_LENGTH);
    }

    private void extractMethodAnnotations(TypeDescription typeDescription, ToolEntity toolEntity) {
        for (MethodDescription.InDefinedShape methodDescription : typeDescription.getDeclaredMethods()) {
            AnnotationDescription.Loadable<ToolMethod> toolAnnotation =
                    methodDescription.getDeclaredAnnotations().ofType(ToolMethod.class);
            if (toolAnnotation == null) {
                continue;
            }
            MethodEntity method = new MethodEntity();
            method.setMethodName(toolAnnotation.load().name());
            method.setMethodDescription(toolAnnotation.load().description());
            method.setReturnType(
                    Objects.requireNonNull(JacksonTypeParser.getParameterSchema(methodDescription.getReturnType()))
                            .toString());

            AnnotationDescription.Loadable<Property> propertyAnnotation =
                    methodDescription.getDeclaredAnnotations().ofType(Property.class);
            if (propertyAnnotation != null) {
                method.setReturnDescription(propertyAnnotation.load().description());
            }

            List<ParameterEntity> parameterEntities = this.extractParameterAnnotations(methodDescription);
            method.setParameterEntities(parameterEntities);
            this.checkFitableAndGenericableAnnotations(typeDescription, method, methodDescription);
            toolEntity.addMethod(method);
        }
    }

    private List<ParameterEntity> extractParameterAnnotations(MethodDescription.InDefinedShape methodDescription) {
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

    private void checkFitableAndGenericableAnnotations(TypeDescription typeDescription, MethodEntity method,
            MethodDescription.InDefinedShape methodDescription) {
        AnnotationDescription.Loadable<Fitable> fitableAnnotation =
                methodDescription.getDeclaredAnnotations().ofType(Fitable.class);
        if (fitableAnnotation == null) {
            return;
        }
        String fitableId = fitableAnnotation.load().value();
        method.setFitableId(fitableId);
        for (TypeDescription.Generic interfaceType : typeDescription.getInterfaces()) {
            TypeDescription interfaceDescription = interfaceType.asErasure();
            this.parseGenericable(method, methodDescription, interfaceDescription);
        }
    }

    private void parseGenericable(MethodEntity method, MethodDescription.InDefinedShape methodDescription,
            TypeDescription interfaceDescription) {
        for (MethodDescription.InDefinedShape interfaceMethod : interfaceDescription.getDeclaredMethods()) {
            if (methodDescription.getName().equals(interfaceMethod.getName()) && methodDescription.getDescriptor()
                    .equals(interfaceMethod.getDescriptor())) {
                AnnotationDescription.Loadable<Genericable> genericableAnnotation =
                        interfaceMethod.getDeclaredAnnotations().ofType(Genericable.class);
                if (genericableAnnotation != null) {
                    String genericableId = genericableAnnotation.load().value();
                    method.setGenericableId(genericableId);
                }
            }
        }
    }
}
