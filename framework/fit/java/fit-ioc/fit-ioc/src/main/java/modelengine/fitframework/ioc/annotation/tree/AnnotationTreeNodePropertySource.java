/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.ioc.annotation.tree;

import modelengine.fitframework.util.convert.Converter;

/**
 * 为注解的属性提供数据来源定义。
 *
 * @author 梁济时
 * @since 2022-07-07
 */
public interface AnnotationTreeNodePropertySource {
    /**
     * 获取数据来源的注解节点。
     *
     * @return 表示注解节点的 {@link AnnotationTreeNode}。
     */
    AnnotationTreeNode node();

    /**
     * 获取数据来源的属性名称。
     *
     * @return 表示属性名称的 {@link String}。
     */
    String property();

    /**
     * 获取值转换程序。
     *
     * @return 表示值转换程序的 {@link Converter}。
     */
    Converter converter();
}
