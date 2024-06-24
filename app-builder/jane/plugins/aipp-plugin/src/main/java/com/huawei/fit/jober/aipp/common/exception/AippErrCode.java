/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common.exception;

import com.huawei.fit.jane.common.response.ErrorCode;

import lombok.AllArgsConstructor;

/**
 * HsipReturnCode
 *
 * @author y00612997
 * @since 2023/9/26
 */
@AllArgsConstructor
public enum AippErrCode implements ErrorCode {
    /**
     * 请求成功
     */
    OK(0, "success"),

    /** ------------ Generic Exception. From 90000000 to 90000999 --------------------- */

    /**
     * 入参不合法
     */
    INPUT_PARAM_IS_INVALID(90000000, "Invalid param: {0}."),

    /**
     * 资源不存在
     */
    NOT_FOUND(90000001, "Cannot find entity: {0}."),

    /**
     * 未知服务器内部错误
     */
    UNKNOWN(90000002, "Server internal error, please contact administrator."),

    /**
     * 禁止操作
     */
    FORBIDDEN(90000003, "Prohibited operation."),

    /** ------------ Configuration Exception. From 90001000 to 90001999 --------------------- */

    /**
     * 创建流程失败
     */
    CREATE_FLOW_FAILED(90001001, "Create flow config failed."),

    /**
     * 不允许终止实例
     */
    TERMINATE_INSTANCE_FORBIDDEN(90001002, "Only allow to terminate a running instance"),

    /**
     * 不允许删除实例
     */
    DELETE_INSTANCE_FORBIDDEN(90001003, "Not allow to delete a running instance"),

    /**
     * 发布流程失败
     */
    PUBLISH_FLOW_FAILED(90001004, "Publish flow failed, please check flow definition."),

    /**
     * 流程已存在
     */
    FLOW_ALREADY_EXIST(90001005, "Flow already exist, please check flow name and version."),

    /**
     * 发布到小海平台内部错误
     */
    XIAOHAI_APP_PUBLISH_INNER_ERROR(90001006, "Publish aipp to xiaohai inner error."),

    /**
     * 发布到小海平台http请求失败
     */
    XIAOHAI_APP_PUBLISH_HTTP_ERROR(90001007, "Publish aipp to xiaohai http error."),

    /**
     * 不允许删除已发布的应用或工具流
     */
    DELETE_AIPP_FORBIDDEN(90001008, "Not allow to delete an active aipp."),

    /**
     * 不允许更新已发布的应用或工具流
     */
    UPDATE_AIPP_FORBIDDEN(90001009, "Not allow to update an active aipp."),

    /**
     * 预览的临时版本已存在
     */
    PREVIEW_AIPP_FORBIDDEN(90001010, "Preview aipp version already exist."),

    /**
     * 调试失败
     */
    PREVIEW_AIPP_FAILED(90001011, "配置有误，请查看工作流编排是否正确。"),

    /**
     * 属性key重复
     */
    AIPP_PROPS_KEY_DUPLICATE(90001012, "Duplicate property key are not allow."),

    /**
     * 名称为空
     */
    AIPP_NAME_IS_EMPTY(90001013, "名称为空或者只包含空格。"),

    /**
     * 名称已存在
     */
    AIPP_NAME_IS_DUPLICATE(90001014, "名称已存在。"),

    /**
     * 实例历史记录不存在
     */
    AIPP_INSTANCE_LOG_IS_NULL(90001015, "Aipp instance log is null."),

    /**
     * 上传历史记录到小海平台http请求失败
     */
    XIAOHAI_UPLOAD_CHAT_HISTORY_HTTP_ERROR(90001016, "Upload chat history to xiaohai http error."),

    /**
     * 上传历史记录到小海平台内部错误
     */
    XIAOHAI_UPLOAD_CHAT_HISTORY_INNER_ERROR(90001017, "Upload chat history to xiaohai inner error."),

    /**
     * APP 长度超过最大值
     */
    AIPP_NAME_LENGTH_OUT_OF_BOUNDS(90001018, "The length of name is out of bounds."),

    /**
     * 通过小海平台分享对话失败
     */
    XIAOHAI_SHARED_CHAT_HTTP_ERROR(90001019, "Shared chat history to xiaohai http error."),

    /** ------------ aipp runtime Exception. From 90002000 to 90002999 --------------------- */
    /**
     * 上传文件失败
     */
    UPLOAD_FAILED(90002000, "Upload file failed."),

    /**
     * 上传文件失败
     */
    FILE_EXPIRED_OR_BROKEN(90002001, "File expired or broken."),

    /**
     * 解析文件内容失败
     */
    EXTRACT_FILE_FAILED(90002002, "Extract file failed."),

    /**
     * json解析失败
     */
    JSON_DECODE_FAILED(90002900, "json decode failed, reason: {0}."),

    /**
     * json编码失败
     */
    JSON_ENCODE_FAILED(90002901, "json encode failed, reason: {0}."),

    /**
     * 获取历史记录失败
     */
    GET_HISTORY_LOG_FAILED(90002902, "Get history log failed."),

    /**
     * 表单配置项类型不合法
     */
    FORM_PROPERTY_TYPE_IS_INVALID(90002903, "form property type is invalid."),

    /**
     * 灵感大全fitable执行失败
     */
    EXECUTE_INSPIRATION_FITABLE_FAILED(90002904, "Execute inspiration fitable failed."),

    /**
     * 解析历史记录配置失败
     */
    PARSE_MEMORY_CONFIG_FAILED(90002905, "Parse memory config failed."),

    /**
     * 模型节点模板解析失败
     */
    LLM_COMPONENT_TEMPLATE_RENDER_FAILED(90002906, "请检查提示词模板中的变量。"),

    /**
     * 创建调试aipp失败
     */
    CREATE_DEBUG_AIPP_FAILED(90002907, "调试失败，请检查流程配置是否正确: {0}"),

    /**
     * 不支持的数据格式
     */
    DATA_TYPE_IS_NOT_SUPPORTED(90002908, "Not supported data type to get. [type={0}]"),

    /**
     * 应用已经发布过
     */
    APP_HAS_PUBLISHED(90002909, "该应用已经成功发布过，请不要重复发布。"),

    /**
     * 错误码截止值
     */
    ERROR_CODE_BUTT(99999999, "Error code butt.");

    private final int code;
    private final String msg;

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    @Override
    public int getErrorCode() {
        return code;
    }

    /**
     * 获取错误提示信息
     *
     * @return 错误信息
     */
    @Override
    public String getMessage() {
        return msg;
    }
}