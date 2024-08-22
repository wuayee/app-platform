/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.biz.operation;

import static com.huawei.fit.jober.common.ErrorCodes.FAILED_TO_GET_MANUAL_OPERATOR;

import com.huawei.fit.jober.common.exceptions.JobberException;
import modelengine.fit.waterflow.biz.operation.operator.Operator;
import modelengine.fit.waterflow.biz.operation.operator.SmartFormOperator;
import modelengine.fit.waterflow.biz.operation.operator.TaskCenterOperator;
import modelengine.fitframework.broker.client.BrokerClient;

/**
 * 任务处理器工厂类
 *
 * @author 晏钰坤
 * @since 2023/9/15
 */
public class OperatorFactory {
    private static final String TASK_CENTER = "TASK_CENTER";

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
            case TASK_CENTER:
                return new TaskCenterOperator(brokerClient);
            case SMART_FORM:
                return new SmartFormOperator(brokerClient);
            default:
                throw new JobberException(FAILED_TO_GET_MANUAL_OPERATOR, type);
        }
    }
}
