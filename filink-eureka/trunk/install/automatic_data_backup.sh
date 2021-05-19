#!/usr/bin/env bash
# filink数据处理器
# 功能： 导出数据，自动执行 需要使用定时任务
# author 姚远 mysqlf@163.com

currTime=`date +"%Y-%m-%d %H:%M:%S"`

#检查目录是否存在
function checkDir() {
    if [[ ! -d "/data/export_data" ]];then
        mkdir -p /data/export_data
    else
#        文件夹存在
        echo ""
    fi

    # 检查日志文件是否存在，不存在则新建，存在则不做任何操作
    if [[ ! -f "/data/export_data/data_backup.log" ]]; then
        touch /data/export_data/data_backup.log
    fi

}

# 检查sql脚本是否存在
function checkSql() {
    if [[ ! -f "/data/export_data/filink.sql" ]];then
        echo "sql脚本文件不存在, 请确保 /data/export_data 下存在 filink.sql文件"
        exit 1
    else
#    文件存在
        echo ""
    fi
}

# 检查dump文件是否存在
function checkDump() {
    if [[ ! -d "/data/export_data/mongo/dump" ]];then
        echo "mongo备份文件不存在, 请确保 /data/export_data/mongo 下存在 dump 文件"
        exit 1
    else
#        文件夹存在
        echo ""
    fi
}


# ==================================我是分界线=============================================
# ================================= 以下是导出逻辑 =============================================

# 导出mysql
function exportMysql() {
    mkdir /data/export_data/mysql
    # 检查容器是否运行
    docker inspect filink-mysql
    if [[ $? -eq 0 ]]; then
        echo "开始导出mysql数据>>>>>>>>>>>"
        # 导出脚本需要注释掉第一行，否则导入报错
        docker exec -it filink-mysql mysqldump -uroot -pwistronits@123 --all-databases > /data/export_data/mysql/filink.sql
        sed -i '1d' /data/export_data/mysql/filink.sql
        echo "${currTime} mysql备份结束，数据存放位置：/data/export_data/mysql/" >> /data/export_data/data_backup.log
    else
        echo "${currTime} mysql备份失败，不存在filink-mysql容器" >> /data/export_data/data_backup.log

    fi
}

# 导出mongo
function exportMongo() {
    checkDir
    mkdir /data/export_data/mongo
    docker inspect filink-mongo
    if [[ $? -eq 0 ]]; then
        echo "开始导出mongo数据>>>>>>>>>>>"
        # 备份
        docker exec -it filink-mongo mongodump
        # copy
        docker cp filink-mongo:/dump/ /data/export_data/mongo/
        echo "${currTime} mongo备份结束，数据存放位置：/data/export_data/mongo/" >> /data/export_data/data_backup.log

    else
        echo "${currTime} mongo备份失败，不存在filink-mongo容器" >> /data/export_data/data_backup.log
    fi
}


# 导出数据
function exportData() {
    checkDir
    exportMysql
    exportMongo
}


# ==================================我是分界线=============================================

# 开始导出
function processStart() {
    echo "${currTime} 开始备份数据" >> /data/export_data/data_backup.log
    exportData
}

processStart



