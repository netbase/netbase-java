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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.netbase.insightapi.v2.ApiErrorException;
import com.netbase.insightapi.v2.ErrorStatus;
import com.netbase.insightapi.v2.NetBase;
import com.netbase.insightapi.v2.NetBaseFactory;
import com.netbase.insightapi.v2.ParamErrorException;
import com.netbase.insightapi.v2.Request;
import com.netbase.insightapi.v2.Response;

public class TopicAnalysisDemo
{
    /**
     * @param args
     */
    public static void main (String[] args)
        throws Exception
    {
        // Please fill in your id/pwd here
        String account = "";
        String pwd = "";
        NetBase nb = NetBaseFactory.getInstance(account, pwd, false);

        // Or you can provode a valid access token

        // String accessToken = "";
        // NetBase nb = NetBaseFactory.getInstance(accessToken,
        // false);

        Set<String> topicNames = new HashSet<String>();
        {
            Request req = new Request();
            req.addQueryParam("scope", "USER");
            try {
                Response res = nb.topics(req);
                if (!res.getJSON().isEmpty() && res.getJSON().isArray()) {
                    JSONArray topics = JSONArray.fromObject(res.getJSON());
                    for (int index = 0; index < topics.size(); index++) {
                        JSONObject item = JSONObject.fromObject(topics.get(index));
                        topicNames.add(item.getString("name"));
                    }
                    StringBuilder topicIndicator = new StringBuilder();
                    topicIndicator.append("Please select one topic from below:\n[`")
                                  .append(StringUtils.join(topicNames, "`,`"))
                                  .append("`]");
                    System.out.println(topicIndicator);
                }
                else {
                    System.out.println("No topics under this account.");
                    byebye();
                    return;
                }

            }
            catch (ParamErrorException e) {
                printError(e);
                return;
            }
            catch (ApiErrorException e) {
                printError(e);
                return;
            }
        }

        Calendar oneMonthAgo = Calendar.getInstance();
        oneMonthAgo.add(Calendar.DAY_OF_MONTH, -30);
        String startDate = DateFormatUtils.format(oneMonthAgo, "yyyy-MM-dd");

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String topicName = null;

        while (true) {
            topicName = readInput(input,
                                  "Please select a topic for furthur queries.");
            if (null == topicName) {
                byebye();
                return;
            }
            if (!topicNames.contains(topicName)) {
                System.out.println("Topic `" + topicName + "` doesn't exist.");
            }
            else {
                break;
            }
        }

        System.out.println("For Topic `" + topicName + "`");
        /*
         * Show metrics NetSetiment and Buzz
         */
        {
            Request req = new Request();
            req.addQueryParam("topics", topicName);
            req.addQueryParam("metricSeries", "TotalBuzz");
            req.addQueryParam("metricSeries", "NetSentiment");
            req.addQueryParam("publicshedDate", startDate);
            try {
                Response res = nb.metricValues(req);
                Map<String, Double> metrics = getMetrics(res.getJSON());
                if (metrics.containsKey("TotalBuzz")) {
                    System.out.println("\tTotalBuzz: "
                            + metrics.get("TotalBuzz"));
                }
                if (metrics.containsKey("NetSentiment")) {
                    System.out.println("\tNetSentiment: "
                            + metrics.get("NetSentiment"));
                }
            }
            catch (ParamErrorException e) {
                printError(e);
                return;
            }
            catch (ApiErrorException e) {
                printError(e);
                return;
            }
        }

        /*
         * Show top 5 Likes and Dislikes
         */
        {
            Request req = new Request();
            int topN = 5;
            req.addQueryParam("topics", topicName);
            req.addQueryParam("sizeNeeded", String.valueOf(topN));
            req.addQueryParam("categories", "Likes");
            req.addQueryParam("categories", "Dislikes");
            req.addQueryParam("publicshedDate", startDate);
            try {
                Response res = nb.insightCount(req);
                Map<String, List<String>> insights = getInsights(res.getJSON());
                if (insights.containsKey("Likes")) {
                    System.out.println("\tTop " + topN + " likes: "
                            + StringUtils.join(insights.get("Likes"), ", "));
                }
                if (insights.containsKey("Dislikes")) {
                    System.out.println("\tTop " + topN + " dislikes: "
                            + StringUtils.join(insights.get("Dislikes"), ", "));
                }
            }
            catch (ParamErrorException e) {
                printError(e);
                return;
            }
            catch (ApiErrorException e) {
                printError(e);
                return;
            }
        }
        /*
         * Show top 5 highest sentiment soundbites
         */
        {
            Request req = new Request();
            int topN = 5;
            req.addQueryParam("topics", topicName);
            req.addQueryParam("sizeNeeded", String.valueOf(topN));
            req.addQueryParam("sort",
                              "confidenceScore -richness -daynumber -secondsIn3");
            req.addQueryParam("precision", "HIGH");
            req.addQueryParam("publicshedDate", startDate);
            try {
                Response res = nb.retrieveSentences(req);
                System.out.println("----------------------------");
                System.out.println("Top " + topN
                        + " highest precision sentiment soundbites.");
                showSentences(res.getJSON());
                System.out.println("----------------------------");
            }
            catch (ParamErrorException e) {
                printError(e);
                return;
            }
            catch (ApiErrorException e) {
                printError(e);
                return;
            }
        }
    }

    static void showSentences (JSON json)
    {
        if (null != json && !json.isEmpty()) {
            JSONArray sentences = JSONObject.fromObject(json)
                                            .getJSONArray("sentences");
            JSONArray documents = JSONObject.fromObject(json)
                                            .getJSONArray("documents");
            if (null != sentences && !sentences.isEmpty()) {
                for (int index = 0; index < sentences.size(); index++) {
                    JSONObject sentence = sentences.getJSONObject(index);
                    JSONObject document = documents.getJSONObject(sentence.getInt("documentIdx"));
                    JSONObject properties = sentence.getJSONObject("properties");
                    JSONObject dProperties = document.getJSONObject("properties");
                    System.out.println(properties.getString("text"));
                    System.out.println("\tSentiment: "
                            + sentence.getString("sentiment"));
                    if (dProperties.containsKey("authorName")) {
                        System.out.println("\tAuthor: "
                                + dProperties.getString("authorName"));
                    }
                    else if (dProperties.containsKey("author")) {
                        System.out.println("\tAuthor: "
                                + dProperties.getString("author"));
                    }
                    if (dProperties.containsKey("url")) {
                        System.out.println("\tURL: "
                                + dProperties.getString("url"));
                    }
                }
            }
        }
    }

    static Map<String, Double> getMetrics (JSON json)
    {
        Map<String, Double> retVal = new HashMap<String, Double>();
        if (null != json && !json.isEmpty()) {
            JSONArray metrics = JSONObject.fromObject(json)
                                          .getJSONArray("metrics");
            if (null != metrics && !metrics.isEmpty()) {
                for (int index = 0; index < metrics.size(); index++) {
                    JSONArray dataset = metrics.getJSONObject(index)
                                               .getJSONArray("dataset");
                    if (null == dataset || dataset.isEmpty()) {
                        continue;
                    }
                    for (int dataIdx = 0; dataIdx < dataset.size(); dataIdx++) {
                        String seriesName = dataset.getJSONObject(dataIdx)
                                                   .getString("seriesName");
                        JSONArray set = dataset.getJSONObject(dataIdx)
                                               .getJSONArray("set");
                        if (null == set || set.size() == 0) {
                            retVal.put(seriesName, 0.0);
                        }
                        else {
                            retVal.put(seriesName, set.getDouble(0));
                        }
                    }
                }
            }
        }
        return retVal;
    }

    static Map<String, List<String>> getInsights (JSON json)
    {
        Map<String, List<String>> retVal = new HashMap<String, List<String>>();
        if (null != json && !json.isEmpty()) {
            JSONArray insights = JSONObject.fromObject(json)
                                           .getJSONArray("insights");
            if (null != insights && !insights.isEmpty()) {
                for (int index = 0; index < insights.size(); index++) {
                    JSONArray dataset = insights.getJSONObject(index)
                                                .getJSONArray("dataset");
                    if (null == dataset || dataset.isEmpty()) {
                        continue;
                    }
                    for (int dataIdx = 0; dataIdx < dataset.size(); dataIdx++) {
                        String insightType = dataset.getJSONObject(dataIdx)
                                                    .getString("insightType");
                        JSONArray set = dataset.getJSONObject(dataIdx)
                                               .getJSONArray("set");
                        if (!retVal.containsKey(insightType)) {
                            retVal.put(insightType, new ArrayList<String>());
                        }
                        for (int itemIdex = 0; itemIdex < set.size(); itemIdex++) {
                            retVal.get(insightType)
                                  .add(set.getJSONObject(itemIdex)
                                          .getString("name"));
                        }
                    }
                }
            }
        }
        return retVal;
    }

    static void printError (ParamErrorException e)
    {
        if (null != e) {
            System.out.println("Error " + e.getErrorType() + ": "
                    + e.getMessage());
        }
    }

    static void printError (ApiErrorException e)
    {
        if (null != e) {
            System.out.println("Error " + e.getErrorCode() + ": "
                    + e.getMessage());
        }
    }

    static void printError (ErrorStatus errorStatus)
    {
        if (null != errorStatus) {
            System.out.println("Error " + errorStatus.errorCode() + ": "
                    + errorStatus.errorMessage());
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
}
