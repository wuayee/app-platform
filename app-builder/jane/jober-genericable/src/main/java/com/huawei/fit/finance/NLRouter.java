/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.finance;

/**
 * NLRouter
 *
 * @author 易文渊
 * @since 2024-04-27
 */
public class NLRouter {
    private String result;
    private boolean matched;

    private String completeQuery;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getCompleteQuery() {
        return completeQuery;
    }

    public void setCompleteQuery(String completeQuery) {
        this.completeQuery = completeQuery;
    }
}