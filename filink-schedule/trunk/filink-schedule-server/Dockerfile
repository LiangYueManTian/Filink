FROM java:8-alpine

MAINTAINER yaoyuan yuanyao@wistronits.com
# 放入文件 重命名
ADD target/filink-schedule-server-0.0.3-RELEASE.jar app.jar

EXPOSE 9601

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app.jar $PARAMS"]

RUN echo "Asia/Shanghai" > /etc/timezone
