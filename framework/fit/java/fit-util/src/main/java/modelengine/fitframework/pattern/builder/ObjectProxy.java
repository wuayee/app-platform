/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.pattern.builder;

import java.util.Map;

/**
 * 需要构建的对象的统一接口，用于输出对象内的内容。
 *
 * @author 季聿阶
 * @since 2022-06-23
 */
public interface ObjectProxy {
    /**
     * 获取对象内的属性内容。
     *
     * @return 表示对象内的属性内容的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> $toMap();
}
