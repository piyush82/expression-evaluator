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
import org.json.simple.JSONValue;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.LinkedList;

@Path("/otfly/")
public class OTFlyEval {
    final static Logger logger = Logger.getLogger(OTFlyEval.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response evalOnTheFly(String incomingMsg)
    {
        JSONObject incoming = (JSONObject)JSONValue.parse(incomingMsg);
        JSONObject outgoing = new JSONObject();
        String result = "";
        try
        {
            String expression = (String) incoming.get("exp");
            JSONArray vals = (JSONArray) incoming.get("values");
            Stack<Double> param = new Stack<Double>();
            for(int i=0; i<vals.size(); i++)
            {
                Double val = new Double((String)vals.get(i));
                param.push(val);
            }
            double threshold = Double.parseDouble((String)incoming.get("threshold"));
            result = evaluateExpression(expression, param, threshold);
        }
        catch(Exception ex)
        {
            if(App.showExceptions)
                ex.printStackTrace();
        }
        logger.info("received expression: " + incoming.get("exp"));
        logger.info("expression evaluation result: " + result);
        //construct proper JSON response.
        outgoing.put("info", "t-nova expression evaluation service");
        if(result != null && result.length() != 0) {
            outgoing.put("result", result);
            outgoing.put("status", "ok");
            KPI.expressions_evaluated += 1;
            KPI.api_calls_success += 1;
            return Response.ok(outgoing.toJSONString(), MediaType.APPLICATION_JSON_TYPE).build();
        }
        else
        {
            outgoing.put("status", "execution failed");
            outgoing.put("msg", "malformed request parameters, check your expression or parameter list for correctness.");
            KPI.expressions_evaluated += 1;
            KPI.api_calls_failed += 1;
            return Response.status(Response.Status.BAD_REQUEST).entity(outgoing.toJSONString()).encoding(MediaType.APPLICATION_JSON).build();
        }
    }

    String evaluateExpression(String exp, Stack<Double> param, double threshold)
    {
        String[] literals = exp.split("(?!^)"); //array of characters
        String tempVal = "";
        Stack<String> sTable = new Stack<String>();
        for(int i=0; i<literals.length; i++)
        {
            if(literals[i].equals("("))
            {
                if(tempVal.trim().length() !=0) {
                    sTable.push("fn " + tempVal.trim());
                    logger.info("pushed function [" + tempVal.trim() + "] into stack.");
                }
                else
                {
                    //parsing error this stage is not allowed
                }
                tempVal = "";
            }
            else if(literals[i].equals(","))
            {
                if(tempVal.trim().length() !=0) {
                    sTable.push("pm " + tempVal.trim());
                    logger.info("pushed parameter [" + tempVal.trim() + "] into stack.");
                }
                else
                {
                    //parsing error this stage is not allowed
                }
                tempVal = "";
            }
            else if(literals[i].equals(")"))
            {
                if(tempVal.trim().length() !=0) {
                    sTable.push("pm " + tempVal.trim());
                    logger.info("pushed parameter [" + tempVal.trim() + "] into stack.");
                }

                logger.info("Proceeding for partial stack evaluation.");
                tempVal = "";
                //proceed to partial evaluation
                try {
                    String output = partialEvaluation(sTable, param, threshold);
                    //push the result back into stack as a literal
                    sTable.push("pm " + output);
                    logger.info("pushed parameter [" + output + "] into stack for further processing.");
                }
                catch (EmptyStackException emex)
                {
                    logger.warn("Malformed expression and value set received.");
                    if(App.showExceptions)
                        emex.printStackTrace();
                    return null;
                }
            }
            else
                tempVal += literals[i];
        }
        //if stack has more than 1 element error, else the only element is the result
        String result = "";
        if(sTable.size() != 1)
        {
            //error
            result = null;
        }
        else
        {
            result = sTable.pop().split(" ")[1];
        }
        return result;
    }

    String partialEvaluation(Stack<String> sTable, Stack<Double> param, double threshold) throws EmptyStackException
    {
        //pop elements till we hit an operator
        String token;
        String result = "";
        LinkedList<Double> paramList = new LinkedList<Double>();
        while(!(token = sTable.pop()).startsWith("fn"))
        {
            String fnParam = token.split(" ")[1];
            if(fnParam.startsWith("vnf"))
            {
                paramList.add(param.pop());
            }
            else
            {
                Double tempVal = new Double(fnParam);
                paramList.add(tempVal);
            }
            logger.info("Popped a parameter: " + fnParam);
        }
        String operation = token.split(" ")[1];
        logger.info("Function to apply on the popped parameters: " + operation);
        if(operation.equalsIgnoreCase("min"))
        {
            result = findMin(paramList);
        }
        else if(operation.equalsIgnoreCase("add"))
        {
            result = findSum(paramList);
        }
        else if(operation.equalsIgnoreCase("gt"))
        {
            result = evalGT(paramList, threshold);
        }
        else if(operation.equalsIgnoreCase("lt"))
        {
            result = evalLT(paramList, threshold);
        }
        else if(operation.equalsIgnoreCase("avg"))
        {
            result = findAvg(paramList);
        }
        else if(operation.equalsIgnoreCase("max"))
        {
            result = findMax(paramList);
        }
        return result;
    }

    String findMin(LinkedList<Double> paramList)
    {
        Double min = paramList.peekFirst();
        for(int i=0; i< paramList.size(); i++)
        {
            if(min > paramList.get(i)) min = paramList.get(i);
        }
        return min.toString();
    }

    String findMax(LinkedList<Double> paramList)
    {
        Double max = paramList.peekFirst();
        for(int i=0; i< paramList.size(); i++)
        {
            if(max < paramList.get(i)) max = paramList.get(i);
        }
        return max.toString();
    }

    String findSum(LinkedList<Double> paramList)
    {
        Double sum = new Double("0.0");
        for(int i=0; i< paramList.size(); i++)
        {
            sum += paramList.get(i);
        }
        return sum.toString();
    }

    String findAvg(LinkedList<Double> paramList)
    {
        Double sum = new Double("0.0");
        for(int i=0; i< paramList.size(); i++)
        {
            sum += paramList.get(i);
        }
        Double avg = sum / (new Double(paramList.size()));
        return avg.toString();
    }

    String evalGT(LinkedList<Double> paramList, double threshold)
    {
        String result = "false";
        if(paramList.get(0).doubleValue() > threshold) result = "true";
        return result;
    }

    String evalLT(LinkedList<Double> paramList, double threshold)
    {
        String result = "false";
        if(paramList.get(0).doubleValue() < threshold) result = "true";
        return result;
    }
}
