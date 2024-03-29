/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.jvm.support;

import com.huawei.fitframework.jvm.ClassDeclaration;
import com.huawei.fitframework.jvm.Modifier;
import com.huawei.fitframework.jvm.classfile.AccessFlag;
import com.huawei.fitframework.jvm.classfile.ClassFile;
import com.huawei.fitframework.jvm.classfile.constant.ClassInfo;
import com.huawei.fitframework.jvm.classfile.constant.Utf8Info;
import com.huawei.fitframework.jvm.classfile.lang.U2;
import com.huawei.fitframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 {@link ClassDeclaration} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-08
 */
public class DefaultClassDeclaration implements ClassDeclaration {
    private final String name;
    private final String superclass;
    private final Set<Modifier> modifiers;

    /**
     * 使用包含类型定义信息的类型文件初始化 {@link DefaultClassDeclaration} 类的新实例。
     *
     * @param file 表示包含类型定义信息的类型文件的 {@link ClassFile}。
     */
    public DefaultClassDeclaration(ClassFile file) {
        this.name = nameOfClass(file, file.thisClass());
        this.superclass = nameOfClass(file, file.superClass());
        this.modifiers = modifiersOf(file.accessFlags());
    }

    private static String nameOfClass(ClassFile file, U2 classIndex) {
        if (classIndex.compareTo(U2.ZERO) > 0) {
            ClassInfo info = file.constants().get(classIndex);
            String className = stringValue(file, info.nameIndex());
            return className.replace('/', '.');
        } else {
            return StringUtils.EMPTY;
        }
    }

    private static String stringValue(ClassFile file, U2 utf8Index) {
        return file.constants().<Utf8Info>get(utf8Index).stringValue();
    }

    private static Set<Modifier> modifiersOf(U2 accessFlags) {
        return AccessFlag.of(accessFlags).stream().map(Modifier::of).collect(Collectors.toSet());
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String superclass() {
        return this.superclass;
    }

    @Override
    public Set<Modifier> modifiers() {
        return this.modifiers;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
