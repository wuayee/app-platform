/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fit.security.Decryptor;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.conf.ConfigDecryptor;

import java.util.Locale;
import java.util.Optional;

/**
 * 配置文件解密配置器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-09-01
 */
// @Component("JoberApplicationConfigPropertyDecrypt")
public class SccConfigDecryptor implements ConfigDecryptor {
    private final Decryptor decryptor;

    public SccConfigDecryptor(Decryptor decryptor) {
        this.decryptor = decryptor;
    }

    private static boolean isEncrypted(String value) {
        return value.toLowerCase(Locale.ROOT).startsWith("enc(") && value.endsWith(")");
    }

    @Override
    public Optional<String> decrypt(String key, String originValue) {
        if (!isEncrypted(originValue)) {
            return Optional.empty();
        }
        return Optional.of(this.decryptor.decrypt(originValue.substring(4, originValue.length() - 1)));
    }
}