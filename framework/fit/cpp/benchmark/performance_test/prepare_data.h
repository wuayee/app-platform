/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/8/12 21:36
 * Notes        :
 */

#ifndef PREPARE_DATA_H
#define PREPARE_DATA_H

#include <genericable/com_huawei_fit_registry_register_fit_service/1.0.0/cplusplus/registerFitService.hpp>
#include <genericable/com_huawei_fit_registry_unregister_fit_service/1.0.0/cplusplus/unregisterFitService.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include "echo/echo.hpp"
#include "echo/echo_impl.hpp"
#include "echo/echo_converter.hpp"

class PrepareData {
public:
    explicit PrepareData(int prepareDataCount) : prepareDataCount(prepareDataCount)
    {
        printf("Prepare %d data, please wait.\n", prepareDataCount);
        fit::registry::registerFitService reg;
        fit::registry::Address address = FitSystemPropertyUtils::Address();
        std::vector<fit::registry::ServiceMeta> regFitables;
        for (int i = 0; i < prepareDataCount; ++i) {
            fit::registry::ServiceMeta meta;
            meta.fitable = Fit::Context::NewObj<fit::registry::Fitable>(reg.ctx_);
            meta.fitable->genericId = std::to_string(i) + Fit::Benchmark::Echo<0>::GENERIC_ID_TAIL;
            meta.fitable->genericVersion = "1.0.0";
            meta.fitable->fitId = std::to_string(i) + Fit::Benchmark::Echo<0>::FITABLE_ID_TAIL;
            meta.fitable->fitVersion = "1.0.0";
            meta.serviceName = "";
            meta.pluginName = "";
            regFitables.push_back(meta);

            if (regFitables.size() == 200 || i == prepareDataCount - 1) {
                bool *regResult = nullptr;
                auto ret = reg(&regFitables, &address, &regResult);
                if (ret != FIT_OK) {
                    printf("Prepare data failed: ret = %X.\n", ret);
                }
                regFitables.clear();
            }
        }

        printf("Data ready: count = %d.\n", prepareDataCount);
    }

    ~PrepareData()
    {
        fit::registry::unregisterFitService unreg;
        fit::registry::Address address = FitSystemPropertyUtils::Address();
        std::vector<fit::registry::Fitable> unregFitables;
        for (int i = 0; i < prepareDataCount; ++i) {
            fit::registry::Fitable fitable;
            fitable.genericId = std::to_string(i) + Fit::Benchmark::Echo<0>::GENERIC_ID_TAIL;
            fitable.genericVersion = "1.0.0";
            fitable.fitId = std::to_string(i) + Fit::Benchmark::Echo<0>::FITABLE_ID_TAIL;
            fitable.fitVersion = "1.0.0";
            unregFitables.push_back(fitable);

            if (unregFitables.size() == 100 || i == prepareDataCount - 1) {
                bool *unregResult = nullptr;
                auto ret = unreg(&unregFitables, &address, &unregResult);
                if (ret != FIT_OK) {
                    printf("Prepare data remove failed: ret = %X.\n", ret);
                }
                unregFitables.clear();
            }
        }
    }

protected:
    int prepareDataCount = 0;
};

#endif // PREPARE_DATA_H
