/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
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

using DataBus::Task::TaskLoop;
using DataBus::Task::TaskHandler;
using namespace std;

namespace DataBus {

const int MAX_EVENTS = 10;
// TD: 从runtime config读取
const int PORT = 5284;
const int MAX_BUFFER_SIZE = 1024;
// 内存分配上限40G
// TD: 从runtime config读取
constexpr uint64_t MALLOC_SIZE_LIMIT = 42949672960;

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
            // buffer指针由Task类管理
            char buffer[MAX_BUFFER_SIZE];
            size_t bytesRead = recv(events[i].data.fd, buffer, MAX_BUFFER_SIZE - 1, 0);
            if (bytesRead > 0) {
                DataBus::logger.Info("Receiving {} bytes from client {}", bytesRead,
                    static_cast<int>(events[i].data.fd));
                taskLoopPtr->AddReadTask(events[i].data.fd, buffer, bytesRead);
            } else if (bytesRead == 0) {
                taskLoopPtr->AddCloseTask(events[i].data.fd);
                epoll_ctl(epollFd, EPOLL_CTL_DEL, events[i].data.fd, nullptr);
            }
        }
    }
}

void StartDataBusService(int serverFd)
{
    struct epoll_event event{}, events[MAX_EVENTS];
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
    // TD: configParser从配置文件解析
    const Runtime::Config config(PORT, MALLOC_SIZE_LIMIT);
    unique_ptr<TaskHandler> taskHandlerPtr = std::make_unique<TaskHandler>(taskLoopPtr, config);
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

    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(DataBus::PORT);

    if (bind(serverFd, (sockaddr*)&address, sizeof(address)) < 0) {
        perror("bind failed");
        return 0;
    }

    if (listen(serverFd, 3) < 0) {
        perror("listen");
        return 0;
    }
    DataBus::logger.Info("Databus service starts up at port {}", DataBus::PORT);
    DataBus::StartDataBusService(serverFd);
    close(serverFd);
    return 0;
}

