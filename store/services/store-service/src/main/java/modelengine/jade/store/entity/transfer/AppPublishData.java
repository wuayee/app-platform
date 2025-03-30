/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.transfer;

/**
 * 表示应用发布的相关数据。
 *
 * @author 兰宇晨
 * @since 2025-01-13
 */
public class AppPublishData extends AppData {
    /**
     * 应用类型。
     */
    private String appCategory;

    /**
     * 设置应用类型。
     *
     * @param appCategory 表示应用类型的 {@link String}。
     */
    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    /**
     * 获取应用类型。
     *
     * @return  appCategory 表示应用类型的 {@link String}。
     */
    public String getAppCategory() {
        return this.appCategory;
    }
}
