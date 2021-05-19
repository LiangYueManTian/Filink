#!/usr/bin/env bash
# kafka 安装
# author 姚远

function echo_green {
        echo -e "\033[32m$1\033[0m"
}

function kafka_install() {
    # 判断有无docker环境 没有则运行docker安装脚本
    docker stop filink-zookeeper
    docker stop filink-kafka
    docker rm filink-zookeeper
    docker rm filink-kafka

    # 安装zookeeper
    echo "开始安装zookeeper>>>>>>>>>>>>>>>>>>>>"
    echo "zookeeper配置端口为：2181>>>>>>>>>>>>>>>>>"
    docker pull wurstmeister/zookeeper
    docker run -d --name filink-zookeeper -p 2181:2181 -t wurstmeister/zookeeper
#    kafka应用于内部端口 ， 所以需要填写内网ip
#    firewall-cmd --add-port=2181/tcp --permanent
#    firewall-cmd --reload

    # 开放端口
    # 安装kafka
    docker pull wurstmeister/kafka
    echo_green "请输入zookeeper的ip地址，kafka应用于内部端口 ， 所以需要填写内网ip，默认端口为2181，如需修改端口，请修改脚本"
    read ZOOKEEPER_IP
    KAFKA_IP=$ZOOKEEPER_IP
    export KAFKA_IP
    export ZOOKEEPER_IP
    docker run -d --name filink-kafka -p 9092:9092 -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=${ZOOKEEPER_IP}:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://${ZOOKEEPER_IP}:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka
    # 开放端口
#    firewall-cmd --add-port=9092/tcp --permanent
#    firewall-cmd --reload
}

kafka_install


