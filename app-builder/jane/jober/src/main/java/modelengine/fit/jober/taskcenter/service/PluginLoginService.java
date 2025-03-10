/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service;

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
