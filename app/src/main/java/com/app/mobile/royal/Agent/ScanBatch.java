package com.app.mobile.royal.Agent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.app.mobile.royal.AgentBatchesGet.AppDatabase;
import com.app.mobile.royal.AgentBatchesGet.Batches;
import com.app.mobile.royal.AgentBatchesGet.BatchesAdapter;
import com.app.mobile.royal.AgentBatchesGet.BatchesInterface;
import com.app.mobile.royal.NetworkStateReceiver;
import com.app.mobile.royal.R;
import com.app.mobile.royal.Web_Services.MyApp;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Utils.Pref;
import com.app.mobile.royal.Web_Services.Web_Interface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import info.androidhive.fontawesome.FontTextView;
import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanBatch extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener , NetworkStateReceiver.NetworkStateReceiverListener {


    public EditText fname, lname, address, pincode, subhurb, idnum, city,passport,expirydate;
    public TextInputLayout idnumtext,passporttext,expirydatetext;
    public RadioGroup networkrg;
    RadioButton vodacom, telkom, cellc, mtn;
    String network, citystring;
    private static final String TAG = "ScanBatch";
    Button activatebacthes,scanbtn,addbtn;
    private ListView listViewScannedBatches,listViewsearchbatches;
    private String mResult=null;
    ArrayList< String > ListElementsArrayList;
    ArrayAdapter< String > adapter;
    Spinner regionspinner1,idspinner;
    String region,idtype;
    ArrayAdapter arrayAdapter;
    String arraylist;
    private SearchView searchView;
    AppDatabase db;
    BatchesInterface batchesInterface;
    BatchesAdapter batchesAdapter;
    ArrayAdapter<String> arrayAdapterstring;
    List<Batches> batcheslist = new ArrayList<>();
    TextView textbatches,nobatches;
    Batches batchesdata = new Batches();
    FontTextView calender;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private NetworkStateReceiver networkStateReceiver;

    /**
     * This functions initialize all the design components for their functionality to be work properly.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_batch);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        db = Room.databaseBuilder(MyApp.getContext(),AppDatabase.class,"batch")
                .allowMainThreadQueries()
                .build();

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

        calender = findViewById(R.id.calender);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ScanBatch.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
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
        cellc = findViewById(R.id.cellc);
        regionspinner1 = findViewById(R.id.regionspinner1);
        activatebacthes=(findViewById(R.id.activatebacthes));
        scanbtn = findViewById(R.id.scanbtn);
        arraylist = Pref.getBatchArray(this);
        listViewScannedBatches= findViewById(R.id.listViewScannedBatches);
        addbtn = findViewById(R.id.addbtn);
        addbtn.setVisibility(View.GONE);
        nobatches = findViewById(R.id.nobatches);
        nobatches.setVisibility(View.GONE);
        textbatches = findViewById(R.id.textbatches);
        listViewsearchbatches = findViewById(R.id.search_results_list);
        listViewsearchbatches.setTextFilterEnabled(true);
        searchView = findViewById(R.id.searchview);
        searchView.setQueryHint("Search");
        searchView.onActionViewExpanded();
        //searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(this);
        activatebacthes.setOnClickListener(this);
        scanbtn.setOnClickListener(this);
        addbtn.setOnClickListener(this);
        listViewsearchbatches.setOnItemClickListener(this);

        //batcheslist=db.batchesInterface().batchestabledata();
        //String klm[]={"123","456","7890"};
        //arrayAdapterstring=new ArrayAdapter<>(this,R.layout.searchview_layout,klm);
        /*batchesAdapter = new BatchesAdapter(this, R.layout.searchview_layout, batcheslist);
        batchesAdapter.notifyDataSetChanged();
        listViewsearchbatches.setAdapter(batchesAdapter);*/


//        Log.d("Arrayadapterlist",BatchesAdapter.arrayAdapter.toString());
        regionspinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    Toast.makeText(ScanBatch.this,"Please Enter data for all the fields",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    region = regionspinner1.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }});

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
        ListElementsArrayList = new ArrayList< String >();


        adapter = new ArrayAdapter < String >
                (ScanBatch.this, R.layout.scan_batches_layout,
                        R.id.tv_batches,
                        ListElementsArrayList);

        listViewScannedBatches.setAdapter(adapter);
    }
   /* private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            batchesInterface.batchestabledata(query).observe(ScanBatch.this, new Observer<List<Batches>>() {
                @Override
                public void onChanged(List<Batches> strings) {
                    if(strings == null)
                    {
                        return;
                    }
                    BatchesAdapter batc
                    hesAdapter = new BatchesAdapter(ScanBatch.this,R.layout.searchview_layout,strings);
                    listViewsearchbatches.setAdapter(batchesAdapter);
                }
            });

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            batchesInterface.batchestabledata(newText).observe(ScanBatch.this, new Observer<List<Batches>>() {
                @Override
                public void onChanged(List<Batches> strings) {
                    if(strings == null)
                    {
                        return;
                    }
                    BatchesAdapter batchesAdapter = new BatchesAdapter(ScanBatch.this,R.layout.searchview_layout,strings);
                    listViewsearchbatches.setAdapter(batchesAdapter);
                }
            });

            return false;
        }

    };*/


    /**
     * This function opens the camera with view bar there for scanning the batch to get the value of that batch.
     */

    private void initiateScan() {
        ZxingOrient integrator = new ZxingOrient(this);
        integrator.setVibration(true);
        integrator.setBeep(true);
        integrator.initiateScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: is called");

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addbtn)
            {
                addBatchValue();
            }
        if(v.getId()==R.id.scanbtn)
        {
            initiateScan();
        }
        if(v.getId() == R.id.activatebacthes)
        {
            String[] batches = new String[ListElementsArrayList.size()];
            for (int j = 0; j < ListElementsArrayList.size(); j++) {
                batches[j] = ListElementsArrayList.get(j);
            }

            if(idspinner.getSelectedItem().toString().equals("ID")) {
                if (fname.getText().toString().length() == 0 || lname.getText().length() == 0 || address.length() == 0 ||
                        pincode.getText().toString().length() == 0 || subhurb.getText().toString().length() == 0 || network.isEmpty()
                        || regionspinner1.getSelectedItemPosition() == 0 || listViewScannedBatches.getCount() == 0 || idnum.getText().length() == 0 || idspinner.getSelectedItem().toString() == "ID Type") {
                    Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show();
                } else {
                    BatchesScanAPI(batches,network, idnum.getText().toString(), fname.getText().toString(), lname.getText().toString(),
                            address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString(),idspinner.getSelectedItem().toString());
                }
            }
            else if(idspinner.getSelectedItem().toString().equals("PASSPORT")) {
                if (fname.getText().toString().length() == 0 || lname.getText().length() == 0 || address.length() == 0 ||
                        pincode.getText().toString().length() == 0 || subhurb.getText().toString().length() == 0 || network.isEmpty()
                        || regionspinner1.getSelectedItemPosition() == 0 || listViewScannedBatches.getCount() == 0 || passport.getText().length() == 0 || expirydate.getText().length() == 0 || idspinner.getSelectedItem().toString() == "ID Type") {
                    Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show();
                } else {
                    BatchesScanAPI1(batches,network, fname.getText().toString(), lname.getText().toString(),
                            address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString(),idspinner.getSelectedItem().toString(),passport.getText().toString(),expirydate.getText().toString());
                }
            }
/*            if (fname.getText().toString().length() == 0 || lname.getText().length() == 0 || address.length() == 0 ||
                    pincode.getText().toString().length() == 0 || subhurb.getText().toString().length() == 0 || network.isEmpty()
                    || listViewScannedBatches.getCount() == 0 || idnum.getText().length() == 0) {
                Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show();
            } else {
                BatchesScanAPI(batches,network, idnum.getText().toString(), fname.getText().toString(), lname.getText().toString(),
                        address.getText().toString(), pincode.getText().toString(), subhurb.getText().toString(), city.getText().toString());
            }*/
//            else if(regionspinner1.getSelectedItemPosition() == 0 && batches != null)
//                {
//                    Toast.makeText(ScanBatch.this,"Please Select the Region",Toast.LENGTH_SHORT).show();
//                }
//            else{
//                Toast.makeText(ScanBatch.this,"Please Scan Batches from Barcode Scanner icon",Toast.LENGTH_SHORT).show();
//            }
        }
    }

    /**
     *  This function returns the API results by getting all the values from the user & pass it into this function.
     * @param batch This parameter is the number of the that Batch. It is an array.
     * @param network What is the network of that Sim?
     * @param fname The first name of the user for the Rica registration form.
     * @param lname The last name of the user for the Rica registration form.
     * @param address The address of the user for the Rica registration form.
     * @param postalcode The postal code of the user for the Rica registration form.
     * @param suburb The suburb of the user for the Rica registration form.
     * @param funcity The suburb of the user for the Rica registration form.
     * @param idtype The type of the id of the user for the Rica registration form.
     * @param passport The Passport number of the user for the Rica registration form.
     * @param expiryDate  The expiry date of the passport of the user for the Rica registration form.
     */
    private void BatchesScanAPI1(String[] batch, String network, String fname, String lname, String address, String postalcode, String suburb, String funcity, String idtype, String passport,String expiryDate) {


        ScanPojo scanPojo = new ScanPojo();
        scanPojo.setBatches(batch);
        scanPojo.setNetwork(network);
        scanPojo.setFname(fname);
        scanPojo.setLname(lname);
        scanPojo.setAddress(address);
        scanPojo.setPostalcode(postalcode);
        scanPojo.setSuburb(suburb);
        scanPojo.setFuncity(funcity);
        scanPojo.setRegion(region);
        scanPojo.setIdtype(idtype);
        scanPojo.setPassport(passport);
        scanPojo.setExpiryDate(expiryDate);


        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);

        Call<ScanBatchesResponse> scanBatchesResponseCall = web_interface.requestScanBatches(scanPojo);
        scanBatchesResponseCall.enqueue(new Callback<ScanBatchesResponse>() {
            @Override
            public void onResponse(Call<ScanBatchesResponse> call, Response<ScanBatchesResponse> response) {
                String status = response.body().getSuccess().toString();
                Log.d(TAG, "onResponse: "+status);
                if(status.equals("true"))
                {
                    String message = response.body().getMessage();
                    for(int i=0;i<batch.length;i++)
                    {
                        db.batchesInterface().deleteBatches(batch[i]);
                    }
                    Toast.makeText(ScanBatch.this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ScanBatch.this, Agent_Mainactivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    String message = response.body().getMessage();
                    Toast.makeText(ScanBatch.this, message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ScanBatchesResponse> call, Throwable t) {

            }
        });
    }

    /**
     *  This function returns the API results by getting all the values from the user & pass it into this function.
     * @param batch This parameter is the number of the that Batch. It is an array.
     * @param network What is the network of that Sim?
     * @param fname The first name of the user for the Rica registration form.
     * @param lname The last name of the user for the Rica registration form.
     * @param address The address of the user for the Rica registration form.
     * @param postalcode The postal code of the user for the Rica registration form.
     * @param suburb The suburb of the user for the Rica registration form.
     * @param funcity The suburb of the user for the Rica registration form.
     * @param idtype The type of the id of the user for the Rica registration form.
     * @param idnum The ID number of the user for the Rica registration form.
     */

    private void BatchesScanAPI(String[] batch, String network, String idnum, String fname, String lname, String address, String postalcode, String suburb, String funcity, String idtype) {


        ScanPojo scanPojo = new ScanPojo();
        scanPojo.setBatches(batch);
        scanPojo.setNetwork(network);
        scanPojo.setFname(fname);
        scanPojo.setLname(lname);
        scanPojo.setIdnum(idnum);
        scanPojo.setAddress(address);
        scanPojo.setPostalcode(postalcode);
        scanPojo.setSuburb(suburb);
        scanPojo.setFuncity(funcity);
        scanPojo.setRegion(region);
        scanPojo.setIdtype(idtype);

        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);

        Call<ScanBatchesResponse> scanBatchesResponseCall = web_interface.requestScanBatches(scanPojo);
        scanBatchesResponseCall.enqueue(new Callback<ScanBatchesResponse>() {
            @Override
            public void onResponse(Call<ScanBatchesResponse> call, Response<ScanBatchesResponse> response) {
                String status = response.body().getSuccess().toString();
                Log.d(TAG, "onResponse: "+status);
                if(status.equals("true"))
                {
                    String message = response.body().getMessage();
                    for(int i=0;i<batch.length;i++)
                    {
                        db.batchesInterface().deleteBatches(batch[i]);
                    }
                    Toast.makeText(ScanBatch.this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ScanBatch.this, Agent_Mainactivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    String message = response.body().getMessage();
                    Toast.makeText(ScanBatch.this, message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ScanBatchesResponse> call, Throwable t) {

            }
        });

    }
    /**
     * It will add the Batch number value for the user registration RICA form if user search or enter it manually.
     */

    private void addBatchValue() {

        String textvalue = searchView.getQuery().toString();
        if(ListElementsArrayList.contains(textvalue))
        {
            //find duplicate values
            Toasty.info(this, "This value is already added", Toast.LENGTH_SHORT).show();
            searchView.setQuery("",false);
        }
        else if(listViewScannedBatches.getCount() == 5)
        {
            Toasty.info(this,"You Can't add more than 5 Batches",Toast.LENGTH_LONG).show();
            listViewsearchbatches.setVisibility(View.GONE);
            listViewScannedBatches.setVisibility(View.VISIBLE);
            activatebacthes.setVisibility(View.VISIBLE);
            searchView.setQuery("",false);
        }
        else {
            //adding only unique values
            ListElementsArrayList.add(textvalue);


            //Log.d(TAG, "onActivityResult: result"+ListElementsArrayList);
            adapter.notifyDataSetChanged();
            listViewsearchbatches.setVisibility(View.GONE);
            listViewScannedBatches.setVisibility(View.VISIBLE);
            activatebacthes.setVisibility(View.VISIBLE);
            searchView.setQuery("",false);
        }
    }

    private void BatchesAPICall(String[] batches,String region) {




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
                if(ListElementsArrayList.contains(mResult))
                {
                    //find duplicate values
                    Toasty.info(this, "This value is already added", Toast.LENGTH_SHORT).show();
                }
                else if(listViewScannedBatches.getCount() == 5)
                {
                    Toasty.info(this,"You Can't add more than 5 Batches",Toast.LENGTH_LONG).show();
                }
                else {
                    //adding only unique values
                    ListElementsArrayList.add(mResult);

                    //Log.d(TAG, "onActivityResult: result"+ListElementsArrayList);
                    adapter.notifyDataSetChanged();
                }

            }

        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(searchView.getQuery().length() == 0)
        {
                nobatches.setVisibility(View.GONE);
                listViewsearchbatches.setVisibility(View.INVISIBLE);
                listViewScannedBatches.setVisibility(View.VISIBLE);
                activatebacthes.setVisibility(View.VISIBLE);
                scanbtn.setVisibility(View.VISIBLE);
                addbtn.setVisibility(View.GONE);
            }
            //arrayAdapterstring.getFilter().filter(newText);
        else
            {
                nobatches.setVisibility(View.INVISIBLE);
                activatebacthes.setVisibility(View.INVISIBLE);
                listViewsearchbatches.setVisibility(View.VISIBLE);
                listViewScannedBatches.setVisibility(View.INVISIBLE);
                scanbtn.setVisibility(View.GONE);
                addbtn.setVisibility(View.VISIBLE);
                String text = "%" + newText + "%";
                batcheslist = db.batchesInterface().count(text);
                batchesAdapter = new BatchesAdapter(this, R.layout.searchview_layout, batcheslist);
                batchesAdapter.notifyDataSetChanged();
                listViewsearchbatches.setAdapter(batchesAdapter);
                if(listViewsearchbatches.getCount() == 0)
                {
                    nobatches.setVisibility(View.VISIBLE);
                }
            }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String batchvalue = batcheslist.get(position).getBatches();
        //String batchvalue = parent.getItemAtPosition(position).toString();
        Log.d("onItemClick: ",batchvalue);
        searchView.setQuery(batchvalue,false);
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

    public void searchviewClick(View view) {

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

