/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.conf.ConfigDecryptor;

import java.util.Optional;

/**
 * 默认解密器的实现类。
 *
 * @author 孙怡菲 s00664640
 * @since 2024-05-11
 */
@Component
public class DefaultConfigDecrypt implements ConfigDecryptor {
    /**
     * 对指定键的原始值进行解密。
     * <p>如果不需要解密，则返回 {@link Optional#empty()}。</p>
     *
     * @param key 表示指定键的 {@link String}。
     * @param originValue 表示指定键的原始配置值的 {@link String}。
     * @return 表示解密后的值的 {@link Optional}{@code <}{@link String}{@code >}。
     */
    @Override
    public Optional<String> decrypt(String key, String originValue) {
        return Optional.of(originValue);
    }
}
