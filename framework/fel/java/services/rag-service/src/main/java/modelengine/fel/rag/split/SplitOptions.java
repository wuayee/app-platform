/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.split;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Token切分器服务相关参数。
 *
 * @since 2024-06-03
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplitOptions {
    private int tokenSize;
    private int overlap;
}
