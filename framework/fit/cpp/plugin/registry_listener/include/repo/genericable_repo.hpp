/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for genericables.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_GENERICABLE_REPO_HPP
#define FIT_REGISTRY_LISTENER_GENERICABLE_REPO_HPP

#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../domain/genericable.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为泛化服务提供仓库。
 */
class GenericableRepo : public virtual RegistryListenerElement {
public:
    /**
     * 获取指定名称及版本的泛化服务。
     *
     * @param id 表示泛化服务的唯一标识的字符串。
     * @param version 表示泛化服务的版本的唯一标识的字符串。
     * @param createNew 若为 true，则当未找到对应泛化服务时创建新的实例；否则直接返回 nullptr。
     * @return 若找到对应的泛化服务，则为指向该实例的指针；否则为 nullptr。
     */
    virtual GenericablePtr Get(const Fit::string& id, const Fit::string& version, bool createNew) = 0;

    /**
     * 移除指定唯一标识和版本的泛化服务。
     *
     * @param id 表示待移除的泛化服务的唯一标识的字符串。
     * @param version 表示待移除的泛化服务的版本的字符串。
     * @return 若存在该泛化服务，则为指向已移除的泛化服务的指针；否则为 nullptr。
     */
    virtual GenericablePtr Remove(const Fit::string& id, const Fit::string& version) = 0;

    /**
     * 列出所有泛化服务的实例。
     *
     * @return 表示指向泛化服务的指针的集合。
     */
    virtual Fit::vector<GenericablePtr> List() const = 0;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_GENERICABLE_REPO_HPP
