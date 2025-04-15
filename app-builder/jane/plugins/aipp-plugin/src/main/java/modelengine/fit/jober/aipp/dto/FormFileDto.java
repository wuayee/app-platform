/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 表单上传dto
 *
 * @author 陈潇文
 * @since 2024/11/18
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormFileDto {
    @Property(name = "imgUrl")
    private String imgUrl;

    @Property(name = "iframeUrl")
    private String iframeUrl;

    @Property(name = "fileUuid")
    private String fileUuid;

    @Property(name = "fileName")
    private String fileName;

    @Property(name = "schema")
    private Map<String, Object> schema;
}
