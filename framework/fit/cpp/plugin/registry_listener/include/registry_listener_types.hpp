/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides type definitions for registry listener.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/08
 */

#ifndef FIT_REGISTRY_LISTENER_TYPES_HPP
#define FIT_REGISTRY_LISTENER_TYPES_HPP

#include <memory>

namespace Fit {
namespace Registry {
namespace Listener {
class RegistryListener;
using RegistryListenerPtr = std::shared_ptr<RegistryListener>;

class Genericable;
using GenericablePtr = std::shared_ptr<Genericable>;
class GenericableRepo;
using GenericableRepoPtr = std::shared_ptr<GenericableRepo>;

class Fitable;
using FitablePtr = std::shared_ptr<Fitable>;
class FitableRepo;
using FitableRepoPtr = std::shared_ptr<FitableRepo>;

class FitableUnavailableEndpoint;
using FitableUnavailableEndpointPtr = std::shared_ptr<FitableUnavailableEndpoint>;
class FitableUnavailableEndpointRepo;
using FitableUnavailableEndpointRepoPtr = std::shared_ptr<FitableUnavailableEndpointRepo>;

class ApplicationFitable;
using ApplicationFitablePtr = std::shared_ptr<ApplicationFitable>;
class FitableApplicationRepo;
using FitableApplicationRepoPtr = std::shared_ptr<FitableApplicationRepo>;
class ApplicationFitableRepo;
using ApplicationFitableRepoPtr = std::shared_ptr<ApplicationFitableRepo>;

class Application;
using ApplicationPtr = std::shared_ptr<Application>;
class ApplicationRepo;
using ApplicationRepoPtr = std::shared_ptr<ApplicationRepo>;

class Worker;
using WorkerPtr = std::shared_ptr<Worker>;
class WorkerRepo;
using WorkerRepoPtr = std::shared_ptr<WorkerRepo>;

class WorkerEndpoint;
using WorkerEndpointPtr = std::shared_ptr<WorkerEndpoint>;
class WorkerEndpointRepo;
using WorkerEndpointRepoPtr = std::shared_ptr<WorkerEndpointRepo>;
}
}
}

#endif // FIT_REGISTRY_LISTENER_TYPES_HPP
