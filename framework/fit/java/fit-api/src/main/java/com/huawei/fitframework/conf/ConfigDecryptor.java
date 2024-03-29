/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.conf;

import java.util.Optional;

/**
 * 为配置提供解密器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-04-14
 */
public interface ConfigDecryptor {
    /**
     * 对指定键的原始值进行解密。
     * <p>如果不需要解密，则返回 {@link Optional#empty()}。</p>
     *
     * @param key 表示指定键的 {@link String}。
     * @param originValue 表示指定键的原始配置值的 {@link String}。
     * @return 表示解密后的值的 {@link Optional}{@code <}{@link String}{@code >}。
     */
    Optional<String> decrypt(String key, String originValue);
}
