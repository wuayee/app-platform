/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.waterflow.domain.common.Constant;
import modelengine.fitframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 流程实例运行时承载的业务数据
 * 使用对象存储代替JSONObject，FIT不支持传JSONObject
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowData {
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

    /**
     * 流程执行时引擎内部数据
     */
    private Map<String, Object> contextData;

    /**
     * 流程执行时非落盘数据
     */
    private Map<String, Object> passData;

    /**
     * 流程执行时发生的异常错误信息
     */
    private String errorMessage;

    /**
     * 通过JSON字符串获取flowData对象
     * 从数据库获取数据对象
     *
     * @param jsonData flowData的json字符串
     * @return flowData对象
     */
    public static FlowData parseFromJson(String jsonData) {
        JSONObject flowData = JSONObject.parseObject(jsonData);
        Map<String, Object> contextDataMap = Optional.ofNullable(flowData.getJSONObject("contextData"))
                .orElse(new JSONObject())
                .toJavaObject(new TypeReference<HashMap<String, Object>>() {});

        Map<String, Object> businessDataMap = Optional.ofNullable(flowData.getJSONObject("businessData"))
                .orElse(new JSONObject())
                .toJavaObject(new TypeReference<HashMap<String, Object>>() {});

        Map<String, Object> passDataMap = Optional.ofNullable(flowData.getJSONObject("passData"))
                .orElse(new JSONObject())
                .toJavaObject(new TypeReference<HashMap<String, Object>>() {});

        LocalDateTime currentTime = LocalDateTime.now();

        return FlowData.builder()
                .operator(flowData.getString(Constant.OPERATOR_KEY))
                .startTime(currentTime)
                .businessData(businessDataMap)
                .contextData(contextDataMap)
                .passData(passDataMap)
                .errorMessage(flowData.getString("errorMessage"))
                .build();
    }

    /**
     * 获取flowData的json字符串
     * 保存到数据库时使用
     *
     * @return json字符串
     */
    public String translateToJson() {
        return JSON.toJSONString(FlowData.builder()
                .businessData(this.businessData)
                .contextData(this.contextData)
                .passData(null)
                .errorMessage(this.errorMessage)
                .operator(this.operator)
                .startTime(this.startTime)
                .build());
    }

    /**
     * 获取flowData中流程启动的应用，比如天舟、云核
     *
     * @return 流程启动的应用
     */
    public String getApplication() {
        return ObjectUtils.cast(businessData.getOrDefault("application", ""));
    }
}
