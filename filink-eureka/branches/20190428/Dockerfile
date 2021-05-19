FROM java:8-alpine

# 放入文件 重命名
ADD target/filink-eureka-0.0.2-RELEASE.jar eureka.jar

EXPOSE 8762

#ENV PARAMS=""

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /eureka.jar $PARAMS"]


