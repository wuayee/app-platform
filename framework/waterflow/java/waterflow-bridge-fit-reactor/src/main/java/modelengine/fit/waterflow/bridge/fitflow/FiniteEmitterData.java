/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.bridge.fitflow;

/**
 * 有限流的data需要实现该接口，并提供以下方法
 *
 * @author 夏斐
 * @since 1.0
 */
public interface FiniteEmitterData {
    /**
     * 是否结束
     *
     * @return 是否是结束标识
     */
    boolean isEnd();

    /**
     * 是否错误
     *
     * @return 是否是错误标识
     */
    boolean isError();

    /**
     * 错误信息
     *
     * @return isError()为true的情况下，错误信息
     */
    String getErrorMessage();
}
