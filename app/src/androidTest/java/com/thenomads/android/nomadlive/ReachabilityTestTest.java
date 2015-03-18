package com.thenomads.android.nomadlive;

import android.util.Log;

import com.thenomads.android.nomadlive.internet.ReachabilityTest;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReachabilityTestTest extends TestCase {

    private static final String TAG = "ReachabilityTestUnit";
    private int port = -1;
    private String hostname = "";
    private ServerSocket s;
    private ServerSocket s1935;

    @Override
    public void setUp() {

        try {
            s = new ServerSocket(0);
            port = s.getLocalPort();
            hostname = s.getInetAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            s1935 = new ServerSocket(1935);
        } catch (IOException e) {
        }


        String address = s.getInetAddress().toString();

        Log.d(TAG, "" + s);
    }

    @Override
    public void tearDown() {
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            s1935.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Covers the most probable case in which it will be used.
     *
     * @throws Exception
     */
    public void testHostnameWithPort() throws Exception {
        boolean passed = testConnectionToServer("http://" + hostname + ":" + port);

        assertTrue(passed);
    }

    public void testLocalhostWithProtocolAndPort() throws Exception {
        boolean passed = testConnectionToServer("http://localhost:" + port);

        assertTrue(passed);
    }

    public void testAllInterfacesWithPort() throws Exception {
        boolean passed = testConnectionToServer("http://0.0.0.0:" + port);

        assertTrue(passed);
    }

    public void testOneTwentySevenWithPort() throws Exception {
        boolean passed = testConnectionToServer("http://127.0.0.1:" + port);

        assertTrue(passed);
    }

    /**
     * Should not pass if no protocol is specified.
     *
     * @throws Exception
     */
    public void testLocalhostWithPortNoProtocol() throws Exception {
        boolean passed = testConnectionToServer(hostname + ":" + port);

        assertTrue(!passed);
    }

    /**
     * Should not pass if no port is given.
     *
     * @throws Exception
     */
    public void testLocalhostWithProtocolNoPort() throws Exception {
        boolean passed = testConnectionToServer("http://" + hostname);

        assertTrue(!passed);
    }

    /**
     * Generic tester that checks if a certain URL string can be joined.
     * Because the testing is done locally, it will wait for only one second before
     * assuming the connection failed.
     *
     * @param url a String of the form "http://localhost:1935"
     * @return
     * @throws Exception
     */
    private boolean testConnectionToServer(String url) throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);
        boolean passed;

        new ReachabilityTest(null, url, new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                signal.countDown();
            }

            @Override
            public void onReachabilityTestFailed() {
            }
        }).execute();

        passed = signal.await(1, TimeUnit.SECONDS);

        return passed;

    }
}