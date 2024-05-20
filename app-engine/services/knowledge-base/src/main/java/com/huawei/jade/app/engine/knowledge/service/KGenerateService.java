/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.knowledge.dto.KGenerateConfigDto;

/**
 * KGenerateService 知识生成
 *
 * @author YangPeng
 * @since 2024-05-20 11:10
 */
public interface KGenerateService {
    /**
     * 导入知识
     *
     * @param configDto 配置信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KGenerateService.importKnowledge")
    void importKnowledge(KGenerateConfigDto configDto);
}
