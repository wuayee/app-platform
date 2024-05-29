/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.jade.app.engine.base.dto.CollectionAppInfoDto;
import com.huawei.jade.app.engine.base.dto.UsrAppCollectionDto;
import com.huawei.jade.app.engine.base.mapper.UsrAppCollectionMapper;
import com.huawei.jade.app.engine.base.po.UsrAppCollectionPo;
import com.huawei.jade.app.engine.base.po.UsrAppInfoAndCollectionPo;
import com.huawei.jade.app.engine.base.service.UsrAppCollectionService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户收藏应用实现类
 *
 * @since 2024-5-25
 *
 */
@Component
public class UsrAppCollectionServiceImpl implements UsrAppCollectionService {
    private static final Logger log = Logger.get(UsrFeedbackServiceImpl.class);

    /**
     * 默认应用id （小海）
     */
    private static final String DEFAULT_APP_ID = "3a617d8aeb1d41a9ad7453f2f0f70d61";

    private final UsrAppCollectionMapper usrAppCollectionMapper;

    public UsrAppCollectionServiceImpl(UsrAppCollectionMapper userAppCollectionMapper) {
        this.usrAppCollectionMapper = userAppCollectionMapper;
    }

    /**
     * 创建应用收藏记录
     *
     * @param usrCollectionDto 用户应用收藏信息
     * @return 应用收藏记录id
     */
    @Transactional
    @Override
    public Long create(UsrAppCollectionDto usrCollectionDto) {
        usrAppCollectionMapper.insert(usrCollectionDto);
        usrAppCollectionMapper.updateCollectionUsrCntByAippId(usrCollectionDto.getAippId(), 1);
        return usrCollectionDto.getId();
    }

    /**
     * 更新应用收藏记录
     *
     * @param collectionId 收藏记录id
     * @param usrCollectionDto 用户应用收藏信息
     */
    @Override
    public void updateOne(Long collectionId, UsrAppCollectionDto usrCollectionDto) {
        usrAppCollectionMapper.updateOne(collectionId, usrCollectionDto.getIsDefault());
    }

    /**
     * 通过id删除应用收藏记录
     *
     * @param usrInfo 用户信息
     * @param aippId 应用Id
     */
    @Transactional
    @Override
    public void deleteByUsrInfoAndAippId(String usrInfo, String aippId) {
        usrAppCollectionMapper.deleteByUsrInfoAndAippId(usrInfo, aippId);
        usrAppCollectionMapper.updateCollectionUsrCntByAippId(aippId, -1);
    }

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param usrInfo 用户信息
     * @return 应用收藏列表
     */
    @Override
    public List<UsrAppCollectionPo> getCollectionsByUsrInfo(String usrInfo) {
        return usrAppCollectionMapper.getCollectionsByUsrInfo(usrInfo);
    }

    /**
     * 获取应用信息
     *
     * @param usrInfo 用户信息
     * @return 应用信息消息类
     */
    @Override
    public CollectionAppInfoDto getAppInfoByUsrInfo(String usrInfo) {
        List<UsrAppInfoAndCollectionPo> collectionList = usrAppCollectionMapper.getAppInfoByUsrInfo(usrInfo);
        UsrAppInfoAndCollectionPo defaultApp = usrAppCollectionMapper.getDefaultAppInfo(DEFAULT_APP_ID);

        for (UsrAppInfoAndCollectionPo usrAppCollectionDto : collectionList) {
            if (usrAppCollectionDto.getIsDefault()) {
                defaultApp = usrAppCollectionDto;
                collectionList.remove(usrAppCollectionDto);
                return new CollectionAppInfoDto(collectionList, defaultApp);
            }
        }

        String aippId = defaultApp.getAippId();
        collectionList = collectionList.stream()
                .filter(n -> !Objects.equals(aippId, n.getAippId()))
                .collect(Collectors.toList());
        return new CollectionAppInfoDto(collectionList, defaultApp);
    }

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param aippId 应用id
     */
    @Override
    public void updateCollectionUsrCntByAippId(String aippId) {
        usrAppCollectionMapper.updateCollectionUsrCntByAippId(aippId, 1);
    }

    /**
     * 通过应用id获取收藏用户数量
     *
     * @param aippId 应用id
     * @return 应用收藏用户数量
     */
    @Override
    public Integer getCollectionUsrCntByAippId(String aippId) {
        return usrAppCollectionMapper.getCollectionUsrCntByAippId(aippId);
    }
}
