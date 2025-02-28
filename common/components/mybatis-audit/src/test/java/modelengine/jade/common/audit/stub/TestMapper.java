/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.audit.stub;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表示测试使用的持久层接口。
 *
 * @author 易文渊
 * @since 2024-08-13
 */
@Mapper
public interface TestMapper {
    /**
     * 插入测试数据。
     *
     * @param testPo 表示测试数据的 {@link TestPo}。
     */
    void insert(TestPo testPo);

    /**
     * 插入测试数据。
     *
     * @param testPoList 表示测试数据的 {@link List}{@code <}{@link TestPo}{@code >}。
     */
    void insertAll(List<TestPo> testPoList);

    /**
     * 插入测试数据，参数存在多个值。
     *
     * @param testPo 表示测试数据的 {@link TestPo}。
     * @param id 表示额外参数的 {@link Long}。
     */
    void insertMultiParm(@Param("param") TestPo testPo, @Param("ignored") Long id);
}