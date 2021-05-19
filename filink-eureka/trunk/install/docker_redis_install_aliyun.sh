#!/usr/bin/env bash
# redis 安装脚本
# 从阿里云获取镜像

function echo_green {
        echo -e "\033[32m$1\033[0m"
}


function redis_install() {
    docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-redis
    if [[ $? -eq 0 ]]; then
        echo_green "redis原始镜像已经存在"
    else
        docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-redis
    fi

    docker inspect filink-redis
    if [[ $? -eq 0 ]]; then
        echo_green "redis 容器已经存在，重新启动>>>>>>>>>>>>>"
        docker restart filink-redis
    else
        echo "开始安装redis"
        mkdir -p /data/redis
        cd /data/redis
        mkdir data conf
        echo_green "开始下载redis配置文件"
    #    如果没有wget 指令 安装 yum install wget
        wget https://raw.githubusercontent.com/antirez/redis/5.0.3/redis.conf -O /data/redis/conf/redis.conf

      # 因为下载链接原因 先copy出来 再赛进去
#        docker run --rm -p 6379:6379 --privileged=true \
#        --name filink-redis -d redis redis-server /usr/local/etc/redis/redis.conf

#        docker cp filink-redis:/usr/local/etc/redis/redis.conf conf/redis.conf
#        docker rm -f filink-redis

        echo_green "修改配置文件"
        sed -i "s/127.0.0.1/0.0.0.0/g"  /data/redis/conf/redis.conf
        sed -i "s/protected-mode yes/protected-mode no/g"  /data/redis/conf/redis.conf
#        下面这一行是开启aof，开启后不断修改redis会增加磁盘io
#        sed -i "s/appendonly no/appendonly yes/g"  /data/redis/conf/redis.conf
        sed -i 's/# requirepass foobared/requirepass "filink"/g'  /data/redis/conf/redis.conf

#        docker cp /data/redis/conf/redis.conf filink-redis:/usr/local/etc/redis/redis.conf
#docker run --name redis-cluster -d -p 7000:7000 -p 7001:7001 -p 7002:7002 -p 7003:7003 -p 7004:7004 -p 7005:7005 -p 7006:7006 -p 7007:7007 grokzen/redis-cluster


        echo_green "启动redis>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
        docker run  -p 6379:6379 --privileged=true --restart=always \
        -v /data/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf \
        -v /etc/localtime:/etc/localtime:ro \
         --name filink-redis -d registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-redis redis-server /usr/local/etc/redis/redis.conf

    #    docker run  -p 3308:6379 -v /data/redis/data:/data -v /data/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf --privileged=true  --name filink-redis -d redis redis-server /usr/local/etc/redis/redis.conf

        echo_green "redis已经启动"
    #    echo_green "数据位置：/data/redis"
        echo_green "端口：6379"
        echo_green "密码：filink"
    fi
}

redis_install
