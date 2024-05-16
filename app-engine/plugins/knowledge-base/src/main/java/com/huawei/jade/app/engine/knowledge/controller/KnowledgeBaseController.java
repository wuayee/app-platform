package com.huawei.jade.app.engine.knowledge.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.knowledge.service.KnowledgeBaseService;

@Component
public class KnowledgeBaseController {
    @Fit
    KnowledgeBaseService knowledgeBaseService;

    @GetMapping(path = "/kb")
    public String test() {
        return "Knowledge Base OK";
    }

    @GetMapping(path = "/kbtest")
    public String test1() {
        return knowledgeBaseService.get();
    }
}
