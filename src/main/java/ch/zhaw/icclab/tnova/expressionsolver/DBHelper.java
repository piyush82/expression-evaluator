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
import java.sql.*;
import java.util.ArrayList;

public class DBHelper {
    final static Logger logger = Logger.getLogger(DBHelper.class);

    static boolean dbCheck;

    //initializing the driver for sqlite
    Connection connection;

    public DBHelper()
    {
        try
        {
            dbCheck = true;
            if(App.dbEngine.equalsIgnoreCase("sqlite") || App.dbEngine.equalsIgnoreCase("sqlite3"))
                connection = DriverManager.getConnection("jdbc:sqlite:" + App.dbFilePath);
            dbCheck = validate(connection);
            connection.close();
        }
        catch (SQLException e) {
            connection = null;
            if(App.showExceptions)
                e.printStackTrace();
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                logger.warn(e.getMessage());
            }
        }
    }

    Connection getDBconnection() throws SQLException {
        if(App.dbEngine.equalsIgnoreCase("sqlite") || App.dbEngine.equalsIgnoreCase("sqlite3"))
            return DriverManager.getConnection("jdbc:sqlite:" + App.dbFilePath);
        else
            return null; //not yet supported
    }

    boolean validate(Connection con)
    {
        try
        {
            DatabaseMetaData metaData = con.getMetaData();
            String tableType[] = {"TABLE"};
            StringBuilder builder = new StringBuilder();
            ResultSet result = metaData.getTables(null,null,null,tableType);
            ArrayList<String> discoveredTableList = new ArrayList<String>();
            logger.info("Performing DB Sanity Checks now.");

            while (result.next())
            {
                discoveredTableList.add(result.getString("TABLE_NAME"));
            }
            result.close();

            logger.info("Number of tables located: " + discoveredTableList.size());
            /*for(int i=0; i<discoveredTableList.size(); i++)
            {
                logger.info("Found table: " + discoveredTableList.get(i));
            }*/
            for(int i=0; i < Init.tableList.length; i++)
            {
                String candidate = Init.tableList[i];
                if(!discoveredTableList.contains(candidate))
                {
                    logger.warn("The following table: {" + candidate + "} was not found in the existing database!");
                    dbCheck = false;
                }
            }

            if(!dbCheck)
            {
                logger.info("It is recommended to reinitialize the database before proceeding.");
                logger.info("I Will proceed to reinitialize the DB. Existing table contents will be lost.");
                return initialize(con);
            }
        }
        catch(Exception ex)
        {
            if(App.showExceptions)
                ex.printStackTrace();
            return false;
        }
        return true;
    }

    boolean initialize(Connection con)
    {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            for (int i=0; i<Init.tableList.length; i++)
            {
                statement.executeUpdate("drop table if exists " + Init.tableList[i]);
                statement.executeUpdate(Init.tableScripts.get(Init.tableList[i]));
                logger.info("(Re)Created table: " + Init.tableList[i]);
            }
            //now initializing the ops table
            for (int i=0; i<Init.opList.length; i++)
                statement.executeUpdate("insert into oplist values(NULL, '" + Init.opList[i] + "', 1)");
            logger.info("Database (re)initialized successfully!");
        }
        catch(Exception ex)
        {
            if(App.showExceptions)
                ex.printStackTrace();
            logger.warn("Exception caught while initializing database.");
            return false;
        }
        return true;
    }

}
