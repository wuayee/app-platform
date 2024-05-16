package com.huawei.jade.app.engine.knowledge.service;

import com.huawei.fitframework.annotation.Genericable;

public interface KnowledgeBaseService {
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.get")
    String get();
}
