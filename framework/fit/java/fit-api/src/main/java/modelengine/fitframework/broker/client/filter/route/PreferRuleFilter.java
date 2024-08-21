/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package modelengine.fitframework.broker.client.filter.route;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.broker.client.Router;

/**
 * 优先使用规则路由的过滤器。
 * <p><b>优先使用规则路由时，请直接使用 {@link Fit} 注入该过滤器。</b></p>
 * <p>该过滤器的行为如下：
 * <ol>
 *     <li>如果存在规则，则使用规则路由，规则路由失败则报错。</li>
 *     <li>如果不存在规则，则使用默认路由，默认路由不存在则报错。</li>
 * </ol>
 * </p>
 *
 * @author 季聿阶
 * @since 2021-08-16
 */
@Genericable
public interface PreferRuleFilter extends Router.Filter {}
