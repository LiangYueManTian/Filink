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
package com.fiberhome.filink.txlcntm.support.service.impl;

import com.fiberhome.filink.txlcncommon.exception.FastStorageException;
import com.fiberhome.filink.txlcncommon.exception.TxManagerException;
import com.fiberhome.filink.txlcntm.config.TxManagerConfig;
import com.fiberhome.filink.txlcntm.core.storage.FastStorage;
import com.fiberhome.filink.txlcntm.support.service.ManagerService;
import com.fiberhome.filink.txlcntm.txmsg.MessageCreator;
import com.fiberhome.filink.txlcntxmsg.RpcClient;
import com.fiberhome.filink.txlcntxmsg.exception.RpcException;
import com.fiberhome.filink.txlcntxmsg.params.NotifyConnectParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/29
 *
 * @author codingapi
 */
@Service
@Slf4j
public class ManagerServiceImpl implements ManagerService {

    private final RpcClient rpcClient;

    private final FastStorage fastStorage;

    private final TxManagerConfig managerConfig;

    @Autowired
    public ManagerServiceImpl(RpcClient rpcClient, FastStorage fastStorage, TxManagerConfig managerConfig) {
        this.rpcClient = rpcClient;
        this.fastStorage = fastStorage;
        this.managerConfig = managerConfig;
    }


    @Override
    public boolean refresh(NotifyConnectParams notifyConnectParams) throws RpcException {
        List<String> keys = rpcClient.loadAllRemoteKey();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                rpcClient.send(key, MessageCreator.newTxManager(notifyConnectParams));
            }
        }
        return true;
    }

    @Override
    public long machineIdSync() throws TxManagerException {
        long machineMaxSize = ~(-1L << (64 - 1 - managerConfig.getSeqLen())) - 1;
        long timeout = managerConfig.getHeartTime() + 2000;
        long id;
        try {
            id = fastStorage.acquireMachineId(machineMaxSize, timeout);
        } catch (FastStorageException e) {
            throw new TxManagerException(e);
        }
        log.info("Acquired machine id {}, max machine id is: {}", id, machineMaxSize);
        return id;
    }

    @Override
    public void refreshMachines(long... machineId) throws TxManagerException {
        long timeout = managerConfig.getHeartTime() + 2000;
        fastStorage.refreshMachines(timeout, machineId);
    }
}
