/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <sys/epoll.h>

#define MAX_EVENTS 10
#define BUF_SIZE 1024
#define PORT 5284

int main()
{
    int server_fd;
    int opt = 1;

    // 创建socket文件描述符
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    // 设置socket选项
    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt))) {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    struct sockaddr_in address;
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr*) &address, sizeof(address)) < 0) {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, 3) < 0) {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    struct epoll_event event, events[MAX_EVENTS];
    event.data.fd = server_fd;
    event.events = EPOLLIN | EPOLLET;
    int epoll_fd;

    epoll_fd = epoll_create1(0);
    if (epoll_fd == -1) {
        perror("epoll_create1");
        exit(EXIT_FAILURE);
    }

    if (epoll_ctl(epoll_fd, EPOLL_CTL_ADD, server_fd, &event) == -1) {
        perror("epoll_ctl: server_fd");
        exit(EXIT_FAILURE);
    }

    while (true) {
        int num_events = epoll_wait(epoll_fd, events, MAX_EVENTS, -1);
        for (int i = 0; i < num_events; ++i) {
            if (events[i].data.fd == server_fd) {
                struct sockaddr_in client_addr;
                socklen_t client_len = sizeof(client_addr);
                int client_fd = accept(server_fd, (struct sockaddr*) &client_addr, &client_len);
                if (client_fd == -1) {
                    continue;
                }

                event.data.fd = client_fd;
                event.events = EPOLLIN | EPOLLET;
                if (epoll_ctl(epoll_fd, EPOLL_CTL_ADD, client_fd, &event) == -1) {
                    close(client_fd);
                    continue;
                }
                std::cout << "Client connected" << std::endl;
            } else {
                char buffer[1024] = {0};
                ssize_t bytes_read = recv(events[i].data.fd, buffer, sizeof(buffer) - 1, 0);
                if (bytes_read > 0) {
                    std::cout << "Received: " << buffer << std::endl;
                } else if (bytes_read == 0) {
                    std::cout << "Client disconnected" << std::endl;
                    close(events[i].data.fd);
                    epoll_ctl(epoll_fd, EPOLL_CTL_DEL, events[i].data.fd, nullptr);
                } else {
                    std::cout << "Received: " << buffer << std::endl;
                }
            }
        }
    }

    close(server_fd);
    close(epoll_fd);
    return 0;
}