/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.biz.common.vo;

import java.util.Map;

/**
 * 流程数据VO类
 *
 * @author 陈镕希
 * @since 2024-02-26
 */
public class FlowDataVO {
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
     * 流程启动人
     */
    private String operator;

    /**
     * 流程启动的时间
     */
    private String startTime;

    /**
     * 流程执行时发生的异常错误信息
     */
    private String errorMessage;

    /**
     * 构造函数
     */
    public FlowDataVO() {
    }

    public FlowDataVO(Map<String, Object> businessData, Map<String, Object> contextData, Map<String, Object> passData,
            String operator, String startTime, String errorMessage) {
        this.businessData = businessData;
        this.contextData = contextData;
        this.passData = passData;
        this.operator = operator;
        this.startTime = startTime;
        this.errorMessage = errorMessage;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getBusinessData() {
        return businessData;
    }

    public void setBusinessData(Map<String, Object> businessData) {
        this.businessData = businessData;
    }

    public Map<String, Object> getContextData() {
        return contextData;
    }

    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }

    public Map<String, Object> getPassData() {
        return passData;
    }

    public void setPassData(Map<String, Object> passData) {
        this.passData = passData;
    }
}
