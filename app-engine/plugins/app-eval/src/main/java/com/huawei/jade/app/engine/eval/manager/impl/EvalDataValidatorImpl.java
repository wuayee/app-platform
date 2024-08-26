/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager.impl;

import static com.huawei.jade.app.engine.eval.code.AppEvalRetCode.EVAL_DATA_INVALID_ERROR;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import com.huawei.jade.app.engine.eval.exception.AppEvalException;
import com.huawei.jade.app.engine.eval.manager.EvalDataValidator;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.schema.SchemaValidator;
import com.huawei.jade.app.engine.schema.exception.SchemaValidateException;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataValidator} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
@Component
public class EvalDataValidatorImpl implements EvalDataValidator {
    private static final Logger log = Logger.get(EvalDataValidatorImpl.class);

    private final EvalDatasetMapper datasetMapper;

    private final SchemaValidator schemaValidator;

    public EvalDataValidatorImpl(EvalDatasetMapper datasetMapper, SchemaValidator schemaValidator) {
        this.datasetMapper = datasetMapper;
        this.schemaValidator = schemaValidator;
    }

    @Override
    public void verify(Long datasetId, List<String> contents) throws AppEvalException {
        String schema = this.datasetMapper.getSchema(datasetId);
        for (String content : contents) {
            try {
                this.schemaValidator.validate(schema, contents);
            } catch (SchemaValidateException e) {
                log.error("Verify content error code: {}, error message: {}", e.getCode(), e.getMessage());
                throw new AppEvalException(EVAL_DATA_INVALID_ERROR, e, content, schema, e.getMessage());
            }
        }
    }

    @Override
    public void verify(Long datasetId, String content) throws AppEvalException {
        this.verify(datasetId, Collections.singletonList(content));
    }
}