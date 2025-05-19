/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.entity;

import java.util.Map;

/**
 * 流程运行错误信息结构
 *
 * @author 杨祥宇
 * @since 2024/9/12
 */
public class FlowErrorInfo {
    /**
     * 错误码
     */
    private Integer errorCode;

    /**
     * 错误详细信息说明
     */
    private String errorMessage;

    /**
     * 错误信息对应参数
     */
    private String[] args;

    /**
     * 调用出错的fitable实现
     * 后续去除
     *
     */
    private String fitableId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 错误的其他信息
     * fitableId:调用的插件
     * toolId:工具Id
     */
    private Map<String, String> properties;

    public FlowErrorInfo(Integer errorCode, String errorMessage, String[] args, String fitableId, String nodeName,
                         Map<String, String> properties) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.args = args;
        this.fitableId = fitableId;
        this.nodeName = nodeName;
        this.properties = properties;
    }

    public FlowErrorInfo() {

    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getFitableId() {
        return fitableId;
    }

    public void setFitableId(String fitableId) {
        this.fitableId = fitableId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
