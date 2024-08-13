/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.fitframework.broker.client.filter.route;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.broker.client.Router;

/**
 * 使用规则路由的过滤器。
 * <p><b>使用规则路由时，请直接使用 {@link Fit} 注入该过滤器。</b></p>
 *
 * @author 季聿阶
 * @since 2021-06-17
 */
@Genericable
public interface RuleFilter extends Router.Filter {}
