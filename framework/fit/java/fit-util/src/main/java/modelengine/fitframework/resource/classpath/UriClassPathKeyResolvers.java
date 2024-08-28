/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package modelengine.fitframework.resource.classpath;

import modelengine.fitframework.resource.classpath.support.FileUriClassPathKeyResolver;

/**
 * 为 {@link UriClassPathKeyResolver} 提供工具方法。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
final class UriClassPathKeyResolvers {
    /**
     * 获取 {@link UriClassPathKeyResolver} 的当前实现。
     */
    static final UriClassPathKeyResolver CURRENT =
            UriClassPathKeyResolver.combine(FileUriClassPathKeyResolver.INSTANCE);

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private UriClassPathKeyResolvers() {}
}
