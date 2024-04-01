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
#include "connection_manager/ConnectionManager.h"

#define MAX_EVENTS 10
#define PORT 5284

using DataBus::Connection::ConnectionManager;
using namespace std;

void HandleEvent(struct epoll_event event, int epollFd, int serverFd)
{
    unique_ptr<ConnectionManager> managerPtr = std::make_unique<ConnectionManager>();
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
            managerPtr->AddNewConnection(clientFd);
            cout << "Client connected" << endl;
        } else {
            char buffer[1024] = {0};
            ssize_t bytesRead = recv(events[i].data.fd, buffer, sizeof(buffer) - 1, 0);
            if (bytesRead > 0) {
                managerPtr->Handle(buffer, bytesRead, events[i].data.fd);
            } else if (bytesRead == 0) {
                cout << "Client disconnected" << endl;
                close(events[i].data.fd);
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

    while (true) {
        HandleEvent(event, epollFd, serverFd);
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
    address.sin_port = htons(PORT);

    if (bind(serverFd, (sockaddr*)&address, sizeof(address)) < 0) {
        perror("bind failed");
        return 0;
    }

    if (listen(serverFd, 3) < 0) {
        perror("listen");
        return 0;
    }
    cout << "Databus service starts up..." << endl;
    StartDataBusService(serverFd);
    close(serverFd);
    return 0;
}
