/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PatchMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.base.common.resp.Response;
import com.huawei.jade.app.engine.base.common.resp.ResponseCode;
import com.huawei.jade.app.engine.base.dto.CollectionAppInfoDto;
import com.huawei.jade.app.engine.base.dto.UsrAppCollectionDto;
import com.huawei.jade.app.engine.base.po.UsrAppCollectionPo;
import com.huawei.jade.app.engine.base.service.UsrAppCollectionService;

import java.util.List;

/**
 * 处理用户收藏应用请求
 *
 * @since 2024-5-29
 *
 */
@Component
@RequestMapping("/aipp/usr")
public class UsrCollectionController {
    private final UsrAppCollectionService usrAppCollectionService;

    public UsrCollectionController(UsrAppCollectionService usrAppCollectionService) {
        this.usrAppCollectionService = usrAppCollectionService;
    }


    /**
     * 创建用户应用收藏记录
     *
     * @param usrAppCollectionDto 用户应用收藏消息体
     * @return 收藏记录id
     */
    @PostMapping("/collection")
    public Response<Long> createUsrAppCollection(@RequestBody UsrAppCollectionDto usrAppCollectionDto) {
        Long collectionId = usrAppCollectionService.create(usrAppCollectionDto);
        return Response.success(collectionId, ResponseCode.OK);
    }

    /**
     * 更新用户应用收藏记录
     *
     * @param collectionId 应用收藏记录id
     * @param usrAppCollectionDto 用户应用收藏消息体
     * @return 响应
     */
    @PatchMapping("/collection/{collectionId}")
    public Response<Void> createUsrAppCollection(@PathVariable("collectionId") Long collectionId,
                                                 @RequestBody UsrAppCollectionDto usrAppCollectionDto) {
        usrAppCollectionService.updateOne(collectionId, usrAppCollectionDto);
        return Response.success(ResponseCode.OK);
    }

    /**
     * 删除应用收藏记录
     *
     * @param usrAppCollectionDto 应用收藏记录信息
     * @return 响应
     */
    @DeleteMapping("/collection")
    public Response<Void> deleteUsrCollectionApp(@RequestBody UsrAppCollectionDto usrAppCollectionDto) {
        usrAppCollectionService.deleteByUsrInfoAndAippId(usrAppCollectionDto.getUsrInfo(),
                usrAppCollectionDto.getAippId());
        return Response.success(ResponseCode.OK);
    }

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param usrInfo 用户信息
     * @return 收藏应用列表
     */
    @GetMapping("/collection/{usrInfo}")
    public Response<List<UsrAppCollectionPo>> getCollectionsByUsrInfo(@PathVariable("usrInfo") String usrInfo) {
        return Response.success(usrAppCollectionService.getCollectionsByUsrInfo(usrInfo), ResponseCode.OK);
    }

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param usrInfo 用户信息
     * @return 收藏应用列表
     */
    @GetMapping("/collection/app/{usrInfo}")
    public Response<CollectionAppInfoDto> getAppCollectionInfoByUsrInfo(@PathVariable("usrInfo") String usrInfo) {
        return Response.success(usrAppCollectionService.getAppInfoByUsrInfo(usrInfo), ResponseCode.OK);
    }

    /**
     * 获取应用收藏用户数量
     *
     * @param aippId 应用id
     * @return 用户收藏数量
     */
    @GetMapping("/collection/count/{aippId}")
    public Response<Integer> getCollectionUsrCntByAippId(@PathVariable("aippId") String aippId) {
        return Response.success(usrAppCollectionService.getCollectionUsrCntByAippId(aippId), ResponseCode.OK);
    }
}
