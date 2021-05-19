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
package com.fiberhome.filink.txlcntc.core.transaction.tcc.control;

import com.fiberhome.filink.txlcncommon.exception.TransactionClearException;
import com.fiberhome.filink.txlcncommon.exception.TransactionException;
import com.fiberhome.filink.txlcntc.core.DTXLocalContext;
import com.fiberhome.filink.txlcntc.core.DTXLocalControl;
import com.fiberhome.filink.txlcntc.core.TxTransactionInfo;
import com.fiberhome.filink.txlcntc.core.context.TCGlobalContext;
import com.fiberhome.filink.txlcntc.core.template.TransactionCleanTemplate;
import com.fiberhome.filink.txlcntc.core.template.TransactionControlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 侯存路
 */
@Service(value = "control_tcc_running")
@Slf4j
public class TccRunningTransaction implements DTXLocalControl {

    private final TCGlobalContext globalContext;

    private final TransactionCleanTemplate transactionCleanTemplate;

    private final TransactionControlTemplate transactionControlTemplate;

    @Autowired
    public TccRunningTransaction(TransactionCleanTemplate transactionCleanTemplate,
                                 TransactionControlTemplate transactionControlTemplate,
                                 TCGlobalContext globalContext) {
        this.transactionCleanTemplate = transactionCleanTemplate;
        this.transactionControlTemplate = transactionControlTemplate;
        this.globalContext = globalContext;
    }

    @Override
    public void preBusinessCode(TxTransactionInfo info) throws TransactionException {

        // 缓存TCC事务信息，如果有必要
        try {
            globalContext.tccTransactionInfo(info.getUnitId(), () -> TccStartingTransaction.prepareTccInfo(info))
                    .setMethodParameter(info.getTransactionInfo().getArgumentValues());
        } catch (Throwable throwable) {
            throw new TransactionException(throwable);
        }
    }

    @Override
    public void onBusinessCodeError(TxTransactionInfo info, Throwable throwable) {
        try {
            transactionCleanTemplate.clean(
                    DTXLocalContext.cur().getGroupId(),
                    info.getUnitId(),
                    info.getTransactionType(),
                    0);
        } catch (TransactionClearException e) {
            log.error("tcc > clean transaction error.", e);
        }
    }

    @Override
    public void onBusinessCodeSuccess(TxTransactionInfo info, Object result) throws TransactionException {
        transactionControlTemplate.joinGroup(info.getGroupId(), info.getUnitId(), info.getTransactionType(),
                info.getTransactionInfo());
    }

}
