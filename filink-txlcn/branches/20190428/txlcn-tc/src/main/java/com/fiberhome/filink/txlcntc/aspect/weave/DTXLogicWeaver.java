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
package com.fiberhome.filink.txlcntc.aspect.weave;

import com.fiberhome.filink.txlcntc.aspect.DTXInfo;
import com.fiberhome.filink.txlcntc.core.DTXLocalContext;
import com.fiberhome.filink.txlcntc.core.DTXServiceExecutor;
import com.fiberhome.filink.txlcntc.core.TxTransactionInfo;
import com.fiberhome.filink.txlcntc.core.context.TCGlobalContext;
import com.fiberhome.filink.txlcntc.core.context.TxContext;
import com.fiberhome.filink.txlcntracing.TracingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/11/29
 *
 * @author ujued
 */
@Component
@Slf4j
public class DTXLogicWeaver {

    private final DTXServiceExecutor transactionServiceExecutor;

    private final TCGlobalContext globalContext;

    @Autowired
    public DTXLogicWeaver(DTXServiceExecutor transactionServiceExecutor, TCGlobalContext globalContext) {
        this.transactionServiceExecutor = transactionServiceExecutor;
        this.globalContext = globalContext;
    }

    public Object runTransaction(DTXInfo dtxInfo, BusinessCallback business) throws Throwable {

        if (Objects.isNull(DTXLocalContext.cur())) {
            DTXLocalContext.getOrNew();
        } else {
            return business.call();
        }

        log.debug("<---- TxLcn start ---->");
        DTXLocalContext dtxLocalContext = DTXLocalContext.getOrNew();
        TxContext txContext;
        // ---------- ???????????????????????????DTX??????????????????TxContext ---------- //
        if (globalContext.hasTxContext()) {
            // ???????????????????????????????????????
            txContext = globalContext.txContext();
            dtxLocalContext.setInGroup(true);
            log.debug("Unit[{}] used parent's TxContext[{}].", dtxInfo.getUnitId(), txContext.getGroupId());
        } else {
            // ????????????????????????????????????
            txContext = globalContext.startTx();
        }

        // ??????????????????
        if (Objects.nonNull(dtxLocalContext.getGroupId())) {
            dtxLocalContext.setDestroy(false);
        }

        dtxLocalContext.setUnitId(dtxInfo.getUnitId());
        dtxLocalContext.setGroupId(txContext.getGroupId());
        dtxLocalContext.setTransactionType(dtxInfo.getTransactionType());

        // ????????????
        TxTransactionInfo info = new TxTransactionInfo();
        info.setBusinessCallback(business);
        info.setGroupId(txContext.getGroupId());
        info.setUnitId(dtxInfo.getUnitId());
        info.setPointMethod(dtxInfo.getBusinessMethod());
        info.setPropagation(dtxInfo.getTransactionPropagation());
        info.setTransactionInfo(dtxInfo.getTransactionInfo());
        info.setTransactionType(dtxInfo.getTransactionType());
        info.setTransactionStart(txContext.isDtxStart());

        //LCN???????????????
        try {
            return transactionServiceExecutor.transactionRunning(info);
        } finally {
            // ??????????????????????????????????????????
            if (dtxLocalContext.isDestroy()) {
                // ????????????????????????
                synchronized (txContext.getLock()) {
                    txContext.getLock().notifyAll();
                }

                // TxContext?????????????????? ???????????????????????????????????????????????????
                if (!dtxLocalContext.isInGroup()) {
                    globalContext.destroyTx();
                }

                DTXLocalContext.makeNeverAppeared();
                TracingContext.tracing().destroy();
            }
            log.debug("<---- TxLcn end ---->");
        }
    }
}
