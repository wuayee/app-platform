/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.mapper;

/**
 * 记录应用的对话数量的数据库操作
 *
 * @author 姚江
 * @since 2024-10-17
 */
public interface AppChatNumMapper {
    /**
     * 插入或者更新app的对话数量 +1
     *
     * @param id 表的id
     * @param appId 应用id
     * @param chatMode 应用对话方式（active、inactive）
     */
    void insertOrAddOne(String id, String appId, String chatMode);

    /**
     * 更新app的对话数量 -1
     *
     * @param appId 应用id
     * @param chatMode 应用对话方式
     */
    void minusOne(String appId, String chatMode);
}
