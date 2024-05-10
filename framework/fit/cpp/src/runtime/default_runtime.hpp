/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/4/15
 * Notes:       :
 */

#ifndef FIT_DEFAULT_RUNTIME_HPP
#define FIT_DEFAULT_RUNTIME_HPP

#include <fit/internal/runtime/runtime.hpp>
#include <fit/stl/vector.hpp>

namespace Fit {
class DefaultRuntime : public Runtime {
public:
    DefaultRuntime() = default;
    ~DefaultRuntime() override = default;

    bool Start() override;
    bool Stop() override;
    void AddElement(unique_ptr<RuntimeElement> element) override;

    bool GetElementAnyOf(const std::function<bool(RuntimeElement*)>& condition) override;

private:
    using ElementInfo = unique_ptr<RuntimeElement>;
    vector<ElementInfo> elements_;
};
}
#endif // FIT_DEFAULT_RUNTIME_HPP
