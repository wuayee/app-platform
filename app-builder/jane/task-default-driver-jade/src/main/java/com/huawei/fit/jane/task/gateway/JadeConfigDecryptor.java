/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.conf.ConfigDecryptor;

import java.util.Optional;

/**
 * 配置文件解密配置器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-09-01
 */
@Component("NoConfigPropertyDecrypt")
public class JadeConfigDecryptor implements ConfigDecryptor {
    @Override
    public Optional<String> decrypt(String key, String originValue) {
        return Optional.of(originValue);
    }
}
