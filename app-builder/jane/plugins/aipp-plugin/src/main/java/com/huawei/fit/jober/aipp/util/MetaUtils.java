/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.enums.DirectionEnum;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippNotFoundException;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.AippMetaStatusEnum;
import com.huawei.fit.jober.aipp.enums.AippSortKeyEnum;
import com.huawei.fit.jober.aipp.enums.AippTypeEnum;
import com.huawei.fit.jober.aipp.enums.JaneCategory;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Meta操作工具类
 *
 * @author l00611472
 * @since 2024/02/21
 */
public class MetaUtils {

    private static final Logger log = Logger.get(MetaUtils.class);

    /**
     * 查询最近更新的任意{@link Meta}
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param version 指定aipp的版本
     * @param context 操作人上下文
     * @return 最近更新的{@link Meta}
     * @throws AippNotFoundException 没有找到
     */
    public static Meta getAnyMeta(MetaService metaService, String metaId, String version, OperationContext context)
            throws AippNotFoundException {
        MetaFilter metaFilter = getAnyMetaFilter(metaId, version);
        return getSingleMetaHandle(metaService, metaId, metaFilter, context);
    }

    /**
     * 查询指定aippId最新的非预览{@link Meta}(包括发布和未发布的)
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param context 操作人上下文
     * @return 最近更新的非预览{@link Meta}
     * @throws AippNotFoundException 没有找到
     */
    public static Meta getLastNormalMeta(MetaService metaService, String metaId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.DEFAULT);
        return getSingleMetaHandle(metaService, metaId, metaFilter, context);
    }

    /**
     * 查询指定aippId最新的已发布{@link Meta}
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param context 操作人上下文
     * @return 最近更新的已发布{@link Meta}
     * @throws AippNotFoundException 没有找到
     */
    public static Meta getLastPublishedMeta(MetaService metaService, String metaId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.PUBLISHED);
        return getSingleMetaHandle(metaService, metaId, metaFilter, context);
    }

    /**
     * 查询指定aippId最新的未发布草稿{@link Meta}
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param context 操作人上下文
     * @return 最近更新的未发布草稿{@link Meta}
     * @throws AippNotFoundException 没有找到
     */
    public static Meta getLastDraftMeta(MetaService metaService, String metaId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.DRAFT);
        return getSingleMetaHandle(metaService, metaId, metaFilter, context);
    }

    /**
     * 查询指定aippId所有发布的 {@link Meta}, 按更新时间倒序
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param context 操作人上下文
     * @return 所有非预览{@link Meta}
     * @throws AippNotFoundException 没有找到
     */
    public static List<Meta> getAllPublishedMeta(MetaService metaService, String metaId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.PUBLISHED);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    /**
     * 查询指定aippId所有预览{@link Meta}, 按更新时间倒序
     *
     * @param metaService 使用的{@link MetaService}
     * @param baselineAippId 指定aipp的id
     * @param context 操作人上下文
     * @return 所有预览{@link Meta}
     * @throws AippNotFoundException 没有找到
     */
    public static List<Meta> getAllPreviewMeta(MetaService metaService, String baselineAippId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getPreviewMetaFilter(baselineAippId);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    private static Meta getSingleMetaHandle(MetaService metaService, String metaId, MetaFilter metaFilter,
            OperationContext context) throws AippException {
        Validation.notBlank(metaId, () -> new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, metaId));
        RangedResultSet<Meta> metaRes =
                metaService.list(metaFilter, true, 0, 1, context, buildOldDataMetaFilter(metaFilter));
        if (metaRes.getResults().isEmpty()) {
            log.warn("meta {} version {} meta status {} not found",
                    metaId,
                    metaFilter.getVersions(),
                    metaFilter.getAttributes().get(AippConst.ATTR_META_STATUS_KEY));
            return null;
        }
        return metaRes.getResults().get(0);
    }

    public static List<Meta> getListMetaHandle(MetaService metaService, MetaFilter metaFilter,
            OperationContext context) throws AippException {
        final int limitPerQuery = 10;
        return getAllFromRangedResult(limitPerQuery,
                (offset) -> metaService.list(metaFilter,
                        false,
                        offset,
                        limitPerQuery,
                        context,
                        buildOldDataMetaFilter(metaFilter))).collect(Collectors.toList());
    }

    public static MetaFilter buildOldDataMetaFilter(MetaFilter metaFilter) {
        MetaFilter oldDataFilter = new MetaFilter(metaFilter.getMetaIds(),
                metaFilter.getVersionIds(),
                metaFilter.getNames(),
                metaFilter.getCategories(),
                metaFilter.getCreators(),
                metaFilter.getOrderBys(),
                metaFilter.getVersions(),
                metaFilter.getAttributes());
        oldDataFilter.setVersionIds(metaFilter.getMetaIds());
        oldDataFilter.setMetaIds(Collections.emptyList());
        oldDataFilter.setVersions(Collections.emptyList());
        Map<String, List<String>> attributes = deepCopy(oldDataFilter.getAttributes());
        attributes.remove(AippConst.ATTR_AIPP_TYPE_KEY);
        oldDataFilter.setAttributes(attributes);
        return oldDataFilter;
    }

    private static Map<String, List<String>> deepCopy(Map<String, List<String>> originalMap) {
        Map<String, List<String>> copiedMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            List<String> originalList = entry.getValue();
            List<String> copiedList = new ArrayList<>(originalList);
            copiedMap.put(key, copiedList);
        }
        return copiedMap;
    }

    private static MetaFilter getAnyMetaFilter(String metaId, String version) {
        MetaFilter metaFilter = new MetaFilter();
        metaFilter.setCategories(Collections.singletonList(JaneCategory.AIPP.name()));
        metaFilter.setMetaIds(Collections.singletonList(metaId));
        if (version != null) {
            metaFilter.setVersions(Collections.singletonList(version));
        }
        metaFilter.setOrderBys(Collections.singletonList(formatSorter(null, null)));
        return metaFilter;
    }

    private static MetaFilter getNormalMetaFilter(String metaId, NormalFilterEnum normalFilter) {
        MetaFilter metaFilter = getAnyMetaFilter(metaId, null);
        Map<String, List<String>> attributes = new HashMap<String, List<String>>() {{
            // 仅查找普通aipp，不包含预览aipp
            put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.NORMAL.name()));
        }};
        if (normalFilter == NormalFilterEnum.PUBLISHED) {
            attributes.put(AippConst.ATTR_META_STATUS_KEY,
                    Collections.singletonList(AippMetaStatusEnum.ACTIVE.getCode()));
        } else if (normalFilter == NormalFilterEnum.DRAFT) {
            attributes.put(AippConst.ATTR_META_STATUS_KEY,
                    Collections.singletonList(AippMetaStatusEnum.INACTIVE.getCode()));
        }
        String sortEncode = MetaUtils.formatSorter("create_at", "descend");
        metaFilter.setOrderBys(Collections.singletonList(sortEncode));
        metaFilter.setAttributes(attributes);
        return metaFilter;
    }

    public static boolean isPublished(Meta meta) {
        Map<String, Object> attributes = meta.getAttributes();
        if (!attributes.containsKey(AippConst.ATTR_AIPP_TYPE_KEY)
                || !attributes.containsKey(AippConst.ATTR_META_STATUS_KEY)) {
            return false;
        }
        String aippType = String.valueOf(attributes.get(AippConst.ATTR_AIPP_TYPE_KEY));
        String metaStatus = String.valueOf(attributes.get(AippConst.ATTR_META_STATUS_KEY));
        return StringUtils.equals(aippType, AippTypeEnum.NORMAL.name()) && Objects.equals(metaStatus,
                AippMetaStatusEnum.ACTIVE.getCode());
    }

    public static List<Meta> getAllMetasByAppId(MetaService metaService, String appId, String aippType,
            OperationContext context) throws AippException {
        MetaFilter metaFilter = getMetaFilter(appId, aippType);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    public static List<Meta> getAllMetasByAppId(MetaService metaService, String appId,
            OperationContext context) throws AippException {
        MetaFilter metaFilter = getMetaFilter(appId);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    private static MetaFilter getMetaFilter(String appId) {
        MetaFilter metaFilter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, Collections.singletonList(appId));
        metaFilter.setAttributes(attributes);
        String sortEncode = MetaUtils.formatSorter("create_at", "descend");
        metaFilter.setOrderBys(Collections.singletonList(sortEncode));
        return metaFilter;
    }

    private static MetaFilter getMetaFilter(String appId, String aippType) {
        MetaFilter metaFilter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, Collections.singletonList(appId));
        attributes.put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.getType(aippType).type()));
        metaFilter.setAttributes(attributes);
        return metaFilter;
    }

    private static MetaFilter getPreviewMetaFilter(String metaId) {
        MetaFilter metaFilter = getAnyMetaFilter(metaId, null);
        metaFilter.setAttributes(new HashMap<String, List<String>>() {{
            // 仅查找预览aipp
            put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.PREVIEW.name()));
        }});
        return metaFilter;
    }

    /**
     * 查询指定aippId所有预览{@link Meta}, 按更新时间倒序
     *
     * @param sortKey 见{@link AippSortKeyEnum}, 默认update_at
     * @param direction 排序方向, 见{@link DirectionEnum}, 默认desc
     * @return {@link MetaFilter}中setOrderBys中的字符串
     */
    public static String formatSorter(String sortKey, String direction) {
        return String.format(Locale.ROOT,
                "%s(%s)",
                DirectionEnum.getDirection(nullIf(direction, DirectionEnum.DESCEND.name())).getValue(),
                AippSortKeyEnum.getSortKey(nullIf(sortKey, AippSortKeyEnum.UPDATE_AT.name())).getKey());
    }

    /**
     * 从rangeResult实例中获取查询结果
     *
     * @param limitPerQuery 表示查询数量限制的{@link int}
     * @param resultGetter 表示返回查询的方法的{@link Function}
     * @return 表示查询结果的流
     * @param <T> 带查询数据的类型
     */
    public static <T> Stream<T> getAllFromRangedResult(int limitPerQuery,
                                                       Function<Long, RangedResultSet<T>> resultGetter) {
        RangedResultSet<T> metaRes = resultGetter.apply(0L);
        if (metaRes.getResults().isEmpty() || metaRes.getRange().getTotal() == 0) {
            return Stream.empty();
        }
        List<T> firstResult = metaRes.getResults();
        if (metaRes.getRange().getTotal() <= limitPerQuery) {
            return firstResult.stream();
        }
        return Stream.concat(firstResult.stream(),
                LongStream.rangeClosed(1, (int) (metaRes.getRange().getTotal() / limitPerQuery))
                        .mapToObj(offsetCount -> CompletableFuture.supplyAsync(() -> resultGetter.apply(
                                offsetCount * limitPerQuery).getResults().stream()))
                        .flatMap(CompletableFuture::join));
    }

    public enum NormalFilterEnum {
        // 已发布
        PUBLISHED,
        // 草稿
        DRAFT,
        // 默认
        DEFAULT
    }

    /**
     * 根据versionId批量删除meta
     * @param metaService 使用的{@link MetaService}
     * @param versionIds 需要删除的versionId列表
     * @param context 操作人上下文
     */
    public static void deleteMetasByVersionIds(MetaService metaService, List<String> versionIds, OperationContext context) {
        versionIds.forEach(versionId -> metaService.delete(versionId, context));
    }
}
