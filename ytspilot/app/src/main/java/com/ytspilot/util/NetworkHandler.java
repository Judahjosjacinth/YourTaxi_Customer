package com.ytspilot.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class NetworkHandler {
    // Enumerations for setting http url method
    public static enum HTTP_METHOD {
        GET, POST, PUT, SET
    }

    ;

    private static String TAG = "NetworkHandler";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }

    }


//    public static String getStringFromURL(String url, ContentValues arguments,
//                                          HTTP_METHOD method) {
//        URL requestURL;
//        HttpURLConnection httpURLConnection;
//        BufferedReader bufferedReader = null;
//        InputStreamReader inputStreamReader = null;
//        StringBuilder contentStringBuilder = null;
//        try {
//            requestURL = new URL(url);
//            // set request method from enumerations
//            httpURLConnection = (HttpURLConnection) requestURL.openConnection();
//            httpURLConnection.setRequestMethod(method.name());
//
//            // Set request parameters
//            if (arguments != null) {
//                OutputStreamWriter writer = new OutputStreamWriter(
//                        httpURLConnection.getOutputStream());
//                writer.write(encodeArguments(arguments));
//                writer.close();
//            }
//            inputStreamReader = new InputStreamReader(
//                    httpURLConnection.getInputStream());
//            bufferedReader = new BufferedReader(inputStreamReader);
//            contentStringBuilder = new StringBuilder();
//            String readLine;
//            while ((readLine = bufferedReader.readLine()) != null) {
//                contentStringBuilder.append(readLine);
//            }
//
//        } catch (MalformedURLException urlException) {
//            Log.d(TAG, "Is the request url is valid?");
//            urlException.printStackTrace();
//        } catch (IOException ioException) {
//            Log.d(TAG, "Cannot connect to server");
//            ioException.printStackTrace();
//        } finally {
//            if (bufferedReader != null && inputStreamReader != null)
//                try {
//                    bufferedReader.close();
//                    inputStreamReader.close();
//                } catch (IOException e) {
//                    Log.d(TAG, "Error closing streams");
//                    e.printStackTrace();
//                }
//        }
//
//        return contentStringBuilder.toString();
//
//    }


    public static String getStringFromURL(String url, ContentValues arguments,HTTP_METHOD method) {
        StringBuilder contentStringBuilder = null;


        BufferedReader reader=null;
//        HttpURLConnection connection;
//       // BufferedReader reader;
//
//        try {
//            URL url = new URL(url1);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.connect();
//            InputStream stream = connection.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(stream));
//            StringBuffer buffer = new StringBuffer();
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                buffer.append( line+"\n");
//            }
//            return buffer.toString();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


//        try
//        {
//            String  data = encodeArguments(arguments);
//            URL url2 = new URL(url);
//            URLConnection conn = url2.openConnection();
//       //     conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setDoOutput(true);
//         //   HttpURLConnection.setFollowRedirects(true);
//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write(data);
//            wr.flush();
//             reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while((line = reader.readLine()) != null)
//            {
//                sb.append(line + "\n");
//            }
//             return sb.toString();
//        }
//        catch(Exception e)  {
//            e.printStackTrace();
//        }
//        finally
//        {
//            try
//            {
//                reader.close();
//            }
//            catch(Exception e) { e.printStackTrace();}
//        }
//        return null;
        //  URL url = new URL("https://www.yourtaxistand.com/agentapp/agent_login");


        try {
            URL requestURL;
           // URL requestURL = new URL("https://www.yourtaxistand.com/myapps/validate_driver?driver_id=D000002&password=9884406994");
            if(arguments!=null){
                 requestURL = new URL(url+"?"+encodeArguments(arguments));
            }
            else{
                 requestURL = new URL(url);
            }

          //  URL requestURL = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
            //HttpURLConnection.setFollowRedirects(true);

            urlConnection.setConnectTimeout(3000);
//            specify the http method

            if (arguments != null)
            {
               // String args = encodeArguments(arguments);
                urlConnection.setRequestMethod(method.name());
//                OutputStream connectionOutputStream = urlConnection.getOutputStream();
//                BufferedWriter connectionWriter = new BufferedWriter(new OutputStreamWriter(connectionOutputStream, "UTF-8"));
//                connectionWriter.write(encodeArguments(arguments));
//                connectionWriter.flush();
//                connectionWriter.close();
//                connectionOutputStream.close();
            }
//            else if (requestMethod.equals(METHOD.GET)) {
//                urlConnection.setRequestMethod("GET");
//            }

            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                BufferedReader bufferedReader = new
                        BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                contentStringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    contentStringBuilder.append(line);
                }
            } else {
                Log.d(TAG, "Invalid Response :" + String.valueOf(responseCode));
            }
            if(contentStringBuilder!=null)
                return contentStringBuilder.toString();
            return "";
        } catch (MalformedURLException exception) {
            Log.d(TAG, "Invalid request URL");
            exception.printStackTrace();
        } catch (IOException iOException) {
            Log.d(TAG, "Cannot open connection");
            iOException.printStackTrace();
        }
        return null;
    }

    private static String encodeArguments(ContentValues argumentValues) {
        boolean isFirstArgument = true;
        StringBuilder argumentsString = new StringBuilder();
        for (Map.Entry<String, Object> item : argumentValues.valueSet()) {
            String argumentSet = "";
            if (isFirstArgument) {
                isFirstArgument = false;
            } else {
                argumentSet = "&";
            }
            argumentSet += item.getKey() + "=" + item.getValue();
            argumentsString.append(argumentSet);
        }
        return argumentsString.toString();
    }
}
