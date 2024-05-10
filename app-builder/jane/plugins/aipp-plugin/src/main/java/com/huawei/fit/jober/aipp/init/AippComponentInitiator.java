/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.init;

import com.huawei.fit.jober.aipp.common.ResourceLoader;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Initialize;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * aipp流程和表单组件初始化
 *
 * @author l00611472
 * @since 2024-01-06
 */
@Component
public class AippComponentInitiator {
    /**
     * 流程/表单组件数据
     */
    public static final Map<String, String> COMPONENT_DATA = new HashMap<>();
    private static final Logger log = Logger.get(AippComponentInitiator.class);
    private static final String RESOURCE_PATH = "component";

    private static final String FLOW_PATH = "/flow.json";

    private static final String FORM_PATH = "/form.json";

    private final Plugin plugin;

    public AippComponentInitiator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Initialize
    private void loadComponentData() throws IOException {
        log.info("load aipp component data.");
        COMPONENT_DATA.put(AippConst.FLOW_COMPONENT_DATA_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FLOW_PATH));
        COMPONENT_DATA.put(AippConst.FORM_COMPONENT_DATA_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FORM_PATH));
    }
}
