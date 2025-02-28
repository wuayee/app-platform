/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.vo;

import lombok.Data;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * 分页数据对象。
 *
 * @param <E> 表示数据泛型。
 * @author 易文渊
 * @since 2024-07-22
 */
@Data
public class PageVo<E> {
    @Property(description = "总数", required = true, example = "10")
    private int total;

    @Property(description = "数据列表", required = true)
    private List<E> items;

    /**
     * 创建分页数据对象。
     *
     * @param total 表示数据总量的 {@code int}。
     * @param items 表示数据列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param <E> 表示数据泛型。
     * @return 表示分页数据对象的 {@link PageVo}{@code <}{@link E}{@code >}。
     */
    public static <E> PageVo<E> of(int total, List<E> items) {
        PageVo<E> pageVo = new PageVo<>();
        pageVo.total = total;
        pageVo.items = items;
        return pageVo;
    }
}