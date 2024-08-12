/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.huawei.fit.dynamicform.common.PageResponse;
import com.huawei.fit.dynamicform.condition.FormQueryCondition;
import com.huawei.fit.dynamicform.condition.PaginationCondition;
import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.http.protocol.support.DefaultMessageHeaders;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.gateway.User;
import com.huawei.fit.jober.form.controller.FormController;
import com.huawei.fit.jober.form.dto.FormDetailDto;
import com.huawei.fit.jober.form.dto.FormDto;
import com.huawei.fit.jober.form.exception.FormErrCode;
import com.huawei.fit.jober.form.service.impl.DynamicFormServiceImpl;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class FormControllerTest {
    final private String tenantId = "10010";
    final private String formId = "a1b2c3d4e5f6";
    final private String formVersion = "1.0.0";
    final private String formName = "someRandomName";
    final private String userName = "userName";
    final private String userW3Account = "a00000001";
    final private String userInfo = String.join(" ", userName, userW3Account);
    final private DynamicFormEntity formEntity =
            new DynamicFormEntity(formId, formVersion, tenantId, formName, null, userInfo, null, null);
    final private String elsaData = "{\"data\":\"elsa data\"}";
    private DynamicFormDetailEntity formDetailEntity = null;
    private FormController formController;
    @Mock
    private DynamicFormServiceImpl serviceMock;
    @Mock
    private HttpClassicServerRequest httpRequestMock;
    @Mock
    private Authenticator authenticatorMock;
    @Mock
    private User userMock;
    private MockedStatic<UserContextHolder> operationContextHolderMock;
    private final UserContext operationContext = new UserContext("Jane", "127.0.0.1", "en");

    @BeforeEach
    void setUp() {
        this.operationContextHolderMock = mockStatic(UserContextHolder.class);
        this.operationContextHolderMock.when(UserContextHolder::get).thenReturn(this.operationContext);
        formController = new FormController(authenticatorMock, serviceMock);
        formDetailEntity = new DynamicFormDetailEntity(formEntity, elsaData);
    }

    @AfterEach
    void tearDown() {
        this.operationContextHolderMock.close();
    }

    @Test
    void shouldSuccessWhenCallQueryWithConditionSuccess() {
        long successCount = 10L;
        FormQueryCondition cond = new FormQueryCondition();
        cond.setFormName(formName);
        PaginationCondition page = new PaginationCondition();
        PageResponse<DynamicFormEntity> result =
                new PageResponse<>(successCount, Collections.singletonList(formEntity));
        when(serviceMock.queryFormWithCondition(eq(tenantId), eq(cond), eq(page))).thenReturn(result);
        PageResponse<FormDto> controllerResult =
                new PageResponse<>(successCount, Collections.singletonList(new FormDto(formEntity)));

        Assertions.assertEquals(formController.queryForm(tenantId, cond, page), Rsp.ok(controllerResult));
    }

    @Test
    void shouldSuccessWhenCallQuerySingleFormSuccess() {
        DynamicFormDetailEntity expectResult = new DynamicFormDetailEntity(formEntity, elsaData);
        when(serviceMock.queryFormDetailByPrimaryKey(eq(formId), eq(formVersion), any())).thenReturn(expectResult);
        when(httpRequestMock.headers()).thenReturn(new DefaultMessageHeaders());
        when(authenticatorMock.authenticate(any())).thenReturn(userMock);

        Assertions.assertEquals(formController.queryForm(httpRequestMock, tenantId, formId, formVersion),
                Rsp.ok(new FormDetailDto(expectResult)));
    }

    @Test
    void shouldSuccessWhenCallSaveSingleFormSuccess() {
        when(serviceMock.saveForm(eq(formDetailEntity), any())).thenReturn(true);
        when(httpRequestMock.headers()).thenReturn(new DefaultMessageHeaders());
        when(authenticatorMock.authenticate(any())).thenReturn(userMock);

        Assertions.assertEquals(formController.saveForm(httpRequestMock,
                tenantId,
                formId,
                new FormDetailDto(formDetailEntity)), Rsp.ok());
    }

    @Test
    void shouldFailWhenCallSaveSingleFormFail() {
        DynamicFormDetailEntity detailEntity = new DynamicFormDetailEntity(formEntity, elsaData);
        when(serviceMock.saveForm(eq(detailEntity), any())).thenReturn(false);
        when(httpRequestMock.headers()).thenReturn(new DefaultMessageHeaders());
        when(authenticatorMock.authenticate(any())).thenReturn(userMock);

        Assertions.assertEquals(formController.saveForm(httpRequestMock,
                tenantId,
                formId,
                new FormDetailDto(detailEntity)), Rsp.err(FormErrCode.UNKNOWN));
    }

    @Test
    void shouldSuccessWhenCallDeleteSingleFormSuccess() {
        when(serviceMock.deleteForm(argThat(formDto -> formDto.getId().equals(formId) && formDto.getVersion()
                .equals(formVersion)), any())).thenReturn(true);
        when(httpRequestMock.headers()).thenReturn(new DefaultMessageHeaders());
        when(authenticatorMock.authenticate(any())).thenReturn(userMock);

        Assertions.assertEquals(formController.deleteForm(httpRequestMock, tenantId, formId, formVersion), Rsp.ok());
    }

    @Test
    void shouldFailWhenCallDeleteSingleFormFail() {
        when(serviceMock.deleteForm(argThat(formDto -> formDto.getId().equals(formId) && formDto.getVersion()
                .equals(formVersion)), any())).thenReturn(false);
        when(httpRequestMock.headers()).thenReturn(new DefaultMessageHeaders());
        when(authenticatorMock.authenticate(any())).thenReturn(userMock);

        Assertions.assertEquals(formController.deleteForm(httpRequestMock, tenantId, formId, formVersion),
                Rsp.err(FormErrCode.NOT_FOUND));
    }
}
