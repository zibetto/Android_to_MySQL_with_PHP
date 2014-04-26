package com.melec.android_to_mysql_with_php.main;

/**public interface, to communicate with JSONTransceiver*/

public interface JSONTransceiverListener {

    public void onLoginError(int error);
    public void onLoginSuccess(String user);

}