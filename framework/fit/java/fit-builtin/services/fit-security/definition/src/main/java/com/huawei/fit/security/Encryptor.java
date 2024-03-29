/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.security;

/**
 * 表示加密服务。
 *
 * @author 季聿阶 j00559309
 * @since 2023-08-02
 */
public interface Encryptor {
    /**
     * 将指定明文进行加密。
     *
     * @param decrypted 表示待加密的明文的 {@link String}。
     * @return 表示加密后的密文的 {@link String}。
     */
    String encrypt(String decrypted);
}
