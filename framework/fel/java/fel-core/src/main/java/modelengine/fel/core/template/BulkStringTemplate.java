/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.template;

import java.util.List;
import java.util.Map;

/**
 * 批量字符串模板接口定义。
 *
 * @author 何嘉斌
 * @since 2024-05-13
 */
public interface BulkStringTemplate extends GenericTemplate<List<Map<String, String>>, String> {}