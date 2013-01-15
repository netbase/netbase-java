/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

public class NetBaseFactory
{
    /**
     * Returns a connection to the NetBase API using the user's credentials
     * (Basic Authentication)
     * 
     * @param user
     * @param password
     * @param production
     *            true: api.netbase.com, false: dev-api.netbase.com
     * 
     * @return
     * @throws Exception
     */
    public static NetBase getInstance (String user,
                                       String password,
                                       boolean production)
        throws Exception
    {
        NetBaseImpl retVal = new NetBaseImpl(user, password, production);
        return retVal;
    }

    /**
     * Returns a connection to the NetBase API using the user's access token
     * (OAuth 2.0)
     * 
     * @param accessToken
     * @param production
     *            true: api.netbase.com, false: dev-api.netbase.com
     * 
     * @return
     * @throws Exception
     */
    public static NetBase getInstance (String accessToken, boolean production)
        throws Exception
    {
        NetBaseImpl retVal = new NetBaseImpl(accessToken, production);
        return retVal;
    }

    /**
     * Returns a connection to the NetBase OAuth 2.0 endpoint using the
     * registered application information.
     * 
     * @param redirectUri
     *            The Callback URL of the registered OAuth 2.0 application
     * @param clientId
     *            The API Key of the OAuth 2.0 application
     * @param clientSecret
     *            The API Secret of the OAuth 2.0 application
     * @param production
     *            true: api.netbase.com, false: dev-api.netbase.com
     * @return
     * @throws Exception
     */
    public static NetbaseOAuth getOAuthInstance (String redirectUri,
                                                 String clientId,
                                                 String clientSecret,
                                                 boolean production)
        throws Exception
    {
        NetbaseOAuthImpl retVal = new NetbaseOAuthImpl(redirectUri,
                                                       clientId,
                                                       clientSecret,
                                                       production);
        return retVal;

    }
}
