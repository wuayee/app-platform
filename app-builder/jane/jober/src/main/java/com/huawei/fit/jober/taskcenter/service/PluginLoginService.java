/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

/**
 * 插件获取cookie
 *
 * @author 姚江
 * @since 2024/7/27
 */
public interface PluginLoginService {
    /**
     * 清除原有cookie及首次使用时添加clientId
     *
     * @param clientId 客户端Id
     */
    void delete(String clientId);

    /**
     * 保存新的cookie
     *
     * @param clientId 客户端Id {@link String}
     * @param cookie 需要被存储的cookie {@link String}
     * @return 登录成功的信息 {@link String}
     */
    String save(String clientId, String cookie);

    /**
     * 根据客户端Id获取cookie
     *
     * @param clientId 客户端Id
     * @return cookie {@link String}
     */
    String get(String clientId);
}
