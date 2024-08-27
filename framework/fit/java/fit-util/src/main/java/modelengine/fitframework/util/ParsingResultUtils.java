/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package modelengine.fitframework.util;

import modelengine.fitframework.util.support.DefaultParsingResult;

/**
 * 为 {@link ParsingResult} 提供工具方法。
 *
 * @author 梁济时
 * @since 1.0
 */
class ParsingResultUtils {
    /** 表示失败的转换结果。 */
    public static final ParsingResult<?> FAILED = new DefaultParsingResult<>(false, null);

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ParsingResultUtils() {}
}
