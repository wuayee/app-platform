/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.broker.server;

/**
 * 表示 FIT 请求过滤器的调用链。
 *
 * @author 季聿阶
 * @since 2024-08-21
 */
public interface GenericableServerFilterChain {
    /**
     * 继续执行下一个过滤器.
     *
     * @param args 表示调用参数的 {@link Object}{@code []}。
     * @throws DoGenericableServerFilterException 当执行过程中发生异常时。
     */
    void doFilter(Object[] args) throws DoGenericableServerFilterException;
}