/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

@SuppressWarnings("serial")
public class ApiErrorException extends Exception
{
    private int errorCode_;

    public ApiErrorException (int errorCode, String message)
    {
        super(message);
        errorCode_ = errorCode;
    }

    public int getErrorCode ()
    {
        return errorCode_;
    }
}
