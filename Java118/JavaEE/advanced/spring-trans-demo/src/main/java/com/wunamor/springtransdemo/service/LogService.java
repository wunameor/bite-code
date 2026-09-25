package com.wunamor.springtransdemo.service;


import com.wunamor.springtransdemo.mapper.LogInfoMapper;
import com.wunamor.springtransdemo.model.LogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class LogService {

    @Autowired
    private LogInfoMapper logInfoMapper;

    @Transactional(propagation = Propagation.NESTED)
    public void insertLog(LogInfo logInfo) {
        logInfoMapper.insertLog(logInfo);
        try {
            int i = 10/0;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
