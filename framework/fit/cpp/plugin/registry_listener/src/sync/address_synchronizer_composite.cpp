/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for address synchronizer composite.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#include <sync/address_synchronizer_composite.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

void AddressSynchronizerComposite::Start()
{
    for (auto& synchronizer : synchronizers_) {
        synchronizer->Start();
    }
}

void AddressSynchronizerComposite::Stop()
{
    for (auto& synchronizer : synchronizers_) {
        synchronizer->Stop();
    }
}

void AddressSynchronizerComposite::Add(AddressSynchronizerPtr synchronizer)
{
    synchronizers_.push_back(std::move(synchronizer));
}
