#!/usr/bin/env bash
# docker 安装fastdfs
# author yaoyuan
# 前提条件 docker环境存在

function echo_green {
        echo -e "\033[32m$1\033[0m"
}

docker pull qbanxiaoli/fastdfs

echo_green "请输入本地IP，服务器请输入公网IP"
read  FASTDFS_IP
# 设置全局环境变量
export FASTDFS_IP

echo_green "请输入Nginx映射端口，通过此IP访问资源，如8080"
read  FASTDFS_PORT
# 设置全局环境变量
export FASTDFS_PORT

docker stop filink-fastdfs
docker rm filink-fastdfs
docker run -d --restart=always --privileged=true --net=host --name=filink-fastdfs -e IP=${FASTDFS_IP} -e WEB_PORT=${FASTDFS_PORT} -v /filink/fastdfs:/var/local/fdfs qbanxiaoli/fastdfs
 # 开放端口
firewall-cmd --add-port=22122/tcp --permanent
firewall-cmd --add-port=23000/tcp --permanent
firewall-cmd --add-port=${FASTDFS_PORT}/tcp --permanent
firewall-cmd --reload
echo_green "fastdfs搭建完毕"
echo_green "IP： ${FASTDFS_IP}"
echo_green "PORT： ${FASTDFS_PORT}"