/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import com.huawei.fit.jane.task.gateway.EmployeeDetailVO;
import com.huawei.fit.jober.UserService;
import com.huawei.fit.jober.common.utils.UserUtil;
import com.huawei.fit.jober.entity.user.EmployeeDetail;

import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

/**
 * {@link UserService}的实现类
 *
 * @author 陈镕希
 * @since 2023-11-14
 */
@Alias("Jane-User")
@Component
public class UserFitable implements UserService {
    private final UserUtil userUtil;

    public UserFitable(UserUtil userUtil) {
        this.userUtil = userUtil;
    }

    @Override
    @Fitable(id = "b4780d4e9c134d70b10b4dbab52e45c4")
    public EmployeeDetail getEmployeeDetail(String employeeNumber, String uid, String uuid, String globalUserId) {
        return this.convert(userUtil.getEmployeeDetail(employeeNumber, uid, uuid, globalUserId));
    }

    private EmployeeDetail convert(EmployeeDetailVO employeeDetailVO) {
        return new EmployeeDetail(employeeDetailVO.getEmployeeNumber(), employeeDetailVO.getUid(),
                employeeDetailVO.getUuid(), employeeDetailVO.getFullName(), employeeDetailVO.getLastName(),
                employeeDetailVO.getEnglishName(), employeeDetailVO.getAduCn(), employeeDetailVO.getGlobalUserId());
    }
}
