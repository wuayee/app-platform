/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.kmc;

import lombok.extern.slf4j.Slf4j;

/**
 * 添加 kmc 解密功能
 *
 * @author 李智超
 * @since 2024-11-26
 */
@Slf4j
public class KmcDecryptor {
    private static final String ME_KS_USER = "modelenginepublic";

    /**
     * 解密数据。
     *
     * @param encryptData 加密后的数据，类型为 {@link String}。
     * @return 解密后的数据，类型为 {@link String}。
     */
    public String decrypt(String encryptData) {
        log.info("Starting decryption process for data: {}", encryptData);
        return KmcUtils.decryptWithCache(encryptData, ME_KS_USER);
    }
}

