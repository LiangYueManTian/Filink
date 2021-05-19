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
echo "====================原始版本已经删除=================="
docker login --username=15171085878 --password=${_password} registry.cn-hangzhou.aliyuncs.com
echo "====================开始下载镜像=================="
docker pull registry.cn-hangzhou.aliyuncs.com/lulala/filink:${_tag_name}
echo "====================镜像下载完毕=================="
docker run -p ${_net_port}:${_container_port} -d --link filink-mongo:filink-mongo --link filink-redis --link mysql5.7:filink-mysql --link filink-kafka --name ${_container_name} registry.cn-hangzhou.aliyuncs.com/lulala/filink:${_tag_name}
echo "====================服务已经启动=================="


filink-alarmcurrent


#docker run -p 8761:8761 -d registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-eureka
#
#_eureka_Id=`docker run -p 8761:8761 -d registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-eureka`

#docker run -p 9901:991 --rm --name filink-system --link filink-mongo:filink-mongo --link mysql5.7:filink-mysql --link filink-kafka registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-system

#docker run  -p 6379:6379 -v /filink/redis/data:/data -v /filink/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf --privileged=true  --name filink-redis -d redis redis-server /usr/local/etc/redis/redis.conf
