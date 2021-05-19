#!/usr/bin/env bash
# filink数据处理器
# 功能： 导出数据，导入数据  需要手动执行
# author 姚远 mysqlf@163.com

#检查目录是否存在
function checkDir() {
    if [[ ! -d "/data/export_data" ]];then
        mkdir -p /data/export_data
    else
#        文件夹存在
        echo ""
    fi
}

# 检查sql脚本是否存在
function checkSql() {
    if [[ ! -f "/data/export_data/mysql/filink.sql" ]];then
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
    else
        echo "filink-mysql容器不存在，请检查环境, 如果容器名称不是filink-mysql，请输入容器名称"
        read mysqlContainerName
        docker inspect ${mysqlContainerName}
        if [[ $? -eq 0 ]]; then
            docker exec -it ${mysqlContainerName} mysqldump -uroot -pwistronits@123 --all-databases > /data/export_data/mysqlfilink.sql
            sed -i '1d' /data/export_data/mysql/filink.sql
        else
            echo "${mysqlContainerName}不存在"
            exit 1
        fi
    fi
    echo "mysql 数据导出结束"
    echo "数据存放位置：/data/export_data/mysql/"
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
    else
        echo "filink-mongo容器不存在，请检查环境, 如果容器名称不是filink-mongo，请输入容器名称"
        read mongoContainerName
        docker inspect ${mongoContainerName}
        if [[ $? -eq 0 ]]; then
            echo "开始导出mongo数据>>>>>>>>>>>"
            docker exec -it filink-mongo mongodump
            docker cp filink-mongo:/dump/ /data/export_data/mongo/
        else
            echo "${mongoContainerName}不存在"
            exit 1
        fi

    fi
    echo "mongo 数据导出结束"
    echo "数据存放位置：/data/export_data/mongo/"
}


# 导出数据
function exportData() {
    checkDir
    echo "请选择导出数据库类型"
    #导出mysql/mongo/es
    echo "导出mysql请输入：mysql ， 导出mongo请输入：mongo ， 导出所有请输入：all"
    read chooseDatabase
    if [[ ${chooseDatabase} == "mysql" ]]; then
        exportMysql
    elif [[ ${chooseDatabase} == "mongo" ]]; then
        exportMongo
    elif [[ ${chooseDatabase} == "all" ]]; then
        exportMysql
        exportMongo
    else
        echo "请输入正确数据库类型"
        exportData
    fi
}

# ==================================我是分界线=============================================
# ================================= 以下是导入 =============================================

# 导入mysql
function importMysql() {
    # 检查容器是否存在
    docker inspect filink-mysql
    if [[ $? -eq 0 ]]; then
        checkSql
        docker exec -i filink-mysql mysql -h localhost -uroot -pwistronits@123 --default-character-set=utf8  < /data/export_data/mysql/filink.sql
    else
        echo "filink-mysql容器不存在，请检查环境, 如果容器名称不是filink-mysql，请输入容器名称"
        read mysqlContainerName
        docker inspect ${mysqlContainerName}
        if [[ $? -eq 0 ]]; then
            docker exec -i ${mysqlContainerName} mysql -h localhost -uroot -pwistronits@123 --default-character-set=utf8  < /data/export_data/mysql/filink.sql
        else
            echo "该容器不存在"
            exit 1
        fi
    fi
    echo "mysql 数据导入结束"
}


# 导入mongo
function importMongo() {
    checkDump
    docker inspect filink-mongo
    if [[ $? -eq 0 ]]; then
        docker cp /data/export_data/mongo/dump/ filink-mongo:/
        docker exec -it filink-mongo mongorestore /dump/
    else
        echo "filink-mongo容器不存在，请检查环境, 如果容器名称不是filink-mongo，请输入容器名称"
        read mongoContainerName
        docker inspect ${mongoContainerName}
        if [[ $? -eq 0 ]]; then
            docker cp /data/export_data/mongo/dump/ ${mongoContainerName}:/
            docker exec -it ${mongoContainerName} mongorestore /dump/
        else
            echo "${mongoContainerName}不存在"
            exit 1
        fi
    fi
    echo "mongo数据导入完毕"
}


# 导入数据
function importData() {
    echo "请选择导入数据库类型"
    #导入mysql/mongo/es
    echo "导入mysql请输入：mysql ， 导入mongo请输入：mongo ，  导入所有请输入：all"
    read chooseDatabase
    if [[ ${chooseDatabase} == "mysql" ]]; then
        importMysql
    elif [[ ${chooseDatabase} == "mongo" ]]; then
        importMongo
    elif [[ ${chooseDatabase} == "all" ]]; then
        importMysql
        importMongo
    else
        echo "请输入正确数据库类型"
        importData
    fi

}

# ==================================我是分界线=============================================

# 开始导出
function processStart() {
    echo "数据处理器开始工作，导入导出之前请确认数据库已经正常工作，如果是导入请确认数据脚本已经放置在正确目录下："
    echo "mysql脚本请放置在：/data/export_data/mysql/ 下"
    echo "mongo脚本请放置在：/data/export_data/mongo/ 下"

    echo "数据处理器开始工作，导出数据请按export，导入数据请输入import"
    read exportOrImport
    if [[ ${exportOrImport} == "import" ]]; then
    # 导入数据
        importData
    elif [[ ${exportOrImport} == "export" ]]; then
    # 导出数据
        exportData
    else
        echo "输入错误，请重新输入"
        processStart
    fi
}

processStart



