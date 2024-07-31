/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.finance;

/**
 * NlRouter
 *
 * @author 易文渊
 * @since 2024-04-27
 */
public class NlRouter {
    private String result;
    private boolean isMatched;

    private String completeQuery;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean isMatched) {
        this.isMatched = isMatched;
    }

    public String getCompleteQuery() {
        return completeQuery;
    }

    public void setCompleteQuery(String completeQuery) {
        this.completeQuery = completeQuery;
    }
}