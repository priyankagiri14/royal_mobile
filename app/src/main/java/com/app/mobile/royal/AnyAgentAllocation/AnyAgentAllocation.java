package com.app.mobile.royal.AnyAgentAllocation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mobile.royal.AllocationCreateResponse.AllocationCreate;
import com.app.mobile.royal.Driver.Driver_Dashboard.Stocks_dashboard;
import com.app.mobile.royal.R;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Web_Interface;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnyAgentAllocation extends AppCompatActivity implements LocationListener{

    EditText etsearch;
    LinearLayout mainlinear;
    Button searchbtn,allocation_btn;
    TextView name,mobile,email,id;
    ImageView profileimage;
    ArrayList<String> bodyArrayListbatches = new ArrayList<String>();
    ProgressDialog progressBar;
    String idstring = null;
    double latitude,longitude;
    LocationManager locationManager;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any_agent_allocation);

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        etsearch = findViewById(R.id.searchuser);
        etsearch.setHint("Enter Mobile Number");

        searchbtn = findViewById(R.id.searchbtn);
        mainlinear = findViewById(R.id.mainlinear);
        mainlinear.setVisibility(View.INVISIBLE);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        id = findViewById(R.id.id);
        profileimage = findViewById(R.id.profileimage);

        allocation_btn = findViewById(R.id.allocation_btn);
        allocation_btn.setVisibility(View.INVISIBLE);

        getLocation();

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etsearch.getText().toString().length() == 0)
                {
                    Toast.makeText(AnyAgentAllocation.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    mainlinear.setVisibility(View.INVISIBLE);
                    allocation_btn.setVisibility(View.INVISIBLE);
                }
                else if(etsearch.getText().toString().length() != 10 && etsearch.getText().toString().length() > 0)
                {
                    Toast.makeText(AnyAgentAllocation.this, "Mobile Number seems incorrect", Toast.LENGTH_SHORT).show();
                    mainlinear.setVisibility(View.INVISIBLE);
                    allocation_btn.setVisibility(View.INVISIBLE);
                }
                else
                {
                    Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
                    Call<AgentSearchResponse> agentSearchResponseCall = web_interface.requestAgentSearchResponse("all",etsearch.getText().toString());
                    agentSearchResponseCall.enqueue(new Callback<AgentSearchResponse>() {
                        @Override
                        public void onResponse(Call<AgentSearchResponse> call, Response<AgentSearchResponse> response) {
                            if(response.isSuccessful() && response.code() ==200)
                            {
                                String success = String.valueOf(response.body().getSuccess());
                                if(success.equals("true")) {
                                    mainlinear.setVisibility(View.VISIBLE);
                                    allocation_btn.setVisibility(View.VISIBLE);
                                    name.setText(response.body().getBody().get(0).getName());
                                    email.setText(response.body().getBody().get(0).getProfile().getEmail());
                                    mobile.setText(response.body().getBody().get(0).getProfile().getMobileNo());
                                    id.setText(response.body().getBody().get(0).getProfile().getIdNo());
                                    idstring = response.body().getBody().get(0).getId().toString();
                                    Integer fileid = 0;
                                    if (response.body().getBody().get(0).getAttachments().size() != 0) {
                                        fileid = response.body().getBody().get(0).getAttachments().get(0).getId();

                                        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
                                        Call<ResponseBody> responseBodyCall = web_interface.requestImagefromserver(fileid);
                                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                String imageView = response.toString();

                                                if (response.body() != null) {
                                                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                                    Log.d("bitmap", "onResponse: " + bitmap);
                                                    profileimage.setImageBitmap(bitmap);
                                                }
                                                Log.d("onImage: ", imageView);
                                                // URI uri = new URI();
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                progressBar.cancel();
                                                Toast.makeText(AnyAgentAllocation.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AgentSearchResponse> call, Throwable t) {
                            progressBar.cancel();
                            Toast.makeText(AnyAgentAllocation.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        allocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.show();

                String[] batches = new String[bodyArrayListbatches.size()];
                batches = getIntent().getStringArrayExtra("allocation_stock");


                Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
                Call<AllocationCreate> allocationCreateCall = web_interface.requestAllocationCreate(idstring, latitude,longitude,batches);
                allocationCreateCall.enqueue(new Callback<AllocationCreate>() {
                    @Override
                    public void onResponse(Call<AllocationCreate> call, Response<AllocationCreate> response) {
                        String message = response.body().getMessage();
                        String success = String.valueOf(response.body().getSuccess());
                        if(success.equals("true")) {
                            Toast.makeText(AnyAgentAllocation.this, message, Toast.LENGTH_SHORT).show();
                            progressBar.cancel();
                            Intent intent = new Intent(AnyAgentAllocation.this, Stocks_dashboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            progressBar.cancel();
                            Toast.makeText(AnyAgentAllocation.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AllocationCreate> call, Throwable t) {
                        progressBar.dismiss();
                        Toast.makeText(AnyAgentAllocation.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    private void getLocation() {

        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, (LocationListener) this);
            progressBar.dismiss();
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("onLocationChanged: ", String.valueOf(latitude));
        Log.d("onLocationChanged: ", String.valueOf(longitude));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        if(!((Activity) AnyAgentAllocation.this).isFinishing()) {
            //show dialog
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Open GPS Settings?");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    if (Settings.ACTION_LOCATION_SOURCE_SETTINGS.equals(true)) {
                        alertDialog.dismiss();
                    }
                    count++;
                }
            });
            if (count > 0) {
                alertDialog.dismiss();
                count = 0;
            }
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(AnyAgentAllocation.this, Stocks_dashboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });
            alertDialog.show();
        }
    }
}
