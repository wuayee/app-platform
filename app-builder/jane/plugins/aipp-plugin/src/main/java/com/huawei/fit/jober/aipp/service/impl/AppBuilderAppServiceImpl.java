/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.common.exception.AippErrCode.TASK_NOT_FOUND;
import static com.huawei.fit.jober.aipp.constants.AippConst.ATTR_UNIQUE_NAME;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.enums.DirectionEnum;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.FlowsService;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.condition.AppQueryCondition;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.convertor.FormMetaConvertor;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.domain.AppBuilderConfig;
import com.huawei.fit.jober.aipp.domain.AppBuilderConfigProperty;
import com.huawei.fit.jober.aipp.domain.AppBuilderFlowGraph;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AippDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigFormDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import com.huawei.fit.jober.aipp.dto.PublishedAppResDto;
import com.huawei.fit.jober.aipp.enums.AippMetaStatusEnum;
import com.huawei.fit.jober.aipp.enums.AippSortKeyEnum;
import com.huawei.fit.jober.aipp.enums.AippTypeEnum;
import com.huawei.fit.jober.aipp.enums.AppState;
import com.huawei.fit.jober.aipp.enums.JaneCategory;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.genericable.entity.AippCreate;
import com.huawei.fit.jober.aipp.mapper.AippLogMapper;
import com.huawei.fit.jober.aipp.mapper.AippUploadedFileMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.service.AippChatService;
import com.huawei.fit.jober.aipp.service.AippFlowService;
import com.huawei.fit.jober.aipp.service.AppBuilderAppService;
import com.huawei.fit.jober.aipp.util.ConvertUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.aipp.util.MetaUtils;
import com.huawei.fit.jober.aipp.util.VersionUtils;
import com.huawei.fit.jober.aipp.validation.AppUpdateValidator;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.Tuple;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.base.service.UsrAppCollectionService;
import com.huawei.jade.store.service.AppService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用开发接口实现类
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
public class AppBuilderAppServiceImpl
        implements AppBuilderAppService, com.huawei.fit.jober.aipp.genericable.AppBuilderAppService {
    private static final Logger log = Logger.get(AppBuilderAppServiceImpl.class);
    private static final int RETRY_CREATE_TIMES = 5;
    private static final String DEFAULT_APP_VERSION = "1.0.0";
    private static final String VERSION_FORMAT = "{0}.{1}.{2}";
    private static final String PUBLISH_UPDATE_DESCRIPTION_KEY = "publishedDescription";
    private static final String PUBLISH_UPDATE_LOG_KEY = "publishedUpdateLog";

    private final AppBuilderAppFactory appFactory;
    private final AippFlowService aippFlowService;
    private final AppBuilderAppRepository appRepository;
    private final int nameLengthMaximum;
    private final MetaService metaService;
    private final UsrAppCollectionService usrAppCollectionService;
    private final AppUpdateValidator appUpdateValidator;
    private final MetaInstanceService metaInstanceService;
    private final AippUploadedFileMapper aippUploadedFileMapper;
    private final String appNameFormat = "^[^-_][\\u4E00-\\u9FA5A-Za-z0-9-_]*$";
    private final AippLogMapper aippLogMapper;
    private final FlowsService flowsService;
    private final AppService appService;
    private final AippChatService aippChatService;

    public AppBuilderAppServiceImpl(AppBuilderAppFactory appFactory, AippFlowService aippFlowService,
            AppBuilderAppRepository appRepository,
            @Value("${validation.task.name.length.maximum:64}") int nameLengthMaximum, MetaService metaService,
            UsrAppCollectionService usrAppCollectionService, AppUpdateValidator appUpdateValidator,
            MetaInstanceService metaInstanceService, AippUploadedFileMapper aippUploadedFileMapper,
            AippLogMapper aippLogMapper, FlowsService flowsService, AppService appService,
            AippChatService aippChatService) {
        this.nameLengthMaximum = nameLengthMaximum;
        this.appFactory = appFactory;
        this.aippFlowService = aippFlowService;
        this.appRepository = appRepository;
        this.metaService = metaService;
        this.usrAppCollectionService = usrAppCollectionService;
        this.appUpdateValidator = appUpdateValidator;
        this.metaInstanceService = metaInstanceService;
        this.aippUploadedFileMapper = aippUploadedFileMapper;
        this.aippLogMapper = aippLogMapper;
        this.flowsService = flowsService;
        this.appService = appService;
        this.aippChatService = aippChatService;
    }

    @Override
    @Fitable(id = "default")
    public AppBuilderAppDto query(String appId) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        return this.buildFullAppDto(appBuilderApp);
    }

    @Override
    @Fitable(id = "default")
    @Transactional
    public Rsp<AippCreateDto> publish(AppBuilderAppDto appDto, OperationContext contextOf) {
        // 要加个save appDto到数据的逻辑
        this.validateApp(appDto.getId());
        AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(appDto);
        this.validateVersion(aippDto, contextOf);
        AippCreateDto aippCreateDto = this.aippFlowService.create(aippDto, contextOf);
        aippDto.setId(aippCreateDto.getAippId());
        String id = appDto.getId();
        AppBuilderApp appBuilderApp = this.appFactory.create(id);
        if (AppState.getAppState(appBuilderApp.getState()) == AppState.PUBLISHED) {
            throw new AippException(AippErrCode.APP_HAS_PUBLISHED);
        }
        appBuilderApp.setState(AppState.PUBLISHED.getName());
        // 添加校验，禁止更低版本手动输入
        this.validateVersionIsLatest(appBuilderApp.getVersion(), appDto.getVersion());
        appBuilderApp.setVersion(appDto.getVersion());
        Map<String, Object> appBuilderAppAttr = appBuilderApp.getAttributes();
        appBuilderAppAttr.put(PUBLISH_UPDATE_DESCRIPTION_KEY, aippDto.getPublishedDescription());
        appBuilderAppAttr.put(PUBLISH_UPDATE_LOG_KEY, aippDto.getPublishedUpdateLog());
        this.appFactory.update(appBuilderApp);
        if (appBuilderApp.getAttributes().containsKey("store_id")) {
            aippDto.setUniqueName(appBuilderApp.getAttributes().get("store_id").toString());
        }
        return this.aippFlowService.publish(aippDto, contextOf);
    }

    /**
     * 校验版本号是否比现有号更新
     *
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    private void validateVersionIsLatest(String oldVersion, String newVersion) {
        if (!VersionUtils.isValidVersion(oldVersion) || !VersionUtils.isValidVersion(newVersion)) {
            throw new AippException(AippErrCode.INVALID_VERSION_NAME);
        }
        if (StringUtils.equals(oldVersion, newVersion)) {
            // 在这里，与旧版本号相同的版本号被认为是最新的
            return;
        }
        String[] oldPart = oldVersion.split("\\.");
        String[] newPart = newVersion.split("\\.");
        for (int i = 0; i < oldPart.length; i++) {
            int oldV = Integer.parseInt(oldPart[i]);
            int newV = Integer.parseInt(newPart[i]);
            if (oldV > newV) {
                // 如果某个号比当前号小，则认为新版本比旧版本小，抛出错误
                throw new AippParamException(AippErrCode.NEW_VERSION_IS_LOWER);
            }
            if (newV > oldV) {
                // 如果某个号比当前号大，则认为新版本比旧版本大，例如2.0.0比1.9.9大
                break;
            }
        }
    }

    private void validateVersion(AippDto aippDto, OperationContext contextOf) {
        String aippId = this.getAippIdByAppId(aippDto.getAppId(), contextOf);
        MetaFilter metaFilter = new MetaFilter();
        metaFilter.setVersions(Collections.singletonList(aippDto.getVersion()));
        metaFilter.setMetaIds(Collections.singletonList(aippId));
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.NORMAL.type()));
        attributes.put(AippConst.ATTR_META_STATUS_KEY, Collections.singletonList(AippMetaStatusEnum.ACTIVE.getCode()));
        metaFilter.setAttributes(attributes);
        RangedResultSet<Meta> metas = this.metaService.list(metaFilter, true, 0, 1, contextOf);
        if (!metas.getResults().isEmpty()) {
            throw new AippException(AippErrCode.APP_VERSION_HAS_ALREADY);
        }
    }

    @Override
    @Fitable(id = "default")
    public AippCreate debug(AppBuilderAppDto appDto, OperationContext contextOf) {
        AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(appDto);
        // Rsp 得统一整改下
        return ConvertUtils.toAippCreate(this.aippFlowService.previewAipp(appDto.getVersion(), aippDto, contextOf));
    }

    @Override
    @Fitable(id = "default")
    public void updateFlow(String appId, OperationContext contextOf) {
        AppBuilderApp app = this.appFactory.create(appId);
        if (ObjectUtils.cast(app.getAttributes().getOrDefault(AippConst.ATTR_APP_IS_UPDATE, false))) {
            AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(this.buildFullAppDto(app));
            this.aippFlowService.previewAipp(app.getVersion(), aippDto, contextOf);
            app.getAttributes().put(AippConst.ATTR_APP_IS_UPDATE, false);
            this.appFactory.update(app);
        }
    }

    @Override
    public AppBuilderAppDto queryLatestOrchestration(String appId, OperationContext context) {
        this.validateApp(appId);
        String aippId = this.getAippIdByAppId(appId, context);
        MetaFilter filter = new MetaFilter();
        filter.setMetaIds(Collections.singletonList(aippId));
        String sortEncode = MetaUtils.formatSorter(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name());
        filter.setOrderBys(Collections.singletonList(sortEncode));
        List<Meta> metas = MetaUtils.getListMetaHandle(this.metaService, filter, context);
        if (metas.isEmpty()) {
            log.error("Meta list can not be empty.");
            throw new AippParamException(TASK_NOT_FOUND);
        }
        Meta latestMeta = metas.get(0);
        String copiedAppId = String.valueOf(latestMeta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        AppBuilderApp copiedApp = this.appFactory.create(copiedAppId);
        if (MetaUtils.isPublished(latestMeta)) {
            return this.create(copiedAppId, this.buildAppBuilderAppCreateDto(copiedApp), context, true);
        }
        return this.query(copiedAppId);
    }

    /**
     * 校验应用是否存在
     *
     * @param appId 待校验的应用 id
     */
    private void validateApp(String appId) {
        this.appFactory.create(appId);
    }

    @Override
    public AippCreate queryLatestPublished(String appId, OperationContext context) {
        List<Meta> metas = this.getPublishedMetaList(appId, context);
        if (metas.isEmpty()) {
            log.error("Meta list can not be empty.");
            throw new AippParamException(TASK_NOT_FOUND);
        }
        Meta latestMeta = metas.get(0);
        return AippCreate.builder().version(latestMeta.getVersion()).aippId(latestMeta.getId()).build();
    }

    private List<Meta> getPublishedMetaList(String appId, OperationContext context) {
        MetaFilter filter = new MetaFilter();
        String aippId = this.getAippIdByAppId(appId, context);
        filter.setMetaIds(Collections.singletonList(aippId));
        String sortEncode = MetaUtils.formatSorter(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name());
        filter.setOrderBys(Collections.singletonList(sortEncode));
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.NORMAL.name()));
        attributes.put(AippConst.ATTR_META_STATUS_KEY, Collections.singletonList(AippMetaStatusEnum.ACTIVE.getCode()));
        filter.setAttributes(attributes);
        return MetaUtils.getListMetaHandle(this.metaService, filter, context);
    }

    private AppBuilderAppCreateDto buildAppBuilderAppCreateDto(AppBuilderApp app) {
        Map<String, Object> attributes = app.getAttributes();
        String description = String.valueOf(attributes.getOrDefault("description", StringUtils.EMPTY));
        String icon = String.valueOf(attributes.getOrDefault("icon", StringUtils.EMPTY));
        String greeting = String.valueOf(attributes.getOrDefault("greeting", StringUtils.EMPTY));
        String appType = String.valueOf(attributes.getOrDefault("app_type", StringUtils.EMPTY));
        String storeId = String.valueOf(attributes.getOrDefault("store_id", StringUtils.EMPTY));
        return AppBuilderAppCreateDto.builder()
                .name(app.getName())
                .description(description)
                .icon(icon)
                .greeting(greeting)
                .appType(appType)
                .type(app.getType())
                .storeId(storeId)
                .build();
    }

    @Override
    @Fitable(id = "default")
    public Optional<AppBuilderConfigFormPropertyDto> getPropertyByName(String appId, String name) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        List<AppBuilderConfigFormPropertyDto> configFormPropertyDtos =
                this.buildAppBuilderConfigFormProperties(appBuilderApp.getConfig());
        return configFormPropertyDtos.stream().filter(prop -> prop.getName().equals(name)).findFirst();
    }

    @Override
    @Fitable(id = "default")
    public Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(AppQueryCondition cond,
            HttpClassicServerRequest httpRequest, String tenantId, long offset, int limit) {
        List<AppBuilderAppMetadataDto> result = this.appRepository.selectWithLatestApp(cond, tenantId, offset, limit)
                .stream()
                .map(this::buildAppMetaData)
                .collect(Collectors.toList());
        long total = this.appRepository.countWithLatestApp(tenantId, cond);
        return Rsp.ok(RangedResultSet.create(result, offset, limit, total));
    }

    private AppBuilderAppMetadataDto buildAppMetaData(AppBuilderApp app) {
        List<String> tags = new ArrayList<>();
        tags.add(app.getType().toUpperCase(Locale.ROOT));
        return AppBuilderAppMetadataDto.builder()
                .id(app.getId())
                .name(app.getName())
                .type(app.getType())
                .state(app.getState())
                .attributes(app.getAttributes())
                .version(app.getVersion())
                .createBy(app.getCreateBy())
                .updateBy(app.getUpdateBy())
                .createAt(app.getCreateAt())
                .updateAt(app.getUpdateAt())
                .tags(tags)
                .build();
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public AppBuilderAppDto create(String appId, AppBuilderAppCreateDto dto, OperationContext context,
            boolean isUpgrade) {
        if (dto != null && !isUpgrade) {
            this.validateCreateApp(dto.getName(), context);
        }
        AppBuilderApp templateApp = this.appFactory.create(appId);
        // 根据模板app复制app，仅需修改所有id
        // 优先copy下层内容，因为上层改变Id后，会影响下层对象的查询
        AppBuilderFlowGraph flowGraph = templateApp.getFlowGraph();
        flowGraph.setId(Entities.generateId());
        Map<String, Object> appearance =
                JSONObject.parseObject(flowGraph.getAppearance(), new TypeReference<Map<String, Object>>() {});
        appearance.computeIfPresent("id", (key, value) -> flowGraph.getId());
        // 这里在创建应用时需要保证graph中的title+version唯一，否则在发布flow时会报错
        appearance.put("title", flowGraph.getId());
        flowGraph.setAppearance(JSONObject.toJSONString(appearance));
        String version = this.buildVersion(templateApp, isUpgrade);
        templateApp.setId(Entities.generateId());
        AppBuilderConfig config = resetConfig(templateApp.getConfig());
        templateApp.setConfigId(config.getId());
        templateApp.setFlowGraphId(flowGraph.getId());
        templateApp.setType("app");
        templateApp.setTenantId(context.getTenantId());
        if (isUpgrade) {
            templateApp.setState(AppState.INACTIVE.getName());
        }
        String preVersion = templateApp.getVersion();
        templateApp.setVersion(version);
        config.setAppId(templateApp.getId());
        if (Objects.nonNull(dto)) {
            templateApp.setAttributes(this.createAppAttributes(dto, isUpgrade, preVersion));
            templateApp.setName(dto.getName());
            templateApp.setType(dto.getType());
        }

        resetOperatorAndTime(templateApp, LocalDateTime.now(), context.getOperator());
        this.saveNewAppBuilderApp(templateApp);
        this.saveMeta(templateApp, version, context);
        return this.buildFullAppDto(templateApp);
    }

    private void saveMeta(AppBuilderApp app, String version, OperationContext context) {
        AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderApp(app);
        int retryTimes = RETRY_CREATE_TIMES;
        do {
            String previewVersion = VersionUtils.buildPreviewVersion(version);
            aippDto.setVersion(previewVersion);
            MetaDeclarationInfo declarationInfo = this.buildInitialMetaDeclaration(aippDto,
                    AippCreateDto.builder().aippId(aippDto.getId()).version(previewVersion).build(),
                    AippTypeEnum.NORMAL.name());
            try {
                this.metaService.create(declarationInfo, context);
                break;
            } catch (FitException e) {
                log.warn("create meta failed, times {} aippId {} version {}, error {}",
                        RETRY_CREATE_TIMES - retryTimes,
                        aippDto.getId(),
                        version,
                        e.getMessage());
            }
        } while (retryTimes-- > 0);
    }

    private MetaDeclarationInfo buildInitialMetaDeclaration(AippDto aippDto, AippCreateDto baselineInfo,
            String aippType) {
        MetaDeclarationInfo declaration = new MetaDeclarationInfo();
        declaration.setCategory(Undefinable.defined(JaneCategory.AIPP.name()));
        declaration.setName(Undefinable.defined(aippDto.getName()));
        declaration.setVersion(Undefinable.defined(aippDto.getVersion()));
        declaration.putAttribute(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        declaration.putAttribute(AippConst.ATTR_PUBLISH_TIME_KEY, LocalDateTime.now().toString());
        declaration.putAttribute(AippConst.ATTR_DESCRIPTION_KEY, aippDto.getDescription());
        declaration.putAttribute(AippConst.ATTR_META_ICON_KEY, aippDto.getIcon());
        declaration.putAttribute(AippConst.ATTR_AIPP_TYPE_KEY, aippType);
        declaration.putAttribute(AippConst.ATTR_APP_ID_KEY, aippDto.getAppId());
        if (baselineInfo != null) {
            declaration.putAttribute(AippConst.ATTR_BASELINE_VERSION_KEY, baselineInfo.getVersion());
        }
        List<MetaPropertyDeclarationInfo> props = AippConst.STATIC_META_ITEMS.stream()
                .map(FormMetaConvertor.INSTANCE::toMetaPropertyDeclarationInfo)
                .collect(Collectors.toList());
        declaration.setProperties(Undefinable.defined(props));
        return declaration;
    }

    private String buildVersion(AppBuilderApp app, boolean isUpgrade) {
        // 当前只考虑升级，如果后续需要做基于应用创建新应用，则需要改动下面逻辑。
        if (!isUpgrade || !VersionUtils.isValidVersion(app.getVersion())) {
            return DEFAULT_APP_VERSION;
        }
        String[] parts = app.getVersion().split("\\.");
        parts[2] = String.valueOf(Integer.parseInt(parts[2]) + 1);
        return StringUtils.format(VERSION_FORMAT, parts[0], parts[1], parts[2]);
    }

    private void validateCreateApp(String name, OperationContext context) {
        this.validateAppName(name, context);
        AppQueryCondition queryCondition =
                AppQueryCondition.builder().tenantId(context.getTenantId()).name(name).build();
        if (!this.appRepository.selectWithCondition(queryCondition).isEmpty()) {
            log.error("Create aipp failed, [name={}, tenantId={}]", name, context.getTenantId());
            throw new AippException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
    }

    private void validateUpdateApp(String appId, String name, OperationContext context) {
        // 这个静态校验名称不能为空、不能超长
        this.validateAppName(name, context);

        // 如果该app已经发布过了，那么将不再允许修改名称
        List<Meta> metaList = this.getPublishedMetaList(appId, context);
        if (CollectionUtils.isNotEmpty(metaList) && !StringUtils.equals(metaList.get(0).getName(), name)) {
            throw new AippException(AippErrCode.APP_NAME_HAS_PUBLISHED);
        }

        // 到这里，要么是metaList是空的，要么就是没有改名
        // 如果metaList不是空的，证明没有改名，不管
        if (CollectionUtils.isNotEmpty(metaList)) {
            return;
        }
        // 如果mataList是空的，即没有发布过，那么名称可以修改为不和其它名称重复的名称
        AppQueryCondition queryCondition =
                AppQueryCondition.builder().tenantId(context.getTenantId()).name(name).build();
        List<AppBuilderApp> appBuilderApps = this.appRepository.selectWithCondition(queryCondition);
        if (appBuilderApps.isEmpty()) {
            return;
        }
        if (appBuilderApps.size() > 1 || !Objects.equals(appBuilderApps.get(0).getId(), appId)) {
            log.error("update aipp failed, [name={}, tenantId={}]", name, context.getTenantId());
            throw new AippException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
    }

    private void validateAppName(String name, OperationContext context) {
        if (!name.matches(this.appNameFormat)) {
            log.error("Create aipp failed: the name format is incorrect. [name={}]", name);
            throw new AippParamException(context, AippErrCode.APP_NAME_IS_INVALID);
        }
        String trimName = StringUtils.trim(name);
        if (StringUtils.isEmpty(trimName)) {
            log.error("Create aipp failed: name can not be empty.");
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_EMPTY);
        } else {
            if (name.length() > this.nameLengthMaximum) {
                log.error("Create aipp failed: the length of task name is out of bounds. [name={}]", name);
                throw new AippParamException(context, AippErrCode.AIPP_NAME_LENGTH_OUT_OF_BOUNDS);
            }
        }
    }

    private Map<String, Object> createAppAttributes(AppBuilderAppCreateDto dto, boolean isUpgrade, String preVersion) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("description", dto.getDescription());
        attributes.put("icon", dto.getIcon());
        attributes.put("greeting", dto.getGreeting());
        attributes.put("app_type", dto.getAppType());
        if (StringUtils.isNotBlank(dto.getStoreId())) {
            attributes.put("store_id", dto.getStoreId());
        }
        if (isUpgrade) {
            attributes.put("latest_version", preVersion);
        }
        return attributes;
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateApp(String appId, AppBuilderAppDto appDto, OperationContext context) {
        if (appDto == null) {
            throw new AippException(AippErrCode.INVALID_OPERATION);
        }

        // 这一行都是有校验的作用，不能挪到下面
        AppBuilderApp update = this.appFactory.create(appId);
        this.validateUpdateApp(appId, appDto.getName(), context);
        this.appUpdateValidator.validate(appId);
        update.setUpdateBy(context.getOperator());
        update.setUpdateAt(LocalDateTime.now());
        update.setName(appDto.getName());
        update.setType(appDto.getType());
        // 避免前端更新将app表的attributes覆盖了
        this.updateAttributes(update, appDto.getAttributes());
        update.setVersion(appDto.getVersion());
        if (StringUtils.isEmpty(update.getId())) {
            // 此时通过mapper没有查询到对应的app，需要创建新的app
            update.setId(Entities.generateId());
            update.setTenantId(context.getTenantId());
            // setConfig
            this.addConfigIntoApp(appDto.getConfig(), update);
            // setGraph
            this.addGraphIntoApp(appDto.getFlowGraph(), update);
            resetOperatorAndTime(update, LocalDateTime.now(), context.getOperator());
            this.saveNewAppBuilderApp(update);
            return Rsp.ok(this.buildFullAppDto(update));
        }
        this.appFactory.update(update);
        return Rsp.ok(this.buildFullAppDto(update));
    }

    private void updateAttributes(AppBuilderApp update, Map<String, Object> attributes) {
        Map<String, Object> attributesOld = update.getAttributes();
        attributesOld.putAll(attributes);
        attributesOld.put(AippConst.ATTR_APP_IS_UPDATE, true);
    }

    private void addGraphIntoApp(AppBuilderFlowGraphDto graphDto, AppBuilderApp app) {
        AppBuilderFlowGraph graph = AppBuilderFlowGraph.builder()
                .id(Entities.generateId())
                .name(graphDto.getName())
                .appearance(JSONObject.toJSONString(graphDto.getAppearance()))
                .build();
        Map<String, Object> appearance =
                JSONObject.parseObject(graph.getAppearance(), new TypeReference<Map<String, Object>>() {});
        appearance.computeIfPresent("id", (key, value) -> graph.getId());
        graph.setAppearance(JSONObject.toJSONString(appearance));
        app.setFlowGraph(graph);
        app.setFlowGraphId(graph.getId());
    }

    private void addConfigIntoApp(AppBuilderConfigDto configDto, AppBuilderApp app) {
        AppBuilderConfig config = AppBuilderConfig.builder()
                .configPropertyRepository(app.getConfigPropertyRepository())
                .formRepository(app.getFormRepository())
                .formPropertyRepository(app.getFormPropertyRepository())
                .build();
        config.setApp(app);
        config.setAppId(app.getId());
        config.setId(Entities.generateId());
        config.setTenantId(app.getTenantId());
        config.setConfigProperties(new ArrayList<>());

        AppBuilderConfigFormDto formDto = configDto.getForm();
        AppBuilderForm form = AppBuilderForm.builder().formPropertyRepository(app.getFormPropertyRepository()).build();
        form.setId(Entities.generateId());
        form.setName(formDto.getName());
        form.setAppearance(formDto.getAppearance());
        form.setTenantId(app.getTenantId());
        form.setFormProperties(new ArrayList<>());
        config.setForm(form);
        config.setFormId(form.getId());
        this.addProperties(formDto, form, config);
        app.setConfigId(config.getId());
        app.setConfig(config);
    }

    private void addProperties(AppBuilderConfigFormDto formDto, AppBuilderForm form, AppBuilderConfig config) {
        for (AppBuilderConfigFormPropertyDto propertyDto : formDto.getProperties()) {
            AppBuilderFormProperty formProperty = AppBuilderFormProperty.builder()
                    .formId(form.getId())
                    .dataType(propertyDto.getDataType())
                    .defaultValue(propertyDto.getDefaultValue())
                    .name(propertyDto.getName())
                    .id(Entities.generateId())
                    .form(form)
                    .build();
            form.getFormProperties().add(formProperty);
            AppBuilderConfigProperty configProperty = AppBuilderConfigProperty.builder()
                    .configId(config.getId())
                    .config(config)
                    .formPropertyId(formProperty.getId())
                    .formProperty(formProperty)
                    .nodeId(propertyDto.getNodeId())
                    .id(Entities.generateId())
                    .build();
            config.getConfigProperties().add(configProperty);
        }
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateConfig(String appId, AppBuilderConfigDto configDto, OperationContext context) {
        this.appUpdateValidator.validate(appId);
        LocalDateTime operateTime = LocalDateTime.now();
        AppBuilderApp oldApp = this.appFactory.create(appId);
        AppBuilderConfig oldConfig = oldApp.getConfig();
        AppBuilderFlowGraph oldFlowGraph = oldApp.getFlowGraph();

        // 先更新config
        this.updateConfigPropertiesByAppBuilderConfigDto(configDto, oldConfig);
        this.updateConfigAndForm(configDto, context, oldConfig, operateTime, oldApp);
        // 然后同步更新flowGraph
        oldFlowGraph.setUpdateBy(context.getOperator());
        oldFlowGraph.setUpdateAt(operateTime);
        oldFlowGraph.setAppearance(this.updateFlowGraphAppearanceByConfigDto(oldFlowGraph.getAppearance(), configDto));
        oldApp.getFlowGraphRepository().updateOne(oldFlowGraph);
        // 最后更新app主表
        oldApp.setUpdateAt(operateTime);
        oldApp.setUpdateBy(context.getOperator());
        this.updateAttributes(oldApp, new HashMap<>());
        this.appFactory.update(oldApp);
        return Rsp.ok(this.buildFullAppDto(oldApp));
    }

    private void updateConfigAndForm(AppBuilderConfigDto configDto, OperationContext context,
            AppBuilderConfig oldConfig, LocalDateTime operateTime, AppBuilderApp oldApp) {
        oldConfig.setUpdateBy(context.getOperator());
        oldConfig.setUpdateAt(operateTime);
        oldApp.getConfigRepository().updateOne(oldConfig);
        oldConfig.getForm().setUpdateBy(context.getOperator());
        oldConfig.getForm().setUpdateAt(operateTime);
        oldConfig.getForm().setName(configDto.getForm().getName());
        oldConfig.getForm().setAppearance(configDto.getForm().getAppearance());
        oldApp.getFormRepository().updateOne(oldConfig.getForm());
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateFlowGraph(String appId, AppBuilderFlowGraphDto graphDto,
            OperationContext context) {
        this.appUpdateValidator.validate(appId);
        LocalDateTime operateTime = LocalDateTime.now();

        AppBuilderApp oldApp = this.appFactory.create(appId);
        // 优先更新graph本身
        AppBuilderFlowGraph oldAppFlowGraph = oldApp.getFlowGraph();
        oldAppFlowGraph.setUpdateAt(operateTime);
        oldAppFlowGraph.setUpdateBy(context.getOperator());
        oldAppFlowGraph.setName(graphDto.getName());
        oldAppFlowGraph.setAppearance(JSONObject.toJSONString(graphDto.getAppearance()));
        oldApp.getFlowGraphRepository().updateOne(oldAppFlowGraph);
        // 根据graph更新config
        String appearance = oldAppFlowGraph.getAppearance();
        AppBuilderConfig oldConfig = oldApp.getConfig();
        this.updateConfigByGlowGraphAppearance(appearance, oldConfig); // 这个方法是在更新properties
        oldConfig.setUpdateAt(operateTime);
        oldConfig.setUpdateBy(context.getOperator());
        oldApp.getConfigRepository().updateOne(oldConfig);
        // 最后更新app主表
        oldApp.setUpdateAt(operateTime);
        oldApp.setUpdateBy(context.getOperator());
        this.updateAttributes(oldApp, new HashMap<>());
        this.appFactory.update(oldApp);
        return Rsp.ok(this.buildFullAppDto(oldApp));
    }

    @Override
    @Fitable(id = "default")
    public void delete(String appId, OperationContext context) {
        this.validateApp(appId);
        MetaFilter filter = new MetaFilter();
        String metaId = this.getAippIdByAppId(appId, context);
        filter.setMetaIds(Collections.singletonList(metaId));
        List<Meta> metas = MetaUtils.getListMetaHandle(this.metaService, filter, context);
        if (CollectionUtils.isEmpty(metas)) {
            return;
        }
        List<String> metaVersionIds = metas.stream().map(Meta::getVersionId).distinct().collect(Collectors.toList());
        List<String> metaIds = metas.stream().map(Meta::getId).distinct().collect(Collectors.toList());
        List<Tuple> versionIdInstances = this.getVersionIdInstanceIds(metaVersionIds, context);
        Set<String> instanceIds = this.getInstanceIds(versionIdInstances);
        List<String> appIds = this.getFullAppIds(metas);
        this.deleteApps(appIds);
        this.deleteMetaInstances(versionIdInstances, context);
        this.deleteMetas(metaVersionIds, context);
        this.deleteFiles(metaIds);
        this.deleteLogs(instanceIds);
        this.deleteFlows(metas, context);
        this.deleteStore(metas);
        this.deleteChats(appIds, context);
        this.deleteUsrAppCollection(appId);
    }

    private void deleteChats(List<String> appIds, OperationContext context) {
        appIds.forEach(appId -> this.aippChatService.deleteChat(null, appId, context));
    }

    private void deleteUsrAppCollection(String appId) {
        this.usrAppCollectionService.deleteByAppId(appId);
    }

    private void deleteStore(List<Meta> metas) {
        List<String> uniqueNames = metas.stream()
                .filter(meta -> meta != null && meta.getAttributes().containsKey(ATTR_UNIQUE_NAME))
                .map(meta -> ObjectUtils.<String>cast(meta.getAttributes().get(ATTR_UNIQUE_NAME)))
                .distinct()
                .collect(Collectors.toList());
        uniqueNames.forEach(this.appService::deleteApp);
    }

    private void deleteFlows(List<Meta> metas, OperationContext context) {
        metas.forEach(meta -> this.flowsService.deleteFlowsWithoutElsa(meta.getId(), meta.getVersion(), context));
    }

    private void deleteLogs(Set<String> instanceIds) {
        if (instanceIds.isEmpty()) {
            return;
        }
        this.aippLogMapper.deleteByInstanceIds(new ArrayList<>(instanceIds));
    }

    private Set<String> getInstanceIds(List<Tuple> versionIdInstances) {
        Set<String> instanceIds = new HashSet<>();
        for (Tuple versionIdInstance : versionIdInstances) {
            List<Instance> instances = ObjectUtils.cast(versionIdInstance.get(1)
                    .orElseThrow(() -> new AippException(AippErrCode.DELETE_ERROR)));
            if (CollectionUtils.isEmpty(instances)) {
                continue;
            }
            instanceIds.addAll(instances.stream().map(Instance::getId).distinct().collect(Collectors.toList()));
        }
        return instanceIds;
    }

    private List<Tuple> getVersionIdInstanceIds(List<String> metaVersionIds, OperationContext context) {
        return metaVersionIds.stream()
                .map(versionId -> Tuple.duet(versionId,
                        MetaInstanceUtils.getInstances(versionId, this.metaInstanceService, context)))
                .collect(Collectors.toList());
    }

    private void deleteFiles(List<String> metaIds) {
        if (CollectionUtils.isEmpty(metaIds)) {
            return;
        }
        this.aippUploadedFileMapper.deleteByAippIds(metaIds);
    }

    private void deleteApps(List<String> appIds) {
        this.appFactory.delete(appIds);
    }

    private List<String> getFullAppIds(List<Meta> metas) {
        return metas.stream()
                .filter(meta -> meta != null && meta.getAttributes().containsKey(AippConst.ATTR_APP_ID_KEY))
                .map(meta -> ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY)))
                .distinct()
                .collect(Collectors.toList());
    }

    private void deleteMetaInstances(List<Tuple> versionIdInstances, OperationContext context) {
        versionIdInstances.forEach(versionIdInstance -> this.deleteMetaInstance(context, versionIdInstance));
    }

    private void deleteMetaInstance(OperationContext context, Tuple versionIdInstance) {
        String versionId = ObjectUtils.cast(versionIdInstance.get(0)
                .orElseThrow(() -> new AippException(AippErrCode.DELETE_ERROR)));
        List<Instance> instances = ObjectUtils.cast(versionIdInstance.get(1)
                .orElseThrow(() -> new AippException(AippErrCode.DELETE_ERROR)));
        if (CollectionUtils.isEmpty(instances)) {
            return;
        }
        instances.forEach(instance -> this.metaInstanceService.deleteMetaInstance(versionId,
                instance.getId(),
                context));
    }

    private void deleteMetas(List<String> metaVersionIds, OperationContext context) {
        metaVersionIds.forEach(versionId -> this.metaService.delete(versionId, context));
    }

    @Override
    public PublishedAppResDto published(String uniqueName, OperationContext context) {
        MetaFilter filter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(ATTR_UNIQUE_NAME, Collections.singletonList(uniqueName));
        filter.setAttributes(attributes);
        RangedResultSet<Meta> metas = this.metaService.list(filter, true, 0, 1, context);
        if (metas.getResults().isEmpty()) {
            log.error("Meta can not be null.");
            throw new AippParamException(TASK_NOT_FOUND);
        }
        Meta meta = metas.getResults().get(0);
        String appId = String.valueOf(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        String publishedDescription = String.valueOf(meta.getAttributes().get(AippConst.ATTR_PUBLISH_DESCRIPTION));
        String publishedUpdateLog = String.valueOf(meta.getAttributes().get(AippConst.ATTR_PUBLISH_UPDATE_LOG));
        return PublishedAppResDto.builder()
                .appId(appId)
                .appVersion(meta.getVersion())
                .publishedAt(meta.getCreationTime())
                .publishedBy(meta.getCreator())
                .publishedDescription(publishedDescription)
                .publishedUpdateLog(publishedUpdateLog)
                .build();
    }

    @Override
    public List<PublishedAppResDto> recentPublished(AppQueryCondition cond, long offset, int limit, String appId,
            OperationContext context) {
        this.validateApp(appId);
        String aippId = this.getAippIdByAppId(appId, context);
        List<Meta> allPublishedMeta = MetaUtils.getAllPublishedMeta(this.metaService, aippId, context)
                .stream()
                .filter(meta -> !this.isAppBelong(appId, meta))
                .collect(Collectors.toList());
        List<String> appIds = allPublishedMeta.stream()
                .map(meta -> String.valueOf(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY)))
                .collect(Collectors.toList());
        cond.setIds(appIds);
        cond.setTenantId(context.getTenantId());
        List<AppBuilderApp> allPublishedApp = this.appRepository.selectWithCondition(cond);
        Map<String, AppBuilderApp> appIdKeyAppValueMap =
                allPublishedApp.stream().collect(Collectors.toMap(AppBuilderApp::getId, Function.identity()));
        return this.buildPublishedAppResDtos(allPublishedMeta, appIdKeyAppValueMap);
    }

    private String getAippIdByAppId(String appId, OperationContext context) {
        List<Meta> metas = MetaUtils.getAllMetasByAppId(this.metaService, appId, context);
        if (CollectionUtils.isEmpty(metas)) {
            log.error("Meta can not be null.");
            throw new AippParamException(TASK_NOT_FOUND);
        }
        return metas.get(0).getId();
    }

    private boolean isAppBelong(String appId, Meta meta) {
        return Objects.equals(String.valueOf(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY)), appId);
    }

    private List<PublishedAppResDto> buildPublishedAppResDtos(List<Meta> metas,
            Map<String, AppBuilderApp> appIdKeyAppValueMap) {
        return metas.stream()
                .map(meta -> this.buildPublishedAppResDto(meta, appIdKeyAppValueMap))
                .collect(Collectors.toList());
    }

    private PublishedAppResDto buildPublishedAppResDto(Meta meta, Map<String, AppBuilderApp> appIdKeyAppValueMap) {
        String appId = String.valueOf(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        String publishedDescription = String.valueOf(meta.getAttributes().get(AippConst.ATTR_PUBLISH_DESCRIPTION));
        String publishedUpdateLog = String.valueOf(meta.getAttributes().get(AippConst.ATTR_PUBLISH_UPDATE_LOG));
        AppBuilderApp app = appIdKeyAppValueMap.get(appId);
        return PublishedAppResDto.builder()
                .appId(appId)
                .appVersion(app.getVersion())
                .publishedAt(meta.getCreationTime())
                .publishedBy(meta.getCreator())
                .publishedDescription(publishedDescription)
                .publishedUpdateLog(publishedUpdateLog)
                .build();
    }

    private static AppBuilderConfig resetConfig(AppBuilderConfig config) {
        AppBuilderForm form = config.getForm();
        // 这里先根据旧的formId查询得到formProperties，然后设置新的formId
        Map<String, AppBuilderFormProperty> idToFormPropertyMap = form.getFormProperties()
                .stream()
                .collect(Collectors.toMap(AppBuilderFormProperty::getId, Function.identity()));
        form.setId(Entities.generateId());
        // 先根据旧的configId查询得到configProperties
        List<AppBuilderConfigProperty> configProperties = config.getConfigProperties();
        config.setId(Entities.generateId());
        configProperties.forEach(configProperty -> resetIdToConfigAndFormProperty(configProperty,
                idToFormPropertyMap,
                form.getId(),
                config.getId()));
        config.setFormId(form.getId());
        return config;
    }

    private static void resetIdToConfigAndFormProperty(AppBuilderConfigProperty configProperty,
            Map<String, AppBuilderFormProperty> idToFormPropertyMap, String formId, String configId) {
        configProperty.setId(Entities.generateId());
        configProperty.setConfigId(configId);
        AppBuilderFormProperty formProperty = idToFormPropertyMap.get(configProperty.getFormPropertyId());
        formProperty.setId(Entities.generateId());
        formProperty.setFormId(formId);
        configProperty.setFormPropertyId(formProperty.getId());
    }

    private static void resetOperatorAndTime(AppBuilderApp app, LocalDateTime time, String operator) {
        app.setCreateBy(operator);
        app.setCreateAt(time);
        app.setUpdateBy(operator);
        app.setUpdateAt(time);
        AppBuilderConfig config = app.getConfig();
        config.setCreateBy(operator);
        config.setCreateAt(time);
        config.setUpdateBy(operator);
        config.setUpdateAt(time);
        AppBuilderFlowGraph flowGraph = app.getFlowGraph();
        flowGraph.setCreateBy(operator);
        flowGraph.setCreateAt(time);
        flowGraph.setUpdateBy(operator);
        flowGraph.setUpdateAt(time);
        AppBuilderForm form = config.getForm();
        form.setCreateBy(operator);
        form.setCreateAt(time);
        form.setUpdateBy(operator);
        form.setUpdateAt(time);
    }

    private void saveNewAppBuilderApp(AppBuilderApp appBuilderApp) {
        // 保存app
        this.appFactory.save(appBuilderApp);
        appBuilderApp.getConfigRepository().insertOne(appBuilderApp.getConfig());
        appBuilderApp.getFlowGraphRepository().insertOne(appBuilderApp.getFlowGraph());
        appBuilderApp.getConfigPropertyRepository().insertMore(appBuilderApp.getConfig().getConfigProperties());
        appBuilderApp.getFormRepository().insertOne(appBuilderApp.getConfig().getForm());
        appBuilderApp.getFormPropertyRepository().insertMore(appBuilderApp.getConfig().getForm().getFormProperties());
    }

    private AppBuilderAppDto buildFullAppDto(AppBuilderApp app) {
        return AppBuilderAppDto.builder()
                .id(app.getId())
                .name(app.getName())
                .type(app.getType())
                .state(app.getState())
                .attributes(app.getAttributes())
                .version(app.getVersion())
                .createBy(app.getCreateBy())
                .updateBy(app.getUpdateBy())
                .createAt(app.getCreateAt())
                .updateAt(app.getUpdateAt())
                .config(this.buildAppBuilderConfig(app.getConfig()))
                .flowGraph(this.buildFlowGraph(app.getFlowGraph()))
                .build();
    }

    private AppBuilderFlowGraphDto buildFlowGraph(AppBuilderFlowGraph flowGraph) {
        return AppBuilderFlowGraphDto.builder()
                .id(flowGraph.getId())
                .name(flowGraph.getName())
                .appearance(JsonUtils.parseObject(flowGraph.getAppearance()))
                .createBy(flowGraph.getCreateBy())
                .updateBy(flowGraph.getUpdateBy())
                .createAt(flowGraph.getCreateAt())
                .updateAt(flowGraph.getUpdateAt())
                .build();
    }

    private AppBuilderConfigDto buildAppBuilderConfig(AppBuilderConfig config) {
        return AppBuilderConfigDto.builder()
                .id(config.getId())
                .tenantId(config.getTenantId())
                .createBy(config.getCreateBy())
                .updateBy(config.getUpdateBy())
                .createAt(config.getCreateAt())
                .updateAt(config.getUpdateAt())
                .form(this.buildAppBuilderConfigFormDto(config))
                .build();
    }

    private AppBuilderConfigFormDto buildAppBuilderConfigFormDto(AppBuilderConfig config) {
        Validation.notNull(config.getForm(), "Form can not be null.");
        return AppBuilderConfigFormDto.builder()
                .id(config.getFormId())
                .name(config.getForm().getName())
                .appearance(config.getForm().getAppearance())
                .properties(this.buildAppBuilderConfigFormProperties(config))
                .build();
    }

    private List<AppBuilderConfigFormPropertyDto> buildAppBuilderConfigFormProperties(AppBuilderConfig config) {
        List<AppBuilderConfigProperty> configProperties = config.getConfigProperties();
        AppBuilderForm form = config.getForm();
        Map<String, AppBuilderFormProperty> formPropertyMapping = form.getFormProperties()
                .stream()
                .collect(Collectors.toMap(AppBuilderFormProperty::getId, Function.identity()));
        return configProperties.stream().map(property -> {
            String formPropertyId = property.getFormPropertyId();
            AppBuilderFormProperty formProperty = formPropertyMapping.get(formPropertyId);
            return AppBuilderConfigFormPropertyDto.builder()
                    .id(formPropertyId)
                    .name(formProperty.getName())
                    .dataType(formProperty.getDataType())
                    .defaultValue(formProperty.getDefaultValue())
                    .nodeId(property.getNodeId())
                    .build();
        }).collect(Collectors.toList());
    }

    private void updateConfigPropertiesByAppBuilderConfigDto(AppBuilderConfigDto dto, AppBuilderConfig config) {
        AppBuilderConfigFormDto form = dto.getForm();
        List<AppBuilderConfigFormPropertyDto> properties = form.getProperties();
        Map<String, AppBuilderConfigFormPropertyDto> idToPropertyDtoMap = properties.stream()
                .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getId, Function.identity()));

        List<AppBuilderConfigProperty> configProperties = config.getConfigProperties(); // 这个对象里全是id，所以是不会改动的
        List<AppBuilderFormProperty> formProperties = config.getForm().getFormProperties(); // name、dataType、value

        Set<String> formPropertyIds =
                formProperties.stream().map(AppBuilderFormProperty::getId).collect(Collectors.toSet());

        // 删除
        this.deleteProperties(config, configProperties, idToPropertyDtoMap, formProperties);

        // 新增
        this.addProperties(config, properties, formPropertyIds);

        // 修改, 待修改的内容, 循环修改
        formProperties.stream()
                .filter(formProperty -> idToPropertyDtoMap.containsKey(formProperty.getId()))
                .forEach(formProperty -> {
                    AppBuilderConfigFormPropertyDto propertyDto = idToPropertyDtoMap.get(formProperty.getId());
                    formProperty.setName(propertyDto.getName());
                    formProperty.setDataType(propertyDto.getDataType());
                    formProperty.setDefaultValue(propertyDto.getDefaultValue());
                    config.getForm().getFormPropertyRepository().updateOne(formProperty);
                });
    }

    private void addProperties(AppBuilderConfig config, List<AppBuilderConfigFormPropertyDto> properties,
            Set<String> formPropertyIds) {
        List<AppBuilderConfigProperty> toAddConfigProperties = properties.stream()
                .filter(pd -> StringUtils.isBlank(pd.getId()) || !formPropertyIds.contains(pd.getId()))
                .map(propertyDto -> {
                    AppBuilderFormProperty formProperty = AppBuilderFormProperty.builder()
                            .formId(config.getFormId())
                            .name(propertyDto.getName())
                            .dataType(propertyDto.getDataType())
                            .defaultValue(propertyDto.getDefaultValue())
                            .id(Entities.generateId())
                            .build();
                    return AppBuilderConfigProperty.builder()
                            .id(Entities.generateId())
                            .configId(config.getId())
                            .nodeId(propertyDto.getNodeId())
                            .formPropertyId(formProperty.getId())
                            .formProperty(formProperty)
                            .build();
                })
                .collect(Collectors.toList());
        List<AppBuilderFormProperty> toAddFormProperties = toAddConfigProperties.stream()
                .map(AppBuilderConfigProperty::getFormProperty)
                .collect(Collectors.toList());

        config.getConfigPropertyRepository().insertMore(toAddConfigProperties);
        config.getForm().getFormPropertyRepository().insertMore(toAddFormProperties);
    }

    private void deleteProperties(AppBuilderConfig config, List<AppBuilderConfigProperty> configProperties,
            Map<String, AppBuilderConfigFormPropertyDto> idToPropertyDtoMap,
            List<AppBuilderFormProperty> formProperties) {
        List<String> toDeleteConfigPropertyIds = configProperties.stream()
                .filter(cp -> !idToPropertyDtoMap.containsKey(cp.getFormPropertyId()))
                .map(AppBuilderConfigProperty::getId)
                .collect(Collectors.toList());
        List<String> toDeleteFormPropertyIds = formProperties.stream()
                .map(AppBuilderFormProperty::getId)
                .filter(id -> !idToPropertyDtoMap.containsKey(id))
                .collect(Collectors.toList());
        config.getConfigPropertyRepository().deleteMore(toDeleteConfigPropertyIds);
        config.getForm().getFormPropertyRepository().deleteMore(toDeleteFormPropertyIds);
    }

    private String updateFlowGraphAppearanceByConfigDto(String oldAppearance, AppBuilderConfigDto dto) {
        // 将dto的properties转成 {nodeId : {name:value, name:value},  ... }形式
        Map<String, Map<String, String>> nodeIdToPropertyNameValueMap = dto.getForm()
                .getProperties()
                .stream()
                .filter(fp -> StringUtils.isNotBlank(fp.getNodeId()))
                .collect(Collectors.groupingBy(AppBuilderConfigFormPropertyDto::getNodeId))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getName,
                                        appBuilderConfigFormPropertyDto -> JsonUtils.toJsonString(
                                                appBuilderConfigFormPropertyDto.getDefaultValue())))));
        JSONObject oldAppearanceObject = JSONObject.parseObject(oldAppearance);
        JSONObject page = ObjectUtils.<JSONObject>cast(oldAppearanceObject.getJSONArray("pages").get(0));
        JSONArray shapes = page.getJSONArray("shapes");

        for (int j = 0; j < shapes.size(); j++) {
            JSONObject node = shapes.getJSONObject(j);
            String id = node.getString("id");
            String type = node.getString("type");
            if (!StringUtils.equals(type, "startNodeStart") && !type.endsWith("NodeState")) {
                continue;
            }

            Map<String, String> nameValue = nodeIdToPropertyNameValueMap.get(id);

            String flowMetaString = node.get("flowMeta").toString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode flowMeta = null;
            try {
                flowMeta = mapper.readTree(flowMetaString);
                JsonNode params = flowMeta.findPath("inputParams");
                for (int i = 0; i < params.size(); i++) {
                    JsonNode child = params.get(i);
                    processParam(child, nameValue);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Object tt = JSON.parse(flowMeta.toString());
            node.put("flowMeta", tt);
        }

        return JSONObject.toJSONString(oldAppearanceObject);
    }

    private void processParam(JsonNode node, Map<String, String> params) {
        List<String> singleLayerParams = new ArrayList<>(Arrays.asList("model", "temperature", "systemPrompt"));
        List<String> doubleLayerParams = new ArrayList<>(Arrays.asList("tools", "workflows"));
        if (params == null) {
            return;
        }
        for (Map.Entry<String, String> param : params.entrySet()) {
            handleParam(node, param, singleLayerParams, doubleLayerParams);
        }
    }

    private void handleParam(JsonNode node, Map.Entry<String, String> param, List<String> singleLayerParams,
            List<String> doubleLayerParams) {
        if (StringUtils.equals(node.get("name").asText(), param.getKey())) {
            if (singleLayerParams.contains(param.getKey())) {
                this.handleParamTemperature(node, param);
                return;
            }

            if (doubleLayerParams.contains(param.getKey())) {
                ArrayNode valueArrayNode = convertList(param.getValue());
                ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
                return;
            }

            if (StringUtils.equals("knowledge", param.getKey())) {
                this.handleParamKnowledge(node, param);
                return;
            }

            if (StringUtils.equals("memory", param.getKey())) {
                JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
                ArrayNode valueArrayNode = nodeFactory.arrayNode();
                Map<String, Object> res = JsonUtils.parseObject(param.getValue(), Map.class);
                if (Objects.equals(res.get("type"), "UserSelect")) {
                    this.parseUserSelect(res, valueArrayNode);
                } else {
                    this.parseOtherMemoryType(res, valueArrayNode);
                }
                ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
            }
        }
    }

    private void handleParamTemperature(JsonNode node, Map.Entry<String, String> param) {
        if (StringUtils.equals(param.getKey(), "temperature")) {
            ObjectUtils.<ObjectNode>cast(node).put(
                    "value", JsonUtils.parseObject(param.getValue(), Float.class));
        } else {
            ObjectUtils.<ObjectNode>cast(node).put(
                    "value", JsonUtils.parseObject(param.getValue(), String.class));
        }
    }

    private void handleParamKnowledge(JsonNode node, Map.Entry<String, String> param) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ArrayNode valueArrayNode = nodeFactory.arrayNode();
        List<Map<String, Object>> res = ObjectUtils.<List<Map<String, Object>>>cast(
                JsonUtils.parseObject(param.getValue(), List.class));
        res.forEach(r -> {
            ArrayNode valueArrayNode1 = nodeFactory.arrayNode();
            for (Map.Entry<String, Object> rr : r.entrySet()) {
                if (StringUtils.equals(rr.getKey(), "id")) {
                    valueArrayNode1.add(convertId(rr.getKey(),
                            ObjectUtils.<Integer>cast(rr.getValue()).longValue()));
                } else {
                    valueArrayNode1.add(convertObject(rr.getKey(), String.valueOf(rr.getValue())));
                }
            }
            Map<String, Object> a = new HashMap<>();
            a.put("id", UUID.randomUUID().toString());
            a.put("type", "Object");
            a.put("from", "Expand");
            a.put("value", valueArrayNode1);
            ObjectNode mapNode = nodeFactory.objectNode();
            for (Map.Entry<String, Object> entry : a.entrySet()) {
                if (StringUtils.equals(entry.getKey(), "value")) {
                    mapNode.put(entry.getKey(), ObjectUtils.<JsonNode>cast(entry.getValue()));
                } else {
                    mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
                }
            }
            valueArrayNode.add(mapNode);
        });
        ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
    }

    private void parseOtherMemoryType(Map<String, Object> res, ArrayNode valueArrayNode) {
        for (Map.Entry<String, Object> resEntry : res.entrySet()) {
            if (Objects.equals(resEntry.getKey(), AippConst.MEMORY_SWITCH_KEY)) {
                this.checkEntryType(resEntry, Boolean.class);
                valueArrayNode.add(this.convertMemorySwitch(resEntry.getKey(), ObjectUtils.cast(resEntry.getValue())));
            } else {
                valueArrayNode.add(this.convertObject(resEntry.getKey(), String.valueOf(resEntry.getValue())));
            }
        }
    }

    private void parseUserSelect(Map<String, Object> res, ArrayNode valueArrayNode) {
        for (Map.Entry<String, Object> resEntry : res.entrySet()) {
            if (Objects.equals(resEntry.getKey(), AippConst.MEMORY_SWITCH_KEY)) {
                this.checkEntryType(resEntry, Boolean.class);
                valueArrayNode.add(this.convertMemorySwitch(resEntry.getKey(),
                        ObjectUtils.cast(resEntry.getValue())));
            } else if (Objects.equals(resEntry.getKey(), "value")) {
                valueArrayNode.add(this.convertValueForUserSelect(resEntry.getKey(),
                        String.valueOf(resEntry.getValue())));
            } else {
                valueArrayNode.add(this.convertObject(resEntry.getKey(), String.valueOf(resEntry.getValue())));
            }
        }
    }

    private ArrayNode convertList(String value) {
        String[] res = JsonUtils.parseObject(value, String[].class);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        List<Map<String, String>> re = Arrays.stream(res).map(this::convert).collect(Collectors.toList());

        ArrayNode valueArrayNode = nodeFactory.arrayNode();
        for (Map<String, String> rr : re) {
            ObjectNode mapNode = nodeFactory.objectNode();
            for (Map.Entry<String, String> entry : rr.entrySet()) {
                mapNode.put(entry.getKey(), entry.getValue());
            }
            valueArrayNode.add(mapNode);
        }
        return valueArrayNode;
    }

    private Map<String, String> convert(String value) {
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        return map;
    }

    private ObjectNode convertObject(String key, String value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapNode.put(entry.getKey(), entry.getValue());
        }
        return mapNode;
    }

    private ObjectNode convertId(String key, Long value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getKey(), "value")) {
                mapNode.put(entry.getKey(), ObjectUtils.<Long>cast(entry.getValue()));
            } else {
                mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
            }
        }
        return mapNode;
    }

    private ObjectNode convertMemorySwitch(String key, Boolean isOpenSwitch) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "Input");
        map.put("type", "Boolean");
        map.put("value", isOpenSwitch);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getKey(), "value")) {
                this.checkEntryType(entry, Boolean.class);
                mapNode.put(entry.getKey(), ObjectUtils.<Boolean>cast(entry.getValue()));
            } else {
                this.checkEntryType(entry, String.class);
                mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
            }
        }
        return mapNode;
    }

    private ObjectNode convertValueForUserSelect(String key, String value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", StringUtils.EMPTY);
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapNode.put(entry.getKey(), entry.getValue());
        }
        return mapNode;
    }

    private void checkEntryType(Map.Entry<String, Object> entry, Class<?> clazz) {
        if (!clazz.isInstance(entry.getValue())) {
            throw new AippException(AippErrCode.DATA_TYPE_IS_NOT_SUPPORTED, entry.getValue().getClass().getName());
        }
    }

    private void updateConfigByGlowGraphAppearance(String appearance, AppBuilderConfig config) {
        // 这个map {nodeId:{name:value}}
        Map<String, Map<String, Object>> nodeIdToJadeConfigMap = this.getJadeConfigsFromAppearance(appearance);
        List<AppBuilderConfigProperty> configProperties = config.getConfigProperties();
        List<AppBuilderFormProperty> formProperties = config.getForm().getFormProperties();
        Map<String, AppBuilderFormProperty> idToFormPropertyMap =
                formProperties.stream().collect(Collectors.toMap(AppBuilderFormProperty::getId, Function.identity()));
        // 这样写避免循环的时候去查询数据库获取configProperty对应的formProperty
        List<AppBuilderConfigProperty> toDelete = new ArrayList<>();
        for (AppBuilderConfigProperty cp : configProperties) {
            if (!idToFormPropertyMap.containsKey(cp.getFormPropertyId())) {
                // 2024/4/29 0029 这里可能拿到null，这里暂时不知道什么问题，先把拿不到的跳过
                continue;
            }
            cp.setFormProperty(idToFormPropertyMap.get(cp.getFormPropertyId()));
            String nodeId = cp.getNodeId();
            if (StringUtils.isBlank(nodeId)) {
                // 这里排除掉空nodeId的config
                continue;
            }
            Map<String, Object> nameValue = nodeIdToJadeConfigMap.get(nodeId);
            AppBuilderFormProperty formProperty = cp.getFormProperty();
            if (MapUtils.isEmpty(nameValue)) {
                // 2024/4/29 0029 暂时先不删除了，仅修改现存的内容
                continue;
            } else {
                if (nameValue.get(formProperty.getName()) == null) {
                    continue;
                }
            }
            formProperty.setDefaultValue(nameValue.get(formProperty.getName()));
            // 更新
            config.getFormPropertyRepository().updateOne(formProperty);
        }
    }

    private Map<String, Map<String, Object>> getJadeConfigsFromAppearance(String appearance) {
        JSONArray pages = JSONObject.parseObject(appearance).getJSONArray("pages");
        // 这个map {nodeId:{name:value}}
        Map<String, Map<String, Object>> nodeIdToJadeConfigMap = new HashMap<>();
        for (int i = 0; i < pages.size(); i++) {
            JSONObject page = pages.getJSONObject(i);
            JSONArray shapes = page.getJSONArray("shapes");
            for (int j = 0; j < shapes.size(); j++) {
                JSONObject node = shapes.getJSONObject(j);
                String nodeId = node.getString("id");
                JSONArray inputParams = this.extractingInputParams(node);
                if (Objects.isNull(inputParams)) {
                    continue;
                }
                nodeIdToJadeConfigMap.put(nodeId, this.extractingExpandObject(inputParams));
            }
        }
        return nodeIdToJadeConfigMap;
    }

    private JSONArray extractingInputParams(JSONObject node) {
        String nodeType = node.getString("type");
        if (StringUtils.equalsIgnoreCase("startNodeStart", nodeType)) {
            return node.getJSONObject("flowMeta").getJSONArray("inputParams");
        } else if (StringUtils.equalsIgnoreCase("endNodeEnd", nodeType)) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("jadeEvent", nodeType)) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("conditionNodeCondition", nodeType)) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("manualCheckNodeState", nodeType)) {
            return node.getJSONObject("flowMeta")
                    .getJSONObject("task")
                    .getJSONObject("converter")
                    .getJSONObject("entity")
                    .getJSONArray("inputParams");
        } else {
            return node.getJSONObject("flowMeta")
                    .getJSONObject("jober")
                    .getJSONObject("converter")
                    .getJSONObject("entity")
                    .getJSONArray("inputParams");
        }
    }

    // 如果type是Array，那么调用这个方法获取一个List<Object>
    private List<Object> extractingExpandArray(JSONArray value) {
        List<Object> result = new ArrayList<>();
        for (int index = 0; index < value.size(); index++) {
            JSONObject jsonObject = value.getJSONObject(index);
            if (StringUtils.equalsIgnoreCase("Input", jsonObject.getString("from"))) {
                result.add(jsonObject.get("value"));
                continue;
            }
            if (StringUtils.equalsIgnoreCase("Expand", jsonObject.getString("from"))) {
                this.handleExpandType(jsonObject, result);
            }
        }
        return result;
    }

    private void handleExpandType(JSONObject jsonObject, List<Object> result) {
        if (StringUtils.equalsIgnoreCase("Array", jsonObject.getString("type"))) {
            List<Object> array = this.extractingExpandArray(jsonObject.getJSONArray("value"));
            result.add(array);
            return;
        }
        if (StringUtils.equalsIgnoreCase("Object", jsonObject.getString("type"))) {
            Map<String, Object> map = this.extractingExpandObject(jsonObject.getJSONArray("value"));
            if (MapUtils.isNotEmpty(map)) {
                result.add(map);
            }
        }
    }

    // 如果type是Object，那么调用这个方法获取一个Map<String, Object>
    private Map<String, Object> extractingExpandObject(JSONArray value) {
        Map<String, Object> result = new HashMap<>();
        for (int index = 0; index < value.size(); index++) {
            JSONObject jsonObject = value.getJSONObject(index);
            if (StringUtils.equalsIgnoreCase("Input", jsonObject.getString("from"))) {
                result.put(jsonObject.getString("name"), jsonObject.get("value"));
                continue;
            }
            if (StringUtils.equalsIgnoreCase("Expand", jsonObject.getString("from"))) {
                this.handleExpandType(jsonObject, result);
            }
        }
        return result;
    }

    private void handleExpandType(JSONObject jsonObject, Map<String, Object> result) {
        if (StringUtils.equalsIgnoreCase("Array", jsonObject.getString("type"))) {
            List<Object> array = this.extractingExpandArray(jsonObject.getJSONArray("value"));
            result.put(jsonObject.getString("name"), array);
            return;
        }
        if (StringUtils.equalsIgnoreCase("Object", jsonObject.getString("type"))) {
            Map<String, Object> map = this.extractingExpandObject(jsonObject.getJSONArray("value"));
            if (MapUtils.isNotEmpty(map)) {
                result.put(jsonObject.getString("name"), map);
            }
        }
    }
}
