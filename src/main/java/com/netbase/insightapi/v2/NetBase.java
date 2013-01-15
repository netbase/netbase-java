/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

public interface NetBase
{
    /**
     * Overrides the credentials in the NetBase connection with the provided
     * username/password (Basic Auth)
     * 
     * @param user
     * @param password
     * @throws Exception
     */
    void setCredentials (String user, String password)
        throws Exception;

    /**
     * Overrides the credentials in the NetBase connection with the provided
     * accessToken (OAuth 2.0)
     * 
     * @param accessToken
     * @throws Exception
     */
    void setCredentials (String accessToken)
        throws Exception;

    /**
     * Executes the profile service
     * 
     * @param req
     * @return
     * @throws RateLimitException
     */
    Response profile (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException;

    /**
     * Executes a simple helloWorld service (useful to test the connection)
     * 
     * @param req
     * @return
     * @throws RateLimitException
     */
    Response helloWorld (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException;

    /**
     * Executes a simple topics service
     * 
     * @param req
     * @return
     * @throws RateLimitException
     */
    Response topics (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException;

    /**
     * Executes a simple insight count service
     * 
     * @param req
     * @return
     * @throws RateLimitException
     */
    Response insightCount (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException;

    /**
     * Executes the metricValues service
     * 
     * @param req
     * @return
     * @throws RateLimitException
     */
    Response metricValues (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException;

    /**
     * Executes the retrieveSentences service
     * 
     * @param req
     * @return
     * @throws RateLimitException
     */
    Response retrieveSentences (Request req)
        throws RateLimitException,
            ApiErrorException,
            ParamErrorException;

    /**
     * Close any persistent HTTP connection to the server.
     * 
     * @throws Exception
     */
    void close ()
        throws Exception;
}
