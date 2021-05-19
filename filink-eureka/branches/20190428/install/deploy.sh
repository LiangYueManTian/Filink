#!/usr/bin/env bash
# 一键部署脚本
# author 姚远

# 此脚本应用前提：仓库中有项目镜像，在全新的环境中部署，

echo "请输入ip,应用"
read

# step-1
# 判断当前环境是否有docker，如果没有则安装
source docker_install.sh

# step-2
# 判断当前环境是否有docker mysql ， 如果没有则安装
source docker_mysql_install.sh

# step-3
# 判断当前环境是否有docker mongo ， 如果没有则安装
source docker_mongo_install.sh

# step-4
# 判断当前环境是否有docker redis ， 如果没有则安装
source docker_redis_install.sh

# step-5
# 判断当前环境是否有docker kafka ，如果没有则安装
source docker_kafka_install.sh

# step-6
# 判断当前环境是否有docker fastdfs ，如果没有则安装
source docker_fastdfs_install.sh

# step-7
# 判断当前环境是否有docker nginx ，如果没有则安装
# 前端项目
# 已经打成docker镜像

# step-8
# 判断当前环境是否有elk , 如果没有则安装
source docker_elk_install.sh

# step-6
# 拉取项目镜像，并启动
source docker_project_deploy.sh

# step-7
# 创建管理关系，重新启动



