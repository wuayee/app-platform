/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fitframework.annotation.Component;

import java.util.Collections;
import java.util.List;

/**
 * {@link PersonService} 的a3000默认实现。
 *
 * @author s00664640
 * @since 2023/11/28
 */
@Component
public class BasePersonService implements PersonService {
    @Override
    public List<EmployeeVO> searchEmployeeInfo(String keyword, String uid) {
        return Collections.emptyList();
    }

    @Override
    public EmployeeDetailVO getPersonInfo(String employeeNumber, String uid, String uuid, String globalUserId) {
        return new EmployeeDetailVO();
    }
}
