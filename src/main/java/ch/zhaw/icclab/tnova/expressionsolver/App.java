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


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import org.apache.log4j.Logger;


public class App {
    public static String BASE_URI = "http://0.0.0.0:8080/exp-eval/";
    final static Logger logger = Logger.getLogger(App.class);
    public static String dbFilePath;
    public static boolean showExceptions;
    public static String dbEngine;
    public static String serverPort;

    public static HttpServer startServer() {

        final ResourceConfig rc = new ResourceConfig().packages("ch.zhaw.icclab.tnova.expressionsolver");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        String resourceName = "expressionsolver.conf";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        Init supportVals;
        DBHelper dbHelper;

        try(InputStream resourceStream = loader.getResourceAsStream(resourceName))
        {
            props.load(resourceStream);
            logger.debug("Configuration file loaded properly.");
            dbFilePath = props.getProperty("dbfile");
            showExceptions = (props.getProperty("showexceptions").equalsIgnoreCase("true")) ? true : false;
            dbEngine = props.getProperty("dbengine");
            serverPort = props.getProperty("port");
            BASE_URI = "http://0.0.0.0:" + serverPort + "/exp-eval/";

            if(dbEngine.equalsIgnoreCase("sqlite") || dbEngine.equalsIgnoreCase("sqlite3")) {
                //loading the sqlite-JDBC driver using the current class loader
                Class.forName("org.sqlite.JDBC");
            }
            supportVals = new Init();
            dbHelper = new DBHelper();
        }
        catch(Exception ex)
        {
            dbFilePath = null;
            logger.error("Error loading configuration file: " + ex.getMessage());
        }
        final HttpServer server = startServer();

        logger.info(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        KPI.initialize();
        logger.info("System KPI parameters have been initialized.");
        System.in.read();
        server.stop();
        logger.info("Jersey app stopped.");
    }
}
