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


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("/")
public class Base {
    final static Logger logger = Logger.getLogger(Base.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String homePage() {
        KPI.api_calls += 1;
        JSONObject obj = new JSONObject();
        obj.put("src","t-nova expression evaluation service");
        obj.put("msg", "api summary list");
        JSONArray apiList = new JSONArray();
        JSONObject api = new JSONObject();
        api.put("uri", "/");
        api.put("method", "GET");
        api.put("purpose", "capability discovery");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/template/");
        api.put("method", "GET");
        api.put("purpose", "list of registered expression templates");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/template/");
        api.put("method", "POST");
        api.put("purpose", "registration of a new expression template");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/template/{id}");
        api.put("method", "GET");
        api.put("purpose", "get specific expression template details");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/expression/{id}");
        api.put("method", "DELETE");
        api.put("purpose", "delete specific expression template, all expression instantiation will be deleted");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/expression/");
        api.put("method", "GET");
        api.put("purpose", "list of registered expressions");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/expression/");
        api.put("method", "POST");
        api.put("purpose", "register a new expression");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/expression/{id}");
        api.put("method", "GET");
        api.put("purpose", "get specific expression details");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/expression/{id}");
        api.put("method", "PUT");
        api.put("purpose", "update a specific expression");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/expression/{id}");
        api.put("method", "POST");
        api.put("purpose", "execute a specific expression");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/expression/{id}");
        api.put("method", "DELETE");
        api.put("purpose", "delete a specific expression");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/kpi");
        api.put("method", "GET");
        api.put("purpose", "get the service key runtime metrics");
        apiList.add(api);

        api = new JSONObject();
        api.put("uri", "/otfly/");
        api.put("method", "POST");
        api.put("purpose", "on the fly stateless expression evaluation");
        api.put("op-supported", "lt, gt, add, max, min, avg");
        apiList.add(api);

        obj.put("api", apiList);

        logger.info("URI:/ Method:GET Request procesed.");
        KPI.api_calls_success += 1;
        return obj.toJSONString();
    }
}
