/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.common.model.TextStringValue;
import com.huawei.fit.service.RegistryService;
import com.huawei.fit.service.entity.FitableMetaInstance;
import com.huawei.fit.service.entity.GenericableInfo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.GenericableMetadata;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Genericable相关Controller。
 *
 * @author 陈镕希
 * @since 2023-07-14
 */
@Component("JaneGenericableController")
@RequestMapping(value = AbstractController.URI_PREFIX + "/genericables", group = "Genericable管理接口")
public class GenericableController extends AbstractController {
    private final RegistryService registryService;

    /**
     * 构造函数
     *
     * @param authenticator 授权校验器
     * @param registryService 注册服务
     */
    public GenericableController(Authenticator authenticator, RegistryService registryService) {
        super(authenticator);
        this.registryService = registryService;
    }

    private static String getDisplayText(List<String> aliases) {
        String displayText;
        if (aliases.size() > 1) {
            displayText = aliases.stream().filter(alias -> !alias.contains("$Fit$")).collect(Collectors.joining(", "));
        } else {
            displayText = String.join(", ", aliases);
        }
        return displayText;
    }

    /**
     * 获取某个Genericable的实现列表信息。
     *
     * @param genericableId 需要查询的Genericable唯一标识的 {@link String}。
     * @return Genericable唯一标识对应Genericable实现列表的 {@link List}{@code <}{@link TextStringValue}{@code >}。
     */
    @GetMapping(path = "/{genericableId}", summary = "根据GenericableID获取对应的fitable实现列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<TextStringValue> getGenericableImplementInfo(@PathVariable("genericableId") String genericableId) {
        GenericableInfo genericableInfo = new GenericableInfo();
        genericableInfo.setGenericableId(genericableId);
        genericableInfo.setGenericableVersion(GenericableMetadata.DEFAULT_VERSION);
        List<FitableMetaInstance> fitableMetaInstances = registryService.queryFitableMetas(
                Collections.singletonList(genericableInfo));
        return fitableMetaInstances.stream().map(fitableMetaInstance -> {
            List<String> aliases = fitableMetaInstance.getMeta().getAliases();
            String displayText = getDisplayText(aliases);
            return TextStringValue.builder()
                    .text(displayText)
                    .value(fitableMetaInstance.getMeta().getFitable().getFitableId())
                    .build();
        }).collect(Collectors.toList());
    }
}
