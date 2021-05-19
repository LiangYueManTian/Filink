#!/usr/bin/env bash
# 关联容器脚本
# author yaoyuan

echo  "gateway================================================="
# gateway
docker stop filink-gateway
docker rm filink-gateway
docker run -p 9801:9801 -p 9001:9001 -d  --link filink-user --link filink-system --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-gateway registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-gateway


echo "alarmCurrent============================================="
# alarmCurrent
docker stop filink-alarmcurrent
docker rm filink-alarmcurrent
docker run  -d --link filink-log --link filink-user  --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-alarmcurrent registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-alarmcurrent


echo "alarmHistory============================================="
# alarmHistory
docker stop filink-alarmhistory
docker rm filink-alarmhistory
docker run  -d --link filink-log  --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-alarmhistory registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-alarmhistory


echo "alarmSet================================================="
# alarmSet
docker stop filink-alarmset
docker rm filink-alarmset
docker run -d --link filink-log  --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-alarmset registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-alarmset


echo "device==================================================="
# device
docker stop filink-device
docker rm filink-device
docker run -d --link filink-log --link filink-alarmcurrent --link filink-user --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-device registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-device


echo "lock====================================================="
# lock
docker stop filink-lock
docker rm filink-lock
docker run -d --link filink-log --link filink-device --link filink-station --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-lock registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-lock

# log 没有依赖模块  不需要
# oss 不需要
# schedule

echo "station=================================================="
# station
docker stop filink-station
docker rm filink-station
docker run -d -p 8083:8080/udp --link filink-lock --link filink-device --link filink-system --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-station registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-station

echo "system==================================================="
# system
docker stop filink-system
docker rm filink-system
docker ps -a
docker run -d --link filink-oss --link filink-device  --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-system registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-system

# user
docker stop filink-user
docker rm filink-user
docker run -d --link filink-oss --link filink-device  --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-user registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-user

