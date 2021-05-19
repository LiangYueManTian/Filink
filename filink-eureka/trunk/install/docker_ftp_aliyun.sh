#!/usr/bin/env bash
# docker安装ftp
# author

function ftp_install() {
    docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-ftp
    if [[ $? -eq 0 ]]; then
#        镜像存在
        echo ""
    else
        docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-ftp
    fi

    docker inspect filink-ftp
    if [[ $? -eq 0 ]]; then
        docker restart filink-ftp
    else
        mkdir -p /data/ftp
        docker run -d \
        --restart=always --privileged=true \
        -v /data/ftp:/home/vsftpd \
        -v /etc/localtime:/etc/localtime:ro \
        -p 2340:20 \
        -p 2341:21 -p 21100-21110:21100-21110 \
        -e FTP_USER=filink \
        -e FTP_PASS=filink \
        -e PASV_MIN_PORT=21100 \
        -e PASV_MAX_PORT=21110 \
        -e PASV_ADDRESS=0.0.0.0 \
        --name filink-ftp registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-ftp

        modprobe ip_nat_ftp
        modprobe ip_conntrack_ftp
        firewall-cmd --add-port=2341/tcp --permanent
        firewall-cmd --reload
        echo "ftp服务已经启动"
        echo "端口为：2341"
        echo "用户名：flink"
        echo "密码  ：flink"
    fi
}

ftp_install
