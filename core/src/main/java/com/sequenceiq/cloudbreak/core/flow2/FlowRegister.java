package com.sequenceiq.cloudbreak.core.flow2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
public class FlowRegister {
    private final Map<String, Pair<Flow, String>> runningFlows = new ConcurrentHashMap<>();
    private final Set<Long> stackIds = new HashSet<>();
    private final Lock writeLock = new ReentrantReadWriteLock(true).writeLock();

    public boolean put(Flow flow, String chainFlowId, Long stackId, boolean force) {
        writeLock.lock();
        try {
            if (!stackIds.add(stackId) && !force) {
                return false;
            }
        } finally {
            writeLock.unlock();
        }
        runningFlows.put(flow.getFlowId(), new ImmutablePair<>(flow, chainFlowId));
        return true;
    }

    public Flow get(String flowId) {
        return runningFlows.get(flowId).getLeft();
    }

    public String getFlowChainId(String flowId) {
        return runningFlows.get(flowId).getRight();
    }

    public Flow remove(String flowId, Long stackId) {
        writeLock.lock();
        try {
            stackIds.remove(stackId);
        } finally {
            writeLock.unlock();
        }
        Pair<Flow, String> pair = runningFlows.remove(flowId);
        return pair == null ? null : pair.getLeft();
    }
}
