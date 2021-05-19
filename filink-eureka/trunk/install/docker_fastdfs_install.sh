#!/usr/bin/env bash
# docker 安装fastdfs
# author yaoyuan
# 前提条件 docker环境存在

function echo_green {
        echo -e "\033[32m$1\033[0m"
}

docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-fastdfs
if [[ $? -eq 0 ]]; then
    echo_green "fastdfs原始镜像已经存在"
else
    docker login --username=fiberhome_filink --password=filink@v2r1 registry.cn-hangzhou.aliyuncs.com
    docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-fastdfs
fi

docker inspect filink-fastdfs
if [[ $? -eq 0 ]]; then
    echo_green "fastdfs容器已经存在，重新启动"
    docker restart filink-fastdfs
else
    echo_green "请输入服务器内网ip"
    read fastdfs_ip
    docker run -d --restart=always \
    --privileged=true --net=host \
    --name=filink-fastdfs -e IP=${fastdfs_ip} \
    -v /etc/localtime:/etc/localtime:ro \
    -e WEB_PORT=10201 \
    -v /data/fastdfs:/var/local/fdfs registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-fastdfs
    # 开放端口
    firewall-cmd --add-port=22122/tcp --permanent
    firewall-cmd --add-port=23000/tcp --permanent
    firewall-cmd --add-port=${FASTDFS_PORT}/tcp --permanent
    firewall-cmd --reload
    echo_green "fastdfs启动完毕"
    echo_green "IP： ${fastdfs_ip}"
    echo_green "资源访问端口： 10201"
fi
