/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.external;

import java.util.List;

/**
 * 调用天舟接口的代理类
 *
 * @author lWX1301876
 * @since 2023-12-12 16:35
 */
public interface TianzhouEmployeeService {
    SearchEmployeeUidResponse searchEmployeeUid(String endpoint, String appId, String appKey, String keyword,
            String uid);

    /**
     * 获取查询员工返回结构体
     */
    class SearchEmployeeUidResponse {
        private List<UidMember> data;

        public List<UidMember> getData() {
            return data;
        }

        public void setData(List<UidMember> data) {
            this.data = data;
        }

        public static class UidMember {

            private String globalUserId;

            /**
             * e.g. 张三
             */
            private String name;

            /**
             * e.g. Zhang San
             */
            private String enname;

            /**
             * e.g. 张三 00123456
             */
            private String allName;

            /**
             * e.g. z00123456
             */
            private String uid;

            /**
             * e.g. 00123456
             */
            private String employeeNumber;

            /**
             * e.g. zhangsan 00123456
             */
            private String ucn;

            private String oldEmployeeNumber;

            /**
             * e.g. zhangsan@huawei.com
             */
            private String email;

            private String mobile;

            /**
             * e.g. 数据存储软件特战队（模块）
             */
            private String departmentName;

            private String l0Name;

            private String l0DeptCode;

            private String l1Name;

            private String l1DeptCode;

            private String l2Name;

            private String l2DeptCode;

            private String l3Name;

            private String l3DeptCode;

            private String l4Name;

            private String l4DeptCode;

            private String l5Name;

            private String l5DeptCode;

            private String l6Name;

            private String l6DeptCode;

            private String lastUpdateDate;

            private String employedFlag;

            private String gradeValue;

            private String appointPos;

            private String roles;

            private String customRoles;

            public String getGlobalUserId() {
                return globalUserId;
            }

            public void setGlobalUserId(String globalUserId) {
                this.globalUserId = globalUserId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getEnname() {
                return enname;
            }

            public void setEnname(String enname) {
                this.enname = enname;
            }

            public String getAllName() {
                return allName;
            }

            public void setAllName(String allName) {
                this.allName = allName;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getEmployeeNumber() {
                return employeeNumber;
            }

            public void setEmployeeNumber(String employeeNumber) {
                this.employeeNumber = employeeNumber;
            }

            public String getUcn() {
                return ucn;
            }

            public void setUcn(String ucn) {
                this.ucn = ucn;
            }

            public String getOldEmployeeNumber() {
                return oldEmployeeNumber;
            }

            public void setOldEmployeeNumber(String oldEmployeeNumber) {
                this.oldEmployeeNumber = oldEmployeeNumber;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getDepartmentName() {
                return departmentName;
            }

            public void setDepartmentName(String departmentName) {
                this.departmentName = departmentName;
            }

            public String getL0Name() {
                return l0Name;
            }

            public void setL0Name(String l0Name) {
                this.l0Name = l0Name;
            }

            public String getL0DeptCode() {
                return l0DeptCode;
            }

            public void setL0DeptCode(String l0DeptCode) {
                this.l0DeptCode = l0DeptCode;
            }

            public String getL1Name() {
                return l1Name;
            }

            public void setL1Name(String l1Name) {
                this.l1Name = l1Name;
            }

            public String getL1DeptCode() {
                return l1DeptCode;
            }

            public void setL1DeptCode(String l1DeptCode) {
                this.l1DeptCode = l1DeptCode;
            }

            public String getL2Name() {
                return l2Name;
            }

            public void setL2Name(String l2Name) {
                this.l2Name = l2Name;
            }

            public String getL2DeptCode() {
                return l2DeptCode;
            }

            public void setL2DeptCode(String l2DeptCode) {
                this.l2DeptCode = l2DeptCode;
            }

            public String getL3Name() {
                return l3Name;
            }

            public void setL3Name(String l3Name) {
                this.l3Name = l3Name;
            }

            public String getL3DeptCode() {
                return l3DeptCode;
            }

            public void setL3DeptCode(String l3DeptCode) {
                this.l3DeptCode = l3DeptCode;
            }

            public String getL4Name() {
                return l4Name;
            }

            public void setL4Name(String l4Name) {
                this.l4Name = l4Name;
            }

            public String getL4DeptCode() {
                return l4DeptCode;
            }

            public void setL4DeptCode(String l4DeptCode) {
                this.l4DeptCode = l4DeptCode;
            }

            public String getL5Name() {
                return l5Name;
            }

            public void setL5Name(String l5Name) {
                this.l5Name = l5Name;
            }

            public String getL5DeptCode() {
                return l5DeptCode;
            }

            public void setL5DeptCode(String l5DeptCode) {
                this.l5DeptCode = l5DeptCode;
            }

            public String getL6Name() {
                return l6Name;
            }

            public void setL6Name(String l6Name) {
                this.l6Name = l6Name;
            }

            public String getL6DeptCode() {
                return l6DeptCode;
            }

            public void setL6DeptCode(String l6DeptCode) {
                this.l6DeptCode = l6DeptCode;
            }

            public String getLastUpdateDate() {
                return lastUpdateDate;
            }

            public void setLastUpdateDate(String lastUpdateDate) {
                this.lastUpdateDate = lastUpdateDate;
            }

            public String getEmployedFlag() {
                return employedFlag;
            }

            public void setEmployedFlag(String employedFlag) {
                this.employedFlag = employedFlag;
            }

            public String getGradeValue() {
                return gradeValue;
            }

            public void setGradeValue(String gradeValue) {
                this.gradeValue = gradeValue;
            }

            public String getAppointPos() {
                return appointPos;
            }

            public void setAppointPos(String appointPos) {
                this.appointPos = appointPos;
            }

            public String getRoles() {
                return roles;
            }

            public void setRoles(String roles) {
                this.roles = roles;
            }

            public String getCustomRoles() {
                return customRoles;
            }

            public void setCustomRoles(String customRoles) {
                this.customRoles = customRoles;
            }
        }
    }
}
