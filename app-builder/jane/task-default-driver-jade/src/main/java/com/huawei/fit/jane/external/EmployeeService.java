/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.external;

import java.util.List;
import java.util.Map;

/**
 * 调用IData接口的代理类。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
public interface EmployeeService {
    SearchEmployeeResponse searchEmployee(String endpoint, String dynamicToken, String searchValue);

    GetPersonInfoResponse getPersonInfo(String endpoint, String dynamicToken, Map<String, Object> map);

    /**
     * 获取查询员工返回结构体。
     *
     * @author 陈镕希 c00572808
     * @since 2023-08-07
     */
    class SearchEmployeeResponse {
        private List<Member> members;

        public List<Member> getMembers() {
            return members;
        }

        public void setMembers(List<Member> members) {
            this.members = members;
        }

        public static class Member {
            /**
             * e.g. 00123456
             */
            private String employee_Number;

            /**
             * e.g. z00123456
             */
            private String w3Account;

            /**
             * e.g. 张三
             */
            private String last_name;

            /**
             * e.g. 数据存储软件特战队（模块）
             */
            private String dept;

            /**
             * e.g. zhangsan 00123456
             */
            private String person_notes_cn;

            /**
             * e.g. Zhang San
             */
            private String english_name;

            /**
             * e.g. zhangsan@huawei.com
             */
            private String person_mail;

            public String getEmployee_Number() {
                return employee_Number;
            }

            public void setEmployee_Number(String employee_Number) {
                this.employee_Number = employee_Number;
            }

            public String getW3Account() {
                return w3Account;
            }

            public void setW3Account(String w3Account) {
                this.w3Account = w3Account;
            }

            public String getLast_name() {
                return last_name;
            }

            public void setLast_name(String last_name) {
                this.last_name = last_name;
            }

            public String getDept() {
                return dept;
            }

            public void setDept(String dept) {
                this.dept = dept;
            }

            public String getPerson_notes_cn() {
                return person_notes_cn;
            }

            public void setPerson_notes_cn(String person_notes_cn) {
                this.person_notes_cn = person_notes_cn;
            }

            public String getEnglish_name() {
                return english_name;
            }

            public void setEnglish_name(String english_name) {
                this.english_name = english_name;
            }

            public String getPerson_mail() {
                return person_mail;
            }

            public void setPerson_mail(String person_mail) {
                this.person_mail = person_mail;
            }
        }
    }

    /**
     * 获取查询员工详情返回结构体。
     *
     * @author 陈镕希 c00572808
     * @since 2023-10-07
     */
    class GetPersonInfoResponse {
        private PersonInfo data;

        public PersonInfo getData() {
            return data;
        }

        public void setData(PersonInfo data) {
            this.data = data;
        }

        public static class PersonInfo {
            /**
             * e.g. 00123456
             */
            private String employee_number;

            /**
             * e.g. z00123456
             */
            private String uid;

            /**
             * e.g. uuid~eVdYXxxx
             */
            private String uuid;

            /**
             * e.g. 张三
             */
            private String full_name;

            /**
             * e.g. 张三
             */
            private String last_name;

            /**
             * e.g. Zhang San
             */
            private String english_name;

            /**
             * e.g. zhangsan 00123456
             */
            private String aducn;

            /**
             * e.g. 167802377736666
             */
            private String global_user_id;

            public String getEmployee_number() {
                return employee_number;
            }

            public void setEmployee_number(String employee_number) {
                this.employee_number = employee_number;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getFull_name() {
                return full_name;
            }

            public void setFull_name(String full_name) {
                this.full_name = full_name;
            }

            public String getLast_name() {
                return last_name;
            }

            public void setLast_name(String last_name) {
                this.last_name = last_name;
            }

            public String getEnglish_name() {
                return english_name;
            }

            public void setEnglish_name(String english_name) {
                this.english_name = english_name;
            }

            public String getAducn() {
                return aducn;
            }

            public void setAducn(String aducn) {
                this.aducn = aducn;
            }

            public String getGlobal_user_id() {
                return global_user_id;
            }

            public void setGlobal_user_id(String global_user_id) {
                this.global_user_id = global_user_id;
            }
        }
    }
}
