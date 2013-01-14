/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

import java.io.IOException;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

public class Response
{
    public static Log LOG = LogFactory.getLog(Response.class);

    protected HttpResponse response;
    protected String text;
    protected JSON json_;
    protected RateLimitStatus rateLimitStatus;
    protected ErrorStatus errorStatus_;

    Response (HttpResponse response)
    {
        this.response = response;

        if (getRateLimitStatus().getRemainingHits() == 0) {
            throw new RateLimitException("RateLimit Exceeded: max hourly calls: "
                    + getRateLimitStatus().getHourlyLimits()
                    + ", Reset time: "
                    + getRateLimitStatus().getResetTimeInSeconds());
        }
        if (!getJSON().isEmpty() && !getJSON().isArray()) {
            JSONObject entity = JSONObject.fromObject(getJSON());
            errorStatus_ = new ErrorResponseParser().parse(entity);
        }
    }

    public StatusLine getStatusLine ()
    {
        return response.getStatusLine();
    }

    public RateLimitStatus getRateLimitStatus ()
    {
        if (rateLimitStatus == null) {
            rateLimitStatus = new RateLimitStatusImpl(response.getAllHeaders());
        }
        return rateLimitStatus;
    }

    public boolean hasError ()
    {
        return null == errorStatus_;
    }

    public ErrorStatus getErrorStatus ()
    {
        return errorStatus_;
    }

    @Override
    public String toString ()
    {
        if (text == null) {
            try {
                text = EntityUtils.toString(response.getEntity());
            }
            catch (ParseException pe) {
                LOG.error("Fatal error when parsing http response, usually caused by internet issues or internal errors. Please check support.\n",
                          pe);
                text = "";
            }
            catch (IOException ie) {
                LOG.error("Fatal error with http response, usually caused by internet issues or internal errors. Please check support.\n",
                          ie);
                text = "";
            }
        }
        return text;
    }

    public JSON getJSON ()
    {
        if (null == json_) {
            try {
                json_ = JSONSerializer.toJSON(this.toString());
            }
            catch (JSONException e) {
                LOG.error("Http response isn't in json format, usually caused by internet issues or internal errors. Please check support.\n"
                        + this.toString());
                throw e;
            }
        }
        return json_;
    }
}
