/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;

public class RateLimitStatusImpl implements RateLimitStatus
{
    public static Log LOG = LogFactory.getLog(RateLimitStatusImpl.class);

    protected int remainingHits = -1;

    protected int hourlyLimits = -1;

    protected int resetTimeInSeconds = -1;

    public RateLimitStatusImpl (Header[] headers)
    {
        for (Header header : headers) {
            String name = header.getName();
            String value = header.getValue();

            if (name.equals(RATE_LIMIT_MAX)) {
                hourlyLimits = Integer.parseInt(value);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Hourly Limit: " + hourlyLimits);
                }
            }
            else if (name.equals(RATE_LIMIT_REMAIN)) {
                remainingHits = Integer.parseInt(value);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Remaining Hits: " + remainingHits);
                }
            }
            else if (name.equals(RATE_LIMIT_RESET)) {
                resetTimeInSeconds = (int)Long.parseLong(value) / 1000;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Reset Time (seconds): " + resetTimeInSeconds);
                }
            }
        }
    }

    public int getRemainingHits ()
    {
        return remainingHits;
    }

    public int getHourlyLimits ()
    {
        return hourlyLimits;
    }

    public int getResetTimeInSeconds ()
    {
        return resetTimeInSeconds;
    }

}
