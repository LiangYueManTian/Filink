#!/usr/bin/env bash
# 先停止现有容器 删除镜像
_net_port=$1
_container_port=$2
_container_name=$3
_tag_name=$4
_password=$5
docker stop ${_container_name}
docker rm ${_container_name}
docker rmi registry.cn-hangzhou.aliyuncs.com/lulala/filink:${_container_name}
echo "====================原油版本已经删除=================="
docker login --username=15171085878 --password=${_password} registry.cn-hangzhou.aliyuncs.com
docker login --username=15171085878 --password=yaoyuan12345 registry.cn-hangzhou.aliyuncs.com
echo "====================开始下载镜像=================="
docker pull registry.cn-hangzhou.aliyuncs.com/lulala/filink:${_tag_name}
docker pull registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-eureka
echo "====================镜像下载完毕=================="
docker run -p ${_net_port}:${_container_port} -d --link filink-mongo:filink-mongo --link filink-redis --link mysql5.7:filink-mysql --link filink-kafka --name ${_container_name} registry.cn-hangzhou.aliyuncs.com/lulala/filink:${_tag_name}
docker run -p 9801:9801 -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-gateway registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-gateway
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-alarmcurrent registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-alarmcurrent
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-alarmhistory registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-alarmhistory
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-alarmset registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-alarmset
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-device registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-device
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-lock registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-lock
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-log registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-log
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-oss registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-oss
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-schedule registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-schedule
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-station registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-station
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-system registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-system
docker run  -d --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql  --name filink-user registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-user
echo "====================服务已经启动=================="



#docker run -p 8761:8761 -d registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-eureka
#
#_eureka_Id=`docker run -p 8761:8761 -d registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-eureka`

#docker run -p 9901:991 --rm --name filink-system --link filink-mongo:filink-mongo --link mysql5.7:filink-mysql --link filink-kafka registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-system

#docker run  -p 6379:6379 -v /filink/redis/data:/data -v /filink/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf --privileged=true  --name filink-redis -d redis redis-server /usr/local/etc/redis/redis.conf
