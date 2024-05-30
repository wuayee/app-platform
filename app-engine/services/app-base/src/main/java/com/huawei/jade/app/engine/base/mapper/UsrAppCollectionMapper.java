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
     * 更新应用收藏记录
     *
     * @param collectionId 应用收藏id
     * @param isDefault 是否被设为默认应用
     */
    void updateOne(Long collectionId, boolean isDefault);

    /**
     * 通过id删除应用收藏记录
     *
     * @param usrInfo 用户信息
     * @param aippId 应用id
     */
    void deleteByUsrInfoAndAippId(String usrInfo, String aippId);

    /**
     * 通过id删除应用收藏记录
     *
     * @param aippId 应用id
     */
    void deleteByAippId(String aippId);

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param usrInfo 用户信息
     * @return 收藏列表
     */
    List<UsrAppCollectionPo> getCollectionsByUsrInfo(String usrInfo);

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param usrInfo 用户信息
     * @return 收藏列表
     */
    List<UsrAppInfoAndCollectionPo> getAppInfoByUsrInfo(String usrInfo);

    /**
     * 获取默认应用（小海）
     *
     * @param aippId 应用id
     * @return 应用信息
     */
    UsrAppInfoAndCollectionPo getDefaultAppInfo(String aippId);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param collectionNum 收藏用户计数
     * @param aippId 应用id
     */
    void updateCollectionUsrCntByAippId(String aippId, Integer collectionNum);

    /**
     * 查询应用收藏用户数量
     *
     * @param aippId 应用id
     * @return 应用收藏用户数
     */
    Integer getCollectionUsrCntByAippId(String aippId);
}
