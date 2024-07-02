/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.entity.MindJsonElement;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 根据小海知识检索接口响应转换为脑图json
 *
 * @author x00649642
 * @since 2023/12/27
 */
@Component
public class LLMJson2Mind implements FlowableService {
    private static final Logger log = Logger.get(LLMJson2Mind.class);
    private final MetaInstanceService metaInstanceService;

    public LLMJson2Mind(@Fit MetaInstanceService metaInstanceService) {
        this.metaInstanceService = metaInstanceService;
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMJson2Mind")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LLMJson2Mind businessData {}", businessData);

        String prompt = DataUtils.getPromptFromFlowContext(flowData);
        Validation.notBlank(prompt, "prompt cannot be null");

        List<FileDto> xiaoHaiAnswer = JsonUtils.parseArray(prompt, FileDto[].class);
        // 转换到脑图json
        Map<String, String> resultGroupedByFileTypes = xiaoHaiAnswer.stream()
                .map(fileDescription -> new MindJsonElement(fileDescription.getFileType(),
                        MindJsonElement.packToElementJson(fileDescription.getFileName(),
                                MindJsonElement.packToElementJson(fileDescription.getFileUrl(), ""))))
                // 按照fileType合并成map, 通过mergeFunction将children字段拼接
                .collect(Collectors.toMap(MindJsonElement::getName,
                        MindJsonElement::getChildren,
                        (child1, child2) -> String.join(",", child1, child2)));
        // 将fileType合并打包到"大模型检索结果"的children
        String resultJson = MindJsonElement.packToElementJson("大模型检索结果",
                resultGroupedByFileTypes.entrySet()
                        .stream()
                        .map(entry -> MindJsonElement.packToElementJson(entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(",")));

        // add result
        businessData.put(AippConst.INST_MIND_DATA_KEY, resultJson);

        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_MIND_DATA_KEY, resultJson).build();
        MetaInstanceUtils.persistInstance(
                metaInstanceService, info, businessData, DataUtils.getOpContext(businessData));
        return flowData;
    }

}
