FROM java:8-alpine

MAINTAINER yaoyuan yuanyao@wistronits.com
# 放入文件 重命名
ADD target/filink-station-server-0.0.1-RELEASE.jar filink-station-server-0.0.1-RELEASE.jar

EXPOSE 1001

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /filink-station-server-0.0.1-RELEASE.jar $PARAMS"]
## 开启远程监控
#ENTRYPOINT ["sh","-c","java \
#-Djava.rmi.server.hostname=47.92.110.221 \
#-Dcom.sun.management.jmxremote \
#-Dcom.sun.management.jmxremote.rmi.port=10089 \
#-Dcom.sun.management.jmxremote.port=10089 \
#-Dcom.sun.management.jmxremote.authenticate=false \
#-Dcom.sun.management.jmxremote.ssl=false \
#-jar \
#-Xmx1024M -Xms1024M \
#/filink-station-server-0.0.1-RELEASE.jar \
#--spring.profiles.active=test $PARAMS"]


#Error: Could not create the Java Virtual Machine.
#Error: A fatal exception has occurred. Program will exit.

#-Djava.rmi.server.hostname=39.98.72.132 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.rmi.port=9358 -Dcom.sun.management.jmxremote.port=9358 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false






