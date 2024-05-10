/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : broker http
 * Author       : songyongtan
 * Create       : 2023-07-29
 * Notes:       :
 */

#include <fit/external/framework/plugin_activator.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <cpp-httplib/httplib.h>
#include <fit/fit_log.h>

#include <genericable/com_huawei_fit_broker_server_start_server/1.0.0/cplusplus/startServer.hpp>
#include <genericable/com_huawei_fit_broker_server_stop_server/1.0.0/cplusplus/stopServer.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_broker_server_process_v3/1.0.0/cplusplus/processV3.hpp>

#include "http_manager.hpp"
#include "http_util.hpp"

using namespace Fit;

namespace {
constexpr uint32_t PROTOCOL_HTTP = 2;
constexpr uint32_t PROTOCOL_HTTPS = 4;
FitCode StartHttpServer(ContextObj ctx, ::fit::registry::Address** result);
FitCode StopHttpServer(ContextObj ctx);
FitCode StartHttpsServer(ContextObj ctx, ::fit::registry::Address** result);
FitCode StopHttpsServer(ContextObj ctx);

FitCode Start(::Fit::Framework::PluginContext* context)
{
    auto config = context->GetConfig();
    int32_t protocol = config->Get("http.protocol").AsInt(PROTOCOL_HTTP);
    string workerPath = config->Get("http.path").AsString("/fit");
    string contextPath = config->Get("worker.extensions.http.context-path").AsString("");
    HttpManager::Instance().SetHttpConfig(contextPath, workerPath, protocol);

    string host = config->Get("http.host").AsString("");
    int32_t port = config->Get("http.port").AsInt(0);

    bool sslVerify = config->Get("https.ssl-verify").AsBool(false);
    string httpsHost = config->Get("https.host").AsString(host);
    int32_t httpsPort = config->Get("https.port").AsInt(0);
    int32_t httpsProtocol = config->Get("https.protocol").AsInt(PROTOCOL_HTTPS);
    string httpsWorkerPath = config->Get("https.path").AsString(workerPath);
    string cerPath = config->Get("https.cer").AsString("");
    string privateKeyPath = config->Get("https.private-key").AsString("");
    string privateKeyPwd = config->Get("https.private-key-pwd").AsString("");
    string caCrtPth = config->Get("https.ca-crt").AsString("");
    string keyPwdFilePath = config->Get("https.private-key-pwd-file").AsString("");
    string pwdCryptoType = config->Get("https.pwd-crypto-type").AsString("");
    string encryptedKeyPwd = HttplibUtil::GetFileFirstLine(keyPwdFilePath);
    if (!encryptedKeyPwd.empty()) {
        privateKeyPwd = encryptedKeyPwd;
    }
    FIT_LOG_CORE("Key password file : crypto type : encrypted key password(%s:%s:%s).",
        keyPwdFilePath.c_str(), pwdCryptoType.c_str(), privateKeyPwd.c_str());
    HttpManager::Instance().SetHttpsConfig(move(contextPath), move(httpsWorkerPath), httpsProtocol, sslVerify,
        move(cerPath), move(privateKeyPath), move(privateKeyPwd), move(caCrtPth), move(keyPwdFilePath),
        move(pwdCryptoType));
    int32_t ret = HttpManager::Instance().InitHttpsClient();
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Init https client failed %d.", ret);
    }
    if (!httpsHost.empty() && httpsPort != 0) {
        HttpManager::Instance().InitHttpsServer(move(httpsHost), httpsPort);
        ::Fit::Framework::Annotation::Fitable(StartHttpsServer)
            .SetGenericId(fit::broker::server::startServer::GENERIC_ID).SetFitableId("https");
        ::Fit::Framework::Annotation::Fitable(StopHttpsServer)
            .SetGenericId(fit::broker::server::stopServer::GENERIC_ID).SetFitableId("https");
    }
    // 开启sslVerify，则关闭http
    if (!sslVerify && !host.empty() && port != 0) {
        HttpManager::Instance().InitHttpServer(host, port);
        ::Fit::Framework::Annotation::Fitable(StartHttpServer)
            .SetGenericId(fit::broker::server::startServer::GENERIC_ID).SetFitableId("http");
        ::Fit::Framework::Annotation::Fitable(StopHttpServer)
            .SetGenericId(fit::broker::server::stopServer::GENERIC_ID).SetFitableId("http");
    }
    return ret;
}

FitCode HandleRequest(const Network::Request& request, Network::Response& response)
{
    fit::hakuna::kernel::broker::server::processV3 proxy;
    Fit::bytes metadata {request.metadata};
    Fit::bytes data {request.payload};
    ::fit::hakuna::kernel::broker::shared::FitResponse* result {nullptr};
    auto ret = proxy(&metadata, &data, &result);
    if (ret != FIT_OK || (result == nullptr)) {
        FIT_LOG_ERROR("Failed to invoke broker server process. (ret=%X).", ret);
        return ret;
    }
    response.metadata = Fit::string(result->metadata.data(), result->metadata.size());
    response.payload = Fit::string(result->data.data(), result->data.size());

    return FIT_OK;
}

template<typename F>
FitCode StartServer(F&& getServer, ContextObj ctx, ::fit::registry::Address** result)
{
    *result = Fit::Context::NewObj<::fit::registry::Address>(ctx);
    if (*result == nullptr) {
        FIT_LOG_ERROR("Failed to new result.");
        return FIT_ERR_CTX_BAD_ALLOC;
    }

    auto server = getServer();
    if (server == nullptr) {
        FIT_LOG_ERROR("Server is not ready.");
        return FIT_ERR_FAIL;
    }
    auto ret = server->Start(HandleRequest);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to start http server. (ret=%X, address=%s:%d, protocol=%d).", ret,
            server->GetHost().c_str(), server->GetPort(), server->GetProtocol());
        return ret;
    }

    (*result)->protocol = server->GetProtocol();
    (*result)->host = server->GetHost();
    (*result)->port = server->GetPort();
    FIT_LOG_INFO("Http server is started. (address=%s:%d, protocol=%d, workerPath=%s).", server->GetHost().c_str(),
        server->GetPort(), server->GetProtocol(), server->GetPattern().c_str());

    return FIT_OK;
}

template<typename F>
FitCode StopServer(F&& getServer, ContextObj ctx)
{
    auto server = getServer();
    if (server == nullptr) {
        FIT_LOG_WARN("Server is not ready.");
        return FIT_OK;
    }
    server->Stop();
    FIT_LOG_INFO("Http server is stopped. (address=%s:%d, protocol=%d).", server->GetHost().c_str(), server->GetPort(),
        server->GetProtocol());
    return FIT_OK;
}

FitCode StartHttpServer(ContextObj ctx, ::fit::registry::Address** result)
{
    return StartServer([]() { return HttpManager::Instance().GetHttpServer(); }, ctx, result);
}
FitCode StopHttpServer(ContextObj ctx)
{
    return StopServer([]() { return HttpManager::Instance().GetHttpServer(); }, ctx);
}

FitCode StartHttpsServer(ContextObj ctx, ::fit::registry::Address** result)
{
    return StartServer([]() { return HttpManager::Instance().GetHttpsServer(); }, ctx, result);
}
FitCode StopHttpsServer(ContextObj ctx)
{
    return StopServer([]() { return HttpManager::Instance().GetHttpsServer(); }, ctx);
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar().SetStart(Start);
}
}