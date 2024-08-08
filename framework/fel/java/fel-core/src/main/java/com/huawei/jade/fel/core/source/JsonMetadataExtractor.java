/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.source;

import java.util.Map;
import java.util.function.Function;

/**
 * 表示 json 文件元数据萃取器接口。
 *
 * @author 易文渊
 * @since 2024-08-10
 */
public interface JsonMetadataExtractor extends Function<Map<String, Object>, Map<String, Object>> {}