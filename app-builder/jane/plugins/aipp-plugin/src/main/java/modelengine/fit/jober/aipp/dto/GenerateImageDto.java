/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 生成图像DTO
 *
 * @author y00679285
 * @since 2024/11/28
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenerateImageDto {
    @Property(description = "图片名称")
    private String name;

    @Property(description = "图片详细描述")
    private String description;

    @Property(description = "图片宽度")
    private int width;

    @Property(description = "图片高度")
    private int height;

    /**
     * 获取图片尺寸
     *
     * @return 图片尺寸
     */
    public String getSize() {
        return width + "x" + height;
    }
}
