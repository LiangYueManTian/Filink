#!/usr/bin/env bash
# author yaoyuan
# desc: 构建docker镜像
_jarPath=target/filink-system-server-0.0.1-RELEASE.jar
_imageName=filink-system-server
_imageTag=filink-system-test
_projectName=filink-system
if [[ -f ${_jarPath} ]]

then
    echo "===============开始构建docker镜像=============="
#    cd /root/.jenkins/workspace/${_projectName}/${_imageName}
    docker build -t filink/${_imageName} .
    echo "===============docker镜像构建完毕=============="
    rm -rf ${_jarPath}
    docker tag filink/${_imageName} registry.cn-hangzhou.aliyuncs.com/lulala/filink:${_imageTag}
    docker login --username=15171085878 --password=yaoyuan12345 registry.cn-hangzhou.aliyuncs.com
    echo "===================开始上传镜像================"
    docker push registry.cn-hangzhou.aliyuncs.com/lulala/filink:${_imageTag}
    echo "===================镜像上传完毕================"
else
    echo "===============jar包不存在，退出构建============"
fi
