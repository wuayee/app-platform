/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.schema;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.schema.exception.JsonContentInvalidException;

import java.util.List;

/**
 * 根据 Schema 校验数据接口。
 *
 * @author 兰宇晨
 * @author 何嘉斌
 * @since 2024-07-29
 */
public interface SchemaValidator {
    /**
     * 根据 Schema 校验评估内容。
     *
     * @param schema 表示用于校验数据的 Schema {@link Object}。
     * @param content 表示评估内容集合的 {@link Object}。
     * @throws JsonContentInvalidException 当 {@code schema} 为无效数据时。
     * @throws JsonContentInvalidException 当 {@code content} 中含有无效数据时。
     */
    @Genericable(id = "modelengine.jade.app.eval.schema.validate.single")
    void validate(Object schema, Object content);

    /**
     * 根据 Schema 批量校验评估内容。
     *
     * @param schema 表示用于校验数据的 Schema {@link Object}。
     * @param contents 表示评估内容集合的 {@link List}{@code <}{@code ?}{@code >}。
     * @throws JsonContentInvalidException 当 {@code schema} 为无效数据时。
     * @throws JsonContentInvalidException 当 {@code contents} 中含有无效数据时。
     */
    @Genericable(id = "modelengine.jade.app.eval.schema.validate.batch")
    void validate(Object schema, List<?> contents);
}