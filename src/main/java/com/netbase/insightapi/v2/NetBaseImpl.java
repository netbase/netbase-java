/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

public class NetBaseImpl implements NetBase
{
    public static Log LOG = LogFactory.getLog(NetBaseImpl.class);

    protected HttpHost api;
    protected DefaultHttpClient client;
    protected BasicHttpContext context;
    protected String oauthAccessToken_;

    /**
     * Creates the NetBase instance.
     * 
     * @param production
     * @throws Exception
     */
    NetBaseImpl (boolean production)
        throws Exception
    {
        String host = (production) ? "api.netbase.com" : "dev-api.netbase.com";
        api = new HttpHost(host, 443, "https");
    }

    /**
     * Creates the NetBase instance.
     * 
     * @param production
     * @throws Exception
     */
    NetBaseImpl (String user, String password, boolean production)
        throws Exception
    {
        this(production);
        setCredentials(user, password);
    }

    /**
     * Creates the NetBase instance.
     * 
     * @param production
     * @throws Exception
     */
    NetBaseImpl (String accessToken, boolean production)
        throws Exception
    {
        this(production);
        setCredentials(accessToken);
    }

    /**
     * Sets the Basic Authentication Credentials
     * 
     * @param user
     * @param password
     * @throws Exception
     */
    public void setCredentials (String user, String password)
        throws Exception
    {
        // Basic Auth with Pre-emptive challenge
        ((DefaultHttpClient)getHttpClient()).getCredentialsProvider()
                                            .setCredentials(new AuthScope(api.getHostName(),
                                                                          api.getPort()),
                                                            new UsernamePasswordCredentials(user,
                                                                                            password));
        AuthCache authCache = new BasicAuthCache();
        authCache.put(api, new BasicScheme());
        context = new BasicHttpContext();
        context.setAttribute(ClientContext.AUTH_CACHE, authCache);
    }

    /**
     * Sets the OAuth 2.0 Access Token
     * 
     * @param accessToken
     * @throws Exception
     */
    public void setCredentials (String accessToken)
        throws Exception
    {
        oauthAccessToken_ = "Bearer " + accessToken;
    }

    /**
     * Returns the HTTPComponents HttpClient instance. This can be used to set
     * Proxy settings or override timeout information.
     * 
     * @return
     * @throws Exception
     */
    public HttpClient getHttpClient ()
        throws Exception
    {
        if (client == null) {
            SSLSocketFactory sf = new SSLSocketFactory(new TrustSelfSignedStrategy(),
                                                       new BrowserCompatHostnameVerifier());
            Scheme scheme = new Scheme("https", api.getPort(), sf);

            client = new DefaultHttpClient();
            client.getConnectionManager().getSchemeRegistry().register(scheme);
        }
        return client;
    }

    protected Response _call (String service, Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException
    {
        HttpGet get = new HttpGet("/cb/insight-api/2/" + service + req);
        long startTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Calling service[" + service + "] with parameters: "
                    + req);
        }
        if (null != oauthAccessToken_) {
            get.addHeader("Authorization", oauthAccessToken_);
        }

        Response response = null;
        try {
            HttpResponse res = null;
            if (context != null) {
                res = getHttpClient().execute(api, get, context);
            }
            else {
                res = getHttpClient().execute(api, get);
            }

            long duration = System.currentTimeMillis() - startTime;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Call completed in " + duration + " ms");
            }
            response = new Response(res);
        }
        catch (Exception exc) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error calling [" + service + "] with params: " + req,
                          exc);
            }
            return null;
        }
        if (null != response && null != response.getErrorStatus()) {
            if (null == response.getErrorStatus().errorType()) {
                throw new ApiErrorException(response.getErrorStatus()
                                                    .errorCode(),
                                            response.getErrorStatus()
                                                    .errorMessage());
            }
            else {
                throw new ParamErrorException(response.getErrorStatus()
                                                      .errorType(),
                                              response.getErrorStatus()
                                                      .errorMessage());
            }
        }
        return response;
    }

    public Response profile (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException
    {
        return _call("profile", req);
    }

    public Response helloWorld (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException
    {
        return _call("helloWorld", req);
    }

    public Response topics (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException
    {
        return _call("topics", req);
    }

    public Response insightCount (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException
    {
        return _call("insightCount", req);
    }

    public Response metricValues (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException
    {
        return _call("metricValues", req);
    }

    public Response retrieveSentences (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException
    {
        return _call("retrieveSentences", req);
    }

    public void close ()
        throws Exception
    {
        if (client != null) {
            ((DefaultHttpClient)client).getConnectionManager().shutdown();
        }

    }
}
