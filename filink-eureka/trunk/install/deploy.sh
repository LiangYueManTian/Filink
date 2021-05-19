#!/usr/bin/env bash
# 一键部署脚本
# author 姚远

# 此脚本应用前提：仓库中有项目镜像，在全新的环境中部署，

# step-1
# 判断当前环境是否有docker，如果没有则安装
echo "是否安装Docker？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputDocker
if [[ ${inputDocker} == "y" ]]; then
    sh /data/shells/docker_install.sh
fi

# step-2
# 判断当前环境是否有docker mysql ， 如果没有则安装
echo "是否安装Mysql？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputMysql
if [[ ${inputMysql} == "y" ]]; then
    sh /data/shells/docker_mysql_install_aliyun.sh
fi

# step-3
# 判断当前环境是否有docker mongo ， 如果没有则安装
echo "是否安装MongoDB？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputMongo
if [[ ${inputMongo} == "y" ]]; then
    sh /data/shells/docker_mongo_install_aliyun.sh
fi


# step-4
# 判断当前环境是否有docker redis ， 如果没有则安装
echo "是否安装Redis？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputRedis
if [[ ${inputRedis} == "y" ]]; then
    sh /data/shells/docker_redis_install_aliyun.sh
fi


# step-5
# 判断当前环境是否有docker kafka ，如果没有则安装
echo "是否安装Kafka？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputKafka
if [[ ${inputKafka} == "y" ]]; then
    sh /data/shells/docker_kafka_install_aliyun.sh
fi


# step-6
# 判断当前环境是否有docker fastdfs ，如果没有则安装
echo "是否安装FastDFS？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputFastDfs
if [[ ${inputFastDfs} == "y" ]]; then
    sh /data/shells/docker_fastdfs_install.sh
fi


# step-8
# 判断当前环境是否有elk , 如果没有则安装 elk为选装，放开注释即为一键部署
#cd /data/shells
echo "是否安装ELK？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputELK
if [[ ${inputELK} == "y" ]]; then
     sh /data/shells/docker_elk_install.sh
fi

# 安装ftp
echo "是否安装ftp？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputFtp
if [[ ${inputFtp} == "y" ]]; then
     sh /data/shells/docker_ftp_aliyun.sh
fi


# step-6
# 拉取项目镜像，并启动
echo "是否开始构建项目所有服务？ 确认请输入 y ,不需要安装请按Enter键跳过"
read inputService
if [[ ${inputService} == "y" ]]; then
     sh /data/shells/docker_service_start.sh
fi




