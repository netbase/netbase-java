/**
 * Copyright (C) 2012 NetBase Solutions, Inc.
 * 2087 Landings Drive, Mountain View, CA 94043.
 * All rights reserved.
 *
 * Created on Nov 30, 2012
 */

package com.netbase.insightapi.v2;

public interface RateLimitStatus
{
    String RATE_LIMIT_MAX = "X-RateLimit-Max";
    String RATE_LIMIT_REMAIN = "X-RateLimit-Remaining";
    String RATE_LIMIT_RESET = "X-RateLimit-Reset";

    int getRemainingHits ();

    int getHourlyLimits ();

    int getResetTimeInSeconds ();

}
