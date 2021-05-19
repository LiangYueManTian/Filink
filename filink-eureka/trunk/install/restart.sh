#!/usr/bin/env bash
# 重启脚本
# author 姚远
# 重启整个服务或者单个服务
# 步骤 先暂停所有服务
# 再根据顺序启动对应服务
# 检查
function checkDockerEnvironment() {
    docker ps
    if [[ $? -eq 0 ]]; then
        service docker restart
    else
        service docker start
    fi
}

function stopAllService() {
    docker stop $(docker ps -aq)
    if [[ $? -eq 0 ]]; then
        echo "服务已经全部停止"
    else
        echo "服务停止失败，请检查原因"
    fi
}

function startSomeAService() {
    docker start $1
    if [[ $? -eq 0 ]]; then
      echo "$1 启动成功"
    else
      echo "$1 启动报错"
    fi
}

function startAllService() {
    startSomeAService filink-mysql
    startSomeAService filink-mongo
    startSomeAService filink-elasticsearch
    startSomeAService filink-logstash
    startSomeAService filink-kibana
    startSomeAService filink-fastdfs
    startSomeAService filink-ftp
    startSomeAService filink-zookeeper
    startSomeAService filink-kafka
    startSomeAService filink-redis
    startSomeAService filink-eureka
    sheep 30
    startSomeAService filink-config
    sleep 30
    startSomeAService filink-schedule
    sleep 30
    startSomeAService filink-monitor
    startSomeAService filink-alarmcurrent
    startSomeAService filink-alarmhistory
    startSomeAService filink-alarmset
    startSomeAService filink-device
    startSomeAService filink-gateway
    startSomeAService filink-lock
    startSomeAService filink-log
    startSomeAService filink-map
    startSomeAService filink-oss
    startSomeAService filink-station
    startSomeAService filink-system
    startSomeAService filink-user
    startSomeAService filink-workflow
    startSomeAService filink-workflow-business
    startSomeAService filink-oceanconnect
    startSomeAService filink-onenet
    startSomeAService filink-rfid
    startSomeAService filink-dump
    startSomeAService filink-web

    echo "服务重启完毕"
}



checkDockerEnvironment
stopAllService
startAllService



