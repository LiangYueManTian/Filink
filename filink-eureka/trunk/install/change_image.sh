#!/usr/bin/env bash
# 换包脚本
# yaoyuan
version=sit

# 仓库地址
adreess=registry.cn-hangzhou.aliyuncs.com/filink/filink

function loginAliyun() {
    docker login --username=fiberhome_filink --password=filink@v2r1 registry.cn-hangzhou.aliyuncs.com
}


function changeService() {
    echo "是否更换$1？ 确认请输入 y ,不更换请输入其他"
    read input
    if [[ ${input} == "y" ]]; then
        echo "替换$1为最新镜像"
        echo ""
        docker pull ${adreess}:filink-$1-${version}
        docker rm -f filink-$1-${version}
        docker run --net=host -d \
        -e JAVA_OPTS="-XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx1024m -XX:+UseG1GC" \
        -e PARAMS="--spring.cloud.config.profile=pro --eureka.client.service-url.defaultZone=http://filink:123456@localhost:8761/eureka/" \
        --name filink-$1-${version} \
         registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-$1-${version}
    else
        echo "$1 保持当前版本不替换"
        echo ""
    fi
}

function batchChange() {
    #批量更换服务
    echo "不支持脚本换包的服务请联系纬创首席架构师----胡孝聪手动替换"
    echo "-------------------------------支持环保的服务为：-----------------"
    echo "-------------------------------schedule---------------------------"
    echo "-------------------------------alarmcurrent-----------------------"
    echo "-------------------------------alarmhistory-----------------------"
    echo "-------------------------------alarmset---------------------------"
    echo "-------------------------------gateway----------------------------"
    echo "-------------------------------lock-------------------------------"
    echo "-------------------------------log--------------------------------"
    echo "-------------------------------map--------------------------------"
    echo "-------------------------------oss--------------------------------"
    echo "-------------------------------station----------------------------"
    echo "-------------------------------system-----------------------------"
    echo "-------------------------------user-------------------------------"
    echo "-------------------------------workflow---------------------------"
    echo "-------------------------------workflow-business------------------"
    echo "-------------------------------oceanconnect-----------------------"
    echo "-------------------------------onenet-----------------------------"
    changeService schedule
    changeService alarmcurrent
    changeService alarmhistory
    changeService alarmset
    changeService device
    changeService gateway
    changeService lock
    changeService log
    changeService map
    changeService oss
    changeService station
    changeService system
    changeService user
    changeService workflow
    changeService workflow-business
    changeService oceanconnect
    changeService onenet
    echo "批量换包完毕，一共更换16个服务"
}

function checkInput() {
    serviceNames=("schedule" "alarmcurrent" "alarmhistory" "alarmset" "monitor" "device" "gateway" "lock" "log" "map" "oss" "station" "system" "user" "workflow" "workflow-business" "oceanconnect" "onenet")
    if [[ "${serviceNames[@]}" =~ $1 ]];then
            echo ""
    else
        echo "当前输入服务名称不存在，请重新输入！"
        echo ""
        changeOne
    fi

}

function changeOne() {
    echo "请输入服务名称，按 Ctril+C 结束换包"
    read serviceName
    checkInput ${serviceName}
    docker inspect filink-${serviceName}-sit
    if [[ $? -eq 0 ]]; then
        changeService ${serviceName}
    else
        echo "都没有这个服务，你更换个屁"
        echo ""
    fi
    changeOne
}

function change() {
    #  登录仓库
    loginAliyun
    
    #选择更换方式
    echo "请选择更换方式，-----------AAA 为批量更换----B 为指定服务更换  C 为更换前端服务----------------------"
    echo "-------------------------请输入AAA/B?------------------------------------------"
    read aorb
    if [[ ${aorb} == "AAA" ]]; then
        echo "当前选择为批量更换"
        batchChange
    elif [[ ${aorb} == "B" ]];then
        echo "当前选择为指定服务更换"
        changeOne
    elif [[ ${aorb} == "C" ]];then
        echo "当前选择为更换前端服务"
        docker pull ${adreess}:filink-web-pro
        docker rm -f filink-web-pro
        docker run -d --net=host \
        --name filink-web-pro \
        registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-web-pro
    else
        echo ""
    fi
}

change
