/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.mapper;

import com.huawei.jade.app.engine.base.dto.UsrAppCollectionDto;
import com.huawei.jade.app.engine.base.po.UsrAppCollectionPo;
import com.huawei.jade.app.engine.base.po.UsrAppInfoAndCollectionPo;

import java.util.List;

/**
 * 应用收藏映射
 *
 * @since 2024-5-25
 *
 */
public interface UsrAppCollectionMapper {
    /**
     * 插入应用收藏记录
     *
     * @param usrAppCollectionDto 应用收藏消息体
     */
    void insert(UsrAppCollectionDto usrAppCollectionDto);

    /**
     * 通过id删除应用收藏记录
     *
     * @param usrInfo 用户信息
     * @param appId 应用id
     */
    void deleteByUsrInfoAndAppId(String usrInfo, String appId);

    /**
     * 通过id删除应用收藏记录
     *
     * @param appId 应用id
     */
    void deleteByAppId(String appId);

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param usrInfo 用户信息
     * @return 收藏列表
     */
    List<UsrAppCollectionPo> getCollectionsByUsrInfo(String usrInfo);

    /**
     * 通过用户信息获取应用收藏列表详细信息
     *
     * @param usrInfo 用户信息
     * @return 收藏列表详细信息
     */
    List<UsrAppInfoAndCollectionPo> getAppInfoByUsrInfo(String usrInfo);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param collectionNum 收藏用户计数
     * @param appId 应用id
     */
    void updateCollectionUsrCntByAppId(String appId, Integer collectionNum);

    /**
     * 查询应用收藏用户数量
     *
     * @param appId 应用id
     * @return 应用收藏用户数
     */
    Integer getCollectionUsrCntByAppId(String appId);

    /**
     * 获取默认应用
     *
     * @param usrInfo 用户信息
     * @return 应用信息
     */
    UsrAppInfoAndCollectionPo getDefaultAppInfo(String usrInfo);
}
