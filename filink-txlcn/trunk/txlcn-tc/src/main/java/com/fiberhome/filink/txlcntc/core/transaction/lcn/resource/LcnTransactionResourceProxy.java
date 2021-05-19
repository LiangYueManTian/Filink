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
package com.fiberhome.filink.txlcntc.core.transaction.lcn.resource;

import com.fiberhome.filink.txlcncommon.exception.TCGlobalContextException;
import com.fiberhome.filink.txlcntc.aspect.weave.ConnectionCallback;
import com.fiberhome.filink.txlcntc.core.DTXLocalContext;
import com.fiberhome.filink.txlcntc.core.context.TCGlobalContext;
import com.fiberhome.filink.txlcntc.support.resouce.TransactionResourceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;

/**
 * @author lorne
 */
@Service(value = "transaction_lcn")
@Slf4j
public class LcnTransactionResourceProxy implements TransactionResourceProxy {

    private final TCGlobalContext globalContext;

    @Autowired
    public LcnTransactionResourceProxy(TCGlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public Connection proxyConnection(ConnectionCallback connectionCallback) throws Throwable {
        String groupId = DTXLocalContext.cur().getGroupId();
        try {
            return globalContext.getLcnConnection(groupId);
        } catch (TCGlobalContextException e) {
            LcnConnectionProxy lcnConnectionProxy = new LcnConnectionProxy(connectionCallback.call());
            globalContext.setLcnConnection(groupId, lcnConnectionProxy);
            lcnConnectionProxy.setAutoCommit(false);
            return lcnConnectionProxy;
        }
    }
}
