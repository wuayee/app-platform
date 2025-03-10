/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.mapper;

import modelengine.jade.store.entity.query.AppQuery;
import modelengine.jade.store.repository.pgsql.entity.AppDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 App 接口。
 *
 * @author 鲁为
 * @since 2024-06-19
 */
public interface AppMapper {
    /**
     * 注册应用。
     *
     * @param appDo 表示待增加的插件信息的 {@link AppDo}。
     */
    void addApp(AppDo appDo);

    /**
     * 基于工具的唯一标识查询某个应用。
     *
     * @param uniqueName 表示应用的唯一标识的 {@link String}。
     * @return 表示应用信息的 {@link AppDo}。
     */
    AppDo getAppByUniqueName(String uniqueName);

    /**
     * 根据动态查询条件分页查询应用。
     *
     * @param appQuery 表示查询参数的实体类的 {@link AppQuery}。
     * @return 所有任务的实体类的实例的 {@link List}{@code <}{@link AppDo}{@code >}。
     */
    List<AppDo> getApps(AppQuery appQuery);

    /**
     * 根据动态查询条件分页查询应用的总数。
     *
     * @param appQuery 表示查询参数的实体类的 {@link AppQuery}。
     * @return 所有任务的实体类的实例的 {@code int}。
     */
    int getAppsCount(AppQuery appQuery);

    /**
     * 删除应用。
     *
     * @param uniqueName 表示工具名的 {@link String}。
     */
    void deleteApp(String uniqueName);

    /**
     * 更新应用。
     *
     * @param appDo 表示应用信息的 {@link AppDo}。
     */
    void updateApp(AppDo appDo);
}
