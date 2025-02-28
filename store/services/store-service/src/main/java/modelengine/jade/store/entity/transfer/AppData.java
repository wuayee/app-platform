/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.transfer;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.carver.tool.ToolSchema.DESCRIPTION;

import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.ToolSchema;
import modelengine.jade.carver.tool.model.transfer.DefinitionData;
import modelengine.jade.carver.tool.model.transfer.DefinitionGroupData;
import modelengine.jade.carver.tool.model.transfer.ToolGroupData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 表示应用的数据内容。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
public class AppData extends StoreToolData {
    /**
     * 表示应用点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示应用下载数量。
     */
    private Integer downloadCount;

    /**
     * 获取应用点赞数量。
     *
     * @return 表示点赞数量的 {@link Integer}。
     */
    public Integer getLikeCount() {
        return this.likeCount;
    }

    /**
     * 设置点赞数量。
     *
     * @param likeCount 表示点赞数量的 {@link Integer}。
     */
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * 获取下载数量。
     *
     * @return 表示下载数量的 {@link Integer}。
     */
    public Integer getDownloadCount() {
        return this.downloadCount;
    }

    /**
     * 设置下载数量。
     *
     * @param downloadCount 表示下载数量的 {@link Integer}。
     */
    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    /**
     * 填充应用数据信息。
     *
     * @param appData 表示应用数据的 {@link AppData}。
     */
    public static void fillAppData(AppData appData) {
        if (StringUtils.isEmpty(appData.getUniqueName())) {
            appData.setUniqueName(UUID.randomUUID().toString());
        }
        if (StringUtils.isEmpty(appData.getGroupName())) {
            appData.setGroupName(appData.getUniqueName());
        }
        if (StringUtils.isEmpty(appData.getDefName())) {
            appData.setDefName(appData.getUniqueName());
        }
        if (StringUtils.isEmpty(appData.getDefGroupName())) {
            appData.setDefGroupName(appData.getUniqueName());
        }
    }

    /**
     * 将应用数据转换为工具组数据。
     *
     * @param appData 表示应用数据的 {@link AppData}。
     * @return 表示工具组数据的 {@link ToolGroupData}。
     */
    public static ToolGroupData toToolGroup(AppData appData) {
        ToolGroupData toolGroupData = new ToolGroupData();
        toolGroupData.setName(appData.getGroupName());
        toolGroupData.setDefGroupName(appData.getDefGroupName());
        toolGroupData.setTools(Arrays.asList(appData));
        return toolGroupData;
    }

    /**
     * 将应用数据转换为定义组组数据。
     *
     * @param appData 表示应用数据的 {@link AppData}。
     * @return 表示定义组组数据的 {@link DefinitionGroupData}。
     */
    public static DefinitionGroupData toDefGroup(AppData appData) {
        String uniqueName = appData.getUniqueName();
        DefinitionGroupData defGroupData = new DefinitionGroupData();
        defGroupData.setName(appData.getDefGroupName());
        DefinitionData defData = new DefinitionData();
        defData.setName(appData.getDefName());
        defData.setGroupName(appData.getDefGroupName());
        defData.setDescription(cast(appData.getSchema().get(DESCRIPTION)));
        Map<String, Object> schema = new HashMap<>(appData.getSchema());
        schema.put(ToolSchema.NAME, appData.getDefName());
        defData.setSchema(schema);
        defGroupData.setDefinitions(Arrays.asList(defData));
        return defGroupData;
    }
}
