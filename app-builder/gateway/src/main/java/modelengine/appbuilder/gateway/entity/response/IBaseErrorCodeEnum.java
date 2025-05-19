/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.entity.response;

/**
 * 基础的错误码表值
 *
 * @author 李智超
 * @since 2024-03-26
 */
public interface IBaseErrorCodeEnum {
    /**
     * 成功 code
     */
    String SUCCESS_CODE = "0";

    /**
     * 成功 message
     */
    String SUCCESS_MSG = "Success";

    /**
     * 获取msg
     *
     * @return msg
     */
    default String getMsg() {
        return "Success";
    }

    /**
     * 获取code
     *
     * @return code
     */
    default String getCode() {
        return "0";
    }

    /**
     * 获取http状态值
     *
     * @return http状态值
     */
    default int getHttpStatus() {
        return 200;
    }
}