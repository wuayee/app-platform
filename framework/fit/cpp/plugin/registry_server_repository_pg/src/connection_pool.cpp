/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : connection pool for registry server repository in pgsql
 * Author       : x00649642
 * Create       : 2023-11-21
 * Notes:       : multithreading-safe
 */
#include "connection_pool.hpp"
#include <thread>
#include "fit/fit_log.h"

namespace Fit {
namespace Repository {
namespace Pg {
ConnectionPool::~ConnectionPool()
{
    TearDown();
}
void ConnectionPool::StopDaemon()
{
    exit_ = true;
    if (daemonThread_ != nullptr && daemonThread_->joinable()) {
        daemonThread_->join();
    }
}
void ConnectionPool::TearDown()
{
    StopDaemon();
    std::unique_lock<std::mutex> lock(mutex_);
    status_ = ConnectionPoolStatus::TERMINATE;
    lock.unlock();
    cv_.notify_all();
    while (true) {
        lock.lock();
        if (connectionNum_ == 0) {
            lock.unlock();
            break;
        }
        cv_.wait(lock, [this] { return !this->connections_.empty(); });
        const int beforeCount = this->connectionNum_;
        for (auto connection = Fit::move(connections_.front()); !connections_.empty(); --connectionNum_) {
            // auto PGfinish in SqlConnection destructor
            connections_.pop();
        }
        FIT_LOG_INFO("ConnectionPool finish %d connections, %d left.", beforeCount - connectionNum_, connectionNum_);
        lock.unlock();
    }
}

ConnectionPool& ConnectionPool::Instance()
{
    static ConnectionPool instance;
    return instance;
}
void ConnectionPool::StartDaemon()
{
    exit_ = false;
    daemonThread_ = std::make_shared<Fit::Thread::fit_thread>([this]() {
        while (!exit_) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
            std::queue<SqlConnectionPtr> tempOfflineConnections {};
            {
                std::lock_guard<std::mutex> lock(mutex_);
                if (offlineConnections_.empty()) {
                    continue;
                }
                tempOfflineConnections.swap(offlineConnections_);
            }

            std::queue<SqlConnectionPtr> onlineConnections {};
            std::queue<SqlConnectionPtr> offlineConnections {};
            while (!tempOfflineConnections.empty()) {
                SqlConnectionPtr connection = Fit::move(tempOfflineConnections.front());
                tempOfflineConnections.pop();
                connection->Reconnect();
                if (connection->IsOk()) {
                    onlineConnections.emplace(std::move(connection));
                    FIT_LOG_CORE("Reconnect success, offline size is pre:temp(%lu:%lu).",
                        tempOfflineConnections.size(), offlineConnections.size());
                } else {
                    offlineConnections.emplace(std::move(connection));
                }
            }

            {
                std::lock_guard<std::mutex> lock(mutex_);
                offlineConnections.swap(offlineConnections_);
                while (!onlineConnections.empty()) {
                    connections_.emplace(std::move(onlineConnections.front()));
                    onlineConnections.pop();
                }
            }
        }
    });
}
FitCode ConnectionPool::SetUp(const Fit::string& config, uint16_t connectionNum, uint16_t maxRetry,
                              const std::function<SqlConnectionPtr(const char*)>& connectionCreator)
{
    if (maxRetry == 0) {
        FIT_LOG_ERROR("ConnectionPool SetUp requires at least 1 try.");
        return FIT_ERR_PARAM;
    }
    {
        std::lock_guard<std::mutex> lock(mutex_);
        if (status_ != ConnectionPoolStatus::INIT) {
            FIT_LOG_ERROR("ConnectionPool SetUp in non-INIT status");
            return FIT_ERR_FAIL;
        }
        while (maxRetry > 0 && connections_.size() < connectionNum) {
            SqlConnectionPtr connection = connectionCreator(config.data());
            if (!connection->IsOk()) {
                FIT_LOG_ERROR("ConnectionPool connect failed. error: %s.", connection->GetErrorMessage());
                --maxRetry;
                continue;
            }
            connections_.emplace(Fit::move(connection));
        }
        connectionNum_ = connections_.size();
        status_ = ConnectionPoolStatus::READY;
        if (connectionNum_ != connectionNum) {
            FIT_LOG_ERROR("ConnectionPool only creates %u connection while config is %u",
                connectionNum_, connectionNum);
            return FIT_ERR_FAIL;
        }
    }

    StartDaemon();
    return FIT_ERR_SUCCESS;
}

SqlExecResultPtr ConnectionPool::Submit(const std::function<SqlExecResultPtr(SqlConnectionPtr&)>& sqlFunctor)
{
    if (this->status_ == ConnectionPoolStatus::INIT) {
        FIT_LOG_ERROR("Call ConnectionPool::Submit without init!");
        return nullptr;
    }

    std::unique_lock<std::mutex> lock(mutex_);
    cv_.wait(lock, [this] { return this->status_ == ConnectionPoolStatus::TERMINATE || !this->connections_.empty(); });
    if (this->status_ == ConnectionPoolStatus::TERMINATE) {
        return nullptr;
    }
    SqlConnectionPtr connection = Fit::move(connections_.front());
    connections_.pop();
    lock.unlock();

    auto res = sqlFunctor(connection);
    if (!res || !res->IsOk()) {
        FIT_LOG_ERROR("Submit failed, (errMsg=%s).", res ? res->GetErrorMessage() : "null result");
        // add to queue, to reconnect
        lock.lock();
        offlineConnections_.emplace(Fit::move(connection));
        lock.unlock();
    } else {
        lock.lock();
        connections_.emplace(Fit::move(connection));
        lock.unlock();
    }
    cv_.notify_one();
    return res;
}

SqlExecResultPtr ConnectionPool::Submit(const Fit::Pg::SqlCmd& sqlCmd)
{
    return Submit([&](SqlConnectionPtr& connection) -> SqlExecResultPtr {
        return connection->ExecParam(sqlCmd.sql.c_str(), sqlCmd.params);
    });
}
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
