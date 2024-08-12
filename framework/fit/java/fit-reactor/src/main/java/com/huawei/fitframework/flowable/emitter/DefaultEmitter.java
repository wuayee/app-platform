/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.emitter;

import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.util.LockUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link Emitter} 的可观测的实现。
 *
 * @param <T> 表示发送数据的类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-13
 */
public class DefaultEmitter<T> implements Emitter<T> {
    private final List<Observer<T>> observers = new ArrayList<>();
    private final Object lock = LockUtils.newSynchronizedLock();

    @Override
    public void emit(T data) {
        List<Observer<T>> observerList = this.getObservers();
        for (Observer<T> observer : observerList) {
            observer.onEmittedData(data);
        }
    }

    @Override
    public void complete() {
        List<Observer<T>> observerList = this.getObservers();
        for (Observer<T> observer : observerList) {
            observer.onCompleted();
        }
    }

    @Override
    public void fail(Exception cause) {
        List<Observer<T>> observerList = this.getObservers();
        for (Observer<T> observer : observerList) {
            observer.onFailed(cause);
        }
    }

    private List<Observer<T>> getObservers() {
        List<Observer<T>> copied;
        synchronized (this.lock) {
            copied = new ArrayList<>(this.observers);
        }
        return copied;
    }

    @Override
    public void observe(Observer<T> observer) {
        if (observer != null) {
            synchronized (this.lock) {
                this.observers.add(observer);
            }
        }
    }
}
