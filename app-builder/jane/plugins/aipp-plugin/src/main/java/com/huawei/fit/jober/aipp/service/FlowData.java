/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Todo
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-29
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FlowData {
    private FlowMeta flowMeta;

    public FlowMeta getFlowMeta() {
        return flowMeta;
    }

    public void setFlowMeta(FlowMeta flowMeta) {
        this.flowMeta = flowMeta;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class FlowMeta {
        private Jober jober;

        public Jober getJober() {
            return jober;
        }

        public void setJober(Jober jober) {
            this.jober = jober;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Jober {
        private List<Map<String, Object>> inputParams;

        public List<Map<String, Object>> getInputParams() {
            return inputParams;
        }

        public void setInputParams(List<Map<String, Object>> inputParams) {
            this.inputParams = inputParams;
        }
    }
}