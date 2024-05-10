/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : 提供访问db的username和password相关信息
 * Author       : w00561424
 * Create       : 2023/07/29
 * Notes:       :
 */
#ifndef FIT_DB_USER_INFO_MANAGER_H
#define FIT_DB_USER_INFO_MANAGER_H
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
namespace Fit {
struct DbInfo {
    Fit::string module {};
    Fit::string userName {};
    Fit::string passwordKey {};
    Fit::string password {};
    Fit::string dbName {};
    int32_t poolSize {};
};

class FitDbInfoManager {
public:
    virtual DbInfo GetDbInfo(const Fit::string& module) = 0;
    static Fit::shared_ptr<FitDbInfoManager> Instance();
};
}
#endif