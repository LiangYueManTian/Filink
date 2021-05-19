FROM java:8-alpine

# 放入文件 重命名
ADD target/*.jar eureka.jar

EXPOSE 8761

ENTRYPOINT ["java","-jar","-Xmx1024M","-Xms1024M","/eureka.jar"]


