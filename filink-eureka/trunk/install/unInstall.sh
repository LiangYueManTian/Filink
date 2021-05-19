#!/usr/bin/env bash
# 卸载脚本
# 卸载服务相关容器
docker rm -f filink-mysql
docker rm -f filink-mongo
docker rm -f filink-elasticsearch
docker rm -f filink-kibana
docker rm -f filink-logstash
docker rm -f filink-fastdfs
docker rm -f filink-ftp
docker rm -f filink-kafka
docker rm -f filink-zookeeper
docker rm -f filink-redis
docker rm -f filink-eureka
docker rm -f filink-config
docker rm -f filink-monitor
docker rm -f filink-schedule
docker rm -f filink-alarmcurrent
docker rm -f filink-alarmhistory
docker rm -f filink-alarmset
docker rm -f filink-device
docker rm -f filink-gateway
docker rm -f filink-lock
docker rm -f filink-log
docker rm -f filink-map
docker rm -f filink-oss
docker rm -f filink-station
docker rm -f filink-system
docker rm -f filink-user
docker rm -f filink-workflow
docker rm -f filink-workflow-business
docker rm -f filink-oceanconnect
docker rm -f filink-onenet
docker rm -f filink-rfid
docker rm -f filink-dump
docker rm -f filink-web

# 删除对应数据
