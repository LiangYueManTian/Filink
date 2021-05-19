FROM java:8-alpine

MAINTAINER yaoyuan yuanyao@wistronits.com
# 放入文件 重命名
ADD target/filink-alarmHistory-server-0.0.1-RELEASE.jar filink-alarmHistory-server-0.0.1-RELEASE.jar

EXPOSE 9204

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /filink-alarmHistory-server-0.0.1-RELEASE.jar $PARAMS"]

