package com.app.mobile.royal.Agent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mobile.royal.Driver.Driver_Dashboard.Stocks_dashboard;
import com.app.mobile.royal.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.location.aravind.getlocation.GeoLocator;
import com.app.mobile.royal.Agent.model.Simallocatemodel;
import com.app.mobile.royal.NetworkStateReceiver;
import com.app.mobile.royal.OpenCloseBatches.CashHistory.AppDatabaseSerials;
import com.app.mobile.royal.OpenCloseBatches.CashHistory.Serials;
import com.app.mobile.royal.OpenCloseBatches.CashHistory.SerialsAdapter;
import com.app.mobile.royal.OpenCloseBatches.CashHistory.SerialsInterface;
import com.app.mobile.royal.R;
import com.app.mobile.royal.Web_Services.MyApp;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Utils.Pref;
import com.app.mobile.royal.Web_Services.Web_Interface;
import com.mapbox.api.geocoding.v5.models.CarmenContext;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import info.androidhive.fontawesome.FontTextView;
import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfflineRica extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener , NetworkStateReceiver.NetworkStateReceiverListener {

    public EditText fname, lname, address, pincode, subhurb, idnum, city,passport,expirydate;
    public TextInputLayout idnumtext,passporttext,expirydatetext;
    public RadioGroup networkrg;
    ListView listViewsearchserials;
    private String mResult = null;
    ArrayList<String> ListElementsArrayList;
    ArrayAdapter<String> adapter;
    String[] batches;
    Toolbar toolbar;
    RadioButton vodacom, telkom, cellc, mtn;
    String network, citystring;
    String simcard = "";
    Button simallocate, agentscanbtn;
    Spinner regionspinner,idspinner;
    String region,idtype;
    String type = "OFFLINE";
    private SearchView searchView;
    AppDatabaseSerials db;
    SerialsInterface serialsInterface;
    SerialsAdapter serialsAdapter;
    List<Serials> serialsList = new ArrayList<>();
    TextView textserials;
    FontTextView calender;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private NetworkStateReceiver networkStateReceiver;
    private AlertDialog alertDialog;
    private String region_1;
    private SharedPreferences agentsharedprefrence,driversharedprefrence;
    private Intent intent;

    /**
     * This functions initialize all the design components for their functionality to be work properly.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_rica);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        regionspinner = (Spinner) findViewById(R.id.regionspinner);

        db = Room.databaseBuilder(MyApp.getContext(), AppDatabaseSerials.class, "serials")
                .allowMainThreadQueries()
                .build();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (SearchView) findViewById(R.id.agentsearchview);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(this);
        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.lastname);
        address = findViewById(R.id.address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mapboxlog", "onClick: call places function");
                setupAutoCompleteAddress();
            }
        });
        pincode = findViewById(R.id.postal_code);
        subhurb = findViewById(R.id.subhurb);
        idnum = findViewById(R.id.idnum);
        city = findViewById(R.id.city);
        passport = findViewById(R.id.passport);
        expirydate = findViewById(R.id.expirydate);
        expirydate.setClickable(false);
        expirydate.setFocusable(false);

        idnumtext = findViewById(R.id.idnumtext);
        passporttext = findViewById(R.id.passporttext);
        expirydatetext = findViewById(R.id.expirydatetext);

        calender = findViewById(R.id.calender);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(OfflineRica.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = dayOfMonth+"/"+month+"/"+year;
                expirydate.setText(date);
            }
        };
        idspinner = findViewById(R.id.idspinner);
        networkrg = findViewById(R.id.netwrokrg);
        networkrg.setOnCheckedChangeListener(this);
        vodacom = findViewById(R.id.vodacom);
        telkom = findViewById(R.id.telkom);
        mtn = findViewById(R.id.mtn);
        simallocate = findViewById(R.id.activate_sim);
        agentscanbtn = findViewById(R.id.agentscanbtn);
        searchView.setQueryHint("Search");
        //searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        agentscanbtn.setOnClickListener(this);
        simallocate.setOnClickListener(this);
        listViewsearchserials = findViewById(R.id.agent_search_results_list);
        listViewsearchserials.setTextFilterEnabled(true);
        listViewsearchserials.setVisibility(View.GONE);
        listViewsearchserials.setOnItemClickListener(this);
        cellc = findViewById(R.id.cellc);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            searchView.setQuery("", false);
        } else {
            simcard = bundle.getString("simcard");
            searchView.setQuery(simcard, false);
        }
        if (Pref.getCity(this) == null) {
            city.setText("");
        } else {
            city.setText(Pref.getCity(this));
        }

        regionspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(OfflineRica.this, "Please Enter data for all the Fields", Toast.LENGTH_SHORT).show();
                } else {
                    region = regionspinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        idspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    /*idnumtext.setVisibility(View.GONE);
                    passporttext.setVisibility(View.GONE);
                    expirydatetext.setVisibility(View.GONE);
                    calender.setVisibility(View.GONE);*/
                    idtype = idspinner.getSelectedItem().toString();
                    idnumtext.setVisibility(View.VISIBLE);
                    passporttext.setVisibility(View.GONE);
                    expirydatetext.setVisibility(View.GONE);
                    calender.setVisibility(View.GONE);
                }
                else if(position == 1)
                {
                    idtype = idspinner.getSelectedItem().toString();
                    idnumtext.setVisibility(View.GONE);
                    passporttext.setVisibility(View.VISIBLE);
                    expirydatetext.setVisibility(View.VISIBLE);
                    calender.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * This function opens the camera with view bar there for scanning the batch to get the value of that batch.
     */

    private void initiateScan() {
        ZxingOrient integrator = new ZxingOrient(this);
        integrator.setVibration(true);
        integrator.setBeep(true);
        integrator.initiateScan();
    }

    /**
     * To get the location of the user, this function is used. It gets the current lat, long of that user.
     */
    private void getLocation() {
        GeoLocator geoLocator = new GeoLocator(getApplicationContext(), this);
        Log.d("startbranding", "getLocation: " + geoLocator.getLattitude() + "\n" + geoLocator.getLongitude());
        address.setText(geoLocator.getAddress());
        Log.d("locationda", address.toString());
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.agentscanbtn) {
            initiateScan();
        }
        if (v.getId() == R.id.activate_sim) {
            /*if (fname.getText().toString().length() == 0 || lname.getText().length() == 0 || address.length() == 0 ||
                    pincode.getText().toString().length() == 0 || subhurb.getText().toString().length() == 0 || network.isEmpty()
                    || searchView.getQuery().length() == 0 || idnum.getText().length() == 0) {
                Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show();
            } else {
                simallocation(searchView.getQuery().toString(), network, idnum.getText().toString(), fname.getText().toString(), lname.getText().toString(),
                        address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString());
            }*/

            if(idspinner.getSelectedItem().toString().equals("ID")) {
                if (fname.getText().toString().length() == 0 || lname.getText().length() == 0 || address.length() == 0 ||
                        pincode.getText().toString().length() == 0 || subhurb.getText().toString().length() == 0 || TextUtils.isEmpty(network)
                        || regionspinner.getSelectedItemPosition() == 0 || searchView.getQuery().length() == 0 || idnum.getText().length() == 0 || idspinner.getSelectedItem().toString() == "ID Type") {
                    Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show();
                } else {
                    simallocation(searchView.getQuery().toString(), network, idnum.getText().toString(), fname.getText().toString(), lname.getText().toString(),
                            address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString(),idspinner.getSelectedItem().toString());
                }
            }
            else if(idspinner.getSelectedItem().toString().equals("PASSPORT")) {
                if (fname.getText().toString().length() == 0 || lname.getText().length() == 0 || address.length() == 0 ||
                        pincode.getText().toString().length() == 0 || subhurb.getText().toString().length() == 0 || TextUtils.isEmpty(network)
                        || regionspinner.getSelectedItemPosition() == 0 || searchView.getQuery().length() == 0 || passport.getText().length() == 0 || expirydate.getText().length() == 0 || idspinner.getSelectedItem().toString() == "ID Type") {
                    Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show();
                } else {
                    simallocation1(searchView.getQuery().toString(), network, fname.getText().toString(), lname.getText().toString(),
                            address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString(), idspinner.getSelectedItem().toString(),passport.getText().toString(),expirydate.getText().toString());
                }
            }
        }

    }

    /**
     *  This function returns the API results by getting all the values from the user & pass it into this function.
     * @param serial This parameter is the serial number of the that sim number.
     * @param network What is the network of that Sim?
     * @param fname The first name of the user for the Rica registration form.
     * @param lname The last name of the user for the Rica registration form.
     * @param address The address of the user for the Rica registration form.
     * @param postalcode The postal code of the user for the Rica registration form.
     * @param subhurb The suburb of the user for the Rica registration form.
     * @param city The suburb of the user for the Rica registration form.
     * @param idtype The type of the id of the user for the Rica registration form.
     * @param passport The Passport number of the user for the Rica registration form.
     * @param expirydate  The expiry date of the passport of the user for the Rica registration form.
     */

    private void simallocation1(String serial, String network, String fname, String lname, String address, String postalcode, String subhurb, String city, String idtype, String passport, String expirydate) {
        Utils.showProgress(this,"Please wait...");
        Web_Interface webInterface = RetrofitToken.getClient().create(Web_Interface.class);
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("serial",serial );
            paramObject.put("network", network);
            paramObject.put("name", fname);
            paramObject.put("surname",lname);
            paramObject.put("address",address );
            paramObject.put("postalCode", postalcode);
            paramObject.put("suburb", subhurb);
            paramObject.put("city", city);
            paramObject.put("region",region);
            paramObject.put("type",type);
            paramObject.put("idType",idtype);
            paramObject.put("passportNo",passport);
            paramObject.put("passportExpiryDate",expirydate);
            Log.d("simalloacte data",serial+"\n"+network +"\n" +idnum+"\n"+fname+"\n"+lname +"\n" +address +"\n" +postalcode +"\n" +subhurb +"\n" +city);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"),(paramObject).toString());
            Call<Simallocatemodel> call= webInterface.simallocate(body);
            //exeuting the service
            call.enqueue(new Callback<Simallocatemodel>() {
                @Override
                public void onResponse(Call<Simallocatemodel> call, Response<Simallocatemodel> response) {
                    if(response.isSuccessful()||response.code()==200){
                        Utils.stopProgress();
                        String message=response.body().getMessage();
                        String success = response.body().getSuccess().toString();
                        if(success.equals("true"))
                        {
                            db.serialsInterface().deleteSerials(serial) ;
                            Toasty.success(getApplicationContext(),message).show();
                            Pref.setCity(MyApp.getContext(),city);
                            agentsharedprefrence=getSharedPreferences("Agent",MODE_PRIVATE);
                            driversharedprefrence=getSharedPreferences("Driver",MODE_PRIVATE);
                            String agentsharedpref=agentsharedprefrence.getString("Agent",null);
                            String driversharedpref=driversharedprefrence.getString("Driver",null);

                            Log.d("sharepreffnull","agentsharedpref"+agentsharedpref +"\n"+"driversharedpref"+driversharedpref);
                            if(agentsharedpref!=null){
                                intent = new Intent(OfflineRica.this, Agent_Mainactivity.class);
                            }
                            if(driversharedpref!=null){
                                intent = new Intent(OfflineRica.this, Stocks_dashboard.class);
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Log.d("offlineRica", "onResponse: message: "+response.body().getMessage());
                            int error_code = response.body().getErrorCode();
                            if (error_code == 5001) {
                                Log.d("Sim Allocation", "onResponse: " + error_code + "message:" + message);
                                // now we will show the popup
                                showAlertDialog(message);
                            }
                            else {
                                Toasty.warning(getApplicationContext(), message).show();
                            }
                        }
                    }
                    else{
                        Utils.stopProgress();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.d("AgentLoginActivity", jObjError.getString("message"));
                            Toasty.info(getApplicationContext(), jObjError.getString("message")).show();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage() , Toast.LENGTH_LONG).show();
                            //stopping progress
                        }
                    }
                }

                @Override
                public void onFailure(Call<Simallocatemodel> call, Throwable t) {
                    Utils.stopProgress();
                    Log.d("simallocate",t.getLocalizedMessage());
                    Toasty.info(getApplicationContext(),t.getLocalizedMessage()).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * It will add the serial number value for the user registration RICA form if user search or enter it manually.
     */
    private void addBatchValue() {

        String textvalue = searchView.getQuery().toString();

        //adding only unique values
        ListElementsArrayList.add(textvalue);
        batches = new String[ListElementsArrayList.size()];
        for (int j = 0; j < ListElementsArrayList.size(); j++) {
            batches[j] = ListElementsArrayList.get(j);
        }

        //Log.d(TAG, "onActivityResult: result"+ListElementsArrayList);
        adapter.notifyDataSetChanged();
        listViewsearchserials.setVisibility(View.GONE);
        searchView.setQuery("",false);

    }

    /**
     *  This function returns the API results by getting all the values from the user & pass it into this function.
     * @param serial This parameter is the serial number of the that sim number.
     * @param network What is the network of that Sim?
     * @param fname The first name of the user for the Rica registration form.
     * @param lname The last name of the user for the Rica registration form.
     * @param address The address of the user for the Rica registration form.
     * @param postalcode The postal code of the user for the Rica registration form.
     * @param subhurb The suburb of the user for the Rica registration form.
     * @param city The suburb of the user for the Rica registration form.
     * @param idtype The type of the id of the user for the Rica registration form.
     * @param idnum THe ID number of the user for the user registration RICA Form.
     */
    private void simallocation(String serial, String network, String idnum, String fname, String lname, String address, String postalcode, String subhurb, String city, String idtype) {
        Utils.showProgress(this,"Please wait...");
        Web_Interface webInterface = RetrofitToken.getClient().create(Web_Interface.class);
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("serial",serial );
            paramObject.put("network", network);
            paramObject.put("idNo", idnum);
            paramObject.put("name", fname);
            paramObject.put("surname",lname);
            paramObject.put("address",address );
            paramObject.put("postalCode", postalcode);
            paramObject.put("suburb", subhurb);
            paramObject.put("city", city);
            paramObject.put("region",region);
            paramObject.put("type",type);
            paramObject.put("idType",idtype);
            Log.d("simalloacte data",serial+"\n"+network +"\n" +idnum+"\n"+fname+"\n"+lname +"\n" +address +"\n" +postalcode +"\n" +subhurb +"\n" +city);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"),(paramObject).toString());
            Call<Simallocatemodel> call= webInterface.simallocate(body);
            //exeuting the service
            call.enqueue(new Callback<Simallocatemodel>() {
                @Override
                public void onResponse(Call<Simallocatemodel> call, Response<Simallocatemodel> response) {
                    if(response.isSuccessful()||response.code()==200){
                        Utils.stopProgress();
                        String message=response.body().getMessage();
                        String success = response.body().getSuccess().toString();
                        if(success.equals("true"))
                        {
                            db.serialsInterface().deleteSerials(serial) ;
                            Toasty.success(getApplicationContext(),message).show();
                            Pref.setCity(MyApp.getContext(),city);
                            agentsharedprefrence=getSharedPreferences("Agent",MODE_PRIVATE);
                            driversharedprefrence=getSharedPreferences("Driver",MODE_PRIVATE);
                            String agentsharedpref=agentsharedprefrence.getString("Agent",null);
                            String driversharedpref=driversharedprefrence.getString("Driver",null);

                            Log.d("sharepreffnull","agentsharedpref"+agentsharedpref +"\n"+"driversharedpref"+driversharedpref);
                            if(agentsharedpref!=null){
                                intent = new Intent(OfflineRica.this, Agent_Mainactivity.class);
                            }
                            if(driversharedpref!=null){
                                intent = new Intent(OfflineRica.this, Stocks_dashboard.class);
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Log.d("offlineRica", "onResponse: message: "+response.body().getMessage());
                            int error_code = response.body().getErrorCode();
                            if (error_code == 5001) {
                                Log.d("Sim Allocation", "onResponse: " + error_code + "message:" + message);
                                // now we will show the popup
                                showAlertDialog(message);
                            }
                            else {
                                Toasty.warning(getApplicationContext(), message).show();
                            }
                        }
                    }
                    else{
                        Utils.stopProgress();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.d("AgentLoginActivity", jObjError.getString("message"));
                            Toasty.info(getApplicationContext(), jObjError.getString("message")).show();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage() , Toast.LENGTH_LONG).show();
                            //stopping progress
                        }
                    }
                }

                @Override
                public void onFailure(Call<Simallocatemodel> call, Throwable t) {
                    Utils.stopProgress();
                    Log.d("simallocate",t.getLocalizedMessage());
                    Toasty.info(getApplicationContext(),t.getLocalizedMessage()).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage(message);
        // set the custom layout
        @SuppressLint("InflateParams") final View customLayout = getLayoutInflater().inflate(R.layout.custom_popup_rica, null);
        builder.setView(customLayout);
        Button btn_submit_rica = customLayout.findViewById(R.id.btn_submit_rica);
        EditText et_rica_username = customLayout.findViewById(R.id.et_rica_username);
        EditText et_rica_user_password = customLayout.findViewById(R.id.et_rica_user_password);
        EditText et_rica_group_name = customLayout.findViewById(R.id.et_rica_group_name);
        CheckBox cb_accept_rica_agreement = customLayout.findViewById(R.id.cb_accept_rica_agreement);
        TextView tv_accept_agreement = customLayout.findViewById(R.id.tv_accept_agreement);
        String str = "By continuing, I accept Rica's Terms and Service. <a href='http://otmerp.co.za:8080/rica/terms'> <u> Click here to read the terms & conditions. </u> </a>";
        tv_accept_agreement.setText(Html.fromHtml(str));
        tv_accept_agreement.setMovementMethod(LinkMovementMethod.getInstance());
        btn_submit_rica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_rica_username.getText().toString();
                String password = et_rica_user_password.getText().toString();
                String group_name = et_rica_group_name.getText().toString();
                if (username.isEmpty()) {
                    et_rica_username.setError("Username Required*");
                    et_rica_username.requestFocus();
                } else if (password.isEmpty()) {
                    et_rica_user_password.setError("Password Required*");
                    et_rica_user_password.requestFocus();
                } else if (group_name.isEmpty()) {
                    et_rica_group_name.setError("Group Name Required*");
                    et_rica_group_name.requestFocus();
                } else if (!cb_accept_rica_agreement.isChecked()) {
                    Toast.makeText(OfflineRica.this, "Please accept the agreement", Toast.LENGTH_SHORT).show();
                } else {
                    sendUpdateRicaDetailsToServer(username, password, group_name);
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
    private void sendUpdateRicaDetailsToServer(String username, String password, String group_name) {
        Utils.showProgress(this,"Please wait....");
        int user_id = Integer.parseInt(Pref.getUserId(this));
        Log.d("OfflineRica", "sendUpdateRicaDetailsToServer: called user_id: "+user_id);
        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
        JSONObject jsonObjectOuter = new JSONObject();
        JSONObject jsonObjectInner = new JSONObject();
        try {
            jsonObjectInner.put("ricaUser",username);
            jsonObjectInner.put("ricaPassword",password);
            jsonObjectInner.put("ricaGroup",group_name);
            jsonObjectOuter.put("profile",jsonObjectInner);
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),(jsonObjectOuter.toString()));
            Call<ResponseBody> call = web_interface.requestRicaDetailsUpdate(user_id,requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful() && response.code() == 200)
                    {
                        if(alertDialog.isShowing())
                        {
                            alertDialog.dismiss();
                        }
                        Utils.stopProgress();
                        if(idspinner.getSelectedItem().toString().equals("ID")) {
                            simallocation(searchView.getQuery().toString(), network, idnum.getText().toString(), fname.getText().toString(), lname.getText().toString(),
                                    address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString(),idspinner.getSelectedItem().toString());
                        }
                        else if(idspinner.getSelectedItem().toString().equals("PASSPORT")) {
                            simallocation1(searchView.getQuery().toString(), network, fname.getText().toString(), lname.getText().toString(),
                                    address.getText().toString(), pincode.getText().toString(),
                                    subhurb.getText().toString(), city.getText().toString(), idspinner.getSelectedItem().toString(),
                                    passport.getText().toString(),expirydate.getText().toString());
                        }
                        try {
                            JSONObject jObjSuccess = new JSONObject(response.body().string());
                            Log.d("OfflineRica", jObjSuccess.getString("message"));
                            Toasty.info(getApplicationContext(), jObjSuccess.getString("message")).show();
                        } catch (Exception e) {
                            // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    else {
                        Utils.stopProgress();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.d("AgentLoginActivity", jObjError.getString("message"));
                            Toasty.info(getApplicationContext(), jObjError.getString("message")).show();
                        } catch (Exception e) {
                            // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            //stopping progress
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utils.stopProgress();
                    Log.d("OfflineRica", "onFailure: "+t.getLocalizedMessage());
                    Toast.makeText(OfflineRica.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setupAutoCompleteAddress() {
        Log.d("mapboxlog", "setupAutoCompleteAddress: called");
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(getResources().getString(R.string.map_box_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .country("ZA")
                        .hint("Search Address")
                        .build(PlaceOptions.MODE_CARDS))
                .build(this);
        startActivityForResult(intent, 101);
    }
        @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.vodacom:
                // do operations specific to this selection
                network = vodacom.getText().toString();
                Log.d("age", network);
                break;

            case R.id.telkom:
                network=telkom.getText().toString();
                break;
            case R.id.mtn:
                // do operations specific to this selection
                network = mtn.getText().toString();
                break;

            case R.id.cellc:
                network=cellc.getText().toString();
                break;



        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     *
     * @param newText It gets the text parameter to search it in the array list.
     * @return Searched value returned
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if(searchView.getQuery().length() == 0)
        {
            listViewsearchserials.setVisibility(View.GONE);
            agentscanbtn.setVisibility(View.VISIBLE);
        }
        //arrayAdapterstring.getFilter().filter(newText);
        else
        {
            listViewsearchserials.setVisibility(View.VISIBLE);
            agentscanbtn.setVisibility(View.GONE);
            String text = "%" + newText + "%";
            serialsList = db.serialsInterface().serialscount(text);
            serialsAdapter = new SerialsAdapter(this, R.layout.searchview_layout, serialsList);
            serialsAdapter.notifyDataSetChanged();
            listViewsearchserials.setAdapter(serialsAdapter);
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String batchvalue = serialsList.get(position).getSerials();
        //String batchvalue = parent.getItemAtPosition(position).toString();
        Log.d("onItemClick: ",batchvalue);
        searchView.setQuery(batchvalue,false);
        listViewsearchserials.setVisibility(View.GONE);
        agentscanbtn.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ZxingOrientResult result = ZxingOrient.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.i("SMW", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.i("SMW", "Scanned");
                mResult = result.getContents();
                searchView.setQuery(mResult,false);
                //Log.d(TAG, "onActivityResult: result"+ListElementsArrayList);
            }

        }
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            PlaceAutocomplete.clearRecentHistory(this);
            if (feature != null) {
                Log.d("mapboxlog", "onActivityResult json: "+feature.toJson());
                List<CarmenContext> featureList = feature.context();
                Log.d("mapboxlog", "onActivityResult: featre:"+featureList);
                String place_name = feature.text();
                assert featureList != null;
                if(featureList.size()>0) {
                    for (int i = 0; i < featureList.size(); i++) {
                        if (featureList.get(i).id().contains("postcode")) {
                            pincode.setText(featureList.get(i).text());
                        }
                        if(featureList.get(i).id().contains("place"))
                        {
                            subhurb.setText(featureList.get(i).text());
                            address.setText(new StringBuilder().append(place_name).append(", ").append(featureList.get(i).text()));
                        }
                        if(featureList.get(i).id().contains("region"))
                        {
                            region_1 = featureList.get(i).text();
                        }
                        if(featureList.get(i).id().contains("country"))
                        {
                            if(region_1!=null)
                            {
                                city.setText(new StringBuilder().append(region_1).append(", ").append(featureList.get(i).text()));
                            }
                            else{
                                Toast.makeText(this,"Address not found",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }

    }

    public void searchviewClickable(View view) {
        searchView.setIconified(false);
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
