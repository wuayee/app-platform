/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.security.kmc;

import modelengine.fit.security.Decryptor;
import modelengine.fitframework.annotation.Component;

/**
 * 添加 kmc 解密功能
 *
 * @author yangxiangyu
 * @since 2024-11-26
 */
@Component
public class KmcDecryptor implements Decryptor {
    /**
     * 表示 {@code ModelEngine} 项目的 {@code kmc} 加解密用户。
     */
    public static final String ME_KS_USER = "modelenginepublic";

    @Override
    public String decrypt(String encryptData) {
        return KmcUtils.decryptWithCache(encryptData, ME_KS_USER);
    }
}

