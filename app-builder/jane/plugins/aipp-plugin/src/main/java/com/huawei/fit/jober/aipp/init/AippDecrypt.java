/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.init;

import com.huawei.fit.security.Decryptor;
import modelengine.fitframework.conf.ConfigDecryptor;
import modelengine.fitframework.inspection.Validation;

import java.util.Locale;
import java.util.Optional;

/**
 * AippDecrypt
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
public class AippDecrypt implements ConfigDecryptor {
    private final Decryptor decryptor;

    public AippDecrypt(Decryptor decryptor) {
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
