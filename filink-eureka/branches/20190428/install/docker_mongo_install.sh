#!/usr/bin/env bash
# mongo 安装脚本

function mongo_install() {
    docker pull mongo:3.4
    docker run --name filink-mongo --privileged=true -v /yy_filink/mongo:/data/db -p 27017:27017 -d mongo:3.4
    echo "mongoDB已经启动"
    echo "数据位置：/data/mongo"
    echo "端口：27017"
}

mongo_install