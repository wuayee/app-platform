/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service;

import modelengine.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.base.dto.CollectionAppInfoDto;
import com.huawei.jade.app.engine.base.dto.UsrAppCollectionDto;
import com.huawei.jade.app.engine.base.po.UsrAppCollectionPo;

import java.util.List;

/**
 * 用户应用收藏功能接口
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
     * 删除用户收藏应用记录
     *
     * @param usrInfo 用户信息
     * @param appId 应用Id
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.deleteByUsrInfoAndAppId")
    void deleteByUsrInfoAndAppId(String usrInfo, String appId);

    /**
     * 删除收藏应用记录
     *
     * @param appId 应用Id
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.deleteByAppId")
    void deleteByAppId(String appId);

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
     * @param appId 应用id
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.updateCollectionUsrCntByAppId")
    void updateCollectionUsrCntByAppId(String appId);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param appId 应用id
     * @return 应用收藏用户数量
     */
    @Genericable(id = "com.huawei.jade.app.engine.base.service.UsrCollectionService.getCollectionUsrCntByAppId")
    Integer getCollectionUsrCntByAppId(String appId);
}
