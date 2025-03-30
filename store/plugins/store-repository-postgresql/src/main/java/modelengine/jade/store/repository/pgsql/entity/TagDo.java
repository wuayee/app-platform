/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.jade.carver.entity.CommonDo;

/**
 * 存入数据库的标签的实体类。
 *
 * @author 李金绪
 * @since 2024/5/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDo extends CommonDo {
    /**
     * 表示工具的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 表示标签的名字。
     */
    private String name;
}
