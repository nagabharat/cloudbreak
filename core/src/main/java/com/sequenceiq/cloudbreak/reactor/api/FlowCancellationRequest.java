package com.sequenceiq.cloudbreak.reactor.api;

import java.util.concurrent.TimeUnit;

import reactor.rx.Promise;
import reactor.rx.Promises;

public class FlowCancellationRequest {
    private final Long stackId;
    private final Promise<Integer> result = Promises.prepare();

    public FlowCancellationRequest(Long stackId) {
        this.stackId = stackId;
    }

    public Long getStackId() {
        return stackId;
    }

    public Promise<Integer> getResult() {
        return result;
    }

    public Integer await() throws InterruptedException {
        return await(1, TimeUnit.SECONDS);
    }

    public Integer await(long timeout, TimeUnit unit) throws InterruptedException {
        Integer result = this.result.await(timeout, unit);
        if (result == null) {
            throw new InterruptedException("Operation timed out, couldn't retrieve result");
        }
        return result;
    }
}
