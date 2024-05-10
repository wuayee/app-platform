/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for fitables associated with specific application.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_APPLICATION_FITABLE_REPO_HPP
#define FIT_REGISTRY_LISTENER_APPLICATION_FITABLE_REPO_HPP

#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../domain/application_fitable.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为应用程序提供其所关联的服务实现的仓库。
 */
class ApplicationFitableRepo : public virtual RegistryListenerElement {
public:
    /**
     * 获取仓库所属的应用程序。
     *
     * @return 表示指向所属应用程序的指针。
     */
    virtual ApplicationPtr GetApplication() const = 0;

    /**
     * 获取当前应用程序与指定服务实现的关联关系。
     *
     * @param fitable 表示指向与当前应用程序关联的服务实现的指针。
     * @param createNew 若为 true，则当关联关系不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在与该服务实现的关联关系，则为指向该关联关系的指针；否则为 nullptr。
     */
    virtual ApplicationFitablePtr Get(const FitablePtr& fitable, bool createNew) = 0;

    /**
     * 移除当前应用程序与指定服务实现的关联关系。
     *
     * @param fitable 表示指向待移除的与当前应用程序关联的服务实现的指针。
     * @return 若存在于该服务实现的关联关系，则为指向已移除的关联关系的指针；否则为 nullptr。
     */
    virtual ApplicationFitablePtr Remove(const FitablePtr& fitable) = 0;

    /**
     * 移除当前应用程序与指定服务实现的关联关系。
     *
     * @param fitable 表示指向待移除的与当前应用程序关联的服务实现的指针。
     * @return 若存在于该服务实现的关联关系，则为指向已移除的关联关系的指针；否则为 nullptr。
     */
    virtual ApplicationFitablePtr RemoveOnlyMyself(const FitablePtr& fitable) = 0;

    /**
     * 附加一个已知的关联关系。
     *
     * @param relation 表示指向待附加的关联关系的指针。
     */
    virtual void Attach(ApplicationFitablePtr relation) = 0;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_APPLICATION_FITABLE_REPO_HPP
