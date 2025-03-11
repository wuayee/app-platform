/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.jade.app.engine.eval.service.impl;

import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.app.engine.eval.mapper.EvalDataMapper;
import modelengine.jade.app.engine.eval.service.EvalListDataService;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import java.util.List;

/**
 * 表示 {@link EvalListDataService} 的默认实现。
 *
 * @author 兰宇晨
 * @since 2024-8-26
 */
@Component
public class EvalListDataServiceImpl implements EvalListDataService {
    private final EvalDataMapper dataMapper;

    public EvalListDataServiceImpl(EvalDataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    @Fitable("default")
    @Override
    public PageVo<EvalDataEntity> listEvalData(EvalDataQueryParam queryParam) {
        List<EvalDataEntity> evalData = this.dataMapper.listEvalData(queryParam);
        int evalDataCount = this.dataMapper.countEvalData(queryParam);
        return PageVo.of(evalDataCount, evalData);
    }
}
