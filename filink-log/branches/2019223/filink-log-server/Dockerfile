FROM java:8-alpine

MAINTAINER yaoyuan yuanyao@wistronits.com
# 放入文件 重命名
ADD target/filink-log-server-0.0.1-RELEASE.jar filink-log-server-0.0.1-RELEASE.jar

EXPOSE 9911

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /filink-log-server-0.0.1-RELEASE.jar $PARAMS"]

#ENTRYPOINT ["java","-jar","-Xmx1024M","-Xms1024M","/filink-log-server-0.0.1-RELEASE.jar","--spring.profiles.active=test"]
#ENTRYPOINT ["sh","-c","java \
#-Djava.rmi.server.hostname=47.92.110.221 \
#-Dcom.sun.management.jmxremote \
#-Dcom.sun.management.jmxremote.rmi.port=10069 \
#-Dcom.sun.management.jmxremote.port=10069 \
#-Dcom.sun.management.jmxremote.authenticate=false \
#-Dcom.sun.management.jmxremote.ssl=false \
#-jar \
#-Xmx1024M -Xms1024M \
#/filink-log-server-0.0.1-RELEASE.jar \
#--spring.profiles.active=test $PARAMS"]

