/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * Represents the NetBase Query Request comprising NameValuePairs.
 * 
 * @author Maneesh Sahu
 * 
 */
public class Request
{
    protected List<NameValuePair> queryParameters;

    public Request ()
    {
        queryParameters = new ArrayList<NameValuePair>();
    }

    public Request (List<NameValuePair> queryParams)
    {
        this.queryParameters = queryParams;
    }

    public Request (String queryString)
    {
        this.queryParameters = URLEncodedUtils.parse(queryString,
                                                     Charset.forName("UTF-8"));
    }

    public void addQueryParam (NameValuePair queryParam)
    {
        _getNameValuePairs().add(queryParam);
    }

    public void addQueryParam (String name, String value)
    {
        _getNameValuePairs().add(new BasicNameValuePair(name, value));
    }

    @Override
    public String toString ()
    {
        if (queryParameters.size() == 0) {
            return "";
        }

        return "?" + URLEncodedUtils.format(queryParameters, "UTF-8");
    }

    protected List<NameValuePair> _getNameValuePairs ()
    {
        if (queryParameters == null) {
            queryParameters = new ArrayList<NameValuePair>();
        }
        return queryParameters;
    }

}
