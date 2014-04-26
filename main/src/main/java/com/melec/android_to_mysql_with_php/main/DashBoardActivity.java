package com.melec.android_to_mysql_with_php.main;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class DashBoardActivity extends ActionBarActivity {

    final static String LOGGED_USER_key = "user";
    TextView welcomeMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity_layout);

        welcomeMsg = (TextView) findViewById(R.id.dashboardWelcomeMsg);


        Bundle extras = getIntent().getExtras();
        String user;

        if(extras != null){

            user = extras.getString(LOGGED_USER_key);
            String mWelcomeMSG = getResources().getString(R.string.dash_board_welcome);

            welcomeMsg.setText(mWelcomeMSG + user + "!");
        }



    }



}
