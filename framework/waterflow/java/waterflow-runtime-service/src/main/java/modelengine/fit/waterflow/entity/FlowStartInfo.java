/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.entity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程启动信息
 *
 * @author yangxiangyu
 * @since 2025/2/22
 */
public class FlowStartInfo {
    /**
     * 流程启动人
     */
    private String operator;
    /**
     * 流程启动的时间
     */
    private LocalDateTime startTime;

    /**
     * 流程执行所需的业务参数
     */
    private Map<String, Object> businessData;

    public FlowStartInfo(String operator, LocalDateTime startTime, Map<String, Object> businessData) {
        this.operator = operator;
        this.startTime = startTime;
        this.businessData = businessData;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Map<String, Object> getBusinessData() {
        return businessData;
    }

    public void setBusinessData(Map<String, Object> businessData) {
        this.businessData = businessData;
    }

    private FlowStartInfo(Builder builder) {
        this.operator = builder.operator;
        this.startTime = builder.startTime != null ? builder.startTime : LocalDateTime.now();
        this.businessData = builder.businessData != null ?
                new HashMap<>(builder.businessData) : Collections.emptyMap();
    }

    public static class Builder {
        private String operator;
        private LocalDateTime startTime;
        private Map<String, Object> businessData;

        public Builder operator(String operator) {
            this.operator = operator;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder businessData(Map<String, Object> businessData) {
            this.businessData = businessData;
            return this;
        }

        public Builder addBusinessData(String key, Object value) {
            if (this.businessData == null) {
                this.businessData = new HashMap<>();
            }
            this.businessData.put(key, value);
            return this;
        }

        public FlowStartInfo build() {
            return new FlowStartInfo(this);
        }
    }
}
