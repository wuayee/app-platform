/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.transaction;

import javax.sql.DataSource;

/**
 * 为事务提供管理程序。
 *
 * @author 梁济时
 * @since 2022-08-24
 */
public interface TransactionManager {
    /**
     * 获取事务管理程序所使用的数据源。
     *
     * @return 表示数据源的 {@link DataSource}。
     */
    DataSource dataSource();

    /**
     * 开始事务。
     *
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @return 表示事务实例的 {@link Transaction}。
     * @throws TransactionCreationException 创建事务失败。
     * @throws TransactionPreparationException 准备事务失败。
     */
    Transaction begin(TransactionMetadata metadata);

    /**
     * 获取当前事务。
     *
     * @return 表示当前事务的 {@link Transaction}。
     */
    Transaction active();

    /**
     * 激活指定事务。
     *
     * @param transaction 表示待激活的事务的 {@link Transaction}。
     * @throws TransactionPreparationException 准备事务失败。
     */
    void activate(Transaction transaction);

    /**
     * 失活指定事务。
     *
     * @param transaction 表示待失活的事务的 {@link Transaction}。
     * @throws TransactionPreparationException 准备事务失败。
     * @throws IllegalStateException {@code transaction} 不是当前的活动事务。
     */
    void deactivate(Transaction transaction);
}
