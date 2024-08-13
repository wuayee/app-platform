#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: core docker container initialization

set -eu

# 创建 logrotate 配置文件
logrotate_file="/etc/logrotate.conf"
cat > "$logrotate_file" << "EOF"
/log/app/databus.log {
    hourly
    missingok
    dateext
    dateformat .%m%d-%H:%M:%S
    compress
    copytruncate
    rotate 168
    maxsize 50M
    create 0640 runtime edatamate
}
EOF

# 创建 logrotate 定时脚本
logrotate_script="/etc/cron.hourly/logrotate"
cat > "$logrotate_script" << "EOF"
#!/bin/sh

/usr/sbin/logrotate /etc/logrotate.conf
EXITVALUE=$?
if [ $EXITVALUE != 0 ]; then
    /usr/bin/logger -t logrotate "ALERT exited abnormally with [$EXITVALUE]"
fi
exit 0
EOF

chmod +x /etc/cron.hourly/logrotate
touch /var/lib/logrotate/status
/usr/sbin/logrotate /etc/logrotate.conf
service cron restart

mkdir -p /log/app
chown -R runtime:edatamate /log
chown -R runtime:edatamate /databus/databus

# 运行 Databus 主程序
su -c '/databus/databus' runtime