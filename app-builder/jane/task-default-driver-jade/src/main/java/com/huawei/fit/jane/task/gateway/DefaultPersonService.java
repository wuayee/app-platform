/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fit.jane.external.EmployeeService;
import com.huawei.fit.jane.external.TianzhouEmployeeService;
import com.huawei.fit.jane.external.W3Client;
import com.huawei.fit.jane.task.util.Cache;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link PersonService} 的默认实现。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-03
 */
@Component
public class DefaultPersonService implements PersonService {
    @Override
    public List<EmployeeVO> searchEmployeeInfo(String keyword, String uid) {
        return Collections.emptyList();
    }

    @Override
    public EmployeeDetailVO getPersonInfo(String employeeNumber, String uid, String uuid, String globalUserId) {
        return new EmployeeDetailVO();
    }
}
