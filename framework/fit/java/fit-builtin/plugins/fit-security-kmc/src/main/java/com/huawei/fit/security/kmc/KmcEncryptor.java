/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.kmc;

import static com.huawei.fit.security.kmc.KmcDecryptor.ME_KS_USER;

import com.huawei.fit.security.Encryptor;
import com.huawei.fitframework.annotation.Component;

/**
 * 添加 kmc 加密功能
 *
 * @author yangxiangyu
 * @since 2024-11-26
 */
@Component
public class KmcEncryptor implements Encryptor {
    @Override
    public String encrypt(String decrypted) {
        return KmcUtils.encrypt(decrypted, ME_KS_USER);
    }
}

