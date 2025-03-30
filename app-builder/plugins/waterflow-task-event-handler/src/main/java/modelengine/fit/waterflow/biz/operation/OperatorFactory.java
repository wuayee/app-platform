/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.biz.operation;

import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.waterflow.biz.operation.operator.Operator;
import modelengine.fit.waterflow.biz.operation.operator.SmartFormOperator;
import modelengine.fitframework.broker.client.BrokerClient;

import static modelengine.fit.jober.common.ErrorCodes.FAILED_TO_GET_MANUAL_OPERATOR;

/**
 * 任务处理器工厂类
 *
 * @author 晏钰坤
 * @since 2023/9/15
 */
public class OperatorFactory {
    private static final String SMART_FORM = "SMART_FORM";

    /**
     * 通过任务类型获取不同的Operator
     *
     * @param type 任务类型
     * @param brokerClient 调用Fitable服务的Client
     * @return Operator
     */
    public static Operator getOperator(String type, BrokerClient brokerClient) {
        switch (type) {
            case SMART_FORM:
                return new SmartFormOperator(brokerClient);
            default:
                throw new JobberException(FAILED_TO_GET_MANUAL_OPERATOR, type);
        }
    }
}
