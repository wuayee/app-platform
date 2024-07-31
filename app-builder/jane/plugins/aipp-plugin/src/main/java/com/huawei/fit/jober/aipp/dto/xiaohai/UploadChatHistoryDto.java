/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.xiaohai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天记录上传结构体
 *
 * @author h00804153
 * @since 2024/3/21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadChatHistoryDto {
    private String answer;
    private String appId;
    private Integer conversationId;
    private String createUser;
    private String query;

    /**
     * 问答结构体
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class XiaohaiQADto {
        private QADto.Question question;
        private Answer answer;

        /**
         * 回答结构体
         */
        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Answer {
            private String answer;
            private List<String> chartType;
            private List<String> chartData;
            private List<String> chartTitle;
            private List<String> chartAnswer;
            private String type;
            private List<String> chartSummary;
        }
    }
}
