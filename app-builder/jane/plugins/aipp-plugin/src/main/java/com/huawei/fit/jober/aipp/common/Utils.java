/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common;

import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.dynamicform.entity.FormMetaInfo;
import com.huawei.fit.dynamicform.entity.FormMetaItem;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Utils {
    /**
     * 上传文件会上传到该共享目录
     */
    public static final String NAS_SHARE_DIR = "/var/share";

    /**
     * 文本最大字符数量
     */
    public static final int MAX_TEXT_LEN = 7168; // 7k

    /**
     * 标题数量
     */
    public static final int MAX_OUTLINE_LINE = 50;

    /**
     * 预览aipp version uuid后缀长度
     */
    public static final int PREVIEW_UUID_LEN = 6;

    /**
     * prompt格式： $(key) 或 ${key}，嵌套场景只提取最里面的key; 待屏蔽$(}和${) 括号混用的场景
     */
    public static final Pattern PROMPT_PATTERN = Pattern.compile("\\$[\\(\\{]([^\\(\\)\\{\\}]+)[\\)\\}]");

    /**
     * aipp log 分布式缓存名称
     */
    public static final String AIPP_LOG_CACHE_NAME = "logcache";

    /**
     * aipp log 分布式缓存名称
     */
    public static final String AIPP_MODEL_NAME = "aipp";

    /**
     * aipp log path分割符
     */
    public static final String PATH_DELIMITER = "/";

    private static final Logger log = Logger.get(Utils.class);

    /**
     * 线程休眠
     *
     * @param second 休眠时间，单位秒
     */
    public static void sleep(long second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPreview(String version) {
        return version.contains("-");
    }

    /**
     * 获取HttpClient 请求配置
     *
     * @param socketTimeout 读取内存超时时长
     * @return 请求配置
     */
    public static RequestConfig requestConfig(int socketTimeout) {
        final int connectTimeout = 5000;
        final int connectRequestTimeout = 5000;
        return RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectRequestTimeout)
                .build();
    }

    /**
     * 从s3获取文件到本地临时目录 Utils.NAS_SHARE_DIR
     * 使用结束需要手动删除临时文件
     *
     * @param instId 实例id, 作为子目录名称
     * @param s3Url s3url
     * @param fileType 文件类型
     * @return 临时文件
     */
    public static File getFileFromS3(String instId, String s3Url, String fileType) throws JobberException {
        HttpGet httpGetImage = new HttpGet(s3Url);
        File tmpFile;
        try (CloseableHttpResponse response = HttpUtils.execute(httpGetImage)) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException(String.format(Locale.ROOT,
                        "bad result code=%d",
                        response.getStatusLine().getStatusCode()));
            }
            tmpFile = AippFileUtils.createFile(instId, fileType + "_" + UUIDUtil.uuid());
            try (InputStream inStream = response.getEntity().getContent();
                 OutputStream outStream = Files.newOutputStream(tmpFile.toPath())) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT,
                            "get %s from s3 failed. url=%s error=%s",
                            fileType,
                            s3Url,
                            e.getMessage()));
        }
        return tmpFile;
    }

    public static String getFilePath(Map<String, Object> businessData, String key) {
        // businessData中的Object是fastjson2的JSONObject导致本项目fastjson的JSONObject无法转换
        Map<String, Object> fileInfo = JsonUtils.parseObject(JsonUtils.toJsonString(businessData.get(key)));
        String fileName = (String) fileInfo.get("file_path");
        Validation.notNull(fileName, "filename cannot be null");
        return fileName;
    }

    public static String parsePrompt(Map<String, Object> businessData, String promptTemplate)
            throws IllegalArgumentException {
        if (StringUtils.isBlank(promptTemplate)) {
            return promptTemplate;
        }
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = PROMPT_PATTERN.matcher(promptTemplate);
        while (matcher.find()) {
            String key = matcher.group(1);
            if (StringUtils.isBlank(key)) {
                log.warn("invalid key in prompt: {}", promptTemplate);
                continue;
            }
            // 兼容key前后带有空格场景
            String value = (String) businessData.get(key.trim());
            Validation.notNull(value, key + " key not exist");
            // 追加替换后的匹配
            matcher.appendReplacement(stringBuffer, value);
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    /**
     * 读取输入流
     *
     * @param inputStream 输入流
     * @return 内容
     */
    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            log.error("read error {}", e.getMessage());
        }
        return "";
    }

    public static Map<String, Object> getAgentParams(List<Map<String, Object>> flowData) {
        return (Map<String, Object>) getBusiness(flowData).get(AippConst.BS_AGENT_PARAM_KEY);
    }

    public static String getAgentId(Map<String, Object> contextData) {
        if (contextData.containsKey(AippConst.BS_EXTRA_CONFIG_KEY)) {
            Map<String, Object> cfgObj = (Map<String, Object>) contextData.get(AippConst.BS_EXTRA_CONFIG_KEY);
            if (cfgObj.containsKey(AippConst.BS_AGENT_ID_KEY)) {
                return (String) cfgObj.get(AippConst.BS_AGENT_ID_KEY);
            }
        }
        return "";
    }

    public static Map<String, Object> getBusiness(List<Map<String, Object>> flowData) {
        if (flowData.isEmpty() || !flowData.get(0).containsKey(AippConst.BS_DATA_KEY)) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.BS_DATA_KEY);
        }
        return (Map<String, Object>) flowData.get(0).get(AippConst.BS_DATA_KEY);
    }

    public static Map<String, Object> getContextData(List<Map<String, Object>> flowData) {
        if (flowData.isEmpty() || !flowData.get(0).containsKey(AippConst.CONTEXT_DATA_KEY)) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.CONTEXT_DATA_KEY);
        }
        return (Map<String, Object>) flowData.get(0).get(AippConst.CONTEXT_DATA_KEY);
    }

    public static String getPromptFromFlowContext(List<Map<String, Object>> flowData) {
        Map<String, Object> extraJober =
                (Map<String, Object>) Utils.getContextData(flowData).get(AippConst.BS_EXTRA_CONFIG_KEY);
        if (extraJober == null) {
            return null;
        }
        String prompt = (String) extraJober.get(AippConst.BS_MODEL_PROMPT_KEY);
        return Utils.parsePrompt(getBusiness(flowData), prompt);
    }

    public static OperationContext getOpContext(Map<String, Object> businessData) {
        return JsonUtils.parseObject((String) businessData.get(AippConst.BS_HTTP_CONTEXT_KEY), OperationContext.class);
    }

    public static void persistInstance(MetaInstanceService service, InstanceDeclarationInfo info,
            Map<String, Object> businessData, OperationContext context) {
        String versionId = (String) businessData.get(AippConst.BS_META_VERSION_ID_KEY);
        String instId = (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY);
        service.patchMetaInstance(versionId, instId, info, context);
    }

    private static boolean checkFormMsg(AippLogData logData, String logType) {
        if (!AippInstLogType.FORM.name().equals(logType)) {
            return true;
        }
        if (StringUtils.isBlank(logData.getFormId()) || logData.getFormId().equals(AippConst.INVALID_FORM_ID)) {
            return false;
        }

        return !StringUtils.isBlank(logData.getFormVersion()) && !logData.getFormVersion()
                .equals(AippConst.INVALID_FORM_VERSION_ID);
    }

    public static void persistAippLog(AippLogService aippLogService, String logType, AippLogData logData,
            Map<String, Object> businessData) {
        String aippId = (String) businessData.get(AippConst.BS_AIPP_ID_KEY);
        String version = (String) businessData.get(AippConst.BS_AIPP_VERSION_KEY);
        String aippType = (String) businessData.get(AippConst.ATTR_AIPP_TYPE_KEY);
        String instId = (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY);
        String w3Account = getOpContext(businessData).getW3Account();
        String parentInstId = (String) businessData.get(AippConst.PARENT_INSTANCE_ID);

        if (!checkFormMsg(logData, logType)) {
            log.warn("invalid logData {}, logType {}, aippId {}, instId {]", logData, logType, aippId, instId);
            return;
        }

        String path = buildPath(aippLogService, instId, parentInstId);

        aippLogService.insertLog(AippLogCreateDto.builder()
                .aippId(aippId)
                .version(version)
                .aippType(aippType)
                .instanceId(instId)
                .logType(logType)
                .logData(JsonUtils.toJsonString(logData))
                .createUserAccount(w3Account)
                .path(path)
                .build());
    }

    @NotNull
    public static String buildPath(AippLogService aippLogService, String instId, String parentInstId) {
        String path;
        if (parentInstId == null) {
            path = PATH_DELIMITER + instId;
        } else {
            String parentPath = aippLogService.getParentPath(parentInstId);
            path = StringUtils.isEmpty(parentPath)
                    ? PATH_DELIMITER + instId
                    : String.join(PATH_DELIMITER, parentPath, instId);
        }
        return path;
    }

    public static boolean checkLogEnable(Map<String, Object> contextData) {
        if (!contextData.containsKey(AippConst.BS_EXTRA_CONFIG_KEY)) {
            return true;
        }
        Map<String, Object> cfgObj = (Map<String, Object>) contextData.get(AippConst.BS_EXTRA_CONFIG_KEY);
        if (cfgObj.containsKey(AippConst.BS_LOG_ENABLE_KEY)) {
            return "true".equalsIgnoreCase((String) cfgObj.get(AippConst.BS_LOG_ENABLE_KEY));
        }
        return true;
    }

    public static void persistAippMsgLog(AippLogService aippLogService, String msg,
            List<Map<String, Object>> flowData) {
        AippLogData logData = AippLogData.builder().msg(msg).build();

        persistAippLog(aippLogService, AippInstLogType.MSG.name(), logData, getBusiness(flowData));
    }

    public static void persistAippErrorLog(AippLogService aippLogService, String msg,
            List<Map<String, Object>> flowData) {
        AippLogData logData = AippLogData.builder().msg(msg).build();
        persistAippLog(aippLogService, AippInstLogType.ERROR.name(), logData, getBusiness(flowData));
    }

    /**
     * 更新指定log id的记录
     *
     * @param aippLogService aipp实例历史记录服务接口
     * @param logId 指定log的id
     * @param newLogData 新的log_data
     */
    public static void updateAippLog(AippLogService aippLogService, Long logId, String newLogData) {
        aippLogService.updateLog(logId, newLogData);
    }

    public static String textLenLimit(String text, Integer limit) {
        int limitReal = limit == null ? MAX_TEXT_LEN : limit;
        if (text.length() < limitReal) {
            return text;
        }
        return text.substring(0, limitReal);
    }

    public static String outlineLenLimit(String outline) {
        int lineCount = 0;
        for (int i = 0; i < outline.length(); ++i) {
            if (outline.charAt(i) == '\n') {
                lineCount++;
                if (lineCount > MAX_OUTLINE_LINE) {
                    return outline.substring(0, i);
                }
            }
        }
        return outline;
    }

    public static String buildPreviewVersion(String version) {
        String uuid = UUIDUtil.uuid();
        String subUuid = (uuid.length() > PREVIEW_UUID_LEN) ? uuid.substring(0, PREVIEW_UUID_LEN) : uuid;
        return version + "-" + subUuid;
    }

    public static AippLogData buildLogDataWithFormData(AppBuilderFormRepository formRepository, String formId,
            String formVersion, Map<String, Object> businessData) {
        List<FormMetaQueryParameter> parameter =
                Collections.singletonList(new FormMetaQueryParameter(formId, formVersion));

        Map<String, Object> formArgs = Utils.buildFormMetaInfos(parameter, formRepository)
                .stream()
                .flatMap(item -> item.getFormMetaItems().stream().map(FormMetaItem::getKey))
                .filter(businessData::containsKey)
                .collect(Collectors.toMap(Function.identity(), businessData::get));

        return AippLogData.builder()
                .formId(formId)
                .formVersion(formVersion)
                .formArgs(JsonUtils.toJsonString(formArgs))
                .build();
    }

    // todo 确认下 form 相关的是否 app使用，如果是的话，该方法可以挪到 DynamicFormServiceImpl
    public static List<FormMetaInfo> buildFormMetaInfos(List<FormMetaQueryParameter> parameters,
            AppBuilderFormRepository formRepository) {
        return parameters.stream()
                .map(parameter -> buildFormMetaInfo(parameter, formRepository))
                .collect(Collectors.toList());
    }

    private static FormMetaInfo buildFormMetaInfo(FormMetaQueryParameter parameter,
            AppBuilderFormRepository formRepository) {
        AppBuilderForm form = formRepository.selectWithId(parameter.getFormId());   // todo 之后改成批量
        FormMetaInfo formMetaInfo = new FormMetaInfo(parameter.getFormId(), parameter.getVersion());
        formMetaInfo.setFormMetaItems(JsonUtils.parseArray(form.getAppearance(), FormMetaItem[].class));
        return formMetaInfo;
    }

    public static DynamicFormDetailEntity queryFormDetailByPrimaryKey(String formId, String version,
            OperationContext context, AppBuilderFormRepository formRepository,
            AppBuilderFormPropertyRepository formPropertyRepository) {
        log.debug("TenantId {} user {} query form {} version {}",
                context.getTenantId(),
                context.getName(),
                formId,
                version);
        AppBuilderForm builderForm = formRepository.selectWithId(formId);
        if (builderForm == null) {
            log.debug("in detail sql query form id {} version {} returns null", formId, version);
            return null;
        }
        builderForm.setFormPropertyRepository(formPropertyRepository);
        String data = buildData(builderForm.getFormProperties());
        return new DynamicFormDetailEntity(convertToDynamicFormEntity(builderForm), data);
    }

    private static String buildData(List<AppBuilderFormProperty> formProperties) {
        Map<String, String> map = formProperties.stream()
                .collect(Collectors.toMap(AppBuilderFormProperty::getName,
                        appBuilderFormProperty -> JsonUtils.toJsonString(appBuilderFormProperty.getDefaultValue())));
        return JsonUtils.toJsonString(map);
    }

    private static DynamicFormEntity convertToDynamicFormEntity(AppBuilderForm builderForm) {
        return DynamicFormEntity.builder()
                .id(builderForm.getId())
                .formName(builderForm.getName())
                .tenantId(builderForm.getTenantId())
                .build();
    }

    /**
     * 获取实例详情
     *
     * @param versionId aipp version id，唯一标识
     * @param instanceId 实例Id
     * @param context 操作上下文
     * @return instance信息
     */
    public static Instance getInstanceDetail(String versionId, String instanceId, OperationContext context,
            MetaInstanceService metaInstanceService) {
        RangedResultSet<Instance> instances = getInstances(versionId, instanceId, context, metaInstanceService);
        if (instances.getRange().getTotal() == 0) {
            log.error("versionId {} inst{} not found.", versionId, instanceId);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "aipp inst not found. inst " + instanceId);
        }
        return instances.getResults().get(0);
    }

    public static RangedResultSet<Instance> getInstances(String versionId, String instanceId, OperationContext context,
            MetaInstanceService metaInstanceService) {
        MetaInstanceFilter filter = new MetaInstanceFilter();
        filter.setIds(Collections.singletonList(instanceId));
        return metaInstanceService.list(versionId, filter, 0, 1, context);
    }

    public static String getFlowTraceId(Map<String, Object> businessData, MetaInstanceService metaInstanceService) {
        OperationContext context = getOpContext(businessData);
        String versionId = ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY));
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        Instance instDetail = Utils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        return instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
    }

    public static String getFlowDefinitionId(Map<String, Object> businessData, MetaService metaService) {
        OperationContext context = getOpContext(businessData);
        Meta meta = metaService.retrieve(ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY)), context);
        return ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_FLOW_DEF_ID_KEY));
    }

    public static <T> Stream<T> getAllFromRangedResult(int limitPerQuery,
            Function<Long, RangedResultSet<T>> resultGetter) {
        RangedResultSet<T> metaRes = resultGetter.apply(0L);
        if (metaRes.getResults().isEmpty() || metaRes.getRange().getTotal() == 0) {
            return Stream.empty();
        }
        Stream<T> firstResult = metaRes.getResults().stream();
        if (metaRes.getRange().getTotal() <= limitPerQuery) {
            return firstResult;
        }
        return Stream.concat(firstResult,
                LongStream.rangeClosed(1, (int) (metaRes.getRange().getTotal() / limitPerQuery))
                        .mapToObj(offsetCount -> CompletableFuture.supplyAsync(() -> resultGetter.apply(
                                offsetCount * limitPerQuery).getResults().stream()))
                        .flatMap(CompletableFuture::join));
    }

    public static String getAippLogRedisMapName(String instanceId) {
        return String.join(":", Arrays.asList(AIPP_MODEL_NAME, AIPP_LOG_CACHE_NAME, instanceId));
    }

    public static Integer getIntegerFromStr(String str) {
        Integer value = null;
        try {
            value = Integer.valueOf(str);
        } catch (NumberFormatException ex) {
            log.error("invalid number string {}", str);
        }

        return value;
    }

    public static String trimLine(String line) {
        return line.trim()
                .replace("\n", "")
                .replace("\b", "")
                .replace("\r", "")
                .replace("\f", "")
                .replace("\t", "")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
