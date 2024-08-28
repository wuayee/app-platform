/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package modelengine.fitframework.broker.client.filter.route;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.broker.client.Router;

/**
 * 使用规则路由的过滤器。
 * <p><b>使用规则路由时，请直接使用 {@link Fit} 注入该过滤器。</b></p>
 *
 * @author 季聿阶
 * @since 2021-06-17
 */
@Genericable
public interface RuleFilter extends Router.Filter {}
