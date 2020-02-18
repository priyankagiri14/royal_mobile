package com.app.mobile.royal.Agent;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.mobile.royal.Agent.UpdateAgentRicaGroupName.UpdateAgentRicaGroupAgent;
import com.app.mobile.royal.Web_Services.MyApp;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Places;
import com.app.mobile.royal.AboutActivity;
import com.app.mobile.royal.Agent.AttendanceGetResponse.AttendanceConfirmResponse;
import com.app.mobile.royal.Agent.AttendanceGetResponse.AttendanceGetResponse;
import com.app.mobile.royal.Agent.AttendanceGetResponse.Body;
import com.app.mobile.royal.AgentAssignBatchesTab;
import com.app.mobile.royal.AgentBatchesGet.AppDatabase;
import com.app.mobile.royal.AgentBatchesGet.Batches;
import com.app.mobile.royal.AgentBatchesGet.BatchesAdapter;
import com.app.mobile.royal.AgentReceiveBatchesTab;
import com.app.mobile.royal.Navigation_main.Navigation_Main;
import com.app.mobile.royal.NetworkStateReceiver;
import com.app.mobile.royal.OpenBatchesResponse.OpenedBatchesActivity;
import com.app.mobile.royal.R;
import com.app.mobile.royal.RicaTab;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Utils.Pref;
import com.app.mobile.royal.Web_Services.Web_Interface;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.arnaudguyon.perm.Perm;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Agent_Mainactivity extends AppCompatActivity implements View.OnClickListener, LocationListener , NetworkStateReceiver.NetworkStateReceiverListener {

    CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6, cardView7, cardView8, cardView9, cardView10, cardviewAttendance, airtimeSales, payWater;

    Toolbar toolbar;

    int id;
    String batchid,nameString;
    private SharedPreferences sharedPreferences;
    private AppDatabase db;
    Perm perm;
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSIONS[] = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.CAMERA};

    TextView textView,nameText,roletext;
    protected LocationManager locationManager;
    double savelatitude,savelongitude;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public static BatchesAdapter batchesAdapter;
    public static List<Batches> batcheslist = new ArrayList<>();
    ProgressDialog progressDialog;
    int count =0;
    private NetworkStateReceiver networkStateReceiver;
    /**
     * Thus method is initializing all the design components which will be used further for some functionalty.
     * @param savedInstancestate
     */
    public void onCreate(Bundle savedInstancestate) {

        super.onCreate(savedInstancestate);
        setContentView(R.layout.agent_mainactivity);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        /*CheckNetworkConnectionHelper
                .getInstance()
                .registerNetworkChangeListener(new StopReceiveDisconnectedListener() {
                    @Override
                    public void onDisconnected() {
                        //Do your task on Network Disconnected!
                        Log.e("onDisconnected","Network");
                        Intent intent = new Intent(Agent_Mainactivity.this,NetworkError.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onNetworkConnected() {
                        //Do your task on Network Connected!
                        Log.d("onNetworkConnected: ","Network");
                       *//* Intent intent = new Intent(Agent_Mainactivity.this,Agent_Mainactivity.class);
                        startActivity(intent);*//*
                    }

                    @Override
                    public Context getContext() {
                        return Agent_Mainactivity.this;
                    }
                });*/
        sharedPreferences=getSharedPreferences("Agent",MODE_PRIVATE);
        String role= sharedPreferences.getString("Agent",null);
        cardView1 = findViewById(R.id.checkStock);
        cardView2 = findViewById(R.id.Callagent);
        cardView3 = findViewById(R.id.sim_activation);
        cardView4 = findViewById(R.id.airtimeSales);
        cardView5 = findViewById(R.id.dataBundle);
        cardView6 = findViewById(R.id.payTv);
        cardView7 = findViewById(R.id.payUtility);
        cardView8 = findViewById(R.id.playLotto);
        cardView9 = findViewById(R.id.microLoan);
        cardView10 = findViewById(R.id.microInsurance1);
        cardviewAttendance = findViewById(R.id.attendanceacpt);
        airtimeSales = findViewById(R.id.activebatches);
        payWater = findViewById(R.id.payWater);
        textView = findViewById(R.id.uhlPrompt);
        roletext=findViewById(R.id.agentroletxt);
        roletext.setText(role+"-  ");

        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);
        cardView5.setOnClickListener(this);
        cardView6.setOnClickListener(this);
        cardView7.setOnClickListener(this);
        cardView8.setOnClickListener(this);
        cardView9.setOnClickListener(this);
        cardView10.setOnClickListener(this);
        payWater.setOnClickListener(this);
        cardviewAttendance.setOnClickListener(this);
        airtimeSales.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        nameText = findViewById(R.id.nametext);

        if (Pref.getBatchID(this) == null) {
            batchid = "";
        } else {
            batchid = Pref.getBatchID(this);
        }
        nameString = Pref.getFirstName(this);
        nameText.setText(nameString);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            /**
             * Toolbar is having three options LogOut, Offline Rica & About the app
             * @param menuItem
             * @return This retun all the menu items functionality
             */
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.logout) {

                    sharedPreferences = getSharedPreferences("Agent", Context.MODE_PRIVATE);
                    if (sharedPreferences.contains("Agent")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        Pref.removeIsRica(MyApp.getContext());

                        Intent i = new Intent(Agent_Mainactivity.this, Navigation_Main.class);
                        startActivity(i);
                        finish();
                    }
                }
                if (menuItem.getItemId() == R.id.update_group_rica_agent) {
                    Intent intent = new Intent(Agent_Mainactivity.this, UpdateAgentRicaGroupAgent.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.bulkrica) {
                    Intent intent = new Intent(Agent_Mainactivity.this, OfflineRica.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.about) {
                    Intent intent = new Intent(Agent_Mainactivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        perm = new Perm(this, PERMISSIONS);
        if (perm.areGranted()) {
            //   Toast.makeText(this, "All Permissions granted", Toast.LENGTH_LONG).show();
        } else {
            perm.askPermissions(PERMISSIONS_REQUEST);
        }

}

    void getLocation() {
        try {
            progressDialog.show();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, (LocationListener) this);
            progressDialog.dismiss();
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        savelongitude= location.getLongitude();
        savelatitude= location.getLatitude();
        //Toast.makeText(this,"Location"+savelatitude +" "+savelongitude,Toast.LENGTH_SHORT).show();


        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            progressDialog.dismiss();
            attendanceGet();
        }catch(Exception e)
        {
            progressDialog.dismiss();
            Toast.makeText(this, "" +e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if(!((Activity) Agent_Mainactivity.this).isFinishing())
        {
            //show dialog
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Open GPS Settings?");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    if(Settings.ACTION_LOCATION_SOURCE_SETTINGS.equals(true))
                    {
                        alertDialog.dismiss();
                        attendanceGet();
                    }
                    progressDialog.cancel();
                    count++;
                }
            });
            if(count>0)
            {
                alertDialog.dismiss();
                progressDialog.cancel();
                count = 0;

            }
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.cancel();
                    progressDialog.cancel();
                    Toast.makeText(Agent_Mainactivity.this, "Turn on GPS Location for Attendance Acknowledgement", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(Agent.this, Stocks_dashboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    private void initLocation () {
        progressDialog.show();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000);

        perm = new Perm(this, PERMISSIONS);
        if (perm.areGranted()) {
            //   Toast.makeText(this, "All Permissions granted", Toast.LENGTH_LONG).show();
        } else {
            perm.askPermissions(PERMISSIONS_REQUEST);
            progressDialog.dismiss();
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            progressDialog.dismiss();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                Intent i=new Intent(this, Navigation_Main.class);
                startActivity(i);
                finish();
                break;
            case R.id.bulkrica:
                Intent intent = new Intent(this,OfflineRica.class);
                startActivity(intent);
                finish();
                break;
            case R.id.about:
                Intent intent1 = new Intent(this,AboutActivity.class);
                startActivity(intent1);
                finish();
                break;
                default:
                    break;
        }
        return true;
    }

    /**
     *  This returns the event functionality of the component which will be clicked. According to its click,what the component will do, is written over there.
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.checkStock){
            Intent i=new Intent(this, AgentAssignBatchesTab.class);
            startActivity(i);
        }
        else if(v.getId()==R.id.sim_activation){
            Intent intent=new Intent(this, Sim_allocation.class);
            startActivity(intent);

        }
        else if(v.getId() == R.id.Callagent)
        {
            if(batchid.length()==0)
            {
                Intent intent = new Intent(this, AgentReceiveBatchesTab.class);
                startActivity(intent);
            }
            else
            {
                anotherBatchActive();
            }
        }
        else if(v.getId() == R.id.airtimeSales)
        {
/*            Intent intent = new Intent(this, AirtimeSalesActivity.class);
            startActivity(intent);*/
            Toast.makeText(Agent_Mainactivity.this, "Coming Soon....", Toast.LENGTH_SHORT).show();

        }
        else if(v.getId() == R.id.dataBundle)
        {
/*            Intent intent = new Intent(this, DataBundleActivity.class);
            startActivity(intent);*/
            Toast.makeText(Agent_Mainactivity.this, "Coming Soon....", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.payTv)
        {
            Toast.makeText(Agent_Mainactivity.this, "Coming Soon....", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.payUtility)
        {
/*            Intent intent = new Intent(this, ElectricityBundleActivity.class);
            startActivity(intent);*/
            Toast.makeText(Agent_Mainactivity.this, "Coming Soon....", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.playLotto)
        {
 /*           Intent intent = new Intent(this, WifiBundle.class);
            startActivity(intent);*/
            Toast.makeText(Agent_Mainactivity.this, "Coming Soon....", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.microLoan)
        {
            Toast.makeText(Agent_Mainactivity.this, "Coming Soon....", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.microInsurance1)
        {
            Toast.makeText(Agent_Mainactivity.this, "Coming Soon....", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.attendanceacpt)
        {
            getLocation();
            initLocation();
        }
        else if(v.getId() == R.id.activebatches)
        {
                if (batchid.length()==0) {
                    noActiveBatches();
                } else {
                    Intent intent = new Intent(Agent_Mainactivity.this, OpenedBatchesActivity.class);
                    startActivity(intent);
                }
        }
        else if(v.getId() == R.id.payWater)
        {
            Toast.makeText(Agent_Mainactivity.this, "Coming Soon....", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This function returns the dialog box which shows a message if any other batch is active when user tries to open second batch.
     */
    private void anotherBatchActive() {

        AlertDialog alertDialog = new AlertDialog.Builder(Agent_Mainactivity.this).create();
        alertDialog.setMessage("Another Batch is already in Active State");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Got It!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    /**
     * This shows dialog box if no batch is active
     */
    private void noActiveBatches() {
        AlertDialog alertDialog = new AlertDialog.Builder(Agent_Mainactivity.this).create();
        alertDialog.setMessage("No Active Batches");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Got It!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    /**
     * This function acknowledges the user attendance if driver mark his/ her attendance
     */
    private void attendanceGet() {


        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
        Call<AttendanceGetResponse> attendanceGetResponseCall = web_interface.requestAttendanceGet(0,0);
        attendanceGetResponseCall.enqueue(new Callback<AttendanceGetResponse>() {
            @Override
            public void onResponse(Call<AttendanceGetResponse> call, Response<AttendanceGetResponse> response) {
                String confirmation = "null";
                List<Body> bodyList = new ArrayList<>();
                bodyList = response.body().getBody();
                Log.d("onResponse: ", confirmation);
                if (bodyList.size() == 0) {
                    noAcknowledgmentDialog();
                } else {
                    confirmation = bodyList.get(0).getConfirmation().toString();
                    Log.d("onResponse: ", confirmation);
                   if (confirmation == "false") {
                       id = bodyList.get(0).getId();
                       //Log.d("onResponse: ", confirmation);
                       alertDialogopen();
                    }
                }
            }

            /**
             * This function shows a dialog box when no attendance acknowledgement is pending from the user side.
             */
            private void noAcknowledgmentDialog() {
                AlertDialog alertDialog = new AlertDialog.Builder(Agent_Mainactivity.this).create();
                alertDialog.setMessage("No Attendance Acknowledgement Pending..!");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Got It!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }

            @Override
            public void onFailure(Call<AttendanceGetResponse> call, Throwable t) {
                Toast.makeText(Agent_Mainactivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alertDialogopen() {

        AlertDialog alertDialog = new AlertDialog.Builder(Agent_Mainactivity.this).create();
        alertDialog.setTitle("Please Acknowledge Your Attendence");
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String confirmation = "true";
                Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);


                try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("confirmation",confirmation);

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"),(jsonObject).toString());
                    Call<AttendanceConfirmResponse> attendanceConfirmResponseCall = web_interface.requestAttendanceConfirm(id,body);
                    attendanceConfirmResponseCall.enqueue(new Callback<AttendanceConfirmResponse>() {
                        @Override
                        public void onResponse(Call<AttendanceConfirmResponse> call, Response<AttendanceConfirmResponse> response) {
                            if(response.isSuccessful() && response.code() == 200)
                            {
                                String message = response.body().getMessage();
                                Toast.makeText(Agent_Mainactivity.this, message, Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<AttendanceConfirmResponse> call, Throwable t) {
                            Toast.makeText(Agent_Mainactivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                catch (JSONException e)
                {
                    Toast.makeText(Agent_Mainactivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {
        Intent i=new Intent(this,NetworkError.class);
        startActivity(i);
    }
}
