/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.xiaohai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 聊天记录结构体
 *
 * @author l00498867
 * @since 2024/3/20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QADto {
    private Question question;
    private Answer answer;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Question {
        private String createUser;
        private String appId;
        private Integer conversationId;
        private String query;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Answer {
        private String answer;
        private List<String> chartType;
        private List<Map<String, Object>> chartData;
        private List<String> chartTitle;
        private List<String> chartAnswer;
        private String type;
        private List<String> chartSummary;
    }
}
