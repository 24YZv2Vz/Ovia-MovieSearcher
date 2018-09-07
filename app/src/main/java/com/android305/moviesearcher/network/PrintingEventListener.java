package com.android305.moviesearcher.network;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class PrintingEventListener extends EventListener {
    private long callStartNanos;
    private HashMap<String, Long> lastNanos = new HashMap<>();
    private String url;

    PrintingEventListener(String url) {
        this.url = url;
    }

    private void printEvent(String name, boolean end) {
        printEvent(name, end, false);
    }

    private void printEvent(String name, boolean end, boolean force) {
        long nanos = System.nanoTime();
        Long processedNanos = lastNanos.get(name);
        if (end && processedNanos != null) {
            long elapsedMS = (nanos - processedNanos) / 1000000;
            Log.v("Perf." + url + "." + name, String.format(Locale.US, "Process time: %dms", elapsedMS));
            return;
        } else if (force) {
            long elapsedNanos = nanos - callStartNanos;
            Log.v("Perf." + url + "." + name, String.format(Locale.US, "At: %dms", elapsedNanos / 1000000));
        }
        lastNanos.put(name, nanos);
    }

    @Override
    public void callStart(Call call) {
        callStartNanos = System.nanoTime();
        printEvent("call", false);
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        printEvent("dns", false);
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        printEvent("dns", true);
    }

    @Override
    public void connectStart(
            Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        printEvent("connect", false);
    }

    @Override
    public void secureConnectStart(Call call) {
        printEvent("secureConnect", false);
    }

    @Override
    public void secureConnectEnd(Call call, Handshake handshake) {
        printEvent("secureConnect", true);
    }

    @Override
    public void connectEnd(
            Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        printEvent("connect", true);
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy,
                              Protocol protocol, IOException ioe) {
        printEvent("connectFailed", false, true);
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        //printEvent("connection", false);
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        //printEvent("connection", true);
    }

    @Override
    public void requestHeadersStart(Call call) {
        //printEvent("requestHeaders", false);
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        //printEvent("requestHeaders", true);
    }

    @Override
    public void requestBodyStart(Call call) {
        printEvent("requestBody", false);
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        printEvent("requestBody", true);
    }

    @Override
    public void responseHeadersStart(Call call) {
        //printEvent("responseHeaders", false);
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        //printEvent("responseHeaders", true);
    }

    @Override
    public void responseBodyStart(Call call) {
        printEvent("responseBody", false);
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        printEvent("responseBody", true);
    }

    @Override
    public void callEnd(Call call) {
        printEvent("call", true);
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        printEvent("callFailed", false, true);
    }
}