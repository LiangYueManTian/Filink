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
package com.fiberhome.filink.txlcntm.txmsg.transaction;

import com.fiberhome.filink.txlcncommon.exception.TransactionException;
import com.fiberhome.filink.txlcncommon.exception.TxManagerException;
import com.fiberhome.filink.txlcnlogger.TxLogger;
import com.fiberhome.filink.txlcntm.core.TransactionManager;
import com.fiberhome.filink.txlcntm.txmsg.RpcExecuteService;
import com.fiberhome.filink.txlcntm.txmsg.TransactionCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Description:
 * Date: 2018/12/11
 *
 * @author ujued
 */
@Service("rpc_create-group")
public class CreateGroupExecuteService implements RpcExecuteService {

    private static final TxLogger txLogger = TxLogger.newLogger(CreateGroupExecuteService.class);

    private final TransactionManager transactionManager;

    @Autowired
    public CreateGroupExecuteService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws TxManagerException {
        try {
            transactionManager.begin(transactionCmd.getGroupId());
        } catch (TransactionException e) {
            throw new TxManagerException(e);
        }
        txLogger.txTrace(transactionCmd.getGroupId(), null, "created group");
        return null;
    }
}
