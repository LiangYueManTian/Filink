#!/usr/bin/env bash
# 拉取镜像启动项目
# author 姚远

pwd=filink@v2r1

# 仓库地址
adreess=registry.cn-hangzhou.aliyuncs.com/filink/filink
# 配置文件后缀
version=${version}

# 端口
#eureka_port=8761
#gateway_websocket_port=9001
#
## 端口
#gateway_port=10000
#alarmCurrent_port=10129
#alarmHistory_port=10029
#alarmSet_port=10039
#device_port=10049
#lock_port=10059
#log_port=10069
#oss_port=10079
#station_port=10089
#system_port=10099
#user_port=10139
#monitor_port=10210
#
#web_port=10140
#websocket_port=10150
#map_port=10160
#schedule_port=10170
#workflow_port=10180
#WORKFLOW_business_port=10190
#
#fastdfs_port=10201
#
#udp_port=10220
#
#oceanconnect_port=10230
#onenet_port=10240


function echo_green {
        echo -e "\033[32m$1\033[0m"
}

# 1. 登录仓库
function loginAliyun() {
    docker login --username=fiberhome_filink --password=${pwd} registry.cn-hangzhou.aliyuncs.com
}

# 2.拉取镜像
#  服务列表：
#       eureka
#       config
#       monitor
#       txlcn
#       schedule
#
#       alarmcurrent
#       alarmhistory
#       alarmset
#       device
#       gateway
#       lock
#       log
#       map
#       oss
#       station
#       system
#       user
#       workflow
#       workflow_business
#       oceanconnect
#       onenet
#       web
#       dump
#       rfid
function pullImages() {
    echo_green "1.拉取当前注册中心服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-eureka

    echo_green "2.拉取配置中心服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-config

    echo_green "3.拉取监控服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-monitor

#    build3 去除分布式事务组件
    echo_green "4.拉取分布式事物组件>>>>>>>>>>>>>>>>>>>>>>>>build3 去除分布式事务组件"
#    docker pull ${adreess}:filink-txlcn

    echo_green "5.拉取调度中心服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-schedule

    echo_green "6.拉取当前告警服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-alarmcurrent

    echo_green "7.拉取历史告警服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-alarmhistory

    echo_green "8.拉取告警设置服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-alarmset

    echo_green "9.拉取设施服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-device

    echo_green "10.拉取网关服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-gateway

    echo_green "11.拉取电子锁服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-lock

    echo_green "12.拉取日志服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-log

    echo_green "13.拉取map服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-map

    echo_green "14.拉取oss服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-oss

    echo_green "15.拉取中转服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-station

    echo_green "16.拉取系统服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-system

    echo_green "17.拉取用户服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-user

    echo_green "18.拉取工作流组件>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-workflow

    echo_green "19.拉取工单服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-workflow-business

    echo_green "20.拉取前端服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-web

    echo_green "21.拉取电信平台服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-oceanconnect

    echo_green "22.拉取移动平台服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-onenet

    echo_green "23.拉取RFID服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-rfid

    echo_green "24.拉取转储服务>>>>>>>>>>>>>>>>>>>>>>>>"
    docker pull ${adreess}:filink-dump

#    todo 后续服务添加
    echo_green "镜像拉取完毕，一共23个镜像，OVER"
}


# 启动注册中心
function startEureka() {
    echo_green "No.1 启动注册中心"
    docker stop filink-eureka
    docker rm filink-eureka
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx216m -XX:+UseG1GC" \
    -e PARAMS="--spring.profiles.active=pre" \
    -v /data/logs/filink-eureka:/logs \
    --name filink-eureka \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-eureka
}

# 启动配置中心服务
function startConfig() {
    echo_green "No.2 启动配置中心"
    docker stop filink-config
    docker rm filink-config
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx512m -XX:+UseG1GC" \
    -e PARAMS="--eureka.instance.ip-address=10.5.24.224" \
    -v /data/logs/filink-config:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-config \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-config
}

# 启动监控服务
function startMonitor() {
    echo_green "No.3 启动监控服务"
    docker stop filink-monitor
    docker rm filink-monitor
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx512m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-monitor:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-monitor \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-monitor
}

# 启动分布式事务组件
#function startTxlcn() {
#    echo_green "No.4 启动分布式事务组件"
#    docker stop filink-txlcn
#    docker rm filink-txlcn
#    docker run --net=host -d \
#    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
#    -e PARAMS="--spring.profiles.active=pro" \
#    --name filink-txlcn \
#    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-txlcn
#}

# 启动调度中心服务
function startSchedule() {
    echo_green "No.5 启动调度中心服务"
    docker stop filink-schedule
    docker rm filink-schedule
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-schedule:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-schedule \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-schedule
}

# 启动当前告警服务
function startAlarmCurrent() {
    echo_green "No.6 启动当前告警服务"
    docker stop filink-alarmcurrent
    docker rm filink-alarmcurrent
     docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-alarmcurrent:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-alarmcurrent \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-alarmcurrent
}

# 启动历史告警服务
function startAlarmHistory() {
    echo_green "No.7 启动历史告警服务"
    docker stop filink-alarmhistory
    docker rm filink-alarmhistory
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} \
    --eureka.instance.ip-address=10.5.24.224 \
    --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-alarmhistory:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-alarmhistory \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-alarmhistory

}

# 启动告警设置服务
function startAlarmSet() {
    echo_green "No.8 启动告警设置服务"
    docker stop filink-alarmset
    docker rm filink-alarmset
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx512m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-alarmset:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-alarmset \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-alarmset
}

# 启动设施服务
function startDevice() {
    echo_green "No.9 启动设施服务"
    docker stop filink-device
    docker rm filink-device
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx2048m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-device:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-device \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-device
}

# 启动网关服务
function startGateway() {
    echo_green "No.10 启动网关服务"
    docker stop filink-gateway
    docker rm filink-gateway
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-gateway:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-gateway \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-gateway
}

# 启动电子锁服务
function startLock() {
    echo_green "No.11 启动电子锁服务"
    docker stop filink-lock
    docker rm filink-lock
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-lock:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-lock \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-lock
}

# 启动日志服务
function startLog() {
    echo_green "No.12 启动日志服务"
    docker stop filink-log
    docker rm filink-log
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-log:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-log \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-log
}

# 启动map服务
function startMap() {
    echo_green "No.13 启动map服务"
    docker stop filink-map
    docker rm filink-map
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx512m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-map:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-map \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-map
}

# 启动OSS服务
function startOss() {
    echo_green "No.14 启动OSS服务"
    docker stop filink-oss
    docker rm filink-oss
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx512m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-oss:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-oss \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-oss
}

# 启动中转服务
function startStation() {
    echo_green "No.15 启动中转服务"
    docker stop filink-station
    docker rm filink-station
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-station:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-station \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-station
}

# 启动系统服务
function startSystem() {
    echo_green "No.16 启动系统服务"
    docker stop filink-system
    docker rm filink-system
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-system:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-system \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-system
}

# 启动用户服务
function startUser() {
    echo_green "No.17 启动用户服务"
    docker stop filink-user
    docker rm filink-user
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-user:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-user \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-user
}

# 启动工作流组件
function startWorkflow() {
    echo_green "No.18 启动工作流组件服务"
    docker stop filink-workflow
    docker rm filink-workflow
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-workflow:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-workflow \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-workflow
}

# 启动工单服务 卸载
function startWorkflowBusiness() {
    echo_green "No.19 启动工单服务"
    docker stop filink-workflow-business
    docker rm filink-workflow-business
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-workflow-business:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-workflow-business \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-workflow-business
}

# 启动电信平台服务
function startOceanconnect() {
    echo_green "No.20 启动电信平台服务"
    docker stop filink-oceanconnect
    docker rm filink-oceanconnect
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-oceanconnect:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-oceanconnect \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-oceanconnect
}

# 启动移动平台服务
function startOnenet() {
    echo_green "No.21 启动移动平台服务"
    docker stop filink-onenet
    docker rm filink-onenet
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-onenet:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-onenet \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-onenet
}

# 启动rfid服务
function startRFID() {
    echo_green "No.22 启动RFID服务"
    docker stop filink-rfid
    docker rm filink-rfid
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-rfid:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-rfid \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-rfid
}

# 启动转储服务
function startDump() {
    echo_green "No.23 启动转储服务"
    docker stop filink-dump
    docker rm filink-dump
    docker run --net=host -d \
    -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
    -e PARAMS="--spring.cloud.config.profile=${version} --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
    -v /data/logs/filink-dump:/logs \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-dump \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-dump
}

# 启动前端服务
function startWeb() {
    echo_green "No.24 启动Nginx服务"
    docker stop filink-web
    docker rm filink-web
    docker run -d --net=host \
    -v /etc/localtime:/etc/localtime:ro \
    --name filink-web \
    registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-web
    echo "服务启动完毕"
}

# 启动服务
function startService() {
    echo_green "注意："
    echo_green "-----脚本启动服务指定的是${version}配置文件，请注意配置文件中环境属性"
    echo_green "-----服务启动指定JVM参数为：-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC"
    echo_green "-----服务使用host网络模式，与宿主机共用ip和端口"
    startEureka
    startConfig
    sleep 30
    startMonitor
#    build3 去除分布式事务组件
#    startTxlcn
    startSchedule
    sleep 30
    startAlarmCurrent
    startAlarmHistory
    sleep 30
    startAlarmSet
    startDevice
    sleep 30
    startGateway
    startLock
    sleep 30
    startLog
    startMap
    sleep 30
    startOss
    startStation
    sleep 30
    startSystem
    startUser
    sleep 30
    startWorkflow
    startWorkflowBusiness
    sleep 30
    startOceanconnect
    startOnenet
    sleep 30
    startRFID
    startDump
    sleep 30
    startWeb
    echo_green "服务启动完毕"
}

# 执行
function execute() {
    loginAliyun
    pullImages
    echo "请输入配置文件后缀"
    read suffix
    version=${suffix}
    startService
}

execute
