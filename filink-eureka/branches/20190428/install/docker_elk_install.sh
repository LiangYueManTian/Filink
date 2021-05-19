#!/usr/bin/env bash
# elk 安装脚本
# author 姚远

function echo_green {
        echo -e "\033[32m$1\033[0m"
}

function elk_install() {
    echo_green "ELK安装成功"
}

elk_install
