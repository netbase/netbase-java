/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

public class ErrorStatusImpl implements ErrorStatus
{
    private int code_;
    private String type_;
    private String message_;

    public ErrorStatusImpl (int code, String type, String message)
    {
        code_ = code;
        type_ = type;
        message_ = message;
    }

    public int errorCode ()
    {
        return code_;
    }

    public String errorType ()
    {
        return type_;
    }

    public String errorMessage ()
    {
        return message_;
    }
}
