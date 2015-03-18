package com.thenomads.android.nomadlive.internet;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.ServerSocket;

public class ReachabilityTestTest extends TestCase {

    private int port = -1;
    private ServerSocket s;

    @Override
    public void setUp() {

        try {
            s = new ServerSocket(0);
            port = s.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String address = s.getInetAddress().toString();

        System.out.println("Fake server listening on port: " + port);
        System.out.println("At address: " + address);
    }

    @Override
    public void tearDown() {
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testOnlineServer() throws Exception {

        new ReachabilityTest(null, "localhost:" + port, new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                assertTrue(true);
            }

            @Override
            public void onReachabilityTestFailed() {
                assertTrue(false);
            }
        }).execute();
    }

    public void testShouldFail() throws Exception {
        assertTrue(false);
    }

    public void testDoInBackground() throws Exception {

    }

    public void testOnPostExecute() throws Exception {

    }
}