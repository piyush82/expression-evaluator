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

import java.util.HashMap;

public class Init {
    final static Logger logger = Logger.getLogger(Init.class);
    //global init variables
    public static HashMap<String, String> tableScripts = new HashMap<String, String>();
    public static String tableList[] = {"template", "expression", "oplist", "vnfinstance"};
    public static String opList[] = {"min", "max", "gt", "lt", "ge", "le", "add", "avg", "mul"};

    public Init()
    {
        if(App.dbEngine.equalsIgnoreCase("sqlite") || App.dbEngine.equalsIgnoreCase("sqlite3")) {
            tableScripts.put("template", "create table template (id INTEGER PRIMARY KEY AUTOINCREMENT, nsdid VARCHAR(128), expression VARCHAR(512))");
            tableScripts.put("expression", "create table expression (id INTEGER PRIMARY KEY AUTOINCREMENT, templateid INT, nsinstid VARCHAR(128), outeropid INT, inneropid INT, constant INT, innervalresult DOUBLE, outervalresult VARCHAR(32), timestamp VARCHAR(45))");
            tableScripts.put("oplist", "create table oplist (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32), supported INT)");
            tableScripts.put("vnfinstance", "create table vnfinstance (id INTEGER PRIMARY KEY AUTOINCREMENT, monparam VARCHAR(32), value DOUBLE, timestamp VARCHAR(32), expressionid INT)");
        }
        logger.info("Support variables have been initialized.");
    }
}
