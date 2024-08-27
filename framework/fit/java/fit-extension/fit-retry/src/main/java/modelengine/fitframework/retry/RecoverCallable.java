/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.retry;

/**
 * 表示恢复的可调用方法。
 *
 * @author 邬涨财
 * @since 2023-02-23
 */
@FunctionalInterface
public interface RecoverCallable<T> {
    /**
     * 执行恢复的回调方法。
     *
     * @param cause 表示恢复发生的异常原因的 {@link Throwable}。
     * @return 表示恢复回调方法执行后的返回值的 {@link T}。
     * @throws Exception 表示执行过程中出现异常的 {@link Exception}。
     */
    T call(Throwable cause) throws Exception;
}
