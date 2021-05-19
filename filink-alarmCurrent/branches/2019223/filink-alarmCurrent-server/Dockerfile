FROM java:8-alpine

MAINTAINER yaoyuan yuanyao@wistronits.com
# 放入文件 重命名
ADD target/filink-alarmCurrent-server-0.0.1-RELEASE.jar filink-alarmCurrent-server-0.0.1-RELEASE.jar

EXPOSE 9203

ENTRYPOINT ["sh","-c","java \
-Djava.rmi.server.hostname=47.92.110.221 \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.rmi.port=10019 \
-Dcom.sun.management.jmxremote.port=10019 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-jar \
-Xmx1024M -Xms1024M \
/filink-alarmCurrent-server-0.0.1-RELEASE.jar \
--spring.profiles.active=test $PARAMS"]
#ENTRYPOINT ["sh","-c","java -Djava.rmi.server.hostname=39.98.72.132 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.rmi.port=10109 -Dcom.sun.management.jmxremote.port=10109 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar -Xmx1024M -Xms1024M /filink-alarmCurrent-server-0.0.1-RELEASE.jar --spring.profiles.active=test $PARAMS"]


