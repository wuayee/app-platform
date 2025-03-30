/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.param;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.util.List;

/**
 * 表示文件提取节点的入参。
 *
 * @author 兰宇晨。
 * @since 2024-12-30。
 */
@Data
public class FileExtractionParam {
    /**
     * 待提取内容的文件链接。
     */
    @JsonIgnore
    private List<String> files;

    /**
     * 指导提取内容方式的提示词。
     */
    private String prompt;
}
