/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.entry;

import modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.dto.TzCvAnalyzerDto;
import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.annotation.Property;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;

/**
 * 天舟简历解析插件接口
 *
 * @author 杨璨宇
 * @since 2024/09/04
 */
@Group(name = "implGroup_weather_Rain_Mobile")
public interface TzCvAnalyzerAppTool {

    /**
     * 解析简历内容，填充提示词模板，输出大模型提示词
     *
     * @param fileUrl 简历文件URL
     * @param instanceId 实例ID
     * @return 包含简历解析结果的大模型提示词
     */
    @ToolMethod(name = "Resume_Parsing_Plugin_interface", description = "简历解析插件接口")
    @Genericable(id = "modelengine.fit.jober.aipp.tool.tianzhou.cv.analyzer")
    TzCvAnalyzerDto analyzeCv(@Property(description = "简历文件URL", required = true) String fileUrl,
            @Property(description = "实例ID", required = true) String instanceId);
}