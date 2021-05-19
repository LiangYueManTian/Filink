#!/usr/bin/env bash
# author yaoyuan
# desc: 构建docker镜像
_jarPath=/root/.jenkins/workspace/filink-txlcn/txlcn-tm/target/txlcn-tm-0.0.1-RELEASE.jar
_imageName=filink-txlcn
_imageTag=filink-txlcn
_projectName=filink-txlcn
if [[ -f ${_jarPath} ]]

then
    echo "===============开始构建docker镜像=============="
    cd /root/.jenkins/workspace/filink-txlcn/txlcn-tm
    docker build -t filink/filink-txlcn .
    echo "===============docker镜像构建完毕=============="
    rm -rf ${_jarPath}
    docker tag filink/filink-txlcn registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-txlcn
    docker login --username=15171085878 --password=yaoyuan12345 registry.cn-hangzhou.aliyuncs.com
    echo "===================开始上传镜像================"
    docker push registry.cn-hangzhou.aliyuncs.com/lulala/filink:filink-txlcn
    echo "===================镜像上传完毕================"
else
    echo "===============jar包不存在，退出构建============"
fi
