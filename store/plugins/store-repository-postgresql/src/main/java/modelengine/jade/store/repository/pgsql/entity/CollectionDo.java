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
 * 存入数据库的收藏关系的实体类。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDo extends CommonDo {
    private String collector;
    private String toolUniqueName;
}
