/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc;

import com.huawei.fitframework.annotation.Order;

import java.util.Comparator;
import java.util.Optional;

/**
 * {@link BeanFactory} 的 {@link Order} 排序器。
 *
 * @author 邬涨财
 * @since 2023-06-26
 */
public class BeanFactoryOrderComparator implements Comparator<BeanFactory> {
    /**
     * 表示 {@link BeanFactoryOrderComparator} 类的唯一实例。
     */
    public static final BeanFactoryOrderComparator INSTANCE = new BeanFactoryOrderComparator();

    private BeanFactoryOrderComparator() {}

    @Override
    public int compare(BeanFactory beanFactory1, BeanFactory beanFactory2) {
        int order1 = this.getOrderValue(beanFactory1);
        int order2 = this.getOrderValue(beanFactory2);
        return Integer.compare(order1, order2);
    }

    private int getOrderValue(BeanFactory beanFactory) {
        return Optional.ofNullable(beanFactory.metadata().annotations().getAnnotation(Order.class))
                .map(Order::value)
                .orElse(Order.MEDIUM);
    }
}
