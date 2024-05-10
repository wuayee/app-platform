/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for applications.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_APPLICATION_REPO_HPP
#define FIT_REGISTRY_LISTENER_APPLICATION_REPO_HPP

#include <cstdint>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../domain/application.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为应用程序提供仓库。
 */
class ApplicationRepo : public virtual RegistryListenerElement {
public:
    /**
     * 获取指定名称和版本的应用程序。
     *
     * @param name 表示应用程序的名称的字符串。
     * @param version 表示应用程序的版本的字符串。
     * @param createNew 若为 true，则当不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在对应的应用程序，则为指向该实例的指针；否则为 nullptr。
     */
    virtual ApplicationPtr Get(const ::Fit::string& name, const ::Fit::string& version,
        const map<string, string>& extensions, bool createNew) = 0;

    /**
     * 移除指定应用程序。
     *
     * @param name 表示待移除的应用程序的名称的字符串。
     * @param version 表示待移除的应用程序的版本的字符串。
     * @return 若存在该应用程序，则为指向已移除的应用程序的指针；否则为 nullptr。
     */
    virtual ApplicationPtr Remove(const Fit::string& name, const Fit::string& version) = 0;

    /**
     * 查询应用程序的数量。
     *
     * @return 表示应用程序数量的32位无符号整数。
     */
    virtual uint32_t Count() const = 0;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_APPLICATION_REPO_HPP
