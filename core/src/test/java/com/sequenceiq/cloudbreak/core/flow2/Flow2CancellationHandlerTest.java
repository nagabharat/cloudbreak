package com.sequenceiq.cloudbreak.core.flow2;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sequenceiq.cloudbreak.core.flow2.chain.FlowChains;
import com.sequenceiq.cloudbreak.reactor.api.FlowCancellationRequest;
import com.sequenceiq.cloudbreak.service.flowlog.FlowLogService;

import reactor.bus.Event;

public class Flow2CancellationHandlerTest {

    private static final String FLOW_ID = "flowId";
    private static final String FLOW_CHAIN_ID = "flowChainId";

    @InjectMocks
    private Flow2CancellationHandler underTest;

    @Mock
    private FlowLogService flowLogService;

    @Mock
    private FlowRegister runningFlows;

    @Mock
    private FlowChains flowChains;

    @Mock
    private Flow flow;

    private FlowCancellationRequest request = new FlowCancellationRequest(1L);

    @Before
    public void setUp() {
        underTest = new Flow2CancellationHandler();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCancelRunningFlows() {
        given(flowLogService.findAllRunningNonTerminationFlowIdsByStackId(anyLong())).willReturn(Collections.singleton(FLOW_ID));
        given(runningFlows.remove(FLOW_ID, request.getStackId())).willReturn(flow);
        given(runningFlows.getFlowChainId(eq(FLOW_ID))).willReturn(FLOW_CHAIN_ID);
        Event<FlowCancellationRequest> event = new Event<>(request);
        event.setKey(Flow2CancellationHandler.FLOW_CANCEL);
        underTest.accept(event);
        verify(flowLogService, times(1)).cancel(anyLong(), eq(FLOW_ID));
        verify(flowChains, times(1)).removeFlowChain(eq(FLOW_CHAIN_ID));
    }

}
