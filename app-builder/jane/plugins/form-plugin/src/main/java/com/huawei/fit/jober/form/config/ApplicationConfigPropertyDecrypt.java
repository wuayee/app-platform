/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.form.config;

import modelengine.fit.security.Decryptor;
import modelengine.fitframework.conf.ConfigDecryptor;
import modelengine.fitframework.inspection.Validation;

import java.util.Locale;
import java.util.Optional;

/**
 * ApplicationConfigPropertyDecrypt
 *
 * @author 熊以可
 * @since 2023-12-21
 */
public class ApplicationConfigPropertyDecrypt implements ConfigDecryptor {
    private final Decryptor decryptor;

    public ApplicationConfigPropertyDecrypt(Decryptor decryptor) {
        this.decryptor = Validation.notNull(decryptor, "The decryptor cannot be null.");
    }

    private static boolean isEncrypted(String value) {
        return value.toLowerCase(Locale.ROOT).startsWith("enc(") && value.endsWith(")");
    }

    @Override
    public Optional<String> decrypt(String key, String originValue) {
        if (!isEncrypted(originValue)) {
            return Optional.empty();
        }
        String encrypted = originValue.substring(4, originValue.length() - 1);
        String decrypted = this.decryptor.decrypt(encrypted);
        return Optional.of(decrypted);
    }
}
