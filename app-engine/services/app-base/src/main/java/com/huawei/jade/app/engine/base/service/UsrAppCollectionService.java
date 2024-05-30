/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.base.dto.CollectionAppInfoDto;
import com.huawei.jade.app.engine.base.dto.UsrAppCollectionDto;
import com.huawei.jade.app.engine.base.po.UsrAppCollectionPo;

import java.util.List;

/**
 * Aipp用户应用手残功能接口
 *
 * @since 2024-5-25
 *
 */
public interface UsrAppCollectionService {
    /**
     * 插入用户收藏应用信息
     *
     * @param usrCollectionDto 用户应用收藏信息
     * @return 收藏记录id
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.create")
    Long create(UsrAppCollectionDto usrCollectionDto);

    /**
     * 更新用户收藏应用记录
     *
     * @param collectionId 收藏记录id
     * @param usrCollectionDto 用户应用收藏信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.updateOne")
    void updateOne(Long collectionId, UsrAppCollectionDto usrCollectionDto);

    /**
     * 删除用户收藏应用记录
     *
     * @param usrInfo 用户信息
     * @param aippId 应用Id
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.deleteByUsrInfoAndAippId")
    void deleteByUsrInfoAndAippId(String usrInfo, String aippId);

    /**
     * 删除收藏应用记录
     *
     * @param aippId 应用Id
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.deleteByAippId")
    void deleteByAippId(String aippId);

    /**
     * 获取用户收藏应用列表
     *
     * @param usrInfo 用户信息
     * @return 用户收藏列表
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.getCollectionsByUsrInfo")
    List<UsrAppCollectionPo> getCollectionsByUsrInfo(String usrInfo);

    /**
     * 获取用户收藏应用列表
     *
     * @param usrInfo 用户信息
     * @return 获取所有应用信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.getAppInfoByUsrInfo")
    CollectionAppInfoDto getAppInfoByUsrInfo(String usrInfo);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param aippId 应用id
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.updateCollectionUsrCntByAippId")
    void updateCollectionUsrCntByAippId(String aippId);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param aippId 应用id
     * @return 应用收藏用户数量
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.getCollectionUsrCntByAippId")
    Integer getCollectionUsrCntByAippId(String aippId);
}
