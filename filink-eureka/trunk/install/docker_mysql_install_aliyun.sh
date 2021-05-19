#!/usr/bin/env bash
# 安装并且启动数据库，前提是存在docker环境
# 数据库镜像命名 filink-mysql
function run_filink_mysql() {
    echo "请保证 /data/mysql/ 下存在数据库脚本"

    docker run -d --name filink-mysql -e MYSQL_ROOT_PASSWORD=wistronits@123 -p 3306:3306  registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-mysql
    # 修改忽略大小写
    docker cp filink-mysql:/etc/mysql/mysql.conf.d/mysqld.cnf /data/shells/mysqld.cnf

    echo "lower_case_table_names=1" >> /data/shells/mysqld.cnf
    echo "max_connections=1000" >> /data/shells/mysqld.cnf
    echo "character_set_server=utf8" >> /data/shells/mysqld.cnf
    echo "character_set_filesystem=utf8" >> /data/shells/mysqld.cnf
    echo "collation-server=utf8_general_ci" >> /data/shells/mysqld.cnf
    echo "init-connect='SET NAMES utf8'" >> /data/shells/mysqld.cnf
    echo "init_connect='SET collation_connection = utf8_general_ci'" >> /data/shells/mysqld.cnf
    echo "skip-character-set-client-handshake" >> /data/shells/mysqld.cnf
    docker rm -f filink-mysql

# 注意文件权限，重启
    docker run --restart=always --privileged=true -d --name filink-mysql \
    -v /etc/localtime:/etc/localtime:ro \
    -v /data/mysql:/docker-entrypoint-initdb.d \
    -v /data/shells/mysqld.cnf:/etc/mysql/mysql.conf.d/mysqld.cnf \
    -e MYSQL_ROOT_PASSWORD=wistronits@123 \
    -p 3306:3306  registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-mysql

    echo "正在导入数据，请等待2分钟>>>>>>>>>>>>>>>>>>>>"
    echo "如果初始化数据过大，存在导入部分不成功，请手动导入数据"

    sleep 60

    firewall-cmd --add-port=3306/tcp --permanent
    firewall-cmd --reload
    echo "mysql5.6 已经安装并启动 "
    echo "容器名：      filink-mysql"
    echo "端口  ：      3306"
    echo "密码  :       wistronits@123"
    echo "密码  :       如果需要修改密码请修改脚本和修改配置文件"
}

function filink_mysql(){
  docker inspect filink-mysql
  if [[ $? -eq 0 ]]; then
      echo "filink-mysql 容器存在，重新启动>>>>>>>>>>>>>"
      docker stop filink-mysql
      docker restart filink-mysql
  else
      echo "filink-mysql 容器不存在，创建容器并启动>>>>>>>>>"
      run_filink_mysql
  fi
}



function docker_mysql_install()
{
    echo "检测 mysql5.6 镜像是否存在>>>>>>>>>>>>>>>"
    docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-mysql
    if [[ $? -eq 0 ]]; then
        echo "mysql5.6已经存在, 检测docker容器是否存在>>>>>>>>>>>"
        filink_mysql
    else
        echo "mysql5.6不存在，执行安装程序>>>>>>>>>"
        docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-mysql
        run_filink_mysql
    fi

}

docker_mysql_install


