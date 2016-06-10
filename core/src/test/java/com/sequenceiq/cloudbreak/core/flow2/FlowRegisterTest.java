package com.sequenceiq.cloudbreak.core.flow2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FlowRegisterTest {

    private FlowRegister underTest;

    @Mock
    private FlowRegister runningFlows;

    @Mock
    private Flow flow;

    @Before
    public void setup() {
        underTest = new FlowRegister();
        MockitoAnnotations.initMocks(this);
        given(flow.getFlowId()).willReturn("flowId");
    }

    @Test
    public void testPutStackIdNotExists() {
        assertTrue("Stack ID not exists, so false not acceptable", underTest.put(flow, "", 1L, false));
    }

    @Test
    public void testPutStackIdExistsAndNotForced() {
        underTest.put(flow, "", 1L, true);
        assertFalse("Stack ID already exists, so true not acceptable", underTest.put(flow, "", 1L, false));
    }

    @Test
    public void testPutStackIdExistsButForced() {
        underTest.put(flow, "", 1L, true);
        assertTrue("Stack ID already exists but forced, so false not acceptable", underTest.put(flow, "", 1L, true));
    }
}
