#!/usr/bin/env bash
# kafka 安装
# author 姚远

function echo_green {
        echo -e "\033[32m$1\033[0m"
}

function kafka_install() {
    echo "开始安装zookeeper>>>>>>>>>>>>>>>>>>>>"

#    下载zookeeper
    docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-zookeeper
    if [[ $? -eq 0 ]]; then
        echo_green "zookeeper 原始镜像已经存在，无需下载"
    else
        docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-zookeeper
    fi

#    下载kafka
    docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-kafka
    if [[ $? -eq 0 ]]; then
        echo_green "kafka 原始镜像已经存在，无需下载"
    else
        docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-kafka
    fi

#    启动zookeeper
    docker inspect filink-zookeeper
    if [[ $? -eq 0 ]]; then
        echo_green "zookeeper已经存在 ， 重新启动"
        docker restart filink-zookeeper
    else
        docker run -d --restart=always --privileged=true -v /etc/localtime:/etc/localtime:ro --name filink-zookeeper -p 2181:2181 -t registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-zookeeper
        docker cp filink-zookeeper:/opt/zookeeper-3.4.13/conf/zoo.cfg ./zoo.cfg
        sed -i "s/#maxClientCnxns=60/maxClientCnxns=1024/g"  zoo.cfg
        docker cp zoo.cfg filink-zookeeper:/opt/zookeeper-3.4.13/conf/zoo.cfg
        rm -rf zoo.cfg
        docker restart filink-zookeeper
    fi
    echo "zookeeper已经启动："
    echo "zookeeper配置端口为：2181>>>>>>>>>>>>>>>>>"

#    启动kafka
    docker inspect filink-kafka
    if [[ $? -eq 0 ]]; then
        echo_green "zookeeper已经存在 ， 重新启动"
        docker restart filink-kafka
    else
#        echo_green "请输入服务器内网ip:"
#        read kafka_ip
        docker run -d --restart=always --privileged=true --net=host \
        --name filink-kafka \
        -v /etc/localtime:/etc/localtime:ro \
        -e KAFKA_BROKER_ID=0 \
        -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
        -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
        -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-kafka
#        先切换为localhost
#        docker run -d --net=host \
#        --name filink-kafka \
#        -e KAFKA_BROKER_ID=0 \
#        -e KAFKA_ZOOKEEPER_CONNECT=${kafka_ip}:2181 \
#        -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://${kafka_ip}:9092 \
#        -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka
    fi
    echo "kafka已经启动："
    echo "kafka链接url为：${kafka_ip}:9092"


#    kafka应用于内部端口 ， 所以需要填写内网ip
#    firewall-cmd --add-port=2181/tcp --permanent
#    firewall-cmd --reload



#    docker run -d --rm --name filink-kafka --net=host -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=localhost:2182 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://10.5.24.224:9093 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9093 -t wurstmeister/kafka
#    docker run -d --rm --name kafka-kafka --net=host -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=39.98.72.132:2182 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://39.98.72.132:9093 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9093 -t wurstmeister/kafka
#    docker run -d --rm --name kafka-kafka2 --net=host -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=39.98.72.132:2182 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://39.98.72.132:9094 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9094 -t wurstmeister/kafka
    # 开放端口
#    firewall-cmd --add-port=9092/tcp --permanent
#    firewall-cmd --reload
}

kafka_install

#docker run --rm -d --name filink-kafka-one \
#--net=host \
#-e KAFKA_BROKER_ID=0 \
#-e KAFKA_ZOOKEEPER_CONNECT=172.26.233.79:2181 \
#-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://172.26.233.79:9092 \
#-e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9093 -t wurstmeister/kafka
#
#
## 云环境 启动
#docker run -d --name filink-kafka --net=host \
#-e KAFKA_BROKER_ID=0 \
#-e KAFKA_ZOOKEEPER_CONNECT=172.26.233.79:2181 \
#-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://172.26.233.79:9092 \
#-e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 \
#-t wurstmeister/kafka

# echo $(ip addr | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}')
