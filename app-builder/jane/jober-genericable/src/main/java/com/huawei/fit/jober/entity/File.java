/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.entity;

/**
 * File
 *
 * @author lwx1301876
 * @since 2024/01/10
 */
public class File {
    private String name;

    private byte[] content;

    private String creator;

    /**
     * File 无参构造函数
     */
    public File() {

    }

    public File(String name, byte[] content, String creator) {
        this.name = name;
        this.content = content;
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
