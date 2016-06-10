package com.sequenceiq.cloudbreak.core.flow2;

import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.core.flow2.chain.FlowChains;
import com.sequenceiq.cloudbreak.reactor.api.FlowCancellationRequest;
import com.sequenceiq.cloudbreak.service.flowlog.FlowLogService;

import reactor.bus.Event;
import reactor.fn.Consumer;

@Component
public class Flow2CancellationHandler implements Consumer<Event<? extends FlowCancellationRequest>> {

    public static final String FLOW_CANCEL = "FLOWCANCEL";

    private static final Logger LOGGER = LoggerFactory.getLogger(Flow2CancellationHandler.class);

    @Inject
    private FlowLogService flowLogService;

    @Inject
    private FlowRegister runningFlows;

    @Inject
    private FlowChains flowChains;

    @Override
    public void accept(Event<? extends FlowCancellationRequest> event) {
        FlowCancellationRequest request = event.getData();
        Set<String> flowIds = flowLogService.findAllRunningNonTerminationFlowIdsByStackId(request.getStackId());
        LOGGER.info("Flow cancellation arrived: ids: {}", flowIds);
        for (String id : flowIds) {
            Flow flow = runningFlows.remove(id, request.getStackId());
            if (flow != null) {
                flowLogService.cancel(request.getStackId(), id);
                flowChains.removeFlowChain(runningFlows.getFlowChainId(id));
            }
        }
        request.getResult().accept(flowIds.size());
    }
}
