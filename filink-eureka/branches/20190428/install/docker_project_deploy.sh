#!/usr/bin/env bash
# 拉取镜像启动项目
# author 姚远

pwd=yaoyuan12345
# 版本
version=test
# 仓库地址
adreess=registry.cn-hangzhou.aliyuncs.com/lulala/filink

#kafka ip
kafka_ip=10.5.24.224
params="--spring.cloud.stream.kafka.binder.brokers=${KAFKA_IP}:9092  --spring.cloud.stream.kafka.binder.zkNodes=${KAFKA_IP}:2181"

# 端口
eureka_port=8761
gateway_port=9801
gateway_websocket_port=9001

# JVM监控端口
alarmCurrent_jvm_monitor_port=10019
alarmHistory_jvm_monitor_port=10029
alarmSet_jvm_monitor_port=10039
device_jvm_monitor_port=10049
lock_jvm_monitor_port=10059
log_jvm_monitor_port=10069
oss_jvm_monitor_port=10079
station_jvm_monitor_port=10089
system_jvm_monitor_port=10099
user_jvm_monitor_port=10109
monitor_port=10119


function echo_green {
        echo -e "\033[32m$1\033[0m"
}

# 登录 仓库
function loginAliyun() {
    docker login --username=15171085878 --password=${pwd} registry.cn-hangzhou.aliyuncs.com
}

# 拉取镜像
function pullImages() {
    echo_green "拉取当前注册中心服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-eureka-${version}

    echo_green "拉取当前告警服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-alarmcurrent-${version}

    echo_green "拉取历史告警服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-alarmhistory-${version}

    echo_green "拉取告警设置服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-alarmset-${version}

    echo_green "拉取设施服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-device-${version}

    echo_green "拉取网关服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-gateway-${version}

    echo_green "拉取电子锁服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-lock-${version}

    echo_green "拉取日志服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-log-${version}

    echo_green "拉取oss服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-oss-${version}

    echo_green "拉取定时任务服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-schedule-${version}

    echo_green "拉取中专服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-station-${version}

    echo_green "拉取系统服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-system-${version}

    echo_green "拉取用户服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-user-${version}

    echo_green "拉取Nginx服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-web
    echo_green "镜像拉取完毕，OVER"
}

    echo_green
    read -p "是否搭建注册中新高可用集群？（y/n)" isCluster
    echo_green ${isCluster}

    read eureka_cluster
    if [[ ${eureka_cluster} -eq y ]]; then
        echo_green "请输入集群数量，必须小于9,大于0"
        read cluster_count
        for (( i = 0; i < ${cluster_count}; i++ ));do
            echo_green
        done
    fi

# 启动注册中心
function startEureka() {
    echo_green "启动注册中心"
    docker run -p ${eureka_port}:${eureka_port}  -d  \
    -e PARAMS=阿里环境 -m 1200M --memory-swap 1200M --name filink-eureka-test ${adreess}:filink-eureka-${version}
}

# 启动网关服务
function startGateway() {
    echo_green "启动网关服务"
    docker run \
    -p ${gateway_port}:${gateway_port} -p ${gateway_websocket_port}:${gateway_websocket_port}  \
    -d  -e PARAMS=${params}\
     -m 1200M --memory-swap 1200M \
     --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
     --name filink-gateway-test ${adreess}:filink-gateway-${version}
}

# 启动当前告警服务
function startAlarmCurrent() {
    echo_green "启动当前告警服务"
    docker run  -d -p ${alarmCurrent_jvm_monitor_port}:${alarmCurrent_jvm_monitor_port}  \
    -e PARAMS=${params} \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-alarmcurrent-test ${adreess}:filink-alarmcurrent-${version}
}

# 启动历史告警服务
function startAlarmHistory() {
    echo_green "启动历史告警服务"
    docker run  -d -p ${alarmHistory_jvm_monitor_port}:${alarmHistory_jvm_monitor_port} \
    -e PARAMS=${params} \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-alarmhistory-test \
    ${adreess}:filink-alarmhistory-${version}
}

# 启动告警设置服务
function startAlarmSet() {
    echo_green "启动告警设置服务"
    docker run -d  -p ${alarmSet_jvm_monitor_port}:${alarmSet_jvm_monitor_port} \
    -e PARAMS=${params} \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-alarmset-test \
    ${adreess}:filink-alarmset-${version}
}

# 启动设施服务
function startDevice() {
    echo_green "启动设施服务"
    docker run  -d -p ${device_jvm_monitor_port}:${device_jvm_monitor_port}\
    -e PARAMS=${params} \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-device-test \
    ${adreess}:filink-device-${version}
}

# 启动电子锁服务
function startLock() {
    echo_green "启动电子锁服务"
    docker run  -d  -p ${lock_jvm_monitor_port}:${lock_jvm_monitor_port}\
    -e PARAMS=${params} \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-lock-test \
    ${adreess}:filink-lock-${version}
}

# 启动日志服务
function startLog() {
    echo_green "启动日志服务"
    docker run  -d  -p ${log_jvm_monitor_port}:${log_jvm_monitor_port}\
    -e PARAMS=${params} \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-log-test \
    ${adreess}:filink-log-${version}
}

# 启动OSS服务
function startOss() {
    echo_green "启动OSS服务"
    docker run  -d -p ${oss_jvm_monitor_port}:${oss_jvm_monitor_port}\
    -e PARAMS="--spring.cloud.stream.kafka.binder.brokers=$KAFKA_IP:9092 \
     --spring.cloud.stream.kafka.binder.zkNodes=$ZOOKEEPER_IP:2181 \
     --fdfs.basePath=http://47.92.110.221:4206/ \
     --fdfs.tracker-list=-172.26.233.79:22122" \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql --name filink-oss-test \
    ${adreess}:filink-oss-${version}
}

# 启动中转服务
function startStation() {
    echo_green "启动中转服务"
    docker run -p ${station_jvm_monitor_port}:${station_jvm_monitor_port}  -d  \
    -e PARAMS=${params} \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-station-test \
    ${adreess}:filink-station-${version}
}

# 启动系统服务
function startSystem() {
     echo_green "启动系统服务"
    docker run  -d  -p ${system_jvm_monitor_port}:${system_jvm_monitor_port} \
    -e PARAMS=${params} \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-system-test \
    ${adreess}:filink-system-${version}
}

# 启动用户服务
function startUser() {
    echo_green "启动用户服务"
    docker run  -d  -p ${user_jvm_monitor_port}:${user_jvm_monitor_port} \
    -e PARAMS=${params} \
    -m 1200M --memory-swap 1200M \
    --link filink-mongo --link filink-eureka --link filink-redis --link filink-mysql \
    --name filink-user-test \
    ${adreess}:filink-user-${version}
}

# 启动前端服务
function startWeb() {
    echo_green "启动Nginx前端服务"
    docker run -d \
    -p 9090:4200 \
    --name filink-web --privileged=true  \
    ${adreess}:filink-web
    # -v /filink/nginx/nginx.conf:/etc/nginx/nginx.conf \
    echo "服务启动完毕，访问端口9090"

}


# 启动服务
function startService() {
    startEureka
    startGateway
    startAlarmCurrent
    startAlarmHistory
    startAlarmSet
    startDevice
    startLock
    startLog
    startOss
    startStation
    startSystem
    startUser
}

# 执行
function execute() {
    loginAliyun
    pullImages
    startService
}

