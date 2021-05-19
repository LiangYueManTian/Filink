#!/usr/bin/env bash
# author yaoyuan
# desc: 构建docker镜像
_jarPath=/root/.jenkins/workspace/filink-alarmHistory/filink-alarmHistory-server/target/filink-alarmHistory-server-0.0.1-RELEASE.jar
_imageName=filink-alarmhistory-server
_imageTag=filink-alarmhistory
_projectName=filink-alarmHistory
if [[ -f ${_jarPath} ]]

then
    echo "===============开始构建docker镜像=============="
    cd /root/.jenkins/workspace/${_projectName}/filink-alarmHistory-server
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
