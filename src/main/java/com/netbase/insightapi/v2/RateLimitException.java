/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

@SuppressWarnings("serial")
public class RateLimitException extends RuntimeException
{
    public RateLimitException (String message)
    {
        super(message);
    }

}
