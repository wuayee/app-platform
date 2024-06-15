/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.chat.CreateChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;
import com.huawei.fit.jober.aipp.service.AippChatService;
import com.huawei.fitframework.annotation.Component;

import java.util.List;

/**
 * 历史对话接口
 *
 * @author x00576283
 * @since 2023/12/11
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/chat", group = "aipp对话管理接口")
public class AippChatController extends AbstractController {
    private final AippChatService aippChatService;


    /**
     * AippChatController
     *
     * @param authenticator authenticator
     * @param aippChatService aippChatService
     */
    public AippChatController(Authenticator authenticator, AippChatService aippChatService) {
        super(authenticator);
        this.aippChatService = aippChatService;
    }


    /**
     * createChat
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param body body
     * @return Rsp<QueryChatRsp>
     */
    @PostMapping(path = "", description = "创建会话接口")
    public Rsp<QueryChatRsp> createChat(HttpClassicServerRequest httpRequest,
                                        @PathVariable("tenant_id") String tenantId,
                                        @RequestBody CreateChatRequest body) {
        return Rsp.ok(this.aippChatService.createChat(body, this.contextOf(httpRequest, tenantId)));
    }


    /**
     * queryChat
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param chatId chatId
     * @param body body
     * @return Rsp<QueryChatRsp>
     */
    @PostMapping(path = "/chat_list/{chat_id}", description = "查询会话接口")
    public Rsp<QueryChatRsp> queryChat(HttpClassicServerRequest httpRequest,
                                       @PathVariable("tenant_id") String tenantId,
                                       @PathVariable("chat_id") String chatId,
                                       @RequestBody QueryChatRequest body) {
        return Rsp.ok(this.aippChatService.queryChat(body, chatId, this.contextOf(httpRequest, tenantId)));
    }


    /**
     * queryChatList
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param body body
     * @return Rsp<List<QueryChatRsp>>
     */
    @PostMapping(path = "/chat_list", description = "查询会话列表接口")
    public Rsp<List<QueryChatRsp>> queryChatList(HttpClassicServerRequest httpRequest,
                                                 @PathVariable("tenant_id") String tenantId,
                                                 @RequestBody QueryChatRequest body) {
        return Rsp.ok(this.aippChatService.queryChatList(body, this.contextOf(httpRequest, tenantId)));
    }


    /**
     * deleteChat
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param appId appId
     * @param chatId chatId
     * @return Rsp<Void>
     */
    @DeleteMapping(path = "", description = "删除会话接口")
    public Rsp<Void> deleteChat(HttpClassicServerRequest httpRequest,
                                @PathVariable("tenant_id") String tenantId,
                                @RequestParam(value = "app_id", required = false) String appId,
                                @RequestParam("chat_id") String chatId) {
        this.aippChatService.deleteChat(chatId, appId, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }


    /**
     * updateChat
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param chatId chatId
     * @param body body
     * @return Rsp<QueryChatRsp>
     */
    @PostMapping(path = "/{chat_id}", description = "更新会话接口")
    public Rsp<QueryChatRsp> updateChat(HttpClassicServerRequest httpRequest,
                                        @PathVariable("tenant_id") String tenantId,
                                        @PathVariable("chat_id") String chatId,
                                        @RequestBody CreateChatRequest body) {
        return Rsp.ok(this.aippChatService.updateChat(chatId, body, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 重新发起会话。
     *
     * @param httpRequest Http 请求体。
     * @param currentInstanceId 需要重新发起会话的实例 ID。
     * @param tenantId 租户 ID。
     * @param body 创建会话请求体。
     * @return 表示会话相应体的 {@link Rsp}{@code <}{@link QueryChatRsp}{@code >}。
     */
    @PostMapping(path = "/instances/{current_instance_id}", description = "重新发起会话接口")
    public Rsp<QueryChatRsp> restartChat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("current_instance_id") String currentInstanceId,
            @RequestBody CreateChatRequest body) {
        return Rsp.ok(this.aippChatService.restartChat(currentInstanceId, body, this.contextOf(httpRequest, tenantId)));
    }
}
