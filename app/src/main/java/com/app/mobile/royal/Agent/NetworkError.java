package com.app.mobile.royal.Agent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.app.mobile.royal.NetworkStateReceiver;
import com.app.mobile.royal.R;

public class NetworkError extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    TextView refreshtext;
    NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        refreshtext = findViewById(R.id.refreshtext);
        /*refreshtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckNetworkConnectionHelper
                        .getInstance()
                        .registerNetworkChangeListener(new StopReceiveDisconnectedListener() {
                            @Override
                            public void onDisconnected() {
                                //Do your task on Network Disconnected!
                                Toast.makeText(NetworkError.this, "Check your Internet Connection", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onNetworkConnected() {
                                //Do your task on Network Connected!
                                Log.d("onNetworkConnected: ","Network" +getClass().getName());
                                //finish();
                                *//*Intent intent = new Intent(NetworkError.this, Navigation_Main.class);
                                startActivity(intent);*//*
                            }

                            @Override
                            public Context getContext() {
                                return NetworkError.this;
                            }
                        });
            }
        });*/
    }

    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }
    @Override
    public void networkAvailable() {
     finish();
    }

    @Override
    public void networkUnavailable() {

    }
}
