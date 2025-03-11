/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.security.kmc;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import com.huawei.framework.crypt.grpc.client.utils.PassUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * kmc加解密工具类
 *
 * @author yangxiangyu
 * @since 2025/1/11
 */
@Component
public class KmcUtils {
    private static final Logger log = Logger.get(KmcUtils.class);
    private static final Cache<String, String> CACHE = CacheBuilder
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

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
