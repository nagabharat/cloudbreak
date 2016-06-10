package com.sequenceiq.cloudbreak.core.flow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.sequenceiq.cloudbreak.api.model.HostGroupAdjustmentJson;
import com.sequenceiq.cloudbreak.api.model.InstanceGroupAdjustmentJson;
import com.sequenceiq.cloudbreak.core.flow2.service.ErrorHandlerAwareFlowEventFactory;
import com.sequenceiq.cloudbreak.core.flow2.service.ReactorFlowManager;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.dispatch.ThreadPoolExecutorDispatcher;

@RunWith(MockitoJUnitRunner.class)
public class ReactorFlowManagerTest {

    @Mock
    private EventBus reactor;

    @Mock
    private ErrorHandlerAwareFlowEventFactory eventFactory;

    @InjectMocks
    @Spy
    private ReactorFlowManager flowManager;

    @Before
    public void setUp() throws Exception {
        reset(reactor);
        reset(eventFactory);
        when(reactor.notify((Object) anyObject(), any(Event.class))).thenReturn(new EventBus(new ThreadPoolExecutorDispatcher(1, 1)));
        when(eventFactory.createEvent(anyObject(), anyString())).thenReturn(new Event<Object>(String.class));
        doNothing().when(flowManager).cancelRunningFlows(anyLong());
    }

    @Test
    public void shouldReturnTheNextFailureTransition() throws Exception {
        Long stackId = 1L;
        InstanceGroupAdjustmentJson instanceGroupAdjustment = new InstanceGroupAdjustmentJson();
        HostGroupAdjustmentJson hostGroupAdjustment = new HostGroupAdjustmentJson();

        flowManager.triggerProvisioning(stackId);
        flowManager.triggerClusterInstall(stackId);
        flowManager.triggerClusterReInstall(stackId);
        flowManager.triggerStackStop(stackId);
        flowManager.triggerStackStart(stackId);
        flowManager.triggerClusterStop(stackId);
        flowManager.triggerClusterStart(stackId);
        flowManager.triggerTermination(stackId);
        flowManager.triggerForcedTermination(stackId);
        flowManager.triggerStackUpscale(stackId, instanceGroupAdjustment);
        flowManager.triggerStackDownscale(stackId, instanceGroupAdjustment);
        flowManager.triggerStackRemoveInstance(stackId, "instanceId");
        flowManager.triggerClusterUpscale(stackId, hostGroupAdjustment);
        flowManager.triggerClusterDownscale(stackId, hostGroupAdjustment);
        flowManager.triggerClusterSync(stackId);
        flowManager.triggerStackSync(stackId);
        flowManager.triggerFullSync(stackId);
        flowManager.triggerClusterCredentialChange(stackId, "admin", "admin1");
        flowManager.triggerClusterTermination(stackId);

        int count = 0;
        for (Method method : flowManager.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("trigger")) {
                count++;
            }
        }
        verify(reactor, times(count)).notify((Object) anyObject(), any(Event.class));
    }
}
