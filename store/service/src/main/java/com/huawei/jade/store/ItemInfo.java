/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

import com.huawei.fitframework.pattern.builder.BuilderFactory;

import java.util.Map;
import java.util.Set;

/**
 * 表示商品信息。
 *
 * @author 季聿阶
 * @since 2024-04-25
 */
public interface ItemInfo {
    /**
     * 获取商品的分类。
     *
     * @return 表示商品分类的 {@link String}。
     */
    String category();

    /**
     * 获取商品的分组。
     *
     * @return 表示商品的分组的 {@link String}。
     */
    String group();

    /**
     * 获取商品的名字。
     *
     * @return 表示商品的名字的 {@link String}。
     */
    String name();

    /**
     * 获取商品的唯一名字。
     *
     * @return 表示商品的唯一名字的 {@link String}。
     */
    String uniqueName();

    /**
     * 获取商品的描述。
     *
     * @return 表示商品的描述的 {@link String}。
     */
    String description();

    /**
     * 获取商品的标签集合。
     *
     * @return 表示商品的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> tags();

    /**
     * 获取商品的格式规范描述。
     *
     * @return 表示商品的格式规范描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> schema();

    /**
     * {@link ItemInfo} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置商品的分类。
         *
         * @param category 表示待设置的商品分类的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder category(String category);

        /**
         * 向当前构建器中设置商品的分组。
         *
         * @param group 表示待设置的商品分组的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder group(String group);

        /**
         * 向当前构建器中设置商品的名字。
         *
         * @param name 表示待设置的商品名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置商品的唯一名字。
         *
         * @param uniqueName 表示待设置的商品唯一名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder uniqueName(String uniqueName);

        /**
         * 向当前构建器中设置商品的描述。
         *
         * @param description 表示待设置的商品描述的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 向当前构建器中设置商品的标签集合。
         *
         * @param tags 表示待设置的商品标签集合的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tags(Set<String> tags);

        /**
         * 向当前构建器中设置商品的格式规范描述。
         *
         * @param schema 表示待设置的商品格式规范描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder schema(Map<String, Object> schema);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link ItemInfo}。
         */
        ItemInfo build();
    }

    /**
     * 获取 {@link ItemInfo} 的构建器。
     *
     * @return 表示 {@link ItemInfo} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link ItemInfo} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link ItemInfo}。
     * @return 表示 {@link ItemInfo} 的构建器的 {@link Builder}。
     */
    static Builder custom(ItemInfo value) {
        return BuilderFactory.get(ItemInfo.class, Builder.class).create(value);
    }
}
