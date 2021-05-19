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

import com.fiberhome.filink.txlcncommon.exception.TxManagerException;
import com.fiberhome.filink.txlcncommon.util.id.ModIdProvider;
import com.fiberhome.filink.txlcntm.config.TxManagerConfig;
import com.fiberhome.filink.txlcntm.support.service.ManagerService;
import com.fiberhome.filink.txlcntm.txmsg.RpcExecuteService;
import com.fiberhome.filink.txlcntm.txmsg.TransactionCmd;
import com.fiberhome.filink.txlcntxmsg.RpcClient;
import com.fiberhome.filink.txlcntxmsg.RpcConfig;
import com.fiberhome.filink.txlcntxmsg.exception.RpcException;
import com.fiberhome.filink.txlcntxmsg.params.InitClientParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/29
 *
 * @author codingapi
 */
@Service(value = "rpc_init-client")
@Slf4j
public class InitClientService implements RpcExecuteService {

    private final RpcClient rpcClient;

    private final TxManagerConfig txManagerConfig;

    private final RpcConfig rpcConfig;

    private final ManagerService managerService;

    private final ModIdProvider modIdProvider;

    @Autowired
    public InitClientService(RpcClient rpcClient, TxManagerConfig txManagerConfig, RpcConfig rpcConfig,
                             ManagerService managerService, ModIdProvider modIdProvider) {
        this.rpcClient = rpcClient;
        this.txManagerConfig = txManagerConfig;
        this.rpcConfig = rpcConfig;
        this.managerService = managerService;
        this.modIdProvider = modIdProvider;
    }


    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws TxManagerException {
        InitClientParams initClientParams = transactionCmd.getMsg().loadBean(InitClientParams.class);
        log.info("Registered TC: {}", initClientParams.getLabelName());
        try {
            rpcClient.bindAppName(transactionCmd.getRemoteKey(), initClientParams.getAppName(), initClientParams.getLabelName());
        } catch (RpcException e) {
            throw new TxManagerException(e);
        }
        // Machine len and id
        initClientParams.setSeqLen(txManagerConfig.getSeqLen());
        initClientParams.setMachineId(managerService.machineIdSync());
        // DTX Time and TM timeout.
        initClientParams.setDtxTime(txManagerConfig.getDtxTime());
        initClientParams.setTmRpcTimeout(rpcConfig.getWaitTime());
        // TM Name
        initClientParams.setAppName(modIdProvider.modId());
        return initClientParams;
    }
}
