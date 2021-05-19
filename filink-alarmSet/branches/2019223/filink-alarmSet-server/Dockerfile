FROM java:8-alpine

MAINTAINER yaoyuan yuanyao@wistronits.com
# 放入文件 重命名
ADD target/filink-alarmSet-server-0.0.1-RELEASE.jar filink-alarmSet-server-0.0.1-RELEASE.jar

EXPOSE 9205

#ENTRYPOINT ["java","-jar","-Xmx1024M","-Xms1024M","/filink-alarmSet-server-0.0.1-RELEASE.jar","--spring.profiles.active=test"]

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /filink-alarmSet-server-0.0.1-RELEASE.jar $PARAMS"]



