/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * PPT页面的数据传输对象
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PptPageDto {
    private String title;

    private String content;
}
