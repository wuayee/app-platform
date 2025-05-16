/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.service.impl;

import modelengine.jade.common.vo.PageVo;

import modelengine.fel.plugin.huggingface.convertor.HuggingfaceModelConvertor;
import modelengine.fel.plugin.huggingface.dto.HuggingfaceModelQueryParam;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;
import modelengine.fel.plugin.huggingface.mapper.HuggingfaceModelMapper;
import modelengine.fel.plugin.huggingface.mapper.HuggingfaceTaskMapper;
import modelengine.fel.plugin.huggingface.po.HuggingfaceModelPo;
import modelengine.fel.plugin.huggingface.service.HuggingfaceModelQueryService;
import modelengine.fel.plugin.huggingface.service.HuggingfaceModelService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;

import java.util.List;

/**
 * 表示 {@link HuggingfaceModelQueryService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
@Component
public class HuggingfaceModelServiceImpl implements HuggingfaceModelService, HuggingfaceModelQueryService {
    private final HuggingfaceModelMapper modelMapper;
    private final HuggingfaceTaskMapper taskMapper;

    public HuggingfaceModelServiceImpl(HuggingfaceModelMapper modelMapper, HuggingfaceTaskMapper taskMapper) {
        this.modelMapper = modelMapper;
        this.taskMapper = taskMapper;
    }

    @Override
    @Transactional
    public void insert(HuggingfaceModelEntity entity) {
        HuggingfaceModelPo po = HuggingfaceModelConvertor.INSTANCE.entityToPo(entity);
        this.modelMapper.insert(po);
        this.taskMapper.increaseModelCount(po.getTaskId());
    }

    @Override
    public PageVo<HuggingfaceModelEntity> listModelInfoQuery(HuggingfaceModelQueryParam modelQueryParam) {
        List<HuggingfaceModelEntity> modelEntityList = this.modelMapper.listModelPartialInfo(modelQueryParam);

        int modelCount = this.modelMapper.countModel(modelQueryParam);
        return PageVo.of(modelCount, modelEntityList);
    }
}