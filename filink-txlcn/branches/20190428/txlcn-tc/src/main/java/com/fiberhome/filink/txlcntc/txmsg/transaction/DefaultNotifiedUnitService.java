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
package com.fiberhome.filink.txlcntc.txmsg.transaction;


import com.fiberhome.filink.txlcncommon.exception.TransactionClearException;
import com.fiberhome.filink.txlcncommon.exception.TxClientException;
import com.fiberhome.filink.txlcnlogger.TxLogger;
import com.fiberhome.filink.txlcntc.core.context.TCGlobalContext;
import com.fiberhome.filink.txlcntc.core.context.TxContext;
import com.fiberhome.filink.txlcntc.core.template.TransactionCleanTemplate;
import com.fiberhome.filink.txlcntc.txmsg.RpcExecuteService;
import com.fiberhome.filink.txlcntc.txmsg.TransactionCmd;
import com.fiberhome.filink.txlcntxmsg.params.NotifyUnitParams;

import java.io.Serializable;
import java.util.Objects;

/**
 * Description: 默认RPC命令业务
 * Date: 2018/12/20
 *
 * @author ujued
 */
public class DefaultNotifiedUnitService implements RpcExecuteService {

    private static final TxLogger txLogger = TxLogger.newLogger(DefaultNotifiedUnitService.class);

    private final TransactionCleanTemplate transactionCleanTemplate;

    private TCGlobalContext globalContext;

    public DefaultNotifiedUnitService(TransactionCleanTemplate transactionCleanTemplate, TCGlobalContext globalContext) {
        this.transactionCleanTemplate = transactionCleanTemplate;
        this.globalContext = globalContext;
    }

    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws TxClientException {
        try {
            NotifyUnitParams notifyUnitParams = transactionCmd.getMsg().loadBean(NotifyUnitParams.class);
            // 保证业务线程执行完毕后执行事务清理操作
            TxContext txContext = globalContext.txContext(transactionCmd.getGroupId());
            if (Objects.nonNull(txContext)) {
                synchronized (txContext.getLock()) {
                    txLogger.txTrace(transactionCmd.getGroupId(), notifyUnitParams.getUnitId(),
                            "clean transaction cmd waiting for business code finish.");
                    txContext.getLock().wait();
                }
            }
            // 事务清理操作
            transactionCleanTemplate.clean(
                    notifyUnitParams.getGroupId(),
                    notifyUnitParams.getUnitId(),
                    notifyUnitParams.getUnitType(),
                    notifyUnitParams.getState());
            return true;
        } catch (TransactionClearException | InterruptedException e) {
            throw new TxClientException(e);
        }
    }
}
