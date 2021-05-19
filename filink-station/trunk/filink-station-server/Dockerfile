FROM java:8-alpine

# 放入文件 重命名
ADD target/filink-station-server-0.0.3-RELEASE.jar app.jar

EXPOSE 8888

#ENV PARAMS=""

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app.jar $PARAMS"]

RUN echo "Asia/Shanghai" > /etc/timezone
