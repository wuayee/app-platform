/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.entity.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * OMS返回对象
 *
 * @param <T> data数据类型
 * @author 李智超
 * @since 2024-12-12
 */
@Getter
@Setter
@Builder
public class ResultVo<T> implements Serializable {
    private static final long serialVersionUID = 8258009436167245885L;

    private String code;
    private String msg;
    private T data;

    /**
     * 构造函数
     *
     * @param errorCodeEnum 错误码
     * @param data 数据
     */
    public ResultVo(IBaseErrorCodeEnum errorCodeEnum, T data) {
        this.code = errorCodeEnum.getCode();
        this.msg = errorCodeEnum.getMsg();
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param errorCodeEnum 错误码
     */
    public ResultVo(IBaseErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum, null);
    }

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param msg 错误信息
     * @param data 数据
     */
    public ResultVo(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param msg 错误信息
     */
    public ResultVo(String code, String msg) {
        this(code, msg, null);
    }

    /**
     * 构造函数
     *
     * @param data 数据
     */
    public ResultVo(T data) {
        this(IBaseErrorCodeEnum.SUCCESS_CODE, IBaseErrorCodeEnum.SUCCESS_MSG, data);
    }

    /**
     * 构造函数
     */
    public ResultVo() {
        this(IBaseErrorCodeEnum.SUCCESS_CODE, IBaseErrorCodeEnum.SUCCESS_MSG);
    }

    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("code", this.code);
        result.put("msg", this.msg);
        result.put("data", JSON.toJSONString(this.data));
        return result.toJSONString();
    }
}

