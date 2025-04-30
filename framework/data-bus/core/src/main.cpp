/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#include <iostream>
#include <memory>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>
#include <sys/epoll.h>

#include "log/Logger.h"
#include "config/ConfigParser.h"
#include "config/DataBusConfig.h"
#include "task_handler/TaskLoop.h"
#include "task_handler/TaskHandler.h"
#include "utils/FileUtils.h"

using DataBus::Task::TaskLoop;
using DataBus::Task::TaskHandler;
using namespace std;

namespace DataBus {

constexpr int MAX_EVENTS = 10;
constexpr int MAX_BUFFER_SIZE = 2048;
constexpr int MAX_CONNECTIONS_QUEUED = 3;

// 配置文件路径
const std::string CONFIG_FILE_PATH = DataBus::Common::FileUtils::GetDataBusDirectory() + "configs/config.json";

void HandleClientData(int clientFd, int epollFd, const std::shared_ptr<TaskLoop>& taskLoopPtr)
{
    auto buffer = std::make_unique<char[]>(MAX_BUFFER_SIZE);
    size_t totalBytesRead = 0;
    size_t newBufferSize = 0;
    // 这里可以使用阻塞模式（最后一个参数设置为 0），但是为了方便之后非阻塞读取数据，需要将 bytesRead 设置为 ssize_t 类型
    ssize_t bytesRead = recv(clientFd, buffer.get(), MAX_BUFFER_SIZE, 0);
    if (bytesRead < 0) {
        DataBus::logger.Error("Receive failed on clientFd {}: {}", clientFd, strerror(errno));
        return;
    }
    if (bytesRead == 0) {
        taskLoopPtr->AddCloseTask(clientFd);
        epoll_ctl(epollFd, EPOLL_CTL_DEL, clientFd, nullptr);
        return;
    }
    totalBytesRead += bytesRead;
    DataBus::logger.Info("Receiving {} bytes from client {}", totalBytesRead, clientFd);
    while (bytesRead >= MAX_BUFFER_SIZE) {
        // 缓冲区已满，扩展缓冲区，大小增加 MAX_BUFFER_SIZE
        newBufferSize = totalBytesRead + MAX_BUFFER_SIZE;
        auto newBuffer = std::make_unique<char[]>(newBufferSize);
        std::copy(buffer.get(), buffer.get() + totalBytesRead, newBuffer.get());
        buffer.swap(newBuffer);
        DataBus::logger.Info("Buffer expanded to {} bytes", newBufferSize);
        // 非阻塞读取数据（最后一个参数设置为 MSG_DONTWAIT）
        bytesRead = recv(clientFd, buffer.get() + totalBytesRead, MAX_BUFFER_SIZE, MSG_DONTWAIT);
        if (bytesRead <= 0) {
            break;
        }
        totalBytesRead += bytesRead;
        DataBus::logger.Info("Receiving {} bytes from client {}", totalBytesRead, clientFd);
    }
    taskLoopPtr->AddReadTask(clientFd, buffer.get(), totalBytesRead);
}

void HandleEvent(struct epoll_event event, int epollFd, int serverFd,
    const shared_ptr<TaskLoop>& taskLoopPtr)
{
    struct epoll_event events[MAX_EVENTS];
    int numEvents = epoll_wait(epollFd, events, MAX_EVENTS, -1);
    for (int i = 0; i < numEvents; ++i) {
        if (events[i].data.fd == serverFd) {
            struct sockaddr_in clientAddr{};
            socklen_t clientLen = sizeof(clientAddr);
            int clientFd = accept(serverFd, (struct sockaddr*) &clientAddr, &clientLen);
            if (clientFd == -1) {
                continue;
            }

            event.data.fd = clientFd;
            event.events = EPOLLIN | EPOLLET;
            if (epoll_ctl(epollFd, EPOLL_CTL_ADD, clientFd, &event) == -1) {
                close(clientFd);
                continue;
            }
            taskLoopPtr->AddOpenTask(clientFd);
        } else {
            HandleClientData(events[i].data.fd, epollFd, taskLoopPtr);
        }
    }
}

void StartDataBusService(int serverFd, const Runtime::Config& databusConfig)
{
    struct epoll_event event{};
    event.data.fd = serverFd;
    event.events = EPOLLIN | EPOLLET;
    int epollFd;

    epollFd = epoll_create1(0);
    if (epollFd == -1) {
        perror("epoll_create1");
        return;
    }

    if (epoll_ctl(epollFd, EPOLL_CTL_ADD, serverFd, &event) == -1) {
        perror("epoll_ctl: serverFd");
        return;
    }
    shared_ptr<TaskLoop> taskLoopPtr = std::make_shared<TaskLoop>();

    unique_ptr<TaskHandler> taskHandlerPtr = std::make_unique<TaskHandler>(taskLoopPtr, databusConfig);
    taskHandlerPtr->Init();
    while (true) {
        HandleEvent(event, epollFd, serverFd, taskLoopPtr);
    }
}
}

int main()
{
    DataBus::Runtime::ConfigParser::Parse();
    DataBus::logger.Info("Hello, {}", "databus");
    int serverFd;
    int opt = 1;

    // 创建socket文件描述符
    if ((serverFd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("socket failed");
        return 0;
    }

    // 设置socket选项
    if (setsockopt(serverFd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt))) {
        perror("setsockopt");
        return 0;
    }

    // 读取DataBus配置文件。
    const DataBus::Runtime::Config config = DataBus::Runtime::ConfigParser::Parse(DataBus::CONFIG_FILE_PATH);

    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(config.GetPort());

    if (bind(serverFd, (sockaddr*)&address, sizeof(address)) < 0) {
        perror("bind failed");
        return 0;
    }

    if (listen(serverFd, DataBus::MAX_CONNECTIONS_QUEUED) < 0) {
        perror("listen");
        return 0;
    }
    DataBus::logger.Info("Databus service starts up at port {}", config.GetPort());
    DataBus::StartDataBusService(serverFd, config);
    close(serverFd);
    return 0;
}

