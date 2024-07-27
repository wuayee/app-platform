/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 基础领域对象，所有领域对象都应该继承此类
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDomain {
    private String createBy;
    private String updateBy;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean isDeleted;

    /**
     * 懒加载，获取一个对象
     *
     * @param target 待获取的目标
     * @param supplier 如果目标为null, 则执行supplier获取
     * @param consumer 获取后回调该方法, 用于设置属性
     * @param <X> 延迟加载的类型
     * @return 延迟加载的结果
     */
    protected static <X> X lazyGet(X target, Supplier<X> supplier, Consumer<X> consumer) {
        if (target != null) {
            return target;
        }
        X newTarget = supplier.get();
        consumer.accept(newTarget);
        return newTarget;
    }
}
