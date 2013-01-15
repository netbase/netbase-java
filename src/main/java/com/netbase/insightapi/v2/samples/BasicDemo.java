/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2.samples;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class BasicDemo
{

    public static DefaultHttpClient createHttpsClient ()
        throws Exception
    {
        SSLSocketFactory sf = new SSLSocketFactory(new TrustSelfSignedStrategy(),
                                                   new BrowserCompatHostnameVerifier());
        Scheme scheme = new Scheme("https", 443, sf);

        DefaultHttpClient retVal = new DefaultHttpClient();
        retVal.getConnectionManager().getSchemeRegistry().register(scheme);
        return retVal;
    }

    public static void main (String[] args)
        throws Exception
    {

        // Please fill in your id/pwd here
        String id = "";
        String pwd = "";

        DefaultHttpClient client = createHttpsClient();
        try {
            HttpHost api = new HttpHost("dev-api.netbase.com", 443, "https");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("language", "Chinese"));

            String query = URLEncodedUtils.format(nvps, "UTF-8");

            HttpGet request = new HttpGet("/cb/insight-api/2/helloWorld" + "?"
                    + query);

            // Proxy
            HttpHost squidproxy = new HttpHost("127.0.0.1", 3128, "http");
            client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
                                            squidproxy);

            // Basic Auth with Pre-emptive challenge
            client.getCredentialsProvider()
                  .setCredentials(new AuthScope(api.getHostName(),
                                                api.getPort()),
                                  new UsernamePasswordCredentials(id, pwd));
            AuthCache authCache = new BasicAuthCache();
            authCache.put(api, new BasicScheme());
            BasicHttpContext context = new BasicHttpContext();
            context.setAttribute(ClientContext.AUTH_CACHE, authCache);

            // Send Request, Get Response
            System.out.println("Sending request to " + api + request + " via "
                    + squidproxy);
            HttpResponse response = client.execute(api, request, context);
            HttpEntity entity = response.getEntity();

            System.out.println("----------------------------");
            System.out.println(response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                System.out.println(headers[i]);
            }
            System.out.println("----------------------------");

            if (entity != null) {
                String jsonStr = EntityUtils.toString(entity);
                JSON retVal = JSONSerializer.toJSON(jsonStr);
                // JSONObject jObject = JSONObject.
                System.out.println(retVal.toString(1));
            }
        }
        finally {
            client.getConnectionManager().shutdown();
        }
    }
}
