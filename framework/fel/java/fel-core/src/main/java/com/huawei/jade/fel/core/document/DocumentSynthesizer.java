/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.document;

import com.huawei.jade.fel.core.pattern.Synthesizer;

import java.util.List;

/**
 * 表示文档检索结果的合成器。
 *
 * @author 易文渊
 * @since 2024-08-12
 */
public interface DocumentSynthesizer extends Synthesizer<List<? extends Document>> {}