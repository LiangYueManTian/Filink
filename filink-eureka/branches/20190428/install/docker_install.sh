#!/usr/bin/env bash
# 检测docker环境
# 如果当前环境没有安装docker 则使用yum安装

function docker_install()
{
    echo "正在检测 docker 环境>>>>>>>>>>>>>>>>>>>>>>>>"
    dokcer -v
    if [[ $? -eq 0 ]]; then
        echo "检测到 docker 已经安装"
    else
        echo "检测到 docker 未安装，执行yum安装docker程序"
        yum install docker
        service docker start
        echo "安装docker环境...安装完成!"
        echo "如果启动报错，出现：Job for docker.service failed because the control process exited with error code. See systemctl status docker.service and journalctl -xe for details"
        echo "原因是因为docker不支持图像内核驱动，需要编辑docker配置文件"
        echo "vi /etc/sysconfig/docker"
        echo "修改  --selinux-enabled=false"
        echo "重新编译  systemctl restart docker"
    fi
}

docker_install