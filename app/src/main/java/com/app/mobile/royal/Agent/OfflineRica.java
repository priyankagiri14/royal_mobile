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
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mobile.royal.CredentialsCheck.CredentailsCheckResponse;
import com.app.mobile.royal.Driver.DriverSubAgentRegister.CountryModel;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import info.androidhive.fontawesome.FontTextView;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfflineRica extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, NetworkStateReceiver.NetworkStateReceiverListener {

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
    private AutoCompleteTextView searchView;
    AppDatabaseSerials db;
    SerialsInterface serialsInterface;
    SerialsAdapter serialsAdapter;
    List<Serials> serialsList = new ArrayList<>();
    TextView textserials;
    FontTextView calender;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private NetworkStateReceiver networkStateReceiver;
    private AlertDialog alertDialog;
    private static final String TAG = "OfflineRica";
    private String region_1;
    private SharedPreferences sharedPreferencesForDriver;
    private SharedPreferences sharedPreferencesForAgent;
    private String isDriver,isAgent;
    private LinearLayout ll_nationality;
    private Spinner sp_country_nationality;
    private List<CountryModel> nationality_array_list = new ArrayList<>();
    private String id_country_code;
    long delay = 2000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here
                hitSearchSerialsApi();
            }
        }
    };

    /**
     * This functions initialize all the design components for their functionality to be work properly.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_rica);
        ll_nationality = findViewById(R.id.ll_nationality);
        regionspinner = (Spinner) findViewById(R.id.regionspinner);
        sp_country_nationality = findViewById(R.id.sp_country_nationality);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        regionspinner = (Spinner) findViewById(R.id.regionspinner);

        sharedPreferencesForDriver = getSharedPreferences("Driver",0);
        sharedPreferencesForAgent = getSharedPreferences("Agent",0);
        isDriver = sharedPreferencesForDriver.getString("Driver",null);
        isAgent = sharedPreferencesForAgent.getString("Agent",null);
        Log.d(TAG, "onCreate: driver:"+isDriver+"  "+"Agent:"+isAgent);
        db = Room.databaseBuilder(MyApp.getContext(), AppDatabaseSerials.class, "serials")
                .allowMainThreadQueries()
                .build();
        // check rica
        String isRica = Pref.getIsRica(this);
        if(isRica!=null && isRica.equals("false")) {
            Log.d(TAG, "onCreate: checking rica called for first time");
            checkIfValidRica();
        }
        // init nationality spinner
        showNationalitySpinner();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (AutoCompleteTextView) findViewById(R.id.agentsearchview);
        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.lastname);
        address = findViewById(R.id.address);
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

        // address on click listener
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Offline", "onClick: call places function");
                setupAutoCompleteAddress();
            }
        });

        /**
         * implementing text watcher to autocomplete text view
         */
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //You need to remove this to run only once
                handler.removeCallbacks(input_finish_checker);


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>=4 && s.length()<7)
                {
                    Log.d(TAG, "afterTextChanged: four characters reach then hit api");
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);                }
                else {
                    Log.d(TAG, "afterTextChanged: serial length reaches 7 characters");
                }

            }
        });

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
        //searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        agentscanbtn.setOnClickListener(this);
        simallocate.setOnClickListener(this);
        listViewsearchserials = findViewById(R.id.agent_search_results_list);
        listViewsearchserials.setTextFilterEnabled(true);
        listViewsearchserials.setVisibility(View.GONE);
        cellc = findViewById(R.id.cellc);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            searchView.setText("", false);
        } else {
            simcard = bundle.getString("simcard");
            searchView.setText(simcard, false);
        }
        if (Pref.getCity(this) == null) {
            city.setText("");
        } else {
            city.setText(Pref.getCity(this));
        }

        if (Pref.getSuburb(this) == null) {
            subhurb.setText("");
        } else {
            subhurb.setText(Pref.getSuburb(this));
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
                    ll_nationality.setVisibility(View.GONE);
                }
                else if(position == 1)
                {
                    idtype = idspinner.getSelectedItem().toString();
                    idnumtext.setVisibility(View.GONE);
                    passporttext.setVisibility(View.VISIBLE);
                    expirydatetext.setVisibility(View.VISIBLE);
                    calender.setVisibility(View.VISIBLE);
                    ll_nationality.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * initalising nationality spinner
     */
    private void showNationalitySpinner() {
        Log.d(TAG, "showNationalitySpinner: called");
        String[] isoCountryCodes = Locale.getISOCountries();
        for (String countryCode : isoCountryCodes) {
            Locale locale = new Locale("", countryCode);
            String iso = locale.getISO3Country();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry();
            if (!"".equals(iso) && !"".equals(name) && !"".equals(code)) {
                nationality_array_list.add(new CountryModel(iso, code, name));
            }
            Log.d(TAG, "initViews: couuntry name:" + code);
        }
        Log.d(TAG, "initViews: countrysize:" + nationality_array_list.size());
        // now set adapter
        ArrayAdapter<CountryModel> nationality_spinner_adapter = new ArrayAdapter<>(OfflineRica.this,
                R.layout.support_simple_spinner_dropdown_item, nationality_array_list);
        sp_country_nationality.setAdapter(nationality_spinner_adapter);
        sp_country_nationality.setSelection(getIndex(sp_country_nationality,"South Africa"));
        sp_country_nationality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String country_name = nationality_array_list.get(position).getName();
                id_country_code = nationality_array_list.get(position).getCode();
                Log.d(TAG, "onItemSelected: country:code:" + id_country_code + " name:" + country_name);
                // setting country code edit text
                int dialing_code = PhoneNumberUtil.createInstance(getApplicationContext()).getCountryCodeForRegion(id_country_code+"");
                Log.d(TAG, "onItemSelected: dialing code:"+dialing_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private int getIndex(Spinner spinner, String myString) {
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        // Check for this when you set the position.
        return -1;
    }

    private void hitSearchSerialsApi() {
        Log.d(TAG, "hitSearchSerialsApi: called");
        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
        String sim_serial = searchView.getText().toString();
        Call<ResponseBody> call = web_interface.requestAgentSerialsSearch(sim_serial);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.code() == 200)
                {
                    Log.d(TAG, "onResponse: success");
                    try{
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        // now we will access the body array
                        List<String> body_serials = new ArrayList<>();
                        body_serials.clear();
                        String numbers =null;
                        JSONArray bodyArray = jsonObject.getJSONArray("body");
                        // check if body is not null
                        if(bodyArray.length()>0) {
                            for (int i=0; i<bodyArray.length();i++)
                            {
                                // traverse the array and get each element in a list
                                // numbers = bodyArray.get(i).toString();
                                Log.d(TAG, "onResponse: numers:"+numbers);
                                body_serials.add(bodyArray.get(i).toString());

                            }

                            Log.d(TAG, "onResponse: numbers:"+body_serials);

                            setAdapter(body_serials);
                        }
                        else {
                            Log.d(TAG, "onResponse: no serial found");
                            Toast.makeText(OfflineRica.this, "No serial found, please try again with different serial", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(OfflineRica.this, "Some error occurred, please try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
                Toast.makeText(OfflineRica.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setAdapter(List<String> body_serials) {
        Log.d(TAG, "setAdapter: called");
        Log.d(TAG, "setAdapter: serials:size:"+body_serials.size());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, body_serials);
        //Getting the instance of AutoCompleteTextView
        searchView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        searchView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
       /* InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/
        searchView.showDropDown();
    }


    private void checkIfValidRica() {
        Utils.showProgress(this,"Please wait...");
        Log.d(TAG, "checkIfValidRica: called");

        Web_Interface webInterface = RetrofitToken.getClient().create(Web_Interface.class);
        Call<CredentailsCheckResponse> credentailsCheckResponseCall = webInterface.requestCredentialsCheck();
        credentailsCheckResponseCall.enqueue(new Callback<CredentailsCheckResponse>() {
            @Override
            public void onResponse(Call<CredentailsCheckResponse> call, Response<CredentailsCheckResponse> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Utils.stopProgress();
                    if (response.body() != null) {
                        boolean isRica = response.body().getBody().getProfile().getIsRica();
                        if(!isRica)
                        {
                            String message = "Please fill rica details and accept terms";
                            showRicaAlertDialog(message);
                        }
                        else {
                            Log.d(TAG, "onResponse: isRica:"+isRica);
                            Pref.setIsRica(OfflineRica.this,isRica+"");

                        }
                    }
                }

                else {
                    Toasty.warning(OfflineRica.this,"Some Error Occurred").show();
                    Log.d(TAG, "onResponse: some error occured");
                    Utils.stopProgress();
                }


            }

            @Override
            public void onFailure(Call<CredentailsCheckResponse> call, Throwable t) {
                Toast.makeText(OfflineRica.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
                Utils.stopProgress();

            }
        });
    }

    private void showRicaAlertDialog(String message) {
        Log.d(TAG, "showRicaAlertDialog: called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage(message);
        builder.setCancelable(false);
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
                    sendRicaDetailsToServer(username, password, group_name);
                }

            }

        });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }



    /**
     * setup places auto complete
     */
    private void setupAutoCompleteAddress() {
        Log.d(TAG, "setupAutoCompleteAddress: called");
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

    private void sendRicaDetailsToServer(String username, String password, String group_name) {
        Utils.showProgress(this,"Please wait....");
        int user_id = Integer.parseInt(Pref.getUserId(this));
        Log.d(TAG, "sendUpdateRicaDetailsToServer: called user_id: "+user_id);

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
                        try {
                            JSONObject jObjSuccess = new JSONObject(response.body().string());
                            Log.d(TAG, jObjSuccess.getString("message"));
                            Toasty.success(getApplicationContext(), jObjSuccess.getString("message")).show();

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
                    Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
                    Toast.makeText(OfflineRica.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        || regionspinner.getSelectedItemPosition() == 0 || searchView.getText().length() == 0 || idnum.getText().length() == 0 || idspinner.getSelectedItem().toString() == "ID Type") {
                    Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show();
                } else {
                    simallocation(searchView.getText().toString(), network, idnum.getText().toString(), fname.getText().toString(), lname.getText().toString(),
                            address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString(),idspinner.getSelectedItem().toString());
                }
            }
            else if(idspinner.getSelectedItem().toString().equals("PASSPORT")) {
                if (fname.getText().toString().length() == 0 || lname.getText().length() == 0 || address.length() == 0 ||
                        pincode.getText().toString().length() == 0 || subhurb.getText().toString().length() == 0 || TextUtils.isEmpty(network)
                        || regionspinner.getSelectedItemPosition() == 0 || searchView.getText().length() == 0 || passport.getText().length() == 0 || expirydate.getText().length() == 0 || idspinner.getSelectedItem().toString() == "ID Type") {
                    Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show();
                } else {
                    simallocation1(searchView.getText().toString(), network, fname.getText().toString(), lname.getText().toString(),
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
            paramObject.put("countryCode",id_country_code.toUpperCase());
            Log.d("simalloacte data",serial+"\n"+network +"\n" +idnum+"\n"+fname+"\n"+lname +"\n" +address +"\n" +postalcode +"\n" +subhurb +"\n" +city);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"),(paramObject).toString());
            Call<Simallocatemodel> call= webInterface.simallocate(body);
            //exeuting the service
            call.enqueue(new Callback<Simallocatemodel>() {
                @Override
                public void onResponse(Call<Simallocatemodel> call, Response<Simallocatemodel> response) {
                    if(response.isSuccessful()&& response.code()==200){
                        Pref.setCity(MyApp.getContext(),city);
                        Pref.setSuburb(MyApp.getContext(),subhurb);
                        Utils.stopProgress();
                        String message=response.body().getMessage();
                        String success = response.body().getSuccess().toString();
                        if(success.equals("true"))
                        {
                            db.serialsInterface().deleteSerials(serial) ;
                            Toasty.success(getApplicationContext(),message).show();
                            Pref.setCity(MyApp.getContext(),city);
                            Pref.setSuburb(MyApp.getContext(),subhurb);
                            if(isDriver!=null)
                            {
                                Intent intent = new Intent(OfflineRica.this, Stocks_dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            if(isAgent!=null) {
                                Intent intent = new Intent(OfflineRica.this, Agent_Mainactivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
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
                        Pref.setCity(MyApp.getContext(),city);
                        Pref.setSuburb(MyApp.getContext(),subhurb);
                        Utils.stopProgress();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.d("AgentLoginActivity", jObjError.getString("message"));
                            Toasty.info(getApplicationContext(), jObjError.getString("message")).show();

                        } catch (Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage() , Toast.LENGTH_LONG).show();
                            //stopping progress
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Simallocatemodel> call, Throwable t) {
                    Log.d("offlineRIca",t.getLocalizedMessage());
                    Toasty.info(getApplicationContext(),t.getLocalizedMessage()).show();
                    Utils.stopProgress();
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

                            simallocation(searchView.getText().toString(), network, idnum.getText().toString(), fname.getText().toString(), lname.getText().toString(),
                                    address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString(),idspinner.getSelectedItem().toString());
                        }
                        else if(idspinner.getSelectedItem().toString().equals("PASSPORT")) {
                            simallocation1(searchView.getText().toString(), network, fname.getText().toString(), lname.getText().toString(),
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



    /**
     * It will add the serial number value for the user registration RICA form if user search or enter it manually.
     */
    private void addBatchValue() {

        String textvalue = searchView.getText().toString();

        //adding only unique values
        ListElementsArrayList.add(textvalue);
        batches = new String[ListElementsArrayList.size()];
        for (int j = 0; j < ListElementsArrayList.size(); j++) {
            batches[j] = ListElementsArrayList.get(j);
        }

        //Log.d(TAG, "onActivityResult: result"+ListElementsArrayList);
        adapter.notifyDataSetChanged();
        listViewsearchserials.setVisibility(View.GONE);
        searchView.setText("",false);

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
                        Pref.setCity(MyApp.getContext(),city);
                        Pref.setSuburb(MyApp.getContext(),subhurb);
                        Utils.stopProgress();
                        String message=response.body().getMessage();
                        String success = response.body().getSuccess().toString();
                        if(success.equals("true"))
                        {
                            db.serialsInterface().deleteSerials(serial) ;
                            Toasty.success(getApplicationContext(),message).show();
                            Pref.setCity(MyApp.getContext(),city);
                            Pref.setSuburb(MyApp.getContext(),subhurb);
                            if(isDriver!=null)
                            {
                                Intent intent = new Intent(OfflineRica.this, Stocks_dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            if(isAgent!=null) {
                                Intent intent = new Intent(OfflineRica.this, Agent_Mainactivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
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
                        Pref.setCity(MyApp.getContext(),city);
                        Pref.setSuburb(MyApp.getContext(),subhurb);
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
                    Log.d("simallocate",t.getLocalizedMessage());
                    Toasty.info(getApplicationContext(),t.getLocalizedMessage()).show();
                    Utils.stopProgress();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

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



   /* @Override
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
    }*/

  /*  @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String batchvalue = serialsList.get(position).getSerials();
        //String batchvalue = parent.getItemAtPosition(position).toString();
        Log.d("onItemClick: ",batchvalue);
        searchView.setQuery(batchvalue,false);
        listViewsearchserials.setVisibility(View.GONE);
        agentscanbtn.setVisibility(View.GONE);
    }*/

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
                searchView.setText(mResult,false);
                //Log.d(TAG, "onActivityResult: result"+ListElementsArrayList);
            }

        }

        /**
         * for place address
         */
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            PlaceAutocomplete.clearRecentHistory(this);

            if (feature != null) {

                Log.d(TAG, "onActivityResult json: "+feature.toJson());

                List<CarmenContext> featureList = feature.context();
                Log.d(TAG, "onActivityResult: featre:"+featureList);
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
                            else {
                                Toast.makeText(this, "Address not found, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                }

            }

        }
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