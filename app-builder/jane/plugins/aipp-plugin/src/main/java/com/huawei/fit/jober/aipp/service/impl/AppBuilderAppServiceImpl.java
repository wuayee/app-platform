/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.common.exception.AippErrCode.TASK_NOT_FOUND;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.aipp.common.ConvertUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.MetaUtils;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.condition.AppQueryCondition;
import com.huawei.fit.jober.aipp.constants.AippConst;
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
import com.huawei.fit.jober.aipp.enums.AippTypeEnum;
import com.huawei.fit.jober.aipp.enums.AppTypeEnum;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.genericable.entity.AippCreate;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.service.AippFlowService;
import com.huawei.fit.jober.aipp.service.AppBuilderAppService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.base.service.UsrAppCollectionService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
public class AppBuilderAppServiceImpl
        implements AppBuilderAppService, com.huawei.fit.jober.aipp.genericable.AppBuilderAppService {
    private static final Logger log = Logger.get(AppBuilderAppServiceImpl.class);
    private final AppBuilderAppFactory appFactory;
    private final AippFlowService aippFlowService;
    private final AppBuilderAppRepository appRepository;
    private final int nameLengthMaximum;
    private final MetaService metaService;

    private final UsrAppCollectionService usrAppCollectionService;

    public AppBuilderAppServiceImpl(AppBuilderAppFactory appFactory, AippFlowService aippFlowService,
                                    AppBuilderAppRepository appRepository,
                                    @Value("${validation.task.name.length.maximum:64}") int nameLengthMaximum,
                                    MetaService metaService, UsrAppCollectionService usrAppCollectionService) {
        this.nameLengthMaximum = nameLengthMaximum;
        this.appFactory = appFactory;
        this.aippFlowService = aippFlowService;
        this.appRepository = appRepository;
        this.metaService = metaService;
        this.usrAppCollectionService = usrAppCollectionService;
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
        // todo 要加个save appDto到数据的逻辑
        AippDto aippDto = ConvertUtils.toAppDto(appDto);
        AippCreateDto aippCreateDto = this.aippFlowService.create(aippDto, contextOf);
        aippDto.setId(aippCreateDto.getAippId());
        String id = appDto.getId();
        AppBuilderApp appBuilderApp = this.appFactory.create(id);
        appBuilderApp.setState("RUNNING");
        this.appFactory.update(appBuilderApp);
        return this.aippFlowService.publish(aippDto, contextOf);
    }

    @Override
    @Fitable(id = "default")
    public AippCreate debug(AppBuilderAppDto appDto, OperationContext contextOf) {
        AippDto aippDto = ConvertUtils.toAppDto(appDto);
        // todo Rsp 得统一整改下
        return ConvertUtils.toAippCreate(this.aippFlowService.previewAipp(appDto.getVersion(), aippDto, contextOf));
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
        List<AppBuilderAppMetadataDto> result =
                this.appRepository.selectByTenantIdWithPage(cond, tenantId, AppTypeEnum.APP.code(), offset, limit)
                        .stream()
                        .map(this::buildAppMetaData)
                        .collect(Collectors.toList());
        long total = this.appRepository.countByTenantId(tenantId, AppTypeEnum.APP.code());
        return Rsp.ok(RangedResultSet.create(result, offset, limit, total));
    }

    private AppBuilderAppMetadataDto buildAppMetaData(AppBuilderApp app) {
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
                .build();
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public AppBuilderAppDto create(String appId, AppBuilderAppCreateDto dto, OperationContext context) {
        if (dto != null) {
            this.validateCreateApp(dto.getName(), context);
        }
        AppBuilderApp templateApp = this.appFactory.create(appId);
        // 根据模板app复制app，仅需修改所有id
        // 优先copy下层内容，因为上层改变Id后，会影响下层对象的查询
        AppBuilderConfig config = resetConfig(templateApp.getConfig());
        AppBuilderFlowGraph flowGraph = templateApp.getFlowGraph();
        flowGraph.setId(Entities.generateId());
        Map<String, Object> appearance =
                JSONObject.parseObject(flowGraph.getAppearance(), new TypeReference<Map<String, Object>>() {});
        appearance.computeIfPresent("id", (key, value) -> flowGraph.getId());
        // 这里在创建应用时需要保证graph中的title+version唯一，否则在发布flow时会报错
        appearance.put("title", flowGraph.getId());
        flowGraph.setAppearance(JSONObject.toJSONString(appearance));

        templateApp.setId(Entities.generateId());
        templateApp.setConfigId(config.getId());
        templateApp.setFlowGraphId(flowGraph.getId());
        templateApp.setType("app");
        templateApp.setTenantId(context.getTenantId());
        config.setAppId(templateApp.getId());
        if (Objects.nonNull(dto)) {
            templateApp.setAttributes(this.createAppAttributes(dto));
            templateApp.setName(dto.getName());
            templateApp.setType(dto.getType());
        }

        resetOperatorAndTime(templateApp, LocalDateTime.now(), context.getOperator());
        this.saveNewAppBuilderApp(templateApp);
        return this.buildFullAppDto(templateApp);
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
        this.validateAppName(name, context);
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
        if (StringUtils.isEmpty(name)) {
            log.error("Create aipp failed: name can not be empty.", name);
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_EMPTY);
        } else if (name.length() > this.nameLengthMaximum) {
            log.error("Create aipp failed: the length of task name is out of bounds. [name={}]", name);
            throw new AippParamException(context, AippErrCode.AIPP_NAME_LENGTH_OUT_OF_BOUNDS);
        }
    }

    private Map<String, Object> createAppAttributes(AppBuilderAppCreateDto dto) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("description", dto.getDescription());
        attributes.put("icon", dto.getIcon());
        attributes.put("greeting", dto.getGreeting());
        attributes.put("app_type", dto.getAppType());
        return attributes;
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateApp(String appId, AppBuilderAppDto appDto, OperationContext context) {
        if (appDto != null) {
            this.validateUpdateApp(appId, appDto.getName(), context);
        }
        AppBuilderApp update = this.appFactory.create(appId);
        update.setUpdateBy(context.getOperator());
        update.setUpdateAt(LocalDateTime.now());
        update.setName(appDto.getName());
        update.setType(appDto.getType());
        update.setAttributes(appDto.getAttributes());
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
        this.appFactory.update(oldApp);
        return Rsp.ok(this.buildFullAppDto(oldApp));
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public void delete(String appId, OperationContext context) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        // step1 删除app相关
        if (StringUtils.isEmpty(appBuilderApp.getId())) {
            return;
        }
        this.appFactory.delete(appBuilderApp);

        // step2 删除task相关 此处应该是会删除相关的task和tasktemplate 但不会删除taskinstance
        List<Meta> metaList = MetaUtils.getAllMetasByAppId(metaService, appId, AippTypeEnum.PREVIEW.type(), context);
        metaList.addAll(MetaUtils.getAllMetasByAppId(metaService, appId, AippTypeEnum.NORMAL.type(), context));
        List<String> ids = metaList.stream().map(Meta::getVersionId).distinct().collect(Collectors.toList());
        MetaUtils.deleteMetasByVersionIds(metaService, ids, context);

        // todo step3 删除日志
        // todo step4 删除流程定义相关
        // todo step5 删除store相关

        // step6 删除应用收藏记录相关
        usrAppCollectionService.deleteByAppId(appId);

    }

    @Override
    public List<PublishedAppResDto> published(AppQueryCondition cond, long offset, int limit, String appId,
            OperationContext context) {
        List<Meta> metas = MetaUtils.getAllMetasByAppId(this.metaService, appId, context);
        if (CollectionUtils.isEmpty(metas)) {
            log.error("Meta can not be null.");
            throw new AippParamException(TASK_NOT_FOUND);
        }
        String aippId = metas.get(0).getId();
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
        AppBuilderApp app = appIdKeyAppValueMap.get(appId);
        return PublishedAppResDto.builder()
                .appId(appId)
                .appVersion(app.getVersion())
                .publishedAt(meta.getCreationTime())
                .publishedBy(meta.getCreator())
                .build();
    }

    @NotNull
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
        JSONObject page = (JSONObject) oldAppearanceObject.getJSONArray("pages").get(0);
        JSONArray shapes = page.getJSONArray("shapes");

        for (int j = 0; j < shapes.size(); j++) {
            JSONObject node = shapes.getJSONObject(j);
            String id = node.getString("id");
            String type = node.getString("type");
            if (!type.endsWith("NodeState")) {
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
            if (StringUtils.equals(node.get("name").asText(), param.getKey())) {
                if (singleLayerParams.contains(param.getKey())) {
                    if (StringUtils.equals(param.getKey(), "temperature")) {
                        ((ObjectNode) node).put("value", JsonUtils.parseObject(param.getValue(), Float.class));

                    } else {
                        ((ObjectNode) node).put("value", JsonUtils.parseObject(param.getValue(), String.class));

                    }
                    continue;
                }
                if (doubleLayerParams.contains(param.getKey())) {
                    ArrayNode valueArrayNode = convertList(param.getValue());
                    ((ObjectNode) node).set("value", valueArrayNode);
                    continue;
                }
                if (StringUtils.equals("knowledge", param.getKey())) {
                    JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
                    ArrayNode valueArrayNode = nodeFactory.arrayNode();
                    List<Map<String, Object>> res =
                            (List<Map<String, Object>>) JsonUtils.parseObject(param.getValue(), List.class);
                    res.forEach(r -> {
                        ArrayNode valueArrayNode1 = nodeFactory.arrayNode();
                        for (Map.Entry<String, Object> rr : r.entrySet()) {
                            if (StringUtils.equals(rr.getKey(), "id")) {
                                valueArrayNode1.add(convertId(rr.getKey(), ((Integer) rr.getValue()).longValue()));
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
                                mapNode.put(entry.getKey(), (JsonNode) entry.getValue());
                            } else {
                                mapNode.put(entry.getKey(), (String) entry.getValue());
                            }
                        }
                        valueArrayNode.add(mapNode);
                    });
                    ((ObjectNode) node).set("value", valueArrayNode);
                }
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
                mapNode.put(entry.getKey(), (Long) entry.getValue());
            } else {
                mapNode.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return mapNode;
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
                // TODO: 2024/4/29 0029 这里可能拿到null，这里暂时不知道什么问题，先把拿不到的跳过
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
            if (MapUtils.isEmpty(nameValue) || null == nameValue.get(formProperty.getName())) {
                // TODO: 2024/4/29 0029 暂时先不删除了，仅修改现存的内容
                // toDelete.add(cp);
                continue;
            }
            formProperty.setDefaultValue(nameValue.get(formProperty.getName()));
            // 更新
            config.getFormPropertyRepository().updateOne(formProperty);
        }
        // TODO: 2024/4/29 0029 删除代码暂时移除
        // 删除的
        // config.getFormPropertyRepository()
        //         .deleteMore(toDelete.stream()
        //                 .map(AppBuilderConfigProperty::getFormPropertyId)
        //                 .collect(Collectors.toList()));
        // config.getConfigPropertyRepository()
        //         .deleteMore(toDelete.stream().map(AppBuilderConfigProperty::getId).collect(Collectors.toList()));

        // TODO: 2024/4/20 0020 理应有新增的，暂时不管
    }

    @NotNull
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
        if (StringUtils.equalsIgnoreCase("startNodeStart", node.getString("type"))) {
            return node.getJSONObject("flowMeta").getJSONArray("inputParams");
        } else if (StringUtils.equalsIgnoreCase("endNodeEnd", node.getString("type"))) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("jadeEvent", node.getString("type"))) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("conditionNodeCondition", node.getString("type"))) {
            return null;
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
            } else if (StringUtils.equalsIgnoreCase("Expand", jsonObject.getString("from"))) {
                if (StringUtils.equalsIgnoreCase("Array", jsonObject.getString("type"))) {
                    List<Object> array = this.extractingExpandArray(jsonObject.getJSONArray("value"));
                    result.add(array);
                } else if (StringUtils.equalsIgnoreCase("Object", jsonObject.getString("type"))) {
                    Map<String, Object> map = this.extractingExpandObject(jsonObject.getJSONArray("value"));
                    if (MapUtils.isNotEmpty(map)) {
                        result.add(map);
                    }
                }
            }
        }
        return result;
    }

    // 如果type是Object，那么调用这个方法获取一个Map<String, Object>
    private Map<String, Object> extractingExpandObject(JSONArray value) {
        Map<String, Object> result = new HashMap<>();
        for (int index = 0; index < value.size(); index++) {
            JSONObject jsonObject = value.getJSONObject(index);
            if (StringUtils.equalsIgnoreCase("Input", jsonObject.getString("from"))) {
                result.put(jsonObject.getString("name"), jsonObject.get("value"));
            } else if (StringUtils.equalsIgnoreCase("Expand", jsonObject.getString("from"))) {
                if (StringUtils.equalsIgnoreCase("Array", jsonObject.getString("type"))) {
                    List<Object> array = this.extractingExpandArray(jsonObject.getJSONArray("value"));
                    result.put(jsonObject.getString("name"), array);
                } else if (StringUtils.equalsIgnoreCase("Object", jsonObject.getString("type"))) {
                    Map<String, Object> map = this.extractingExpandObject(jsonObject.getJSONArray("value"));
                    if (MapUtils.isNotEmpty(map)) {
                        result.put(jsonObject.getString("name"), map);
                    }
                }
            }
        }
        if (result.containsKey("knowledge")) {
            List<Map<String, Object>> knowledge = ObjectUtils.cast(result.get("knowledge"));
            knowledge.stream()
                    .filter(k -> Objects.nonNull(k.get("id")))
                    .forEach(o1 -> o1.put("id", Long.parseLong(o1.get("id").toString())));
        }
        return result;
    }
}
