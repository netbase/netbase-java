/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

public class TokenResponse
{
    protected String accessToken;
    protected String tokenType;
    protected String refreshToken;
    protected long expiresIn;

    public String getAccessToken ()
    {
        return accessToken;
    }

    public void setAccessToken (String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getTokenType ()
    {
        return tokenType;
    }

    public void setTokenType (String tokenType)
    {
        this.tokenType = tokenType;
    }

    public String getRefreshToken ()
    {
        return refreshToken;
    }

    public void setRefreshToken (String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    public long getExpiresIn ()
    {
        return expiresIn;
    }

    public void setExpiresIn (long expiresIn)
    {
        this.expiresIn = expiresIn;
    }
}
