/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.conf.ConfigDecryptor;

import java.util.Optional;

/**
 * 为 {@link ConfigDecryptor} 提供去除kms的a3000默认实现。
 *
 * @author 孙怡菲
 * @since 2023/11/28
 */
@Component("NoConfigPropertyDecrypt")
public class DefaultConfigDecryptor implements ConfigDecryptor {
    @Override
    public Optional<String> decrypt(String key, String originValue) {
        return Optional.of(originValue);
    }
}
