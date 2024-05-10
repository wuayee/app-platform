/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : connection pool for registry server repository in pgsql
 * Author       : x00649642
 * Create       : 2023-11-21
 * Notes:       : multithreading-safe
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_CONNECTION_POOL_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_CONNECTION_POOL_HPP

#include "fit/stl/mutex.hpp"
#include "fit/stl/queue.hpp"
#include "fit/stl/memory.hpp"
#include "fit/stl/condition_variable.hpp"
#include "fit/stl/string.hpp"
#include "fit/fit_code.h"
#include "fit/internal/util/thread/fit_thread.h"
#include "sql_wrapper/sql_cmd.hpp"
#include "sql_wrapper/sql_exec_result.hpp"
#include "sql_wrapper/sql_connection.hpp"

namespace Fit {
namespace Repository {
namespace Pg {

enum class ConnectionPoolStatus { INIT, READY, TERMINATE };

class ConnectionPool final {
    using SqlExecResultPtr = unique_ptr<AbstractSqlExecResult>;
    using SqlConnectionPtr = unique_ptr<AbstractSqlConnection>;

public:
    ConnectionPool(ConnectionPool&) = delete;
    ConnectionPool(ConnectionPool&&) = delete;

    ConnectionPool& operator=(ConnectionPool&) = delete;
    ConnectionPool& operator=(ConnectionPool&&) = delete;

    static ConnectionPool& Instance();

    FitCode SetUp(const Fit::string& config, uint16_t connectionNum, uint16_t maxRetry,
                  const std::function<SqlConnectionPtr(const char*)>& connectionCreator);
    void TearDown();

    size_t GetAvailableConnectionCount()
    {
        return connections_.size();
    }
    size_t GetTotalConnectionCount()
    {
        return connectionNum_;
    }

    ConnectionPoolStatus GetStatus()
    {
        return status_;
    }
    // only for testing
    void SetStatus(ConnectionPoolStatus newStatus)
    {
        status_ = newStatus;
    }

    SqlExecResultPtr Submit(const std::function<SqlExecResultPtr(SqlConnectionPtr&)>& sqlFunctor);
    SqlExecResultPtr Submit(const Fit::Pg::SqlCmd& sqlCmd);
private:
    void StartDaemon();
    void StopDaemon();
private:
    ConnectionPool() = default;

    ~ConnectionPool();
    // connection queue
    std::queue<SqlConnectionPtr> connections_;
    // works with `connections_` to ensure thread-safe
    std::mutex mutex_;
    // notification cv
    std::condition_variable cv_;
    // connection pool stopped flag
    ConnectionPoolStatus status_{ConnectionPoolStatus::INIT};
    // total connection num
    int connectionNum_{0};

    bool exit_ {false};
    Fit::shared_ptr<Fit::Thread::fit_thread> daemonThread_ {};
    std::queue<SqlConnectionPtr> offlineConnections_;
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit

#endif  // REGISTRY_SERVER_REPOSITORY_PG_CONNECTION_POOL_HPP
