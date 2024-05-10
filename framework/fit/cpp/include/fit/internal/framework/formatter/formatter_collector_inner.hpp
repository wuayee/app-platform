/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/14
 * Notes:       :
 */

#ifndef FORMATTERCOLLECTORINNER_HPP
#define FORMATTERCOLLECTORINNER_HPP

#include <functional>
#include <fit/external/framework/formatter/formatter_collector.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {
class FormatterMetaReceiver {
public:
    std::function<void(const FormatterMetaPtrList &)> Register;
    std::function<void(const FormatterMetaPtrList &)> UnRegister;
};

__attribute__ ((visibility ("default"))) FormatterMetaPtrList PopFormatterMetaCache();
/**
 * change the receiver
 * @param target : new receiver
 * @return old receiver
 */
__attribute__ ((visibility ("default"))) FormatterMetaReceiver *FormatterMetaFlowTo(FormatterMetaReceiver *target);
}
}
}
#endif // FORMATTERCOLLECTORINNER_HPP
