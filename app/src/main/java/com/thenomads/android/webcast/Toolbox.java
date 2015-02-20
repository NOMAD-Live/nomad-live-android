package com.thenomads.android.webcast;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * A small toolbox with small method useful for the project.
 */
class Toolbox {

    /**
     * If an instance of Wowza is running at the given url, it should return something like:
     * <p/>
     * Wowza Streaming Engine 4 Trial Edition (Expires: Aug 10, 2015) 4.1.1 build13180
     *
     * @param unsafeURL a url to test
     * @return returns true if a Wowza instance seems to be running
     */
    public static boolean isWowzaAvailable(String unsafeURL) {

        boolean available = false;

        try {
            URL url = new URL(unsafeURL);

            // Strips to just protocol + host + port
            URL simpleURL = new URL(url.getProtocol() + "://" + url.getAuthority());

            // Retrieves content
            String content = Toolbox.getURLContentAsString(simpleURL);

            Log.d("Toolbox", "Page content:" + content);

            if (content.contains("Wowza Streaming Engine")) {
                available = true;
            }


        } catch (IOException e) {
            // Fails silently.
            // If a local path is passed it will do nothing.
            Log.d("Toolbox", "Wowza seems down !");
            e.printStackTrace();
        }

        return available;

    }

    static String getURLContentAsString(URL url) throws IOException {

        URLConnection urlConnection = url.openConnection();
        InputStream is = urlConnection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);

        int numCharsRead;
        char[] charArray = new char[1024];
        StringBuilder sb = new StringBuilder();

        while ((numCharsRead = isr.read(charArray)) > 0) {
            sb.append(charArray, 0, numCharsRead);
        }

        return sb.toString();
    }
}