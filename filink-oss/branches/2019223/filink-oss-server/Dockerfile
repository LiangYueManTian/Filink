FROM java:8-alpine

MAINTAINER yaoyuan yuanyao@wistronits.com
# 放入文件 重命名
ADD target/filink-oss-server-0.0.1-RELEASE.jar filink-oss-server-0.0.1-RELEASE.jar

EXPOSE 8001

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /filink-oss-server-0.0.1-RELEASE.jar $PARAMS"]

#ENTRYPOINT ["sh","-c","java \
#-Djava.rmi.server.hostname=47.92.110.221 \
#-Dcom.sun.management.jmxremote \
#-Dcom.sun.management.jmxremote.rmi.port=10079 \
#-Dcom.sun.management.jmxremote.port=10079 \
#-Dcom.sun.management.jmxremote.authenticate=false \
#-Dcom.sun.management.jmxremote.ssl=false \
#-jar \
#-Xmx1024M -Xms1024M \
#/filink-oss-server-0.0.1-RELEASE.jar \
#--spring.profiles.active=test $PARAMS"]
