
package com.netbase.insightapi.v2;

import net.sf.json.JSONObject;

public class ErrorResponseParser
{

    public ErrorStatus parse (JSONObject entity)
    {
        if (null != entity && entity.containsKey("error")) {
            int errorCode = -1;
            String errorType = null;
            String errorMessage = entity.getString("error");
            if (entity.containsKey("status")) {
                errorCode = entity.getInt("status");
            }
            if (entity.containsKey("error_description")) {
                errorType = errorMessage;
                errorMessage = entity.getString("error_description");
            }

            if (null != errorMessage) {
                return new ErrorStatusImpl(errorCode, errorType, errorMessage);
            }
        }
        return null;
    }
}
