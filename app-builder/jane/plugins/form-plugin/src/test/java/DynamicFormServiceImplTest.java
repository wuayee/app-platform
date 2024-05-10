/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.dynamicform.common.PageResponse;
import com.huawei.fit.dynamicform.condition.FormQueryCondition;
import com.huawei.fit.dynamicform.condition.PaginationCondition;
import com.huawei.fit.dynamicform.dto.DynamicFormDto;
import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.elsa.generable.GraphExposeService;
import com.huawei.fit.elsa.generable.entity.Graph;
import com.huawei.fit.elsa.generable.entity.GraphParam;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.form.mapper.FormMapper;
import com.huawei.fit.jober.form.service.impl.DynamicFormServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class DynamicFormServiceImplTest {
    private final String tenantId = "tenantId";
    private final String userName = "userName";
    private final String userW3Account = "a00000001";
    private final String userInfo = String.join(" ", userName, userW3Account);
    private final String formId = "a1b2c3d4e5f6";
    private final String formVersion = "1.0.0";
    private final String formName = "some random title";
    private final String formJsonString =
            String.format("{\"id\":\"%s\",\"version\":\"%s\",\"title\":\"%s\"}", formId, formVersion, formName);
    private final FormMetaQueryParameter formParameter = new FormMetaQueryParameter(this.formId, this.formVersion);
    private final ArgumentMatcher<GraphParam> elsaDataMatcher =
            graphParam -> graphParam.getGraphId().equals(formId) && graphParam.getVersion().equals(formVersion)
                    && graphParam.getJson().equals(formJsonString);
    private final ArgumentMatcher<GraphParam> elsaPrimaryKeyMatcher =
            graphParam -> graphParam.getGraphId().equals(formId) && graphParam.getVersion().equals(formVersion);
    private DynamicFormEntity formEntity;
    private DynamicFormDetailEntity formDetailEntity;
    private DynamicFormServiceImpl formService;
    @Mock
    private FormMapper mapperMock;
    @Mock
    private GraphExposeService elsaClientMock;
    @Mock
    private OperationContext contextMock;

    @BeforeEach
    void setUp() {
        formService = new DynamicFormServiceImpl(mapperMock, elsaClientMock, "");
        formEntity = new DynamicFormEntity(formId, formVersion, tenantId, formName, null, userInfo, null, null);
        formDetailEntity = new DynamicFormDetailEntity(formEntity, formJsonString);
    }

    @Test
    void shouldSuccessWhenQueryFormWithConditionWithoutContextSuccess() {
        FormQueryCondition cond = new FormQueryCondition();
        cond.setFormName(formName);
        PaginationCondition page = new PaginationCondition();
        long resultCount = 10L;
        List<DynamicFormEntity> sqlResult = Collections.singletonList(formEntity);
        PageResponse<DynamicFormEntity> result = new PageResponse<>(resultCount, sqlResult);
        when(mapperMock.selectWithCondition(eq(tenantId), eq(cond), eq(page))).thenReturn(sqlResult);
        when(mapperMock.countWithCondition(eq(tenantId), eq(cond))).thenReturn(resultCount);
        Assertions.assertEquals(formService.queryFormWithCondition(tenantId, cond, page), result);
        verify(mapperMock, times(1)).selectWithCondition(eq(tenantId), eq(cond), eq(page));
    }

    @Test
    void shouldSuccessWhenQueryFormWithConditionWithContextSuccess() {
        FormQueryCondition cond = new FormQueryCondition();
        cond.setFormName(formName);
        PaginationCondition page = new PaginationCondition();
        long resultCount = 10L;
        List<DynamicFormEntity> sqlResult = Collections.singletonList(formEntity);
        PageResponse<DynamicFormEntity> result = new PageResponse<>(resultCount, sqlResult);
        when(contextMock.getTenantId()).thenReturn(tenantId);
        when(mapperMock.selectWithCondition(eq(tenantId), eq(cond), eq(page))).thenReturn(sqlResult);
        when(mapperMock.countWithCondition(eq(tenantId), eq(cond))).thenReturn(resultCount);
        Assertions.assertEquals(formService.queryFormWithCondition(cond, page, contextMock), result);
        verify(mapperMock, times(1)).selectWithCondition(eq(tenantId), eq(cond), eq(page));
    }

    @Test
    void shouldSuccessWhenQueryFormDetailByPrimaryKeySuccess() {
        DynamicFormDetailEntity expectResult = new DynamicFormDetailEntity(formEntity, formJsonString);
        when(mapperMock.selectByPrimaryKey(eq(formId), eq(formVersion))).thenReturn(formEntity);
        when(elsaClientMock.get(argThat(elsaPrimaryKeyMatcher), eq(contextMock))).thenReturn(formJsonString);
        Assertions.assertEquals(formService.queryFormDetailByPrimaryKey(formId, formVersion, contextMock),
                expectResult);
        verify(mapperMock, times(1)).selectByPrimaryKey(eq(formId), eq(formVersion));
        verify(elsaClientMock, times(1)).get(argThat(elsaPrimaryKeyMatcher), eq(contextMock));
    }

    @Test
    void shouldFailWhenQueryFormDetailByPrimaryKeyWithSqlFail() {
        when(mapperMock.selectByPrimaryKey(eq(formId), eq(formVersion))).thenReturn(null);

        Assertions.assertNull(formService.queryFormDetailByPrimaryKey(formId, formVersion, contextMock));
        verify(mapperMock, times(1)).selectByPrimaryKey(eq(formId), eq(formVersion));
        verify(elsaClientMock, never()).get(any(), any());
    }

    @Test
    void shouldSuccessWhenQueryFormDetailByPrimaryKeyAndMapSuccess() {
        List<Map<FormMetaQueryParameter, DynamicFormDetailEntity>> expectResult =
                Collections.singletonList(new HashMap<FormMetaQueryParameter, DynamicFormDetailEntity>() {{
                    put(formParameter, formDetailEntity);
                }});
        List<FormMetaQueryParameter> parameters = Collections.singletonList(this.formParameter);
        List<DynamicFormEntity> entities = Collections.singletonList(this.formEntity);
        ArgumentMatcher<List<GraphParam>> elsaParamsMatcher =
                graphParams -> graphParams.get(0).getGraphId().equals(this.formId)
                        && graphParams.get(0).getVersion().equals(this.formVersion);
        Graph elsaGraph = new Graph();
        elsaGraph.setGraphId(this.formId);
        elsaGraph.setVersion(this.formVersion);
        elsaGraph.setJson(this.formJsonString);
        List<Graph> elsaGraphs = Collections.singletonList(elsaGraph);
        when(this.mapperMock.selectFormByPrimaryKeyList(eq(parameters))).thenReturn(entities);
        when(this.elsaClientMock.list(argThat(elsaParamsMatcher), eq(this.contextMock)))
                .thenReturn(elsaGraphs);
        Assertions.assertEquals(this.formService.queryFormDetailByPrimaryKeyAndMap(parameters, this.contextMock),
                expectResult);
        verify(this.mapperMock, times(1)).selectFormByPrimaryKeyList(eq(parameters));
        verify(this.elsaClientMock, times(1))
                .list(argThat(elsaParamsMatcher), eq(contextMock));
    }

    @Test
    void shouldReturnEmptyWhenQueryFormDetailByPrimaryKeyAndMapWithSqlReturnEmpty() {
        List<FormMetaQueryParameter> parameters = Collections.singletonList(this.formParameter);
        when(this.mapperMock.selectFormByPrimaryKeyList(eq(parameters))).thenReturn(Collections.emptyList());
        Assertions.assertEquals(this.formService.queryFormDetailByPrimaryKeyAndMap(parameters, this.contextMock),
                Collections.emptyList());
        verify(this.mapperMock, times(1)).selectFormByPrimaryKeyList(eq(parameters));
        verify(this.elsaClientMock, never()).get(any(), any());
    }

    @Test
    void shouldSuccessWhenSaveFormSuccess() {
        formEntity.setUpdateUser(userInfo);
        DynamicFormDetailEntity detailEntity = new DynamicFormDetailEntity(formEntity, formJsonString);
        ArgumentMatcher<DynamicFormEntity> updateMatcher =
                entity -> entity.getId().equals(formId) && entity.getVersion().equals(formVersion)
                        && entity.getFormName().equals(formName) && entity.getUpdateUser().equals(userInfo);
        when(contextMock.getName()).thenReturn(userName);
        when(contextMock.getW3Account()).thenReturn(userW3Account);
        when(elsaClientMock.save(argThat(elsaDataMatcher), eq(contextMock))).thenReturn(0);

        Assertions.assertTrue(formService.saveForm(detailEntity, contextMock));
        verify(mapperMock, times(1)).insertOrUpdateByPrimaryKey(argThat(updateMatcher));
        verify(elsaClientMock, times(1)).save(argThat(elsaDataMatcher), eq(contextMock));
    }

    @Test
    void shouldFailWhenSaveFormWithElsaFail() {
        when(contextMock.getName()).thenReturn(userName);
        when(contextMock.getTenantId()).thenReturn(tenantId);
        when(elsaClientMock.save(argThat(elsaDataMatcher), eq(contextMock))).thenReturn(-1);

        Assertions.assertFalse(formService.saveForm(formDetailEntity, contextMock));
        verify(mapperMock, never()).insertOrUpdateByPrimaryKey(any());
    }

    @Test
    void shouldFailWhenUpdateFormWithElsaDeleteFail() {
        formEntity.setUpdateUser(userInfo);
        DynamicFormDetailEntity detailEntity = new DynamicFormDetailEntity(formEntity, formJsonString);
        when(elsaClientMock.save(argThat(elsaDataMatcher), eq(contextMock))).thenReturn(-1);

        Assertions.assertFalse(formService.saveForm(detailEntity, contextMock));
        verify(mapperMock, never()).insertOrUpdateByPrimaryKey(any());
        verify(elsaClientMock, times(1)).save(argThat(elsaDataMatcher), eq(contextMock));
    }

    @Test
    void shouldSuccessWhenDeleteFormSuccess() {
        DynamicFormDto formDto = DynamicFormDto.builder().id(formId).version(formVersion).build();
        when(elsaClientMock.delete(argThat(elsaPrimaryKeyMatcher), eq(contextMock))).thenReturn(0);

        Assertions.assertTrue(formService.deleteForm(formDto, contextMock));
        verify(mapperMock, times(1)).deleteByPrimaryKey(eq(formId), eq(formVersion));
        verify(elsaClientMock, times(1)).delete(argThat(elsaPrimaryKeyMatcher), eq(contextMock));
    }

    @Test
    void shouldFailWhenDeleteFormWithElsaFail() {
        DynamicFormDto formDto = DynamicFormDto.builder().id(formId).version(formVersion).build();
        when(elsaClientMock.delete(argThat(elsaPrimaryKeyMatcher), eq(contextMock))).thenReturn(-1);

        Assertions.assertFalse(formService.deleteForm(formDto, contextMock));
        verify(mapperMock, never()).deleteByPrimaryKey(any(), any());
        verify(elsaClientMock, times(1)).delete(argThat(elsaPrimaryKeyMatcher), eq(contextMock));
    }
}
