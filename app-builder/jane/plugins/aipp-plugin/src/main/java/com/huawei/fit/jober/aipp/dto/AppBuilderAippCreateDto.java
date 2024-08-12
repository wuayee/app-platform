/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 启动Aipp实例信息
 *
 * @author 陈潇文
 * @since 2024-05-24
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAippCreateDto {
    /**
     * context 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     */
    private Map<String, Object> context;

    /**
     * appDto 信息
     */
    private AppBuilderAppDto appDto;
}
