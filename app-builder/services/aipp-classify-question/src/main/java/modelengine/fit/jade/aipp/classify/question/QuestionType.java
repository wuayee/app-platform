/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question;

import lombok.Data;

/**
 * 问题类型。
 *
 * @author 张越
 * @since 2024-11-18
 */
@Data
public class QuestionType {
    private String id;

    private String questionTypeDesc;

    /**
     * 将问题类型转换为model可识别的格式.
     *
     * @return model可识别的格式.
     */
    public String toModelFormat() {
        return "{\"类型ID\":\"" + this.getId() + "\", \"问题类型\":\"" + this.getQuestionTypeDesc() + "\"}";
    }
}
