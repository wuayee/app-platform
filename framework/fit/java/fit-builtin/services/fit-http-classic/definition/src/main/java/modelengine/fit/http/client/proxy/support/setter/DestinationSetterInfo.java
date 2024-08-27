/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.http.client.proxy.support.setter;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示数据设置器的相关信息。
 *
 * @author 王攀博
 * @since 2024-06-12
 */
public class DestinationSetterInfo {
    private final DestinationSetter destinationSetter;
    private final String sourcePath;

    public DestinationSetterInfo(DestinationSetter destinationSetter, String sourcePath) {
        this.destinationSetter = notNull(destinationSetter, "The destination setter cannot be null.");
        this.sourcePath = ObjectUtils.nullIf(sourcePath, StringUtils.EMPTY);
    }

    /**
     * 获取数据设置器。
     *
     * @return 表示获取到的数据设置器的 {@link DestinationSetter}。
     */
    public DestinationSetter destinationSetter() {
        return this.destinationSetter;
    }

    /**
     * 获取在源数据中的路径。
     *
     * @return 表示获取在源数据中的路径的 {@link String}。
     */
    public String sourcePath() {
        return this.sourcePath;
    }
}