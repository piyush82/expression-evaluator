package ch.zhaw.icclab.tnova.expressionsolver;

/*
 * Copyright (c) 2015. Zuercher Hochschule fuer Angewandte Wissenschaften
 *  All Rights Reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may obtain
 *     a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *     WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *     License for the specific language governing permissions and limitations
 *     under the License.
 */

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Path("/kpi")
public class KPI {
    final static Logger logger = Logger.getLogger(KPI.class);

    //global KPI variables
    public static int api_calls;
    public static int api_calls_success;
    public static int api_calls_failed;
    public static int expressions_evaluated;
    public static int expressions_under_evaluation;
    public static long lastknownunixtime;

    public static void initialize()
    {
        api_calls = 0;
        api_calls_success = 0;
        api_calls_failed = 0;
        expressions_evaluated = 0;
        expressions_under_evaluation = 0;
        lastknownunixtime = System.currentTimeMillis();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String showKPI() {
        KPI.api_calls += 1; //tracking the current call
        JSONObject obj = new JSONObject();
        obj.put("src","t-nova expression evaluation service");
        obj.put("msg", "kpi parameters data");

        obj.put("api-calls-total", api_calls);
        obj.put("api-calls-failed", api_calls_failed);
        obj.put("expressions-evaluated", expressions_evaluated);
        obj.put("expressions-under-evaluation", expressions_under_evaluation);
        Date date = new Date(lastknownunixtime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String formattedDate = sdf.format(date);
        obj.put("data-since", formattedDate);

        KPI.api_calls_success += 1;
        obj.put("api-calls-success", api_calls_success);
        //lastknownunixtime = System.currentTimeMillis();
        initialize();
        logger.info("URI:/kpi Method:GET Request procesed. The KPI parameters have been reset.");
        return obj.toJSONString();
    }
}
