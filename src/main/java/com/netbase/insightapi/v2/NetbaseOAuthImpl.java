/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class NetbaseOAuthImpl implements NetbaseOAuth
{
    public static Log LOG = LogFactory.getLog(NetbaseOAuthImpl.class);
    static String AUTHORIZATON_CODE = "authorization_code";
    static String REFRESH_TOKEN = "refresh_token";
    static String ACCESS_TOKEN_REQUEST = "/cb/oauth/token";
    static String AUTHORIZE_REQUEST = "/cb/oauth/authorize";
    static String PARAM_REFRESH_TOKEN = "refresh_token";
    static String PARAM_REDIRECT_URI = "redirect_uri";
    static String PARAM_SECRET = "client_secret";
    static String PARAM_CODE = "code";
    static String PARAM_GRANT_TYPE = "grant_type";
    static String PARAM_CLIENT_ID = "client_id";
    static String PARAM_RESPONSE_TYPE = "response_type";

    static String RESPONSE_TYPE = "code";
    static String RESP_REFRESH_TOKEN = "refresh_token";
    static String RESP_ACCESS_TOKEN = "access_token";
    static String RESP_TOKEN_TYPE = "token_type";
    static String RESP_EXPIRED_IN = "expires_in";

    private String redirectUri_ = null;
    private String clientId_ = null;
    private String clientSecret_ = null;
    protected HttpHost api_;
    protected DefaultHttpClient client_;

    public NetbaseOAuthImpl (String redirectUri,
                             String clientId,
                             String clientSecret,
                             boolean production)
    {
        redirectUri_ = redirectUri;
        clientId_ = clientId;
        clientSecret_ = clientSecret;
        String host = (production) ? "api.netbase.com" : "dev-api.netbase.com";
        api_ = new HttpHost(host, 443, "https");
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
        if (client_ == null) {
            SSLSocketFactory sf = new SSLSocketFactory(new TrustSelfSignedStrategy(),
                                                       new BrowserCompatHostnameVerifier());
            Scheme scheme = new Scheme("https", api_.getPort(), sf);

            client_ = new DefaultHttpClient();
            client_.getConnectionManager().getSchemeRegistry().register(scheme);
        }
        return client_;
    }

    protected HttpResponse _send (String service, List<NameValuePair> nvps)
        throws ClientProtocolException,
            IOException,
            Exception
    {
        long startTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Calling service[" + service + "] with parameters: "
                    + nvps);
        }
        HttpPost atRequest = new HttpPost(service);
        atRequest.setEntity(new UrlEncodedFormEntity(nvps));

        HttpResponse res = getHttpClient().execute(api_, atRequest);

        long duration = System.currentTimeMillis() - startTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Call completed in " + duration + " ms");
        }
        return res;

    }

    protected TokenResponse _call (String service, List<NameValuePair> nvps)
        throws ApiErrorException,
            ParamErrorException
    {
        TokenResponse response = null;
        ErrorStatus errorStatus = null;
        JSONObject entity = null;
        try {

            HttpResponse res = _send(service, nvps);

            try {
                String json = EntityUtils.toString(res.getEntity());
                entity = JSONObject.fromObject(JSONSerializer.toJSON(json));
            }
            catch (ParseException pe) {
                LOG.error("Fatal error when parsing http response, usually caused by internet issues or internal errors. Please check support.\n",
                          pe);
            }
            catch (IOException ie) {
                LOG.error("Fatal error with http response, usually caused by internet issues or internal errors. Please check support.\n",
                          ie);
            }
            errorStatus = new ErrorResponseParser().parse(entity);
        }
        catch (Exception exc) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error calling [" + service + "] with params: "
                        + nvps, exc);
            }
            return null;
        }

        if (null != errorStatus) {
            if (null == errorStatus.errorType()) {
                throw new ApiErrorException(errorStatus.errorCode(),
                                            errorStatus.errorMessage());
            }
            else {
                throw new ParamErrorException(errorStatus.errorType(),
                                              errorStatus.errorMessage());
            }
        }
        if (null != entity) {
            response = new TokenResponse();
            if (entity.has(RESP_ACCESS_TOKEN)) {
                response.setAccessToken(entity.getString(RESP_ACCESS_TOKEN));
            }
            if (entity.has(RESP_REFRESH_TOKEN)) {
                response.setRefreshToken(entity.getString(RESP_REFRESH_TOKEN));
            }
            if (entity.has(RESP_TOKEN_TYPE)) {
                response.setTokenType(entity.getString(RESP_TOKEN_TYPE));
            }
            if (entity.has(RESP_EXPIRED_IN)) {
                response.setExpiresIn(entity.getInt(RESP_EXPIRED_IN));
            }
        }
        return response;
    }

    public TokenResponse getAccessTokenByAuthCode (String code)
        throws ApiErrorException,
            ParamErrorException
    {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(PARAM_REDIRECT_URI, redirectUri_));
        nvps.add(new BasicNameValuePair(PARAM_SECRET, clientSecret_));
        nvps.add(new BasicNameValuePair(PARAM_GRANT_TYPE, AUTHORIZATON_CODE));
        nvps.add(new BasicNameValuePair(PARAM_CLIENT_ID, clientId_));
        nvps.add(new BasicNameValuePair(PARAM_CODE, code));
        return _call(ACCESS_TOKEN_REQUEST, nvps);
    }

    public TokenResponse getRefreshedToken (String refreshToken)
        throws ApiErrorException,
            ParamErrorException
    {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(PARAM_SECRET, clientSecret_));
        nvps.add(new BasicNameValuePair(PARAM_GRANT_TYPE, REFRESH_TOKEN));
        nvps.add(new BasicNameValuePair(PARAM_CLIENT_ID, clientId_));
        nvps.add(new BasicNameValuePair(PARAM_REFRESH_TOKEN, refreshToken));
        return _call(ACCESS_TOKEN_REQUEST, nvps);
    }

    public void close ()
        throws Exception
    {
        if (client_ != null) {
            ((DefaultHttpClient)client_).getConnectionManager().shutdown();
        }

    }
}
