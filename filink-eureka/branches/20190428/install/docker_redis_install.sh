#!/usr/bin/env bash
# redis 安装脚本

function echo_green {
        echo -e "\033[32m$1\033[0m"
}


function redis_install() {
    docker pull redis:latest
    mkdir -p /data/redis
    cd /data/redis
    mkdir data conf
    echo_green "开始下载redis配置文件"
#    如果没有wget 指令 安装 yum install wget
    wget https://raw.githubusercontent.com/antirez/redis/5.0.3/redis.conf -O conf/redis.conf

    echo_green "修改配置文件"
    sed -i "s/127.0.0.1/0.0.0.0/g"  conf/redis.conf
    sed -i "s/protected-mode yes/protected-mode no/g"  conf/redis.conf
    sed -i "s/appendonly no/appendonly yes/g"  conf/redis.conf
    sed -i 's/# requirepass foobared/requirepass "filink"/g'  conf/redis.conf

    echo_green "启动redis>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
    docker run  -p 6379:6379 -v /data/redis/data:/data -v /data/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf --privileged=true  --name filink-redis -d redis redis-server /usr/local/etc/redis/redis.conf


    echo_green "redis已经启动"
    echo_green "数据位置：/data/redis"
    echo_green "端口：6379"
    echo_green "密码：filink"
}

redis_install