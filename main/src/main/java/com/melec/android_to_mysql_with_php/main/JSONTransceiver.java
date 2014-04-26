package com.melec.android_to_mysql_with_php.main;

/** use HttpURLConnection, not the apache library see here:
 http://android-developers.blogspot.com/2011/09/androids-http-clients.html
 */


        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.os.AsyncTask;
        import android.os.SystemClock;
        import android.util.Log;
        import android.view.Gravity;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONException;
        import org.json.JSONObject;
        import org.json.JSONTokener;
        import java.io.BufferedReader;
        import java.io.DataOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.ConnectException;
        import java.net.HttpURLConnection;
        import java.net.InetAddress;
        import java.net.MalformedURLException;
        import java.net.SocketException;
        import java.net.URL;


public class JSONTransceiver extends AsyncTask<JSONObject, Void, JSONObject> {

    private Activity mActivity; /**reference to parent activity*/


    JSONTransceiverListener listener = null;




       /**constructor*/
    public JSONTransceiver( Activity activity, JSONTransceiverListener listener ) {

        mActivity = activity;
        this.listener = listener;
    }

    ProgressDialog  mPDialog;

    @Override
    protected JSONObject doInBackground(JSONObject...  data) {


        JSONObject jsonResponse = null; /**jsonResponse from server*/

        URL  url; /**server url*/


        final int HTTP_OK = 200; /** httpCode 200, means server found*/
        int serverResponseCode;
        boolean serverDown = false;


          /**foreach statement*/
        for (JSONObject args : data)
            try {

                   url = new URL("http://192.168.100.2/json.php");

                    String jsonSend = "TAG=" + args.toString(); /**JSONObject to send*/
                    Log.d("JSONTransceiver-jsonSend: ", args.toString());

                                   /**start connection*/
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        connection.setRequestMethod("POST");


                        /**add request header*/
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                        connection.setUseCaches(false);
                        connection.setDoInput(true); /**allow receiving data*/
                        connection.setDoOutput(true); /**allows sending data.*/


                        /**Send post request*/
                        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                        wr.writeBytes(jsonSend);
                        wr.flush();
                        wr.close();


                        serverResponseCode = connection.getResponseCode();
                        /**if server-response Code == 200, get received data*/
                        if(serverResponseCode == HTTP_OK) {


                            InputStream is = connection.getInputStream();  /**get received data*/


                            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                            StringBuilder sb = new StringBuilder();

                            String line;

                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\r\n");
                            }

                            is.close();

                            String json = sb.toString();
                            Log.d("server-response: ", json);

                            /**encode response into a jsonobject*/
                            jsonResponse = (JSONObject) new JSONTokener(json).nextValue();


                            /**close connection to remote server*/
                            connection.disconnect();

                        }



            } catch (ConnectException e){
                e.printStackTrace();
                serverDown = true;

            } catch(JSONException e){
                e.printStackTrace();

            } catch(IOException e){
                e.printStackTrace();
            }



        if(serverDown){

            try{
                JSONObject serverIsDown = new JSONObject();
                serverIsDown.put("tag","serverdown");
                jsonResponse = serverIsDown;

            } catch (JSONException e){
                e.printStackTrace();
            }
       }

        return jsonResponse;

    }





    @Override
    protected  void onPreExecute(){
        super.onPreExecute();
        /**Showing progress dialog*/


        mPDialog = new ProgressDialog( mActivity);
        mPDialog.setMessage("Loading...");
        mPDialog.setCancelable(false);
        mPDialog.show();

    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

        String loginTag = "login";
        String serverDownTag = "serverdown";
        boolean serverDown = false;


        /**if the returned JSONObject from doInBackground is valid, parse*/
       if(result != null) {

           try {

               String tag = result.getString("tag");

               /**handle serverdown error*/
                if (tag.equals(serverDownTag)){
                    serverDown = true;
                }

                /**if server is up, process data*/
               if(!serverDown) {

                   if (tag.equals(loginTag)) {

                       String username = result.getString("username");

                       int success = result.getInt("success");
                       int error = result.getInt("error");

                       if (success == 0 && error > 0) { /** login failed*/

                           listener.onLoginError(error);

                       } else if (success == 1 && error == 0) { /**login successful*/

                           listener.onLoginSuccess(username);
                       }


                       Log.d("onPostExecute-serverResponse-username:", username);
                       Log.d("onPostExecute-serverResponse-success:", Integer.toString(success));
                       Log.d("onPostExecute-serverResponse-error:", Integer.toString(error));

                   }

               }

               /**if server is down,create dialog*/
               if(serverDown){
                   Log.d("serverisDown","true");

                   /**create an alert dialog*/
                   AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                   alert.setMessage(R.string.errServerConnetionFailed);
                   alert.setTitle(R.string.error);
                   alert.setIcon(R.drawable.error_icon);
                   alert.setCancelable(true);

                   /**add OK button*/
                   alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int i) {


                           dialog.cancel(); /**onButtonClicked, dismiss*/
                       }
                   });

                   /**center text in dialog*/
                   AlertDialog mdialog = alert.show();
                   TextView messageText =  (TextView) mdialog.findViewById(android.R.id.message);
                   messageText.setGravity(Gravity.CENTER);

                   /**show dialog*/
                   mdialog.show();

               }

           } catch (JSONException e) {
               e.printStackTrace();
           }
       }



        if(mPDialog.isShowing()){
            mPDialog.dismiss();
        }


    }

}

