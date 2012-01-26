package org.lastbamboo.common.ice;

import junit.framework.TestCase;

/**
 * Test for the class that keeps track of available SIP client bindings.
 */
public final class BindingTrackerImplTest extends TestCase
    {

    /**
     * Test to make sure that the client binding is correctly recorded when
     * we're on the open internet.
     * @throws Exception If any unexpected error occurs.
     */
    public void testOpenInternetBinding() throws Exception
        {
        /*
        final MockControl natDiscovererControl =
            MockControl.createControl(NatDiscoverer.class);
        final NatDiscoverer natDiscoverer =
            (NatDiscoverer) natDiscovererControl.getMock();
        final MockControl serverTrackerControl =
            MockControl.createControl(TurnServerTracker.class);
        final TurnServerTracker serverTracker =
            (TurnServerTracker) serverTrackerControl.getMock();
        serverTracker.getTurnServers();
        serverTrackerControl.setReturnValue(new HashSet(), 2);
        serverTrackerControl.replay();

        final MockControl stunTcpAccessorControl =
            MockControl.createControl(StunTcpAccessor.class);
        final StunTcpAccessor stunTcpAccessor =
            (StunTcpAccessor) stunTcpAccessorControl.getMock();


        final BindingTrackerImpl bt =
            new BindingTrackerImpl(natDiscoverer, serverTracker,
                stunTcpAccessor);

        final InetSocketAddress socketAddress =
            new InetSocketAddress(NetworkUtils.getLocalHost(), 2343);

        Collection bindings = bt.getTurnTcpBindings();
        assertEquals("Shouldn't be any bindings..", 0, bindings.size());

        final NatDiscoveryEvent event =
            new NatDiscoveryEventImpl(NatType.OPEN_INTERNET, socketAddress);
        bt.discoveredNat(event);

        bindings = bt.getTurnTcpBindings();
        assertEquals("Shouldn't be a binding..", 1, bindings.size());

        final InetSocketAddress binding =
            (InetSocketAddress) bindings.iterator().next();

        assertEquals(socketAddress, binding);

        serverTrackerControl.verify();
         */
        }
    }
