/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.kmc;

import com.huawei.framework.crypt.grpc.client.utils.PassUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * kmc加解密工具类
 *
 * @author yangxiangyu
 * @since 2025/1/11
 */
@Slf4j
public class KmcUtils {
    private static final Cache<String, String> CACHE =
            CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(1000).build();

    /**
     * kmc解密并放入缓存中
     *
     * @param encryptData 需要解密的数据
     * @param user 用户
     * @return 解密结果
     */
    public static String decryptWithCache(String encryptData, String user) {
        String decryptData = "";
        try {
            decryptData = CACHE.get(encryptData, () -> PassUtils.decryptWithUser(encryptData, user));
        } catch (ExecutionException e) {
            log.error("Kmc decrypt error, encryptData:{}, user:{}.", encryptData, user);
            log.error("Exception: ", e);
        }
        return decryptData;
    }

    /**
     * kmc解密
     *
     * @param encryptData 需要解密的数据
     * @param user 用户
     * @return 解密结果
     */
    public static String decrypt(String encryptData, String user) {
        return PassUtils.decryptWithUser(encryptData, user);
    }

    /**
     * kmc加密
     *
     * @param decryptData 需要加密的数据
     * @param user 用户
     * @return 加密结果
     */
    public static String encrypt(String decryptData, String user) {
        return PassUtils.encryptWithUser(decryptData, user);
    }
}
