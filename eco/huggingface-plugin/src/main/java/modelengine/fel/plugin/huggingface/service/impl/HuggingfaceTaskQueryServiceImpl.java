/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.service.impl;

import modelengine.jade.common.globalization.LocaleService;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceTaskInfo;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceTaskEntity;
import modelengine.fel.plugin.huggingface.mapper.HuggingfaceTaskMapper;
import modelengine.fel.plugin.huggingface.service.HuggingfaceTaskQueryService;
import modelengine.fitframework.annotation.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link HuggingfaceTaskQueryService} 的默认实现。
 *
 * @author 邱晓霞
 * @since 2024-09-09
 */
@Component
public class HuggingfaceTaskQueryServiceImpl implements HuggingfaceTaskQueryService {
    private final HuggingfaceTaskMapper taskMapper;
    private final LocaleService localeService;

    /**
     * 表示可用 Huggingface 任务数据服务实现的构建器。
     *
     * @param taskMapper 表示 Huggingface 任务数据持久层接口的 {@link HuggingfaceTaskMapper}。
     * @param localeService 表示 Huggingface 可用任务的国际化信息管理工具的 {@link LocaleService}。
     */
    public HuggingfaceTaskQueryServiceImpl(HuggingfaceTaskMapper taskMapper, LocaleService localeService) {
        this.taskMapper = taskMapper;
        this.localeService = localeService;
    }

    @Override
    public List<HuggingfaceTaskInfo> listAvailableTasks() {
        List<HuggingfaceTaskEntity> taskEntities = this.taskMapper.listAvailableTasks();
        List<HuggingfaceTaskInfo> huggingfaceTaskInfos = new ArrayList<>();
        for (HuggingfaceTaskEntity taskEntity : taskEntities) {
            String taskName = this.localeService.localize(taskEntity.getTaskNameCode());
            String taskDescription = this.localeService.localize(taskEntity.getTaskDescriptionCode());
            huggingfaceTaskInfos.add(new HuggingfaceTaskInfo(taskEntity.getTaskId(), taskName, taskDescription));
        }
        return huggingfaceTaskInfos;
    }
}
