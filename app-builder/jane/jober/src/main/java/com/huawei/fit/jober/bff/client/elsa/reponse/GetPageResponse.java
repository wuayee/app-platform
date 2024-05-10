/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.bff.client.elsa.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 分页查询流程定义列表response
 *
 * @author y00679285
 * @since 2023/10/11
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPageResponse {
    private int code;

    private List<FlowInfo> data;

    private int size;

    private int count;

    private int cursor;

    private String msg;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowInfo {
        private int id;

        private String documentId;

        private String text;

        private String spaceName;

        private String type;

        private String parentId;

        private String extension;

        private String createUser;

        private String updateUser;

        private String createTime;

        private String updateTime;

        private String roleId;

        private String version;

        private String versionStatus;

        private String releaseTime;

        private int runningInstance;

        private int allInstance;

        private int errorInstance;
    }
}
