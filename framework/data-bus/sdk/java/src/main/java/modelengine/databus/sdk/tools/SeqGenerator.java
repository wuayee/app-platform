/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.tools;

/**
 * DataBus 消息发号类。当前类线程安全，发号从1开始，每次递增1，最大号与 {@code Integer.MAX_VALUE} 相等后，会被重置为1。
 *
 * @author 王成
 * @since 2024/06/20
 */
public class SeqGenerator {
    private static final SeqGenerator INSTANCE = new SeqGenerator();

    private long currentNumber = 1L;

    private SeqGenerator() {}

    /**
     * 获取 {@link SeqGenerator} 的单例对象。
     *
     * @return {@link SeqGenerator}。
     */
    public static synchronized SeqGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * 获取下一个序列号。本方法线程安全。
     *
     * @return {@code long}。
     */
    public synchronized long getNextNumber() {
        // 复位为1
        if (currentNumber == Integer.MAX_VALUE) {
            currentNumber = 1L;
        }
        return currentNumber++;
    }

    /**
     * 设置序列号。仅用作测试。
     *
     * @param currentNumber 传入的 {@code int}。
     */
    synchronized void setCurrentNumber(long currentNumber) {
        this.currentNumber = currentNumber;
    }
}
