/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.OnInstancesCategoryChanged;
import com.huawei.fit.jober.entity.InstanceCategoryChanged;
import com.huawei.fit.jober.taskcenter.domain.CategoryEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskCategoryTriggerEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.CategoryChangedNotifier;
import com.huawei.fit.jober.taskcenter.service.CategoryService;

import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为 {@link CategoryChangedNotifier} 提供默认实现。
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
public class DefaultCategoryChangedNotifier extends AbstractNotifier implements CategoryChangedNotifier {
    private static final String GENERICABLE_ID = "e504e51720c242ab8edf5d0ccf97f5cc";

    private static final Logger log = Logger.get(DefaultCategoryChangedNotifier.class);

    private final CategoryService categoryService;

    private final Map<String, Map<Object, String>> categories;

    private final List<InstanceCategoryChanged> changes;

    /**
     * 构造函数
     *
     * @param broker 调度器
     * @param task 任务
     * @param categoryService 为任务实例的类目提供管理
     */
    public DefaultCategoryChangedNotifier(BrokerClient broker, TaskEntity task, CategoryService categoryService) {
        super(broker, task);
        this.categoryService = categoryService;
        this.categories = new HashMap<>();
        this.changes = new LinkedList<>();
        for (TaskProperty property : this.task().getProperties()) {
            List<PropertyCategory> propertyCategories = nullIf(property.categories(), Collections.emptyList());
            Map<Object, String> valueCategoryMappings = new HashMap<>();
            for (PropertyCategory propertyCategory : propertyCategories) {
                ParsingResult<Object> parsingResult = property.dataType().parse(propertyCategory.getValue());
                if (!parsingResult.isParsed()) {
                    throw new IllegalStateException(StringUtils.format(
                            "Unknown value of property '{0}': {1}", property.name(), propertyCategory.getValue()));
                }
                Object propertyValue = parsingResult.getResult();
                String categoryName = propertyCategory.getCategory();
                valueCategoryMappings.put(propertyValue, categoryName);
            }
            this.categories.put(property.name(), valueCategoryMappings);
        }
    }

    @Override
    public CategoryChangedNotifier notice(TaskInstance instance, Collection<String> olds, Collection<String> news,
            OperationContext context) {
        Set<String> categoryNames = new HashSet<>(olds.size() + news.size());
        categoryNames.addAll(olds);
        categoryNames.addAll(news);
        Map<String, CategoryEntity> categoriesMap = this.categoryService.listByNames(categoryNames)
                .stream()
                .collect(Collectors.toMap(CategoryEntity::getName, Function.identity()));
        Map<String, Set<String>> oldCategories = group(olds, categoriesMap);
        Map<String, Set<String>> newCategories = group(news, categoriesMap);
        for (Map.Entry<String, Set<String>> entry : newCategories.entrySet()) {
            Set<String> newValues = entry.getValue();
            Set<String> oldValues = oldCategories.get(entry.getKey());
            Set<String> addedCategories = CollectionUtils.difference(newValues, oldValues);
            for (String addedCategory : addedCategories) {
                InstanceCategoryChanged changed = new InstanceCategoryChanged();
                this.fillInstanceInfo(changed, instance, context);
                changed.setNewCategory(addedCategory);
                this.changes.add(changed);
            }
        }
        return this;
    }

    private static Map<String, Set<String>> group(Collection<String> names, Map<String, CategoryEntity> categories) {
        return names.stream().map(categories::get).filter(Objects::nonNull).collect(Collectors.groupingBy(
                CategoryEntity::getGroup, Collectors.mapping(CategoryEntity::getName, Collectors.toSet())));
    }

    @Override
    public void run() {
        List<TaskCategoryTriggerEntity> triggers = nullIf(this.task().getCategoryTriggers(), Collections.emptyList());
        Map<String, List<InstanceCategoryChanged>> grouped = this.changes.stream()
                .collect(Collectors.groupingBy(InstanceCategoryChanged::getNewCategory));
        Map<String, List<InstanceCategoryChanged>> targets = new HashMap<>();
        for (TaskCategoryTriggerEntity trigger : triggers) {
            List<InstanceCategoryChanged> categoryChanged = grouped.get(trigger.getCategory());
            if (CollectionUtils.isEmpty(categoryChanged)) {
                continue;
            }
            List<String> fitableIds = nullIf(trigger.getFitableIds(), Collections.emptyList());
            for (String fitableId : fitableIds) {
                targets.computeIfAbsent(fitableId, key -> new LinkedList<>()).addAll(categoryChanged);
            }
        }
        for (Map.Entry<String, List<InstanceCategoryChanged>> entry : targets.entrySet()) {
            String fitableId = entry.getKey();
            List<InstanceCategoryChanged> messages = entry.getValue();
            try {
                this.broker()
                        .getRouter(OnInstancesCategoryChanged.class, GENERICABLE_ID)
                        .route(new FitableIdFilter(fitableId))
                        .invoke(messages);
            } catch (FitException t) {
                log.error("Failed to notify fitable that category of instances has been changed. [fitableId={}]",
                        fitableId, t);
                log.error(t.getClass().getName(), t);
            }
        }
    }
}
