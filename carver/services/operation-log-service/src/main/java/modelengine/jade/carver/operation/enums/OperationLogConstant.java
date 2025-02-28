/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.operation.enums;

/**
 * OperationLogConstant枚举类。
 *
 * @author 方誉州
 * @since 2024-08-02
 */
public interface OperationLogConstant {
    /**
     * 操作执行结果的键。
     */
    String SYS_OP_RESULT_KEY = "sys_operation_result_key";

    /**
     * 系统 Locale 语言环境的键。
     */
    String SYS_OP_LANGUAGE_KEY = "sys_op_language_key";

    /**
     * 操作人员的键。
     */
    String SYS_OP_OPERATOR_KEY = "sys_op_operator_key";

    /**
     * 操作人员ip地址的键。
     */
    String SYS_OP_IPADDR_KEY = "sys_op_ipaddr_key";

    /**
     * 操作执行结果成功。
     */
    String SYS_OP_SUCCEED = "SUCCESS";

    /**
     * 操作执行结果失败。
     */
    String SYS_OP_FAILED = "FAILED";

    /**
     * 系统属性 span event 的名称。
     */
    String SYSTEM_ATTRIBUTE_EVENT_NAME = "system_attribute";
}
