/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.fitable;

import modelengine.fit.jane.task.gateway.EmployeeDetailVO;
import modelengine.fit.jober.common.utils.UserUtil;

import modelengine.fit.jober.UserService;
import modelengine.fit.jober.entity.user.EmployeeDetail;
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
