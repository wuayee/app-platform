/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import com.huawei.jade.store.entity.transfer.AppData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存入数据库的应用的实体类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppDo extends CommonDo {
    /**
     * 表示应用的点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示应用的下载量。
     */
    private Integer downloadCount;

    /**
     * 表示应用的名字。
     */
    private String toolName;

    /**
     * 表示应用的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 表示用 {@link AppData} 构造 {@link AppDo}。
     *
     * @param appData 表示传输层应用的数据的 {@link AppData}。
     * @return 表示应用的数据库层数据的 {@link AppDo}。
     */
    public static AppDo from(AppData appData) {
        AppDo appDo = new AppDo();
        appDo.setLikeCount(appData.getLikeCount());
        appDo.setDownloadCount(appData.getDownloadCount());
        appDo.setToolName(appData.getName());
        appDo.setToolUniqueName(appData.getUniqueName());
        return appDo;
    }
}
