/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.query;

/**
 * 表示查询的工具类。
 *
 * @author 李金绪
 * @since 2024-09-13
 */
public class QueryUtils {
    /**
     * 判断分页参数是否无效。
     *
     * @param offset 表示分页起始位置的 {@link Integer}。
     * @param limit 表示分页大小的 {@link Integer}。
     * @return 表示分页参数是否无效的 {@code boolean}。
     */
    public static boolean isPageInvalid(Integer offset, Integer limit) {
        return (offset != null && offset < 0) || (limit != null && limit < 0);
    }
}
