FROM java:8-alpine

MAINTAINER yaoyuan yuanyao@wistronits.com
# 放入文件 重命名
ADD target/filink-schedule-server-0.0.1-RELEASE.jar filink-schedule-server-0.0.1-RELEASE.jar

EXPOSE 9601

ENTRYPOINT ["java","-jar","-Xmx1024M","-Xms1024M","/filink-schedule-server-0.0.1-RELEASE.jar","--spring.profiles.active=test"]


