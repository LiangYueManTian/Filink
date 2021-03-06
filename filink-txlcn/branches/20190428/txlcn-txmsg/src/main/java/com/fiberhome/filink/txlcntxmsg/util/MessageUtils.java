/*
 * Copyright 2017-2019 CodingApi .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fiberhome.filink.txlcntxmsg.util;


import com.fiberhome.filink.txlcntxmsg.MessageConstants;
import com.fiberhome.filink.txlcntxmsg.dto.MessageDto;

/**
 * Description:
 * Date: 2018/12/18
 *
 * @author ujued
 */
public class MessageUtils {

    /**
     * 响应消息状态
     *
     * @param messageDto 请求对象
     * @return  响应状态
     */
    public static boolean statusOk(MessageDto messageDto) {
        return messageDto.getState() == MessageConstants.STATE_OK;
    }
}
