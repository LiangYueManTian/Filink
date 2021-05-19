#!/usr/bin/env bash
# mongo 安装脚本

function mongo_install() {


docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-mongo
if [[ $? -eq 0 ]]; then
    echo_green "mongo原始镜像已经存在"
else
    docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-mongo
fi

docker inspect filink-mongo
if [[ $? -eq 0 ]]; then
    echo_green "filink-mongo容器已经存在，重新启动》》》》》》"
    docker restart filink-mongo
else
    docker run --restart=always --privileged=true \
    --name filink-mongo \
    -v /etc/localtime:/etc/localtime:ro \
    -v /data/mongo/data:/data/db  \
    -p 27017:27017 \
    -d registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-mongo
    echo "mongoDB已经启动"
    echo "数据位置：/data/mongo/data"
    echo "端口：27017"
fi
}

mongo_install
