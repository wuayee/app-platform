/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import com.huawei.fit.security.Decryptor;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.conf.ConfigDecryptor;
import com.huawei.fitframework.inspection.Validation;

import java.util.Locale;
import java.util.Optional;

/**
 * kms 解密器的实现类。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-29
 */
@Component
public class PgsqlConfigDecrypt implements ConfigDecryptor {
    private final Decryptor decryptor;

    /**
     * 根据解密器类的实例构造 {@link PgsqlConfigDecrypt} 的实现类。
     *
     * @param decryptor 表示解密器类的实例的 {@link Decryptor}。
     */
    public PgsqlConfigDecrypt(Decryptor decryptor) {
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
