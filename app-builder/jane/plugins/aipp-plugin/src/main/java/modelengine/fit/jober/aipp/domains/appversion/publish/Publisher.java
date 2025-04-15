/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.appversion.publish;

import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.PublishContext;

/**
 * 发布接口.
 *
 * @author 张越
 * @since 2025-01-14
 */
public interface Publisher {
    /**
     * 发布.
     *
     * @param context 发布上下文信息.
     * @param appVersion 应用版本对象.
     */
    void publish(PublishContext context, AppVersion appVersion);
}
