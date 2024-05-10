/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for fitables.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_FITABLE_REPO_HPP
#define FIT_REGISTRY_LISTENER_FITABLE_REPO_HPP

#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../domain/fitable.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
class FitableRepo : public virtual RegistryListenerElement {
public:
    /**
     * 获取仓库所属的泛化服务。
     *
     * @return 表示指向所属的泛化服务的指针。
     */
    virtual GenericablePtr GetGenericable() const = 0;

    /**
     * 获取指定名称及版本的服务实现。
     *
     * @param id 表示服务实现的唯一标识的字符串。
     * @param version 表示服务实现的版本的唯一标识的字符串。
     * @param createNew 若为 true，则当未找到对应服务实现时创建新的实例；否则直接返回 nullptr。
     * @return 若找到对应的服务实现，则为指向该实例的指针；否则为 nullptr。
     */
    virtual FitablePtr Get(const Fit::string& id, const Fit::string& version, bool createNew) = 0;

    /**
     * 移除指定唯一标识和版本的服务实现。
     *
     * @param id 表示待移除的服务实现的唯一标识的字符串。
     * @param version 表示待移除的服务实现的版本的字符串。
     * @return 若存在该服务实现，则为指向已移除的服务实现的指针；否则为 nullptr。
     */
    virtual FitablePtr Remove(const Fit::string& id, const Fit::string& version) = 0;

    /**
     * 列出所有服务实现的实例。
     *
     * @return 表示指向服务实现的指针的集合。
     */
    virtual Fit::vector<FitablePtr> List() const = 0;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_FITABLE_REPO_HPP
