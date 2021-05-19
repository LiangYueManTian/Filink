#!/usr/bin/env bash
# 执行sql文件
# MYSQL_ROOT_PASSWORD 数据库密码
# WORK_PATH 工作路径
# FILE_1 sql文件

mysql -uroot -pwistronits@123 <<EOF
source /filink/sql/filink_schedule.sql;
source /filink/sql/filink_sys.sql;
source /filink/sql/filink_user.sql;
EOF

