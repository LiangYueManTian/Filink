#!/usr/bin/env bash
# elk 安装脚本
# author 姚远

function echo_green {
        echo -e "\033[32m$1\033[0m"
}

function elasticsearch_install() {
#    docker inspect docker.elastic.co/elasticsearch/elasticsearch:6.6.2
    docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-elasticsearch
    if [[ $? -eq 0 ]]; then
        echo_green "elasticsearch镜像已经存在"
    else
#        docker pull docker.elastic.co/elasticsearch/elasticsearch:6.6.2
        docker login --username=fiberhome_filink --password=filink@v2r1 registry.cn-hangzhou.aliyuncs.com
        docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-elasticsearch
        sysctl -w vm.max_map_count=262144
    fi

    docker inspect filink-elasticsearch
    if [[ $? -eq 0 ]]; then
        docker restart filink-elasticsearch
    else
#    -m 5120M --memory-swap=10240M  -m 300M 是可以使用的物理内存  --memory-swap 居然是容器可以使用的物理内存和可以使用的 swap 之和
        docker run -d --restart=always --privileged=true -v /etc/localtime:/etc/localtime:ro --net=host --name filink-elasticsearch -e "discovery.type=single-node" registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-elasticsearch
    fi
    echo_green "elasticsearch 已经启动"
    echo_green "本地访问端口 9200 9300"
}

function kibana_install() {
#    docker inspect docker.elastic.co/kibana/kibana:6.6.2
    docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-kibana
    if [[ $? -eq 0 ]]; then
          echo_green "kibana镜像已经存在"
    else
#        docker pull docker.elastic.co/kibana/kibana:6.6.2
        docker login --username=fiberhome_filink --password=filink@v2r1 registry.cn-hangzhou.aliyuncs.com
        docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-kibana
    fi

    docker inspect filink-kibana
    if [[ $? -eq 0 ]]; then
        docker restart filink-kibana
    else
        docker run -d --restart=always --privileged=true --net=host --name filink-kibana -v /etc/localtime:/etc/localtime:ro -e ELASTICSEARCH_URL=http://localhost:9200 registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-kibana
    fi
    firewall-cmd --add-port=5601/tcp --permanent
    firewall-cmd --reload
    echo_green "kibana 已经启动"
    echo_green "访问地址： ip:5601"

}

function logstash_install() {
#    docker inspect docker.elastic.co/logstash/logstash:6.6.2
    docker inspect registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-logstash
    if [[ $? -eq 0 ]]; then
          echo_green "logstash镜像已经存在"
    else
#        docker pull docker.elastic.co/logstash/logstash:6.6.2
        docker login --username=fiberhome_filink --password=filink@v2r1 registry.cn-hangzhou.aliyuncs.com
        docker pull registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-logstash
    fi

    docker inspect filink-logstash
    if [[ $? -eq 0 ]]; then
        docker restart filink-logstash
    else
        mkdir -p /data/elk/logstash/conf
        cd /data/elk/logstash/conf
        touch logstash.conf
echo """
input {
  tcp {
        host => \"0.0.0.0\"
        port => 4560
        mode => \"server\"
        tags => [\"tags\"]
        codec => json_lines
     }
 }

# 输出到elasticsearch或者文件,redis等
output {
  elasticsearch {
    hosts => [\"http://localhost:9200\"]
        index => \"%{[appname]}-%{+YYYY.MM.dd}\"
  }
  stdout {
        codec => rubydebug
  }
}
""" > logstash.conf
        docker run --net=host --name filink-logstash \
        -d -v /data/elk/logstash/conf/logstash.conf:/usr/share/logstash/pipeline/logstash.conf \
        -v /etc/localtime:/etc/localtime:ro \
        registry.cn-hangzhou.aliyuncs.com/filink/filink:filink-logstash
    fi
    echo_green "logstash 已经启动"
    echo_green "连接端口： 4560"
}

function elk_install() {
    elasticsearch_install
    kibana_install
    logstash_install
    echo_green "ELK安装成功"
}
elk_install
