#!/usr/bin/env bash
# author yaoyuan
# 定时执行该脚本
# 使用crontab -e 编写定时任务
# */1 * * * * /data/shells/nginx_recovery.sh

currTime=`date +"%Y-%m-%d %H:%M:%S"`

# 检查Nginx容器是否退出
exist=`docker inspect --format '{{.State.Running}}' filink-web`
if [[ "${exist}" != "true" ]]; then
    # 容器退出 需要重新启动
    docker rm -f filink-web

    docker run -d --net=host \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-web \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-web

    # 记录
    echo "${currTime} 重启docker容器，容器名称：filink-web" >> /data/logs/nginx/nginx_recovery.log

fi

