/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.scc;

import com.huawei.fit.security.Decryptor;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.seccomponent.common.SCException;
import com.huawei.seccomponent.crypt.CryptoAPI;

/**
 * 添加 scc 加解密功能
 *
 * @author 杭潇 h00675922
 * @since 2024-03-15
 */
@Component
public class SccDecryptor implements Decryptor {
    private final CryptoAPI cryptoAPI = new CryptoAPI();

    /**
     * 初始化 SCC 加解密插件。
     *
     * @param configValue 表示 SCC 配置文件地址的 {@code String}。
     * @throws SCException 当 SCC 初始化错误时抛出的异常。
     */
    public SccDecryptor(@Value("${plugin.scc.conf-file-path}") String configValue) throws SCException {
        this.cryptoAPI.initialize(configValue);
    }

    /**
     * 解密方法。
     *
     * @param cipherText 密文。
     * @return 解密后的明文。
     */
    @Override
    public String decrypt(String cipherText) {
        try {
            return cryptoAPI.decrypt(cipherText).getString();
        } catch (SCException ex) {
            throw new IllegalStateException("Failed to decrypt.", ex);
        }
    }
}

