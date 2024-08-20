/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.init;

import com.huawei.fit.jober.aipp.common.ResourceLoader;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Initialize;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.jade.common.ui.globalization.LocaleUiWord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * aipp流程和表单组件初始化
 *
 * @author 刘信宏
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
    private static final String FLOW_ZH_PATH = "/flow_zh.json";
    private static final String FLOW_EN_PATH = "/flow_en.json";
    private static final String FORM_ZH_PATH = "/form_zh.json";
    private static final String FORM_EN_PATH = "/form_en.json";
    private static final String BASIC_NODE_ZH_PATH = "/basic_node_zh.json";
    private static final String BASIC_NODE_EN_PATH = "/basic_node_en.json";

    private final Plugin plugin;

    public AippComponentInitiator(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 根据语言获取国际化对象。
     *
     * @param zhKey 表示中文键值的 {@link String}。
     * @param enKey 表示英文键值的 {@link String}。
     * @param targetClass 表示类的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示泛型的 {@code <}{@link T}{@code >}。
     * @return 表示泛型实例的 {@link T}。
     */
    public static <T> T getLocaleObject(String zhKey, String enKey, Class<T> targetClass) {
        return JsonUtils.parseObject(LocaleUiWord.getLocale().getLanguage().equals(Locale.ENGLISH.getLanguage())
                ? COMPONENT_DATA.get(zhKey)
                : COMPONENT_DATA.get(enKey), targetClass);
    }

    @Initialize
    private void loadComponentData() throws IOException {
        log.info("load aipp component data.");
        COMPONENT_DATA.put(AippConst.FLOW_COMPONENT_DATA_ZH_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FLOW_ZH_PATH));
        COMPONENT_DATA.put(AippConst.FORM_COMPONENT_DATA_ZH_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FORM_ZH_PATH));
        COMPONENT_DATA.put(AippConst.BASIC_NODE_COMPONENT_DATA_ZH_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + BASIC_NODE_ZH_PATH));
        COMPONENT_DATA.put(AippConst.FLOW_COMPONENT_DATA_EN_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FLOW_EN_PATH));
        COMPONENT_DATA.put(AippConst.FORM_COMPONENT_DATA_EN_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FORM_EN_PATH));
        COMPONENT_DATA.put(AippConst.BASIC_NODE_COMPONENT_DATA_EN_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + BASIC_NODE_EN_PATH));
    }
}
