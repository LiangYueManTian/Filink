#!/usr/bin/env bash
# 安装并且启动数据库，前提是存在docker环境
# 数据库镜像命名 filink-mysql

function filink_mysql()
{
  docker inspect filink-mysql
  if [[ $? -eq 0 ]]; then
      echo "filink-mysql 容器存在，重新启动>>>>>>>>>>>>>"
      docker stop filink-mysql
      docker restart filink-mysql
  else
      echo "filink-mysql 容器不存在，创建容器并启动>>>>>>>>>"
      echo "请设置mysql密码："
      read password
      FILINK_MYSQL_PASSWORD=${password}
      export FILINK_MYSQL_PASSWORD
      docker run --name filink-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=${FILINK_MYSQL_PASSWORD} -d mysql:5.7
      echo "mysql5.7 已经安装并启动 ， ,"
      echo "容器名：      filink-mysql"
      echo "端口  ：      3306"
      echo "密码  :       ${FILINK_MYSQL_PASSWORD}"
  fi
}

function docker_mysql_install()
{
    echo "检测 mysql5.7 镜像是否存在>>>>>>>>>>>>>>>"
    docker inspect mysql:5.7
    if [[ $? -eq 0 ]]; then
        echo "mysql5.7已经存在, 检测docker容器是否存在>>>>>>>>>>>"
        filink_mysql
    else
        echo "mysql5.7不存在，执行安装程序>>>>>>>>>"
        docker pull mysql:5.7
        echo "请设置mysql密码："
        read password
        FILINK_MYSQL_PASSWORD=${password}
        export FILINK_MYSQL_PASSWORD
        docker run --name filink-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=${FILINK_MYSQL_PASSWORD} -d mysql:5.7
        echo "mysql5.7 已经安装并启动 ， ,"
        echo "容器名：      filink-mysql"
        echo "端口  ：      3306"
        echo "密码  :       ${FILINK_MYSQL_PASSWORD}"

        firewall-cmd --add-port=3306/tcp --permanent
        firewall-cmd --reload
#         导入脚本
    fi

}

docker_mysql_install