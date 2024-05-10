/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.gateway.EmployeeDetailVO;
import com.huawei.fit.jane.task.gateway.PersonService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link UserUtil}对应测试类
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-08
 */
@ExtendWith(MockitoExtension.class)
class UserUtilTest {
    @Mock
    PersonService iDataDelegate;

    private UserUtil userUtil;

    private static void assertEmployeeDetailVo(EmployeeDetailVO actual) {
        Assertions.assertEquals("00123456", actual.getEmployeeNumber());
        Assertions.assertEquals("z00123456", actual.getUid());
        Assertions.assertEquals("uuid~eVdYXxxx", actual.getUuid());
        Assertions.assertEquals("张三", actual.getFullName());
        Assertions.assertEquals("Zhang San", actual.getEnglishName());
        Assertions.assertEquals("zhangsan 00123456", actual.getAduCn());
        Assertions.assertEquals("167802377736666", actual.getGlobalUserId());
    }

    @BeforeEach
    void before() {
        userUtil = new UserUtil(iDataDelegate);
        when(iDataDelegate.getPersonInfo(any(), any(), any(), any())).thenReturn(this.constructEmployeeDetailVO());
    }

    private EmployeeDetailVO constructEmployeeDetailVO() {
        EmployeeDetailVO employee = new EmployeeDetailVO();
        employee.setEmployeeNumber("00123456");
        employee.setUid("z00123456");
        employee.setUuid("uuid~eVdYXxxx");
        employee.setFullName("张三");
        employee.setEnglishName("Zhang San");
        employee.setAduCn("zhangsan 00123456");
        employee.setGlobalUserId("167802377736666");
        return employee;
    }

    @Nested
    @DisplayName("测试获取员工详情接口")
    class TestGetEmployeeDetail {
        @Test
        @DisplayName("使用employeeNumber调用，触发iDataDelegate")
        void invokeByEmployeeNumberThenInvokeIDataDelegate() {
            EmployeeDetailVO actual = userUtil.getEmployeeDetail("eN", null, null, null);
            verify(iDataDelegate, times(1)).getPersonInfo("eN", null, null, null);
            assertEmployeeDetailVo(actual);
        }

        @Test
        @DisplayName("重复使用employeeNumber调用，仅触发iDataDelegate1次")
        void invokeTwiceThenInvokeIDataDelegateOnce() {
            userUtil.getEmployeeDetail("eN", null, null, null);
            EmployeeDetailVO actual = userUtil.getEmployeeDetail("eN", null, null, null);
            verify(iDataDelegate, times(1)).getPersonInfo("eN", null, null, null);
            assertEmployeeDetailVo(actual);
        }

        @Test
        @DisplayName("使用uid调用，触发iDataDelegate")
        void invokeByUidThenInvokeIDataDelegate() {
            EmployeeDetailVO actual = userUtil.getEmployeeDetail(null, "uid", null, null);
            verify(iDataDelegate, times(1)).getPersonInfo(null, "uid", null, null);
            assertEmployeeDetailVo(actual);
        }

        @Test
        @DisplayName("使用uuid调用，触发iDataDelegate")
        void invokeByUuidThenInvokeIDataDelegate() {
            EmployeeDetailVO actual = userUtil.getEmployeeDetail(null, null, "uuid", null);
            verify(iDataDelegate, times(1)).getPersonInfo(null, null, "uuid", null);
            assertEmployeeDetailVo(actual);
        }

        @Test
        @DisplayName("使用globalUserId调用，触发iDataDelegate")
        void invokeByGlobalUserIdThenInvokeIDataDelegate() {
            EmployeeDetailVO actual = userUtil.getEmployeeDetail(null, null, null, "globalUserId");
            verify(iDataDelegate, times(1)).getPersonInfo(null, null, null, "globalUserId");
            assertEmployeeDetailVo(actual);
        }
    }
}