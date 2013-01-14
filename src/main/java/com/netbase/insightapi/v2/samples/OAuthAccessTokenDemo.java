/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.netbase.insightapi.v2.ApiErrorException;
import com.netbase.insightapi.v2.NetBaseFactory;
import com.netbase.insightapi.v2.NetbaseOAuth;
import com.netbase.insightapi.v2.ParamErrorException;
import com.netbase.insightapi.v2.TokenResponse;

public class OAuthAccessTokenDemo
{
    /**
     * @param args
     */
    public static void main (String[] args)
        throws Exception
    {
        /*
         * Please fill in the refirect_uri, client_id, client_secret based on
         * your registered application
         */
        String redirectUri = "https://dev-api.netbase.com/explorer/authSuccess/netbase-dev-oauth";
        String clientId = "285e45119745327d3f6d8b251eef7f95";
        String clientSecret = "2d482c9f05c4352e3a3529f3071272ddae0a1dbf08cd5179";

        NetbaseOAuth oauth = null;

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String code = readInput(input, "Please provide the authorization code.");

        try {
            oauth = NetBaseFactory.getOAuthInstance(redirectUri,
                                                    clientId,
                                                    clientSecret,
                                                    false);

            String refreshToken = null;
            String accessToken = null;

            /*
             * Demo: how to get your access token and refresh token by
             * authorization code
             */
            try {
                TokenResponse tokenResponse = oauth.getAccessTokenByAuthCode(code);
                refreshToken = tokenResponse.getRefreshToken();
                accessToken = tokenResponse.getAccessToken();
            }
            catch (ApiErrorException e) {
                printError(e);
                return;
            }
            catch (ParamErrorException e) {
                printError(e);
                return;
            }

            if (null != accessToken) {
                System.out.println("Now you got a new aceess token: "
                        + accessToken);
            }
            if (null != refreshToken) {
                System.out.println("You can use this token to refesh your access token: "
                        + refreshToken);
            }
            if (null == refreshToken) {
                return;
            }

            /*
             * Demo: how to refresh your access token
             */
            try {
                TokenResponse tokenResponse = oauth.getRefreshedToken(refreshToken);
                accessToken = tokenResponse.getAccessToken();
                System.out.println("Your access token has been refreshed: "
                        + accessToken);
            }
            catch (ApiErrorException e) {
                printError(e);
                return;
            }
            catch (ParamErrorException e) {
                printError(e);
                return;
            }
        }
        finally {
            if (null != oauth) {
                oauth.close();
            }
            if (null != input) {
                input.close();
            }
            byebye();
        }
    }

    static String readInput (BufferedReader input, String message)
    {
        System.out.println(message);
        try {
            return input.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void byebye ()
    {
        System.out.println("bye-bye!");
    }

    static void printError (ApiErrorException e)
    {
        if (null != e) {
            System.out.println("Error " + e.getErrorCode() + ": "
                    + e.getMessage());
        }
    }

    static void printError (ParamErrorException e)
    {
        if (null != e) {
            System.out.println("Error " + e.getErrorType() + ": "
                    + e.getMessage());
        }
    }
}
