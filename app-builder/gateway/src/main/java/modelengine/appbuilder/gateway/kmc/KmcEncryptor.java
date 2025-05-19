/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.kmc;

/**
 * 添加 kmc 加密功能
 *
 * @author 李智超
 * @since 2024-11-26
 */
public class KmcEncryptor {
    private static final String ME_KS_USER = "modelenginepublic";

    /**
     * 加密数据。
     *
     * @param decrypted 加密后的数据，类型为 {@link String}。
     * @return 解密后的数据，类型为 {@link String}。
     */
    public String encrypt(String decrypted) {
        return KmcUtils.encrypt(decrypted, ME_KS_USER);
    }
}

