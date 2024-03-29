/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.broker;

import com.huawei.fitframework.util.StringUtils;

import java.util.Optional;

/**
 * 表示泛服务的通信抽象方式。
 *
 * @author 王成 w00863339
 * @since 2023-11-17
 */
public enum CommunicationType {
    /**
     * 表示泛化服务以同步方式通信。
     */
    SYNC("SYNC"),

    /**
     * 表示泛化服务以异步方式通信。
     */
    ASYNC("ASYNC");

    /**
     * 表示默认的服务类型。
     */
    public static final CommunicationType DEFAULT = SYNC;

    private final String code;

    CommunicationType(String code) {
        this.code = code;
    }

    /**
     * 获取泛化服务通信类型的编号。
     *
     * @return 表示类型编号的 {@link String}。
     */
    public String code() {
        return this.code;
    }

    /**
     * 获取指定编号的泛化服务通信类型。
     *
     * @param code 表示通信类型编号的 {@link String}。
     * @return 若存在该编号的通信类型，则为该类型的
     * {@link Optional}{@code <}{@link CommunicationType}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    public static Optional<CommunicationType> fromCode(String code) {
        for (CommunicationType value : CommunicationType.values()) {
            if (StringUtils.equalsIgnoreCase(value.code(), code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
