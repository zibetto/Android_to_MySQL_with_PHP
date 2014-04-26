package com.melec.android_to_mysql_with_php.main;



        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.provider.Settings;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONException;
        import org.json.JSONObject;



public class LoginActivity extends ActionBarActivity implements JSONTransceiverListener {

    final static String LOGGED_USER_KEY = "user";

    /** username and password boxes*/
    EditText usernameBox;
    EditText passwordBox;

    /**flag for Internet connection status*/
    boolean isConnected;
    /**Connection detector class*/
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);

        /**creating connection detector class instance*/
         cd = new ConnectionDetector(getApplicationContext());

        /**get a reference to the username and password box widgets*/
        usernameBox = (EditText) findViewById(R.id.usernameEditText);
        passwordBox = (EditText) findViewById(R.id.passwordEditText);




    }

    @Override
    public void onLoginSuccess(String user){

        /**start DashBoardActivity*/
        Intent mIntent = new Intent (this, DashBoardActivity.class);
        mIntent.putExtra(LOGGED_USER_KEY,user);

        startActivity(mIntent);


        Log.d("onLoginSuccess",user);
    }


    @Override
    public void  onLoginError( int error ){

        passwordBox = (EditText) findViewById(R.id.passwordEditText);

        /**load string array from recource file*/
        String[] errorCode = getResources().getStringArray(R.array.alertLoginErrorCodes);
        int index = error -1; /** minus one, because the first item in the array is cero*/


        /**create an alert dialog*/
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(errorCode[index]);
        alert.setTitle(R.string.error);
        alert.setIcon(R.drawable.error_icon);
        alert.setCancelable(true);

        /**add OK button*/
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                passwordBox.setText(""); /**empty the password field*/
                dialog.cancel(); /**onButtonClicked, dismiss*/
            }
        });

        /**center text in dialog*/
        AlertDialog mdialog = alert.show();
        TextView messageText =  (TextView) mdialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);

        /**show dialog*/
        mdialog.show();


        Log.d("onLoginError:", errorCode[index]);


    }



    public void loginButton_clicked(View v){

        /**get Internet status*/
        isConnected = cd.isConnectedToInternet();


        /**if there is network connection, proceed*/
        if(isConnected){

            /**get input Text*/
            String username = usernameBox.getText().toString();
            String password = passwordBox.getText().toString();

            /**check if there is some text in the boxes, proceed*/
            if(!username.isEmpty() && !password.isEmpty() ) {

                try {
                    JSONObject mJSON;
                    mJSON = new JSONObject();
                    mJSON.put("tag", "login");
                    mJSON.put("username", username);
                    mJSON.put("password", password);


                    JSONTransceiver mTask = new JSONTransceiver(this,this);
                    mTask.execute(mJSON);

                    Log.d("LoginActivity-mJSON", mJSON.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else{
                /**if username or password is empty, alert user*/
                Toast.makeText(this,R.string.alertLoginEmpty,Toast.LENGTH_SHORT).show();

            }

        } else{ /**show no connection dialog*/

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(R.string.alertNoConnection);
            alert.setTitle(R.string.error);
            alert.setIcon(R.drawable.error_icon);
            alert.setCancelable(true);

            /**add OK button*/
            alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                   /**open network settins*/
                    Intent mIntent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(mIntent);

                }


            });

            alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public  void onClick( DialogInterface dialog, int i ){
                    dialog.cancel();
                }

            });




            /**center text in dialog*/
            AlertDialog mdialog = alert.show();
            TextView messageText =  (TextView) mdialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);

            /**show dialog*/
            mdialog.show();
        }
    }

}