/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.dynamicform.entity.FormMetaItem;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jober.FlowDefinitionService;
import com.huawei.fit.jober.FlowsService;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippForbiddenException;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.condition.AippQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.convertor.FormMetaConvertor;
import com.huawei.fit.jober.aipp.convertor.MetaConvertor;
import com.huawei.fit.jober.aipp.convertor.TaskPropertyConvertor;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AippDetailDto;
import com.huawei.fit.jober.aipp.dto.AippDto;
import com.huawei.fit.jober.aipp.dto.AippNodeForms;
import com.huawei.fit.jober.aipp.dto.AippOverviewDto;
import com.huawei.fit.jober.aipp.dto.AippOverviewRspDto;
import com.huawei.fit.jober.aipp.dto.AippVersionDto;
import com.huawei.fit.jober.aipp.enums.AippMetaStatusEnum;
import com.huawei.fit.jober.aipp.enums.AippTypeEnum;
import com.huawei.fit.jober.aipp.enums.AppCategory;
import com.huawei.fit.jober.aipp.enums.JaneCategory;
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.AippFlowService;
import com.huawei.fit.jober.aipp.service.AippRunTimeService;
import com.huawei.fit.jober.aipp.util.AippStringUtils;
import com.huawei.fit.jober.aipp.util.FormUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaUtils;
import com.huawei.fit.jober.aipp.util.UUIDUtil;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.entity.FlowDefinitionResult;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.jober.entity.FlowNodeFormInfo;
import com.huawei.fit.jober.entity.consts.NodeTypes;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.Tuple;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.entity.transfer.AppData;
import com.huawei.jade.store.service.AppService;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * aipp编排服务层接口实现
 *
 * @author l00611472
 * @since 2023-12-12
 */
@Component
public class AippFlowServiceImpl implements AippFlowService {
    /**
     * 预览aipp version uuid后缀长度
     */
    public static final int PREVIEW_UUID_LEN = 6;
    private static final Logger log = Logger.get(AippFlowServiceImpl.class);
    private static final int RETRY_PREVIEW_TIMES = 5;
    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String OLD_VERSION_ID = "00000000000000000000000000000000";

    private final AppBuilderFormRepository formRepository;
    private final FlowsService flowsService;
    private final MetaService metaService;
    private final FlowDefinitionService flowDefinitionService;
    private final AippRunTimeService aippRunTimeService;

    private final BrokerClient brokerClient;

    private final AppBuilderAppMapper appBuilderAppMapper;

    public AippFlowServiceImpl(@Fit FlowsService flowsService, @Fit MetaService metaService,
            @Fit FlowDefinitionService flowDefinitionService, @Fit AippRunTimeService aippRunTimeService,
            @Fit AppBuilderFormRepository formRepository, @Fit BrokerClient brokerClient,
            AppBuilderAppMapper appBuilderAppMapper) {
        this.flowsService = flowsService;
        this.metaService = metaService;
        this.flowDefinitionService = flowDefinitionService;
        this.aippRunTimeService = aippRunTimeService;
        this.formRepository = formRepository;
        this.brokerClient = brokerClient;
        this.appBuilderAppMapper = appBuilderAppMapper;
    }

    private String buildPreviewVersion(String version) {
        String uuid = UUIDUtil.uuid();
        String subUuid = (uuid.length() > PREVIEW_UUID_LEN) ? uuid.substring(0, PREVIEW_UUID_LEN) : uuid;
        return version + "-" + subUuid;
    }

    /**
     * 查询aipp详情
     *
     * @param aippId aippId
     * @param version aipp版本
     * @param context 操作上下文
     * @return aipp 详情
     */
    @Override
    public Rsp<AippDetailDto> queryAippDetail(String aippId, String version, OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        log.info("queryAippDetail aipp {} version {}, meta attr {}", aippId, version, meta.getAttributes());
        String flowConfigId = (String) meta.getAttributes().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY);

        FlowInfo rsp = this.flowsService.getFlows(flowConfigId, version, context);  // todo 是否要改？
        AippDetailDto detail = MetaConvertor.INSTANCE.toAippDetailDto(meta);
        detail.setFlowViewData(JsonUtils.parseObject(rsp.getConfigData()));
        detail.setVersion(version);
        detail.setStatus((String) meta.getAttributes().get(AippConst.ATTR_META_STATUS_KEY));
        detail.setIcon((String) meta.getAttributes().get(AippConst.ATTR_META_ICON_KEY));
        if (meta.getAttributes().containsKey(AippConst.ATTR_DESCRIPTION_KEY)) {
            detail.setDescription((String) meta.getAttributes().get(AippConst.ATTR_DESCRIPTION_KEY));
        }
        if (meta.getAttributes().containsKey(AippConst.ATTR_PUBLISH_TIME_KEY)) {
            detail.setPublishAt(LocalDateTime.parse((String) meta.getAttributes()
                    .get(AippConst.ATTR_PUBLISH_TIME_KEY)));
        }

        return Rsp.ok(detail);
    }

    /**
     * 查询aipp详情
     *
     * @param cond 过滤条件
     * @param page 分页
     * @param context 操作上下文
     * @return aipp 概况
     */
    @Override
    public PageResponse<AippOverviewRspDto> listAipp(AippQueryCondition cond, PaginationCondition page,
            OperationContext context) {
        log.info("listAipp cond{} page{}", cond, page);
        MetaFilter metaFilter = new MetaFilter();
        if (StringUtils.isNotBlank(cond.getName())) {
            metaFilter.setNames(Collections.singletonList(cond.getName()));
        }
        if (StringUtils.isNotBlank(cond.getCreator())) {
            metaFilter.setCreators(Collections.singletonList(cond.getCreator()));
        }
        metaFilter.setCategories(Collections.singletonList(JaneCategory.AIPP.name()));
        metaFilter.setAttributes(Collections.singletonMap(AippConst.ATTR_AIPP_TYPE_KEY,
                Collections.singletonList(AippTypeEnum.NORMAL.name())));
        String sortEncode = MetaUtils.formatSorter(cond.getSort(), cond.getOrder());
        metaFilter.setOrderBys(Collections.singletonList(sortEncode));
        // todo 兼容老数据，当前把没有 aipp_type 的数据也进行获取。等数据库刷完数据后，更改逻辑
        RangedResultSet<Meta> metaRes = this.metaService.list(metaFilter,
                true,
                page.getOffset(),
                page.getPageSize(),
                context,
                this.buildOldDataMetaFilter(metaFilter));
        List<AippOverviewRspDto> overviewDtoList = metaRes.getResults().stream().map(item -> {
            this.handleOldData(item);
            AippOverviewRspDto dto = MetaConvertor.INSTANCE.toAippOverviewRspDto(item);
            String status = (String) item.getAttributes().get(AippConst.ATTR_META_STATUS_KEY);
            dto.setStatus(status);

            dto.setVersion(item.getVersion());  // 兼容没有基线版本的1.0.0版本草稿
            if (this.isDraft(item, status)) {
                dto.setVersion((String) item.getAttributes().get(AippConst.ATTR_BASELINE_VERSION_KEY));
                dto.setDraftVersion(item.getVersion());
            }

            if (item.getAttributes().containsKey(AippConst.ATTR_PUBLISH_TIME_KEY)) {
                dto.setPublishAt(LocalDateTime.parse((String) item.getAttributes()
                        .get(AippConst.ATTR_PUBLISH_TIME_KEY)));
            }
            return dto;
        }).sorted(Comparator.comparing(AippOverviewDto::getUpdatedAt).reversed()).collect(Collectors.toList());

        return new PageResponse<>(metaRes.getRange().getTotal(), null, overviewDtoList);
    }

    private void handleOldData(Meta item) {
        if (Objects.equals(item.getId(), OLD_VERSION_ID)) {
            item.setId(item.getVersionId());
        }
    }

    private MetaFilter buildOldDataMetaFilter(MetaFilter metaFilter) {
        MetaFilter oldFilter = new MetaFilter(metaFilter.getMetaIds(),
                metaFilter.getVersionIds(),
                metaFilter.getNames(),
                metaFilter.getCategories(),
                metaFilter.getCreators(),
                metaFilter.getOrderBys(),
                metaFilter.getVersions(),
                metaFilter.getAttributes());
        oldFilter.setMetaIds(Collections.singletonList(OLD_VERSION_ID));
        oldFilter.setAttributes(Collections.emptyMap());
        return oldFilter;
    }

    private boolean isDraft(Meta item, String status) {
        return item.getAttributes().get(AippConst.ATTR_BASELINE_VERSION_KEY) != null && !Objects.equals(
                AippMetaStatusEnum.getAippMetaStatus(status),
                AippMetaStatusEnum.ACTIVE);
    }

    /**
     * 查询指定aipp的版本列表
     *
     * @param aippId aippId
     * @param context 操作上下文
     * @return aipp 版本概况
     */
    @Override
    public List<AippVersionDto> listAippVersions(String aippId, OperationContext context) {
        return MetaUtils.getAllPublishedMeta(this.metaService, aippId, context)
                .stream()
                .map(meta -> new AippVersionDto(meta.getVersion(),
                        ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_META_STATUS_KEY)),
                        meta.getCreator(),
                        meta.getCreationTime()))
                .collect(Collectors.toList());
    }

    /**
     * 删除aipp
     *
     * @param aippId aippId
     * @param version aipp版本
     * @param context 操作上下文
     * @throws AippForbiddenException 禁止删除aipp异常
     */
    @Override
    public void deleteAipp(String aippId, String version, OperationContext context) throws AippForbiddenException {
        log.info("deleting aipp {} version {}", aippId, version);
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        Map<String, Object> attr = meta.getAttributes();
        if (!AippMetaStatusEnum.INACTIVE.getCode().equals(attr.get(AippConst.ATTR_META_STATUS_KEY))) {
            log.error("not allow to delete an active aipp, aippId {} version {} status {}",
                    aippId,
                    version,
                    attr.getOrDefault(AippConst.ATTR_META_STATUS_KEY, "null"));
            throw new AippForbiddenException(context, AippErrCode.DELETE_AIPP_FORBIDDEN);
        }
        int ret = this.flowsService.deleteFlows((String) attr.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY),
                meta.getVersion(),
                context);
        if (ret != 0) {
            log.error("delete aipp {} version {} failed, ret {}", aippId, version, ret);
        }
        this.metaService.delete(meta.getVersionId(), context);
    }

    private MetaDeclarationInfo buildInitialMetaDeclaration(AippDto aippDto, AippCreateDto baselineInfo,
            FlowInfo flowInfo, String aippType) {
        MetaDeclarationInfo declaration = new MetaDeclarationInfo();
        declaration.setCategory(Undefinable.defined(JaneCategory.AIPP.name()));
        declaration.setName(Undefinable.defined(aippDto.getName()));
        declaration.setVersion(Undefinable.defined(aippDto.getVersion()));

        String description = aippDto.getDescription();
        declaration.putAttribute(AippConst.ATTR_FLOW_CONFIG_ID_KEY, flowInfo.getFlowId());
        declaration.putAttribute(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        declaration.putAttribute(AippConst.ATTR_DESCRIPTION_KEY, description == null ? "aipp 编排应用" : description);
        declaration.putAttribute(AippConst.ATTR_META_ICON_KEY, aippDto.getIcon());
        declaration.putAttribute(AippConst.ATTR_AIPP_TYPE_KEY, aippType);
        declaration.putAttribute(AippConst.ATTR_APP_ID_KEY, aippDto.getAppId());
        if (baselineInfo != null) {
            declaration.putAttribute(AippConst.ATTR_BASELINE_VERSION_KEY, baselineInfo.getVersion());
            declaration.setBasicMetaTemplateId(Undefinable.defined(baselineInfo.getAippId()));
        }

        List<MetaPropertyDeclarationInfo> props = AippConst.STATIC_META_ITEMS.stream()
                .map(FormMetaConvertor.INSTANCE::toMetaPropertyDeclarationInfo)
                .collect(Collectors.toList());
        declaration.setProperties(Undefinable.defined(props));

        return declaration;
    }

    /**
     * 创建aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id和版本信息
     * @throws AippParamException 入参异常
     * @throws AippException 创建aipp异常
     */
    @Override
    public AippCreateDto create(AippDto aippDto, OperationContext context) throws AippException {
        String version =
                aippDto.getFlowViewData().getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, DEFAULT_VERSION).toString();
        aippDto.setVersion(version);
        return this.createAippHandle(aippDto, context);
    }

    /**
     * 创建aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @throws AippParamException 入参异常
     * @throws AippException 创建aipp异常
     */
    private AippCreateDto createAippHandle(AippDto aippDto, OperationContext context) {
        Tuple flowCreateArgs = Tuple.duet(JsonUtils.toJsonString(aippDto.getFlowViewData()), context);
        return this.saveAipp(aippDto,
                null,
                context,
                (tuple -> this.buildFlowCreateFunc(flowCreateArgs)),
                flowCreateArgs);
    }

    private FlowInfo buildFlowCreateFunc(Tuple flowCreateArgs) {
        if (flowCreateArgs.capacity() < 2) {
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID,
                    StringUtils.format("Flow create size is {0}", flowCreateArgs.capacity()));
        }
        String configData = ObjectUtils.cast(flowCreateArgs.get(0).orElse(null));
        OperationContext operationContext = ObjectUtils.cast(flowCreateArgs.get(1).orElse(null));
        return this.flowsService.createFlows(configData, operationContext);
    }

    private AippCreateDto upgradeAippHandle(AippDto aippDto, AippCreateDto baselineInfo, OperationContext context,
            String flowId, String newFlowVersion) {
        Tuple flowUpgradeArgs =
                Tuple.quartet(flowId, newFlowVersion, JsonUtils.toJsonString(aippDto.getFlowViewData()), context);
        return this.saveAipp(aippDto,
                baselineInfo,
                context,
                (tuple -> this.buildFlowUpgradeFunc(flowUpgradeArgs)),
                flowUpgradeArgs);
    }

    private FlowInfo buildFlowUpgradeFunc(Tuple flowUpgradeArgs) {
        if (flowUpgradeArgs.capacity() < 4) {
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID,
                    StringUtils.format("flow upgrade size is {0}", flowUpgradeArgs.capacity()));
        }
        String flowId = ObjectUtils.cast(flowUpgradeArgs.get(0).orElse(null));
        String newFlowVersion = ObjectUtils.cast(flowUpgradeArgs.get(1).orElse(null));
        String configData = ObjectUtils.cast(flowUpgradeArgs.get(2).orElse(null));
        OperationContext operationContext = ObjectUtils.cast(flowUpgradeArgs.get(3).orElse(null));
        return this.flowsService.upgradeFlows(flowId, newFlowVersion, configData, operationContext);
    }

    /**
     * 保存 aipp 相关信息。
     *
     * @param aippDto aipp定义
     * @param baselineInfo aipp基线版本信息, 非升级场景为null
     * @param context 操作上下文
     * @return aipp id和版本信息
     * @throws AippParamException 入参异常
     * @throws AippException 创建aipp异常
     */
    private AippCreateDto saveAipp(AippDto aippDto, AippCreateDto baselineInfo, OperationContext context,
            Function<Tuple, FlowInfo> flowSaveFunc, Tuple flowSaveArgs) {
        log.info("create aipp, name {}", aippDto.getName());
        if (StringUtils.isBlank(aippDto.getName())) {
            log.error("aipp name cant be blank, create flow failed, tenantId {}", context.getTenantId());
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_EMPTY);
        }
        FlowInfo flowInfo;
        try {
            flowInfo = flowSaveFunc.apply(flowSaveArgs);
        } catch (JobberException e) {
            log.error("create flow failed, tenantId {} aipp {}, error {}",
                    context.getTenantId(),
                    aippDto.getName(),
                    e.getMessage());
            throw new AippException(context, AippErrCode.CREATE_FLOW_FAILED);
        }
        MetaDeclarationInfo declarationInfo =
                this.buildInitialMetaDeclaration(aippDto, baselineInfo, flowInfo, AippTypeEnum.NORMAL.name());
        log.debug("create aipp, declaration attr info {}", declarationInfo.getAttributes().getValue());
        try {
            Meta meta = this.metaService.create(declarationInfo, context);
            return AippCreateDto.builder().aippId(meta.getId()).version(meta.getVersion()).build();
        } catch (ConflictException e) {
            log.error("create aipp failed, error: {}", e.getMessage());
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
    }

    private void putAttrIfNotBlank(Map<String, Object> attr, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            attr.put(key, value);
        }
    }

    private void updateAttribute(Map<String, Object> attr, AippDto aippDto) {
        putAttrIfNotBlank(attr, AippConst.ATTR_DESCRIPTION_KEY, aippDto.getDescription());
        putAttrIfNotBlank(attr, AippConst.ATTR_META_ICON_KEY, aippDto.getIcon());

        Map<String, Object> flowView = aippDto.getFlowViewData();
        if (flowView != null && !flowView.isEmpty()) {
            attr.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY,
                    flowView.getOrDefault(AippConst.FLOW_CONFIG_ID_KEY, attr.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY)));
            attr.put(AippConst.ATTR_VERSION_KEY,
                    flowView.getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, attr.get(AippConst.ATTR_VERSION_KEY)));
        }
    }

    private void updateMetaDeclaration(String metaVersionId, String version, AippDto aippDto, Map<String, Object> attr,
            OperationContext context) {
        MetaDeclarationInfo declaration = new MetaDeclarationInfo();
        if (StringUtils.isNotBlank(aippDto.getName())) {
            declaration.setName(Undefinable.defined(aippDto.getName()));
            declaration.setVersion(Undefinable.defined(version));  // 底层task更新name的时候必须带上version
        }
        updateAttribute(attr, aippDto);
        declaration.setAttributes(Undefinable.defined(attr));
        log.debug("patch meta, metaVersionId {} name {} attr {}",
                metaVersionId,
                declaration.getName().getDefined() ? declaration.getName().getValue() : "undefined",
                declaration.getAttributes().getDefined() ? declaration.getAttributes().getValue() : "undefined");
        this.metaService.patch(metaVersionId, declaration, context);
    }

    private void validateUpdate(String aippId, Map<String, Object> attr, String name, OperationContext context) {
        if (!AippMetaStatusEnum.INACTIVE.getCode().equals(attr.get(AippConst.ATTR_META_STATUS_KEY))) {
            log.error("not allow to update an active aipp, aippId {} status {}",
                    aippId,
                    attr.getOrDefault(AippConst.ATTR_META_STATUS_KEY, "null"));
            throw new AippForbiddenException(context, AippErrCode.UPDATE_AIPP_FORBIDDEN);
        }
        if (StringUtils.isBlank(name)) {
            log.error("aipp name cant be blank, create flow failed, aippId {}", aippId);
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_EMPTY);
        }
    }

    /**
     * 更新aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id信息
     * @throws AippForbiddenException 禁止更新aipp异常
     * @throws AippParamException 入参异常
     */
    @Override
    public AippCreateDto update(AippDto aippDto, OperationContext context)
            throws AippForbiddenException, AippParamException {
        String aippId = aippDto.getId();
        String version = aippDto.getVersion();
        log.info("update aipp {} name {}", aippId, aippDto.getName());
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, version, context);
        if (meta == null) {
            return this.updateNewVersionAipp(aippDto, context, aippId, version);
        }
        Map<String, Object> attr = meta.getAttributes();
        validateUpdate(aippId, attr, aippDto.getName(), context);
        updateMetaDeclaration(meta.getVersionId(), meta.getVersion(), aippDto, attr, context);
        // 更新流程
        if (aippDto.getFlowViewData() == null || aippDto.getFlowViewData().isEmpty()) {
            return AippCreateDto.builder().aippId(aippId).version(meta.getVersion()).build();
        }
        this.flowsService.updateFlows((String) attr.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY),
                (String) attr.get(AippConst.ATTR_VERSION_KEY),
                JsonUtils.toJsonString(aippDto.getFlowViewData()),
                context);
        return AippCreateDto.builder().aippId(aippId).version(meta.getVersion()).build();
    }

    private AippCreateDto updateNewVersionAipp(AippDto aippDto, OperationContext context, String aippId,
            String version) {
        Meta lastDraftMeta = MetaUtils.getLastDraftMeta(this.metaService, aippId, context);
        String flowId = lastDraftMeta.getAttributes()
                .getOrDefault(AippConst.ATTR_FLOW_CONFIG_ID_KEY, StringUtils.EMPTY)
                .toString();
        this.upgradeAippHandle(aippDto, AippCreateDto.builder().aippId(aippId).build(), context, flowId, version);
        return this.update(aippDto, context);
    }

    private AippCreateDto createPreviewAipp(String baselineVersion, AippDto aippDto, OperationContext context) {
        Map<String, Object> flowViewData = aippDto.getFlowViewData();
        String flowId = (String) flowViewData.get(AippConst.FLOW_CONFIG_ID_KEY);
        String previewVersion = (String) flowViewData.get(AippConst.FLOW_CONFIG_VERSION_KEY);

        // 创建、发布流程定义
        FlowInfo flowInfo = this.flowsService.publishFlowsWithoutElsa(flowId,
                previewVersion,
                JsonUtils.toJsonString(flowViewData),
                context);
        // todo 预览时，aipp 的 version 用的是 flowInfo 的 version，是否合理待确认
        aippDto.setVersion(flowInfo.getVersion());
        MetaDeclarationInfo declarationInfo = this.buildInitialMetaDeclaration(aippDto,
                AippCreateDto.builder().aippId(aippDto.getId()).version(baselineVersion).build(),
                flowInfo,
                AippTypeEnum.PREVIEW.name());

        List<AippNodeForms> aippNodeForms = buildAippNodeForms(flowInfo);
        // 追加attribute
        Map<String, Object> attr = declarationInfo.getAttributes().getValue();
        appendAttribute(attr, aippNodeForms, flowInfo.getFlowDefinitionId());

        log.debug("create preview aipp, declaration attr info {}", attr);
        Meta meta = metaService.create(declarationInfo, context);
        return AippCreateDto.builder().aippId(meta.getId()).version(previewVersion).build();
    }

    private void appendAttribute(Map<String, Object> attr, List<AippNodeForms> aippNodeForms, String flowDefinitionId) {
        for (AippNodeForms node : aippNodeForms) {
            if (node.getMetaInfo().isEmpty()) {
                continue;
            }
            if (NodeTypes.START.getType().equalsIgnoreCase(node.getType())) {
                attr.put(AippConst.ATTR_START_FORM_ID_KEY, node.getMetaInfo().get(0).getFormId());
                attr.put(AippConst.ATTR_START_FORM_VERSION_KEY, node.getMetaInfo().get(0).getVersion());
            }
            if (NodeTypes.END.getType().equalsIgnoreCase(node.getType())) {
                attr.put(AippConst.ATTR_END_FORM_ID_KEY, node.getMetaInfo().get(0).getFormId());
                attr.put(AippConst.ATTR_END_FORM_VERSION_KEY, node.getMetaInfo().get(0).getVersion());
            }
        }
        attr.put(AippConst.ATTR_FLOW_DEF_ID_KEY, flowDefinitionId);
        attr.put(AippConst.ATTR_PUBLISH_TIME_KEY, LocalDateTime.now().toString());
        attr.put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.ACTIVE.getCode());
    }

    private MetaPropertyDeclarationInfo trimPropsName(MetaPropertyDeclarationInfo info) {
        String newName = info.getName().getValue().trim();
        info.setName(Undefinable.defined(newName));
        return info;
    }

    private List<MetaPropertyDeclarationInfo> getMetaPropertyDeclarationInfos(List<AippNodeForms> aippNodeForms) {
        List<MetaPropertyDeclarationInfo> formProps = aippNodeForms.stream()
                .flatMap(node -> node.getMetaInfo().stream())
                .flatMap(metaList -> metaList.getFormMetaItems().stream())
                .map(FormMetaConvertor.INSTANCE::toMetaPropertyDeclarationInfo)
                .filter(item -> StringUtils.isNotBlank(item.getName().getValue()))
                .map(this::trimPropsName)
                .collect(Collectors.toList());

        // 检查name不能重复、且不能和初始变量重复
        validateProps(formProps);
        return formProps;
    }

    /**
     * 预览aipp
     *
     * @param baselineVersion aipp 的基线版本
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return 创建预览aipp的id和version
     * @throws AippException 预览aipp异常
     */
    @Override
    public AippCreateDto previewAipp(String baselineVersion, AippDto aippDto, OperationContext context)
            throws AippException {
        FlowDefinitionResult definitionResult = this.getSameFlowDefinition(aippDto);
        if (definitionResult != null) {
            RangedResultSet<Meta> metas =
                    this.metaService.list(this.buildFlowDefinitionFilter(definitionResult), true, 0, 1, context);
            if (!metas.getResults().isEmpty()) {
                Meta meta = metas.getResults().get(0);
                return AippCreateDto.builder().aippId(meta.getId()).version(meta.getVersion()).build();
            }
        }
        // 过滤预览版本
        if (AippStringUtils.isPreview(baselineVersion)) {
            throw new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, "version is preview");
        }
        // 设置预览版本
        int retryTimes = RETRY_PREVIEW_TIMES;
        String previewVersion;
        String errorMsg;
        do {
            previewVersion = buildPreviewVersion(baselineVersion);
            aippDto.getFlowViewData().put(AippConst.FLOW_CONFIG_VERSION_KEY, previewVersion);
            try {
                return this.createPreviewAipp(baselineVersion, aippDto, context);
            } catch (JobberException e) {
                if (e.getCode() != ErrorCodes.FLOW_ALREADY_EXIST.getErrorCode()) {
                    errorMsg = e.getMessage();
                    break;
                }
                errorMsg = e.getMessage();
                log.warn("create preview aipp failed, times {} aippId {} version {}, error {}",
                        RETRY_PREVIEW_TIMES - retryTimes,
                        aippDto.getId(),
                        previewVersion,
                        e.getMessage());
            }
        } while (retryTimes-- > 0);
        log.error("Failed to preview aipp.[errorMsg={}]", errorMsg);
        throw new AippException(context, AippErrCode.PREVIEW_AIPP_FAILED, errorMsg);
    }

    private MetaFilter buildFlowDefinitionFilter(FlowDefinitionResult definitionResult) {
        MetaFilter filter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("flow_definition_id", Collections.singletonList(definitionResult.getFlowDefinitionId()));
        attributes.put("flow_config_id", Collections.singletonList(definitionResult.getMetaId()));
        filter.setAttributes(attributes);
        return filter;
    }

    private FlowDefinitionResult getSameFlowDefinition(AippDto aippDto) {
        Map<String, Object> flowViewData = aippDto.getFlowViewData();
        String metaId = String.valueOf(flowViewData.getOrDefault(AippConst.FLOW_CONFIG_ID_KEY, StringUtils.EMPTY));
        String version =
                String.valueOf(flowViewData.getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, StringUtils.EMPTY));
        List<FlowDefinitionResult> flowDefinitions =
                this.flowDefinitionService.getFlowDefinitionByMetaIdAndPartVersion(metaId, version + "-", null);
        String parsedGraphData =
                this.flowDefinitionService.getParsedGraphData(JsonUtils.toJsonString(aippDto.getFlowViewData()),
                        version);
        Map<String, Object> aippFlowDefinitionMapping = this.buildFlowDefinition(parsedGraphData);
        return flowDefinitions.stream().filter(definition -> {
            Map<String, Object> map = this.buildFlowDefinition(definition.getGraph());
            return this.compareMaps(map, aippFlowDefinitionMapping);
        }).findAny().orElse(null);
    }

    private Map<String, Object> buildFlowDefinition(String flowDefinition) {
        Map<String, Object> parsedFlowDefinitionMapping = JsonUtils.parseObject(flowDefinition);

        // 这边 name 和 version 不需要比较
        parsedFlowDefinitionMapping.remove(AippConst.FLOW_CONFIG_NAME);
        parsedFlowDefinitionMapping.remove(AippConst.FLOW_CONFIG_VERSION_KEY);
        return parsedFlowDefinitionMapping;
    }

    public boolean compareMaps(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == map2) {
            return true;
        }
        if (map1 == null || map2 == null) {
            return false;
        }
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            String key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = map2.get(key);
            if (!map2.containsKey(key) || !this.isSameObject(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameObject(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        if (obj1 instanceof Map && obj2 instanceof Map) {
            return compareMaps(ObjectUtils.cast(obj1), ObjectUtils.cast(obj2));
        }
        return Objects.equals(obj1, obj2);
    }

    /**
     * 退出预览aipp的清理
     *
     * @param previewAippId 预览版本的aippId
     * @param previewVersion 预览版本号
     * @param context 操作上下文
     */
    public void cleanPreviewAipp(String previewAippId, String previewVersion, OperationContext context) {
        // 过滤非预览版本
        if (!AippStringUtils.isPreview(previewVersion)) {
            throw new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, "version is not preview");
        }
        CompletableFuture.runAsync(() -> {
            Meta previewMeta = MetaUtils.getAnyMeta(metaService, previewAippId, previewVersion, context);
            cleanResourceForPreview(previewMeta, previewAippId, previewVersion, context);
        });
    }

    private void cleanResourceForPreview(Meta previewMeta, String previewAippId, String previewVersion,
            OperationContext context) {
        if (previewMeta.getAttributes()
                .getOrDefault(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode())
                .equals(AippMetaStatusEnum.ACTIVE.getCode())) {
            this.aippRunTimeService.terminateAllPreviewInstances(previewAippId,
                    previewMeta.getVersionId(),
                    true,
                    context);
        }
        String flowId = previewMeta.getAttributes().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY).toString();
        if (!StringUtils.isBlank(flowId)) {
            this.flowsService.deleteFlowsWithoutElsa(flowId, previewVersion, context);
        }
        this.metaService.delete(previewMeta.getVersionId(), context);
    }

    private boolean isValidUpgradeVersion(String oldVersion, String newVersion) {
        final String delimiter = "\\.";
        if (StringUtils.isBlank(oldVersion) || StringUtils.isBlank(newVersion)) {
            return false;
        }
        String[] oldVersionArray = oldVersion.split(delimiter);
        String[] newVersionArray = newVersion.split(delimiter);
        if (oldVersionArray.length != newVersionArray.length) {
            return false;
        }
        try {
            for (int i = 0; i < oldVersionArray.length; ++i) {
                int oldVersionInt = Integer.parseInt(oldVersionArray[i]);
                int newVersionInt = Integer.parseInt(newVersionArray[i]);
                if (newVersionInt > oldVersionInt) {
                    return true;
                } else if (newVersionInt < oldVersionInt) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;  // 解析不成int
        }
        return true;  // 全相等
    }

    /**
     * 升级aipp
     *
     * @param baselineVersion 基线版本
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id信息
     */
    @Override
    public AippCreateDto upgrade(String baselineVersion, AippDto aippDto, OperationContext context) {
        String aippId = aippDto.getId();
        log.info("upgrade aipp {} name {}", aippId, aippDto.getName());
        String newAippVersion = aippDto.getVersion();

        // 校验版本号是否为递增
        if (!this.isValidUpgradeVersion(baselineVersion, newAippVersion)) {
            throw new AippParamException(context,
                    AippErrCode.INPUT_PARAM_IS_INVALID,
                    AippConst.FLOW_CONFIG_VERSION_KEY);
        }
        Meta latestMeta = MetaUtils.getLastNormalMeta(this.metaService, aippId, context);
        String latestMetaStatus = latestMeta.getAttributes()
                .getOrDefault(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode())
                .toString();
        String flowId = latestMeta.getAttributes().getOrDefault(AippConst.ATTR_FLOW_CONFIG_ID_KEY, "").toString();
        Validation.notBlank(flowId, () -> {
            throw new AippParamException(context,
                    AippErrCode.INPUT_PARAM_IS_INVALID,
                    AippConst.ATTR_FLOW_CONFIG_ID_KEY);
        });
        String newFlowVersion =
                aippDto.getFlowViewData().getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, DEFAULT_VERSION).toString();
        if (this.isUpgradeVersion(newAippVersion, latestMeta, latestMetaStatus)) {
            this.upgradeAippHandle(aippDto,
                    AippCreateDto.builder().aippId(aippId).version(baselineVersion).build(),
                    context,
                    flowId,
                    newFlowVersion);
        }
        return this.update(aippDto, context);
    }

    /**
     * 如果是第一个草稿版本，或者新的草稿版本与之前版本号不一致，都需要升级版本操作。
     */
    private boolean isUpgradeVersion(String newAippVersion, Meta latestMeta, String latestMetaStatus) {
        return latestMetaStatus.equals(AippMetaStatusEnum.ACTIVE.getCode()) || !Objects.equals(newAippVersion,
                latestMeta.getVersion());
    }

    private void validateProps(List<MetaPropertyDeclarationInfo> props) {
        Set<String> staticKeySet =
                AippConst.STATIC_META_ITEMS.stream().map(FormMetaItem::getKey).collect(Collectors.toSet());
        if (staticKeySet.size() != AippConst.STATIC_META_ITEMS.size()) {
            log.error("The initial meta item key cant be repeated.");
            throw new AippException(AippErrCode.AIPP_PROPS_KEY_DUPLICATE);
        }
        Iterator<MetaPropertyDeclarationInfo> iter = props.iterator();
        while (iter.hasNext()) {
            MetaPropertyDeclarationInfo prop = iter.next();
            if (prop.getName().getDefined() && staticKeySet.contains(prop.getName().getValue())) {
                iter.remove();
                log.warn("The form field repeat {}", prop.getName().getValue());
                continue;
            }
            staticKeySet.add(prop.getName().getValue());
        }
    }

    private List<MetaPropertyDeclarationInfo> buildPatchProps(List<MetaPropertyDeclarationInfo> props, Meta meta) {
        if (meta.getProperties() == null) {
            return props;
        }
        List<MetaPropertyDeclarationInfo> propsPatch = meta.getProperties()
                .stream()
                .map(TaskPropertyConvertor.INSTANCE::toMetaPropertyDeclarationInfo)
                .collect(Collectors.toList());
        propsPatch.addAll(props);

        return propsPatch;
    }

    private MetaDeclarationInfo buildPublishMetaDeclaration(String aippId, List<AippNodeForms> aippNodeForms,
            String flowDefinitionId, Meta meta, AippDto aippDto) {
        // 追加aipp meta属性字段
        MetaDeclarationInfo declaration = new MetaDeclarationInfo();

        // 追加/更新 aipp attribute字段
        Map<String, Object> attrPatch = meta.getAttributes();
        appendAttribute(attrPatch, aippNodeForms, flowDefinitionId);
        updateAttribute(attrPatch, aippDto);
        declaration.setAttributes(Undefinable.defined(attrPatch));
        declaration.setName(Undefinable.defined(meta.getName()));
        declaration.setVersion(Undefinable.defined(meta.getVersion()));

        log.debug("patch meta, aippId {} name {} attr {}",
                aippId,
                declaration.getName().getDefined() ? declaration.getName().getValue() : "undefined",
                declaration.getAttributes().getDefined() ? declaration.getAttributes().getValue() : "undefined");
        return declaration;
    }

    private List<AippNodeForms> buildAippNodeForms(FlowInfo flowInfo) {
        if (flowInfo.getFlowNodes() == null) {
            return Collections.emptyList();
        }
        return flowInfo.getFlowNodes().stream().filter(item -> item.getFlowNodeForm() != null).map(item -> {
            FlowNodeFormInfo form = item.getFlowNodeForm();
            List<FormMetaQueryParameter> parameter =
                    Collections.singletonList(new FormMetaQueryParameter(form.getFormId(), form.getVersion()));
            return AippNodeForms.builder()
                    .type(item.getType())
                    .metaInfo(FormUtils.buildFormMetaInfos(parameter, this.formRepository))
                    .build();
        }).collect(Collectors.toList());
    }

    private void rollbackAipp(String versionId, FlowInfo flowInfo, OperationContext context) {
        try {
            if (flowInfo != null) {
                this.flowDefinitionService.deleteFlows(flowInfo.getFlowDefinitionId(), context);
            }
            this.metaService.delete(versionId, context);
        } catch (Exception e) {
            log.error("rollbackAipp failed, versionId {}, e = {}", versionId, e);
        }
    }

    private FlowInfo publishFlow(AippDto aippDto, Map<String, Object> attr, OperationContext context) {
        String flowConfigId = (String) attr.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY);
        String version = (String) attr.get(AippConst.ATTR_VERSION_KEY);

        FlowInfo flowInfo;
        try {
            flowInfo = this.flowsService.publishFlows(flowConfigId,
                    version,
                    JsonUtils.toJsonString(aippDto.getFlowViewData()),
                    context);
        } catch (JobberException e) {
            AippErrCode retCode = (e.getCode() == ErrorCodes.FLOW_ALREADY_EXIST.getErrorCode())
                    ? AippErrCode.FLOW_ALREADY_EXIST
                    : AippErrCode.PUBLISH_FLOW_FAILED;
            throw new AippException(context, retCode);
        }
        return flowInfo;
    }

    /**
     * 发布aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return 发布aipp概况
     * @throws AippForbiddenException 禁止更新aipp异常
     * @throws AippParamException 入参异常
     * @throws AippException 发布aipp异常
     */
    @Override
    public Rsp<AippCreateDto> publish(AippDto aippDto, OperationContext context) throws AippException {
        String aippId = aippDto.getId();
        String version = aippDto.getVersion();
        CompletableFuture.runAsync(() -> MetaUtils.getAllPreviewMeta(metaService, aippId, context)
                .forEach(meta -> cleanResourceForPreview(meta, aippId, meta.getVersion(), context)));

        Meta meta = MetaUtils.getLastNormalMeta(metaService, aippId, context);
        Map<String, Object> attr = meta.getAttributes();
        String originalDraftVersion = meta.getVersion();
        if (!this.isValidUpgradeVersion(originalDraftVersion, version)) {
            log.error("old version {} is larger than new one {}", originalDraftVersion, version);
            throw new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, "version");
        }
        attr.put(AippConst.ATTR_VERSION_KEY, version);
        aippDto.getFlowViewData().put(AippConst.ATTR_VERSION_KEY, version);
        meta.setVersion(version);
        log.info("publish aipp {} name {} attr {}", aippId, aippDto.getName(), attr);

        validateUpdate(aippId, attr, aippDto.getName(), context);
        // 发布流程
        FlowInfo flowInfo = null;
        try {
            flowInfo = publishFlow(aippDto, attr, context);
            // 查询表单 元数据
            List<AippNodeForms> aippNodeForms = buildAippNodeForms(flowInfo);
            // 发布aipp
            MetaDeclarationInfo declaration =
                    buildPublishMetaDeclaration(aippId, aippNodeForms, flowInfo.getFlowDefinitionId(), meta, aippDto);
            metaService.patch(meta.getVersionId(), declaration, context);

            String uniqueName = this.publishToStore(aippDto, context, flowInfo);
            return Rsp.ok(new AippCreateDto(aippId, meta.getVersion(), uniqueName));
        } catch (Exception e) {
            log.error("publish aipp {} failed.", aippId, e);
            rollbackAipp(meta.getVersionId(), flowInfo, context);
            throw e;
        }
    }

    private String publishToStore(AippDto aippDto, OperationContext context, FlowInfo flowInfo) {
        AppData itemData = this.buildItemData(aippDto, context, flowInfo);
        String uniqueName = this.brokerClient.getRouter(AppService.class, "com.huawei.jade.store.app.publishApp")
                .route(new FitableIdFilter("store-repository-pgsql"))
                .invoke(itemData);
        appBuilderAppMapper.updateAppWithStoreId(uniqueName, aippDto.getAppId(), aippDto.getVersion());
        return uniqueName;
    }

    @NotNull
    private AppData buildItemData(AippDto aippDto, OperationContext context, FlowInfo flowInfo) {
        AppCategory appCategory = AppCategory.findByType(aippDto.getType())
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID));
        AppData itemData = new AppData();
        itemData.setCreator(context.getOperator());
        itemData.setModifier(context.getOperator());
        itemData.setIcon(aippDto.getIcon());
        itemData.setName(aippDto.getName());
        itemData.setDescription(aippDto.getDescription());
        if (this.isToolCategory(appCategory)) {
            itemData.setSchema(this.buildToolSchema(appCategory, context, aippDto, flowInfo));
        } else if (this.isAppCategory(appCategory)) {
            itemData.setSchema(MapBuilder.<String, Object>get()
                    .put("name", aippDto.getName())
                    .put("description", aippDto.getDescription())
                    .build());
        }
        itemData.setSource(appCategory.getSource());
        itemData.setTags(new HashSet<String>() {{
            add(appCategory.getTag());
        }});
        itemData.setRunnables(this.buildRunnables(appCategory, aippDto));
        return itemData;
    }

    private boolean isToolCategory(AppCategory appCategory) {
        return Objects.equals(appCategory.getCategory(), AppCategory.WATER_FLOW.getCategory());
    }

    private boolean isAppCategory(AppCategory appCategory) {
        return Objects.equals(appCategory.getCategory(), AppCategory.APP.getCategory());
    }

    private Map<String, Object> buildToolSchema(AppCategory appCategory, OperationContext context, AippDto aippDto,
            FlowInfo flowInfo) {
        return MapBuilder.<String, Object>get()
                .put("fitableId", "water.flow.invoke")
                .put("name", aippDto.getName())
                .put("description", aippDto.getDescription())
                .put("parameters", this.buildParameters(aippDto, context, flowInfo, appCategory))
                .put("return", MapBuilder.get().put("type", "string").build())
                .put("manualIntervention", Objects.equals(appCategory, AppCategory.WATER_FLOW))
                .build();
    }

    private Map<String, Object> buildParameters(AippDto aippDto, OperationContext context, FlowInfo flowInfo,
            AppCategory appCategory) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("type", "object");
        parameterMap.put("properties", this.buildPropertiesMap(aippDto, context, appCategory, flowInfo));
        parameterMap.put("order", Arrays.asList("tenantId", "aippId", "version", "inputParams"));
        parameterMap.put("required", Arrays.asList("tenantId", "aippId", "version", "inputParams"));
        return parameterMap;
    }

    private Map<String, Object> buildRunnables(AppCategory appCategory, AippDto aippDto) {
        Map<String, Object> runnablesMap = new HashMap<>();
        runnablesMap.put("FIT", MapBuilder.get().put("genericableId", "07b51bd246594c159d403164369ce1db").build());
        if (isAppCategory(appCategory)) {
            runnablesMap.put("APP", MapBuilder.get().put("appId", aippDto.getAppId()).build());
        }
        return runnablesMap;
    }

    private Map<String, Object> buildPropertiesMap(AippDto aippDto, OperationContext context, AppCategory appCategory,
            FlowInfo flowInfo) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("tenantId",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the tenant id of the waterFlow tool")
                        .put("default", context.getTenantId())
                        .build());
        propertiesMap.put("aippId",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the aipp id of the waterFlow tool")
                        .put("default", aippDto.getId())
                        .build());
        propertiesMap.put("version",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the aipp version of the waterFlow tool")
                        .put("default", aippDto.getVersion())
                        .build());
        propertiesMap.put("inputParams",
                MapBuilder.get()
                        .put("type", "object")
                        .put("properties", this.buildPropertiesMapOfInputParam(flowInfo))
                        .build());
        return propertiesMap;
    }

    private Map<String, Object> buildPropertiesMapOfInputParam(FlowInfo flowInfo) {
        Map<String, Object> propertiesMapOfInputParam = MapBuilder.<String, Object>get()
                .put(AippConst.TRACE_ID, MapBuilder.get().put("type", "string").build())
                .put(AippConst.CALLBACK_ID, MapBuilder.get().put("type", "string").build())
                .put(AippConst.CONTEXT_USER_ID, MapBuilder.get().put("type", "string").build())
                .build();
        flowInfo.getInputParamsByName("input").forEach(inputParam -> {
            String name = inputParam.getOrDefault("name", StringUtils.EMPTY).toString();
            String type = inputParam.getOrDefault("type", StringUtils.EMPTY).toString();
            String description = inputParam.getOrDefault("description", StringUtils.EMPTY).toString();
            propertiesMapOfInputParam.put(name,
                    MapBuilder.get().put("type", type).put("description", description).build());
        });
        return propertiesMapOfInputParam;
    }
}
