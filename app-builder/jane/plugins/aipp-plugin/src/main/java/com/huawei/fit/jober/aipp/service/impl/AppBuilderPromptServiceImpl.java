/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptDto;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.service.AppBuilderPromptService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为 {@link AppBuilderPromptService}提供实现
 *
 * @author 姚江
 * @since 2024-04-26
 */
@Component
public class AppBuilderPromptServiceImpl implements AppBuilderPromptService {
    private final AppBuilderAppFactory appFactory;

    public AppBuilderPromptServiceImpl(AppBuilderAppFactory appFactory) {
        this.appFactory = appFactory;
    }

    @Override
    public Rsp<AppBuilderPromptDto> queryInspirations(String appId, String categoryId, OperationContext context) {
        AppBuilderFormProperty inspiration = this.findInspirationProperty(appId);
        Object defaultValue = inspiration.getDefaultValue();
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(defaultValue));
        List<AppBuilderPromptCategoryDto> categories =
                jsonObject.getObject("category", new TypeReference<List<AppBuilderPromptCategoryDto>>() {});
        AppBuilderPromptCategoryDto flagCategory =
                Objects.equals(categoryId, "others") ? AppBuilderPromptCategoryDto.builder()
                .children(new ArrayList<>())
                .build() : findCategoryById(categories, categoryId);
        Validation.notNull(flagCategory, () -> new IllegalStateException("Category " + categoryId + " not found."));

        List<AppBuilderPromptDto.AppBuilderInspirationDto> inspirations = jsonObject.getObject("inspirations",
                new TypeReference<List<AppBuilderPromptDto.AppBuilderInspirationDto>>() {});
        List<AppBuilderPromptDto.AppBuilderInspirationDto> result = inspirations.stream().filter(dto -> {
            if (categoryId.equals("others")) {
                return Objects.isNull(dto.getCategory());
            }
            if (Objects.isNull(dto.getCategory())) {
                return categoryId.equals("root");
            }
            String[] category = dto.getCategory().split(":");
            // 如果目标节点是叶子节点，那么匹配id为后面部分否则为父节点id
            return categoryId.equals(category[this.ifLeaf(flagCategory) ? 1 : 0]);
        }).collect(Collectors.toList());

        return Rsp.ok(AppBuilderPromptDto.builder()
                .inspirations(result)
                .categories(flagCategory.getChildren())
                .build());
    }

    private AppBuilderPromptCategoryDto findCategoryById(List<AppBuilderPromptCategoryDto> categories, String id) {
        if (CollectionUtils.isEmpty(categories)) {
            return null;
        }
        for (AppBuilderPromptCategoryDto category : categories) {
            if (category.getId().equals(id)) {
                return category;
            }
            AppBuilderPromptCategoryDto match = findCategoryById(category.getChildren(), id);
            if (Objects.nonNull(match)) {
                return match;
            }
        }
        return null;
    }

    private boolean ifLeaf(AppBuilderPromptCategoryDto category) {
        return CollectionUtils.isEmpty(category.getChildren());
    }

    private AppBuilderFormProperty findInspirationProperty(String appId) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        List<AppBuilderFormProperty> formProperties = appBuilderApp.getConfig().getForm().getFormProperties();
        return formProperties.stream()
                .filter(fp -> fp.getName().equals("inspiration"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Don't find property 'inspiration'."));
    }

    @Override
    public Rsp<List<AppBuilderPromptCategoryDto>> listPromptCategories(String appId, OperationContext context) {
        AppBuilderFormProperty inspiration = this.findInspirationProperty(appId);
        Object defaultValue = inspiration.getDefaultValue();
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(defaultValue));
        List<AppBuilderPromptCategoryDto> categories =
                jsonObject.getObject("category", new TypeReference<List<AppBuilderPromptCategoryDto>>() {});
        return Rsp.ok(categories);
    }

    private AppBuilderPromptCategoryDto removeLeaf(AppBuilderPromptCategoryDto root) {
        // 如果没有子节点，那么本身为叶子节点，返回null表示删除本叶子节点
        if (CollectionUtils.isEmpty(root.getChildren())) {
            return null;
        }
        // 否则继续遍历子节点，把所有叶子节点删掉
        List<AppBuilderPromptCategoryDto> newChildren =
                root.getChildren().stream().map(this::removeLeaf).filter(Objects::nonNull).collect(Collectors.toList());
        root.setChildren(newChildren);
        return root;
    }
}
