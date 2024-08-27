/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.pattern;

/**
 * 表示具有量化能力的对象。
 *
 * @author 易文渊
 * @since 2024-08-08
 */
public interface Measurable {
    /**
     * 获取当前对象的量化分数。
     *
     * @return 表示当前对象的量化分数的 {@code double}。
     */
    double score();
}