package com.iflytek.platform.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/16,17:19
 * @see
 */
public final class HttpsUtils {

    private static final int TIME_OUT = 20 * 1000;

    private HttpsUtils() {
        throw new AssertionError();
    }

    public static String get(String url, Map<String, Object> params) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        if (!url.endsWith("?")) {
            urlBuilder.append("?");
        }
        if (null != params && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!TextUtils.isEmpty(key)) {
                    value = null == value ? "" : value;
                    urlBuilder.append("&").append(key).append("=").append(value);
                }
            }
        }
        return get(urlBuilder.toString().replace("?&", "?"));
    }

    public static String post(String url, Map<String, Object> params) {
        // TODO: 8/30/16  
        return null;
    }

    private static String get(String url) {
        if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
            return null;
        }
        String result = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            if (url.startsWith("https")) {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new CustomTrustManager()}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new CustomHostnameVerifier());
                connection = (HttpsURLConnection) new URL(url).openConnection();
            } else {
                connection = (HttpURLConnection) new URL(url).openConnection();
            }
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            result = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Tools.close(reader);
            if (null != connection) {
                connection.disconnect();
            }
        }
        return result;
    }

    private static class CustomHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    private static class CustomTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    }

}