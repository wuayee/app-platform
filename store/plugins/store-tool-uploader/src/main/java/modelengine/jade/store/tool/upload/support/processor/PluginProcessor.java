/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.support.processor;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.carver.tool.ToolSchema.NAME;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.ARTIFACT_ID;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.CHECKSUM;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.DESCRIPTION;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.DOT;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.GROUP_ID;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.HTTP;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.JAVA;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PLUGIN_FULL_NAME;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PLUGIN_JSON;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PLUGIN_NAME;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PYTHON;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PYTHON_NAME;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.TEMP_DIR;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.TYPE;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.UNIQUENESS;
import static modelengine.jade.carver.tool.info.schema.ToolsSchema.BUILT_IN;
import static modelengine.jade.carver.tool.info.schema.ToolsSchema.FIT;
import static modelengine.jade.carver.tool.info.schema.ToolsSchema.TAGS;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildBlankParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildEmptyParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildNullParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildParserException;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getCompressedFile;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.objToMap;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.renameFile;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.info.entity.HttpJsonEntity;
import modelengine.jade.carver.tool.info.entity.PluginJsonEntity;
import modelengine.jade.carver.tool.model.transfer.DefinitionData;
import modelengine.jade.carver.tool.model.transfer.DefinitionGroupData;
import modelengine.jade.carver.tool.model.transfer.ToolData;
import modelengine.jade.carver.tool.model.transfer.ToolGroupData;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.support.DeployStatus;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 插件信息的处理器。
 *
 * @author 李金绪
 * @since 2024-10-29
 */
@Component
public class PluginProcessor extends Processor {
    private static final Logger log = Logger.get(PluginProcessor.class);
    private static final Map<String, List<String>> supLanguages = MapBuilder.<String, List<String>>get()
            .put(JAVA, Arrays.asList(GROUP_ID, ARTIFACT_ID))
            .put(PYTHON, Arrays.asList(PYTHON_NAME))
            .build();

    /** 插件的唯一性校验支持字母和数字的字符串，可以包含点、中划线（-）和下划线(_)，但不能以这些字符开头或结尾。 */
    private static final Pattern PLUGIN_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]+([._-]?[a-zA-Z0-9]+)*$");

    private final ObjectSerializer serializer;

    /**
     * 用于构造一个 {@link PluginProcessor} 的新实例。
     *
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     */
    public PluginProcessor(@Fit(alias = "json") ObjectSerializer serializer) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Override
    public void validate(Object data, Map<String, Object> helper) {
        PluginJsonEntity entity = cast(data);
        notBlank(entity.getType(), () -> buildBlankParserException(PLUGIN_JSON, TYPE));
        notBlank(entity.getChecksum(), () -> buildBlankParserException(PLUGIN_JSON, CHECKSUM));
        notNull(entity.getDescription(), () -> buildNullParserException(PLUGIN_JSON, DESCRIPTION));
        this.validateName(entity.getName(), PLUGIN_JSON, PLUGIN_NAME);
        this.validateUniqueness(entity.getUniqueness(), entity.getType());
    }

    private void validateUniqueness(Map<String, String> info, String language) {
        if (MapUtils.isEmpty(info)) {
            throw buildEmptyParserException(PLUGIN_JSON, UNIQUENESS);
        }
        for (String filed : supLanguages.get(language)) {
            String value = info.get(filed);
            notBlank(value, () -> buildBlankParserException(PLUGIN_JSON, UNIQUENESS + DOT + filed));
            if (!PLUGIN_ID_PATTERN.matcher(value).matches()) {
                throw new ModelEngineException(PluginRetCode.PLUGIN_VALIDATION_FIELD, filed);
            }
        }
    }

    @Override
    public Object transform(Object data, Map<String, Object> helper) {
        PluginJsonEntity entity = cast(data);
        PluginData pluginData = new PluginData();
        Map<String, String> uniqueness = entity.getUniqueness();
        String input = String.join(StringUtils.EMPTY, uniqueness.values());
        pluginData.setPluginId(generatePluginId(input));
        String userName = getUserName();
        pluginData.setCreator(userName);
        pluginData.setModifier(userName);
        File newCompressedFile = renameFile(cast(getCompressedFile(cast(helper.get(TEMP_DIR)))));
        pluginData.setExtension(this.getPluginExtensions(entity, newCompressedFile.getName()));
        pluginData.setPluginName(entity.getName());
        pluginData.setDeployStatus(DeployStatus.UNDEPLOYED.name());
        return pluginData;
    }

    private static String generatePluginId(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return IntStream.range(0, hash.length)
                    .mapToObj(i -> String.format("%02x", hash[i] & 0xFF))
                    .collect(Collectors.joining());
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate pluginId.");
            return StringUtils.EMPTY;
        }
    }

    private Map<String, Object> getPluginExtensions(PluginJsonEntity entity, String newCompressedFileFullName) {
        Map<String, String> extension = MapUtils.flat(objToMap(this.serializer, entity), DOT);
        extension.put(PLUGIN_FULL_NAME, newCompressedFileFullName);
        return cast(extension);
    }

    /**
     * 创建一个 http 插件的实例，要求必须是一个定义组，一个实现组，一个定义，一个工具。
     *
     * @param defGroups 表示定义组的 {@link List}{@code <}{@link DefinitionGroupData}{@code >}。
     * @param toolGroups 表示工具组的 {@link List}{@code <}{@link ToolGroupData}{@code >}。
     * @param httpEntity 表示 http json 实体的 {@link HttpJsonEntity}。
     * @return 表示创建的插件实例的 {@link PluginData}。
     */
    public static PluginData buildHttpPluginData(List<DefinitionGroupData> defGroups, List<ToolGroupData> toolGroups,
            HttpJsonEntity httpEntity) {
        if (toolGroups.size() != 1 || defGroups.size() != 1 || toolGroups.get(0).getTools().size() != 1
                || defGroups.get(0).getDefinitions().size() != 1) {
            throw buildParserException("The http plugin can only have 1 group with 1 tool.");
        }
        handleHttpGroupInfo(defGroups, toolGroups);
        ToolData toolData = toolGroups.get(0).getTools().get(0);
        PluginData pluginData = handleHttpPluginInfo(httpEntity, toolData);
        pluginData.setToolGroupDataList(toolGroups);
        pluginData.setDefinitionGroupDataList(defGroups);
        return pluginData;
    }

    private static void handleHttpGroupInfo(List<DefinitionGroupData> defGroups, List<ToolGroupData> toolGroups) {
        ToolData toolData = toolGroups.get(0).getTools().get(0);
        String uniqueName = toolData.getUniqueName();
        toolGroups.get(0).setName(uniqueName);
        toolGroups.get(0).setDefGroupName(uniqueName);
        toolData.setDefGroupName(uniqueName);
        toolData.setDefName(toolData.getName());
        toolData.setGroupName(uniqueName);
        defGroups.get(0).setName(uniqueName);
        DefinitionData defData = defGroups.get(0).getDefinitions().get(0);
        defData.setName(toolData.getName());
        defData.setGroupName(uniqueName);
        defData.getSchema().put(NAME, toolData.getName());
    }

    private static PluginData handleHttpPluginInfo(HttpJsonEntity httpEntity, ToolData toolData) {
        PluginData pluginData = new PluginData();
        pluginData.setPluginId(generatePluginId(toolData.getUniqueName()));
        String userName = getUserName();
        pluginData.setCreator(userName);
        pluginData.setModifier(userName);
        pluginData.setPluginName(toolData.getName());
        pluginData.setDeployStatus(DeployStatus.RELEASED.name());
        PluginToolData pluginToolData = buildPluginToolData(toolData, pluginData.getPluginId());
        pluginToolData.setSource(httpEntity.getSource());
        pluginData.setIcon(httpEntity.getIcon());
        pluginData.setPluginToolDataList(Arrays.asList(pluginToolData));
        // 设置插件的 extensions 信息
        Map<String, Object> extension = new HashMap<>();
        extension.put(TYPE, HTTP);
        extension.put(DESCRIPTION, toolData.getDescription());
        extension.put(PLUGIN_FULL_NAME, toolData.getName());
        extension.put(PLUGIN_NAME, toolData.getName());
        pluginData.setExtension(extension);
        pluginData.setSource(httpEntity.getSource());
        pluginData.setIcon(httpEntity.getIcon());
        return pluginData;
    }

    /**
     * 构建插件工具数据列表。
     *
     * @param toolGroupDatas 表示工具组数据的 {@link List}{@code <}{@link ToolGroupData}{@code >}
     * @param pluginId 表示插件的唯一标识的 {@link String}。
     * @return 表示插件工具的 {@link List}{@code <}{@link PluginToolData}{@code >}
     */
    public static List<PluginToolData> buildPluginToolDatas(List<ToolGroupData> toolGroupDatas, String pluginId) {
        return toolGroupDatas.stream()
                .flatMap(toolGroupData -> toolGroupData.getTools().stream())
                .map(toolData -> buildPluginToolData(toolData, pluginId))
                .collect(Collectors.toList());
    }

    /**
     * 构建插件工具数据。
     *
     * @param toolData 表示工具数据的 {@link ToolData}。
     * @param pluginId 表示插件的唯一标识的 {@link String}。
     * @return 表示插件工具的 {@link PluginToolData}.
     */
    public static PluginToolData buildPluginToolData(ToolData toolData, String pluginId) {
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setPluginId(pluginId);
        pluginToolData.setUniqueName(toolData.getUniqueName());
        String userName = getUserName();
        pluginToolData.setName(toolData.getName());
        pluginToolData.setCreator(userName);
        pluginToolData.setModifier(userName);
        pluginToolData.setTags(replaceTags(cast(toolData.getExtensions().get(TAGS))));
        return pluginToolData;
    }

    private static Set<String> replaceTags(List<String> oldTags) {
        return oldTags.stream()
                .map(oldTag -> oldTag.equalsIgnoreCase(BUILT_IN) ? FIT : oldTag)
                .collect(Collectors.toSet());
    }
}
