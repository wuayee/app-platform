/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for application fitable relations for fitable.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_FITABLE_APPLICATION_REPO_HPP
#define FIT_REGISTRY_LISTENER_FITABLE_APPLICATION_REPO_HPP

#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../domain/application_fitable.hpp"

#include <fit/stl/vector.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为泛化服务提供其所关联的应用程序的仓库。
 */
class FitableApplicationRepo : public virtual RegistryListenerElement {
public:
    /**
     * 获取仓库所属的服务实现。
     *
     * @return 表示指向所属服务实现的指针。
     */
    virtual FitablePtr GetFitable() const = 0;

    /**
     * 获取服务实现与指定名称和版本的应用程序的关系。
     *
     * @param application 表示指向与当前服务实现关联的应用程序的指针。
     * @param createNew 若为 true，则当关系不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在与该应用程序的关联关系，则为指向关联关系的指针；否则为 nullptr。
     */
    virtual ApplicationFitablePtr Get(const ApplicationPtr& application, bool createNew) = 0;

    /**
     * 移除当前服务实现与指定应用程序的关联关系。
     *
     * @param application 表示指向待移除的与当前服务实现关联的应用程序的指针。
     * @return 若存在于该应用程序的关联关系，则为指向已移除的关联关系的指针；否则为 nullptr。
     */
    virtual ApplicationFitablePtr Remove(const ApplicationPtr& application) = 0;

    /**
     * 移除当前应用程序与指定服务实现的关联关系。
     *
     * @param fitable 表示指向待移除的与当前应用程序关联的服务实现的指针。
     * @return 若存在于该服务实现的关联关系，则为指向已移除的关联关系的指针；否则为 nullptr。
     */
    virtual ApplicationFitablePtr RemoveOnlyMyself(const ApplicationPtr& application) = 0;

    /**
     * 列出包含的所有关联关系。
     *
     * @return 表示指向关联关系的指针的列表。
     */
    virtual Fit::vector<ApplicationFitablePtr> List() const = 0;

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

#endif // FIT_REGISTRY_LISTENER_FITABLE_APPLICATION_REPO_HPP
