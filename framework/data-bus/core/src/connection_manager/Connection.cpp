/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
#include <unistd.h>
#include <cstring>
#include <iostream>
#include <cerrno>
#include <sys/socket.h>

#include "Connection.h"

using namespace std;

namespace DataBus {
namespace Connection {

const int MESSAGE_HEADER_LEN = 24;

void Connection::Close()
{
    lock_guard<recursive_mutex> lock(mutex_);
    if (socketFd_ >= 0) {
        if (close(socketFd_) == -1) {
            cout << "Error closing socket: " << strerror(errno) << endl;
        }
        socketFd_ = -1;
    }
}

int Connection::Send(const unsigned char* buf, size_t size)
{
    lock_guard<recursive_mutex> lock(mutex_);
    if (socketFd_ >= 0) {
        int bytesSent = send(socketFd_, buf, size, 0);
        if (bytesSent < 0) {
            perror("Error sending data");
            Close();
        }
        cout << "Sent " << bytesSent << " bytes to server." << endl;
        return bytesSent;
    }
    return -1;
}

}  // namespace Connection
}  // namespace DataBus