package com.huawei.fit.jober.aipp.enums;

import lombok.Getter;

/**
 * 图片文件后缀
 *
 * @author h00804153
 * @since 2024/4/22
 */
@Getter
public enum ImageExtensionEnum {
    BMP("bmp"),
    GIF("gif"),
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    SVG("svg"),
    WEBP("webp");

    private final String extension;

    ImageExtensionEnum(String imageExtension) {
        this.extension = imageExtension;
    }

    // todo: 写判断图片类型的方法

    /**
     * 判断是否为图片类型
     *
     * @param fileType 文件类型
     * @return 是否为图片
     */
    public static boolean isImageFile(String fileType) {
        for (ImageExtensionEnum type : ImageExtensionEnum.values()) {
            if (type.getExtension().equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }
}
