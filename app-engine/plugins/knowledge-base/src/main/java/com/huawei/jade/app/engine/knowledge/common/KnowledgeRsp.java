/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.common;

import modelengine.fitframework.annotation.Property;

import lombok.Data;

/**
 * 知识库异常场景统一返回结构体。
 *
 * @since 2024-06-18
 */
@Data
public class KnowledgeRsp {
    @Property(description = "状态码", example = "0")
    private int code;
    @Property(description = "状态信息", example = "success")
    private String msg;

    /**
     * 静态构造函数
     *
     * @param code 返回数据错误码
     * @param msg 返回错误信息
     * @return KnowledgeRsp构造的对象
     */
    public static KnowledgeRsp err(int code, String msg) {
        KnowledgeRsp rsp = new KnowledgeRsp();
        rsp.setCode(code);
        rsp.setMsg(msg);
        return rsp;
    }
}
