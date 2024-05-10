/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.domain;

import java.time.LocalDateTime;

/**
 * 为 {@link DomainObject} 的实现提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-12
 */
public abstract class AbstractDomainObject implements DomainObject {
    private final String id;

    private final String creator;

    private final LocalDateTime creationTime;

    private final String lastModifier;

    private final LocalDateTime lastModificationTime;

    public AbstractDomainObject(String id, String creator, LocalDateTime creationTime, String lastModifier,
            LocalDateTime lastModificationTime) {
        this.id = id;
        this.creator = creator;
        this.creationTime = creationTime;
        this.lastModifier = lastModifier;
        this.lastModificationTime = lastModificationTime;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String creator() {
        return this.creator;
    }

    @Override
    public LocalDateTime creationTime() {
        return this.creationTime;
    }

    @Override
    public String lastModifier() {
        return this.lastModifier;
    }

    @Override
    public LocalDateTime lastModificationTime() {
        return this.lastModificationTime;
    }
}
