#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: a3000 docker container initialization

set -eu

# 创建 logrotate 配置文件
mkdir -p /opt/etc/logrotate/
logrotate_file="/opt/etc/logrotate/${HOSTNAME}-logrotate.conf"
cat > "$logrotate_file" << "EOF"
/log/app/databus.log {
    hourly
    missingok
    dateext
    dateformat .%m%d-%H%s
    compress
    copytruncate
    rotate 168
    maxsize 50M
    create 0640 runtime edatamate
}
EOF

echo "* * * * * /usr/sbin/logrotate -s /opt/etc/logrotate/\${HOSTNAME}-logrotate.status /opt/etc/logrotate/\${HOSTNAME}-logrotate.conf" > /var/spool/cron/root

# 启动定时任务
crond

mkdir -p /log/app
chown -R runtime:edatamate /log
chown -R runtime:edatamate /databus/databus

# 运行 Databus 主程序
su -c '/databus/databus' runtime