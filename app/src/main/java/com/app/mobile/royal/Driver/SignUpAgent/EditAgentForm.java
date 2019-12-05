package com.app.mobile.royal.Driver.SignUpAgent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.kyanogen.signatureview.SignatureView;
import com.app.mobile.royal.Agent.NetworkError;
import com.app.mobile.royal.Driver.Contract.ContractResponse;
import com.app.mobile.royal.Driver.DriverAttendance.model.driverattendancephoto.UploadedFile;
import com.app.mobile.royal.Driver.Driver_Dashboard.Stocks_dashboard;
import com.app.mobile.royal.FetchOneAgent.AgentBody;
import com.app.mobile.royal.FetchOneAgent.FetchOneAgent;
import com.app.mobile.royal.NetworkStateReceiver;
import com.app.mobile.royal.R;
import com.app.mobile.royal.Web_Services.Ret;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Utils.Pref;
import com.app.mobile.royal.Web_Services.Web_Interface;
import com.app.mobile.royal.utils.Utils;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import info.androidhive.fontawesome.FontTextView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAgentForm extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    View rowView;
    Spinner titlespinner, authorityspinner, warehousespinner, idspinner, doctypespinner, provincespinner;
    EditText firstname, lastname, username, idnum, passport, expirydate,
            streetno, streetname, suburb, city, postal_code,
            email, mobno, altmobno;
    TextView nametext;
    FontTextView calender;
    public TextInputLayout idnumtext, passporttext, expirydatetext;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    TableLayout tableLayout;
    String doctypestringfill;
    String authorityname, authority;
    String imagename, imagefilename, imagepath, imagecreatedat, imageupdatedat;
    int authorityId, warehouseId, imageid;
    String title, idstring, doctype, province;
    String namestring, datestring;
    Button signupbtn, uploadbtn, addviewbtn, nextbtn, previousbtn, removeviewbtn;
    Boolean enableid, enablepass;
    private String filePath;
    FontTextView cancelbtn;
    int userid = 0;
            String customerid = null;

    EditText network1, dailyrate, simcost, activationcom1, ogr1, sims1;
    EditText network2, activationcom2, ogr2, sims2;
    EditText network3, activationcom3, ogr3, sims3;
    EditText network4, activationcom4, ogr4, sims4;

    EditText signedat;

    JSONArray attachmentArray = new JSONArray();
    JSONArray attachmentArray2 = new JSONArray();

    NetworkStateReceiver networkStateReceiver;

    private static final String TAG = "SignatureActivity";
    Button buttonSign3, buttonSign6, btnClear, btnSave;
    private LinearLayout basicdetailslinear, addresslinear, contactinfolinear,
            commision1linear, commision2linear, commision3linear, commision4linear,
            doctypespinnerlinear, distributorlinear, agentlinear, dailyratelinear,signedatlinear;
    TextView basicdetailstext, addresstext,  contactinfotext, commision1text,
            commision2text, commision3text, commision4text;

    private String path;
    // Creating Separate Directory for saving Generated Images
    private static final String IMAGE_DIRECTORY = "/signdemo";
    int AUTOCOMPLETE_REQUEST_CODE, AUTOCOMPLETE_REQUEST_CODE_SUB;

    View view;
    SignatureView signatureView;
    AssetManager assetManager;
    File root;
    Bitmap bitmap;
    LinearLayout.LayoutParams layoutParams;
    LinearLayout linearLayout;
    public int count = 0, count1 = 0;
    ProgressDialog progressBar;
    int nextbtncount = 0;
    public AgentBody fetchOneAgentList;
    ArrayAdapter<CharSequence> doctypeadapter;
    String doctypeupload;

    PlacesClient placesClient;
    List<Place.Field> placeField = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
    List<String> strings = new ArrayList<>();
    List<String> warehousearray = new ArrayList<>();
    ImageView distImage, userImage;
    ArrayAdapter warehouseadapter, authorityadapter;
    ImageView symbol;

    /**
     * This method is initializing all the design components which will be used further for some functionalty.
     *
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_form);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait... while signing up the user...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        basicdetailslinear = findViewById(R.id.basicdetailslinear);
        addresslinear = findViewById(R.id.addresslinear);
        contactinfolinear = findViewById(R.id.contactinfolinear);
        signedatlinear = findViewById(R.id.signedatlinear);
        signedatlinear.setVisibility(View.GONE);
        symbol = findViewById(R.id.symbol);
        tableLayout = findViewById(R.id.table);
   /*     commision1linear = findViewById(R.id.commision1linear);
        commision2linear = findViewById(R.id.commision2linear);
        commision3linear = findViewById(R.id.commision3linear);
        commision4linear = findViewById(R.id.commision4linear);*/

        symbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(EditAgentForm.this).create();
                alertDialog.setMessage("Are You Sure you want to Exit this screen");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EditAgentForm.this,Stocks_dashboard.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        doctypespinnerlinear = findViewById(R.id.doctypespinnerlinear);
        distributorlinear = findViewById(R.id.distributorlinear);
        agentlinear = findViewById(R.id.agentlinear);
        dailyratelinear = findViewById(R.id.dailyratelinear);

        basicdetailstext = findViewById(R.id.basicdetailstext);
        addresstext = findViewById(R.id.addresstext);
        contactinfotext = findViewById(R.id.contactinfotext);
        commision1text = findViewById(R.id.commision1text);
/*        commision2text = findViewById(R.id.commision2text);
        commision3text = findViewById(R.id.commision3text);
        commision4text = findViewById(R.id.commision4text);*/

        addresslinear.setVisibility(View.GONE);
        contactinfolinear.setVisibility(View.GONE);
        tableLayout.setVisibility(View.GONE);
        commision1text.setVisibility(View.GONE);
/*        commision1linear.setVisibility(View.GONE);
        commision2linear.setVisibility(View.GONE);
        commision3linear.setVisibility(View.GONE);
        commision4linear.setVisibility(View.GONE);*/
        doctypespinnerlinear.setVisibility(View.GONE);
        distributorlinear.setVisibility(View.GONE);
        agentlinear.setVisibility(View.GONE);
        dailyratelinear.setVisibility(View.GONE);


        addresstext.setVisibility(View.GONE);
        contactinfotext.setVisibility(View.GONE);
/*        commision1text.setVisibility(View.GONE);
        commision2text.setVisibility(View.GONE);
        commision3text.setVisibility(View.GONE);
        commision4text.setVisibility(View.GONE);*/


        titlespinner = findViewById(R.id.titlespinner);
        authorityspinner = findViewById(R.id.authorityspinner);
        warehousespinner = findViewById(R.id.warehousespinner);
        idspinner = findViewById(R.id.idspinner);
        provincespinner = findViewById(R.id.provincespinner);


        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        username = findViewById(R.id.username);
        //password = findViewById(R.id.password);
        idnum = findViewById(R.id.idnum);
        passport = findViewById(R.id.passport);
        expirydate = findViewById(R.id.expirydate);
        expirydate.setClickable(false);
        expirydate.setFocusable(false);


        streetno = findViewById(R.id.streetno);
        streetname = findViewById(R.id.streetname);
        suburb = findViewById(R.id.suburb);
        suburb.setFocusable(false);
        suburb.setClickable(true);
        city = findViewById(R.id.city);
        city.setFocusable(false);
        city.setClickable(true);
        postal_code = findViewById(R.id.postal_code);


        email = findViewById(R.id.email);
        mobno = findViewById(R.id.mobno);
        altmobno = findViewById(R.id.altmobno);

        network1 = findViewById(R.id.network1);
        dailyrate = findViewById(R.id.dailyrate1);
        simcost = findViewById(R.id.simcost1);
        simcost.setText("1,00");
        simcost.setFocusable(false);
        simcost.setClickable(false);
        activationcom1 = findViewById(R.id.activatecom1);
        ogr1 = findViewById(R.id.ogr1);
        ogr1.setText("0%");
        ogr1.setFocusable(false);
        ogr1.setClickable(false);
        //cib1 = findViewById(R.id.cib1);
        sims1 = findViewById(R.id.sims1);

        network2 = findViewById(R.id.network2);
        //dailyrate2 = findViewById(R.id.dailyrate2);
      /*  simcost2 = findViewById(R.id.simcost2);
        simcost2.setText("1,00");
        simcost2.setFocusable(false);
        simcost2.setClickable(false);*/
        activationcom2 = findViewById(R.id.activatecom2);
        ogr2 = findViewById(R.id.ogr2);
        ogr2.setText("0%");
        ogr2.setFocusable(false);
        ogr2.setClickable(false);
        //cib2 = findViewById(R.id.cib2);
        sims2 = findViewById(R.id.sims2);

        network3 = findViewById(R.id.network3);
        //dailyrate3 = findViewById(R.id.dailyrate3);
      /*  simcost3 = findViewById(R.id.simcost3);
        simcost3.setText("1,00");
        simcost3.setFocusable(false);
        simcost3.setClickable(false);*/
        activationcom3 = findViewById(R.id.activatecom3);
        ogr3 = findViewById(R.id.ogr3);
        ogr3.setText("0%");
        ogr3.setFocusable(false);
        ogr3.setClickable(false);
        //cib3 = findViewById(R.id.cib3);
        sims3 = findViewById(R.id.sims3);

        network4 = findViewById(R.id.network4);
        // dailyrate4 = findViewById(R.id.dailyrate4);
       /* simcost4 = findViewById(R.id.simcost4);
        simcost4.setText("1,00");
        simcost4.setFocusable(false);
        simcost4.setClickable(false);*/
        activationcom4 = findViewById(R.id.activatecom4);
        ogr4 = findViewById(R.id.ogr4);
        ogr4.setText("0%");
        ogr4.setFocusable(false);
        ogr4.setClickable(false);
        //cib4 = findViewById(R.id.cib4);
        sims4 = findViewById(R.id.sims4);

        //nametext = findViewById(R.id.nametext);

        calender = findViewById(R.id.calender);

        signedat = findViewById(R.id.signedat);


        doctypespinner = findViewById(R.id.doctypespinner);
        uploadbtn = findViewById(R.id.uploadbtn);
        addviewbtn = findViewById(R.id.addviewbtn);

        distImage = (ImageView) findViewById(R.id.dist_sign_image);
        userImage = (ImageView) findViewById(R.id.user_sign_image);

        cancelbtn = findViewById(R.id.cancel);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String doc = doctypespinner.getSelectedItem().toString();
                if (doc.equals("Choose DOC_TYPE")) {

                } else {
                    for (int i = 0; i < attachmentArray2.length(); i++) {
                        try {
                            if (attachmentArray2.getJSONObject(i).get("type").equals(doc)) {
                                attachmentArray2.remove(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        signupbtn = findViewById(R.id.signupbtn);
        nextbtn = findViewById(R.id.nextbtn);
        previousbtn = findViewById(R.id.previousbtn);
        signupbtn.setVisibility(View.GONE);
        previousbtn.setVisibility(View.GONE);


        linearLayout = (LinearLayout) findViewById(R.id.doctypelinear);
        authorityAPICall();
        warehouseAPICall();
        String id = null;
        Intent intent = this.getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
        }
        if (id != null) {
            int idint = Integer.parseInt(id);
            Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
            Call<FetchOneAgent> responseAuthorityCall = web_interface.requestfetchoneagent(idint);
            responseAuthorityCall.enqueue(new Callback<FetchOneAgent>() {
                @Override
                public void onResponse(Call<FetchOneAgent> call, Response<FetchOneAgent> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        List<Body> bodyList = new ArrayList<>();
                        if (response.body() != null) {
                            fetchOneAgentList = response.body().getBody();
                            userid = fetchOneAgentList.getId();
                            customerid = fetchOneAgentList.getCustomerId();
                            String titlestring = fetchOneAgentList.getTitle();
                            String fname = fetchOneAgentList.getFirstName();
                            String lname = fetchOneAgentList.getLastName();
                            String usernamestring = fetchOneAgentList.getUsername();
                            String idstring = fetchOneAgentList.getProfile().getIdType();
                            String streetnostring = fetchOneAgentList.getAddress().getStreetNo();
                            String streetnamestring = fetchOneAgentList.getAddress().getStreetName();
                            String citystring = fetchOneAgentList.getAddress().getCity();
                            String suburbstring = fetchOneAgentList.getAddress().getSuburb();
                            String provincestring = fetchOneAgentList.getAddress().getProvince();
                            String postal_codestring = fetchOneAgentList.getAddress().getPostalCode();
                            String emailstring = fetchOneAgentList.getProfile().getEmail();
                            String mobnostring = fetchOneAgentList.getProfile().getMobileNo();
                            String altmobnostring = fetchOneAgentList.getProfile().getAltMobileNo();
                            String dailyratestring = String.valueOf(fetchOneAgentList.getCommissions().get(0).getDailyRate());

                            String actcom1 = String.valueOf(fetchOneAgentList.getCommissions().get(0).getActivationCom());
                            String actcom2 = String.valueOf(fetchOneAgentList.getCommissions().get(1).getActivationCom());
                            String actcom3 = String.valueOf(fetchOneAgentList.getCommissions().get(2).getActivationCom());
                            String actcom4 = String.valueOf(fetchOneAgentList.getCommissions().get(3).getActivationCom());

                            String sims1string = String.valueOf(fetchOneAgentList.getCommissions().get(0).getSims());
                            String sims2string = String.valueOf(fetchOneAgentList.getCommissions().get(1).getSims());
                            String sims3string = String.valueOf(fetchOneAgentList.getCommissions().get(2).getSims());
                            String sims4string = String.valueOf(fetchOneAgentList.getCommissions().get(3).getSims());

                            dailyrate.setText(dailyratestring);
                            activationcom1.setText(actcom1);
                            activationcom2.setText(actcom2);
                            activationcom3.setText(actcom3);
                            activationcom4.setText(actcom4);

                            sims1.setText(sims1string);
                            sims2.setText(sims2string);
                            sims3.setText(sims3string);
                            sims4.setText(sims4string);

                            if (fetchOneAgentList.getAttachments().size() != 0) {
                                if(!fetchOneAgentList.getAttachments().get(0).getType().equals("USER_SIGNATURE")||
                                !fetchOneAgentList.getAttachments().get(0).getType().equals("DISTRIBUTOR_SIGNATURE")||
                                        !fetchOneAgentList.getAttachments().get(0).getType().equals("CONTRACT")) {
                                    doctypestringfill = fetchOneAgentList.getAttachments().get(0).getType();
                                }ArrayAdapter<CharSequence> doctypeadapter = ArrayAdapter.createFromResource(EditAgentForm.this, R.array.doctype_spinner, android.R.layout.simple_spinner_item);
                                doctypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                doctypespinner.setAdapter(doctypeadapter);

                                if (doctypestringfill != null && doctypestringfill.equals("ID_PROOF")) {
                                    doctypestringfill = "ID_COPY";
                                }
                                if (doctypestringfill != null) {
                                    int spinnerPosition = doctypeadapter.getPosition(doctypestringfill);
                                    doctypespinner.setSelection(spinnerPosition);
                                    Integer id = fetchOneAgentList.getAttachments().get(0).getId();
                                    String name = fetchOneAgentList.getAttachments().get(0).getName();
                                    String file_name = fetchOneAgentList.getAttachments().get(0).getFileName();
                                    String type = fetchOneAgentList.getAttachments().get(0).getType();

                                    try {
                                        JSONObject attachmentobject = new JSONObject();
                                        attachmentobject.put("id",id);
                                        attachmentobject.put("name",name);
                                        attachmentobject.put("fileName",file_name);
                                        attachmentobject.put("type",type);
                                        attachmentArray2.put(0,attachmentobject);
                                        Log.d("AttachmentArray: ","Attach1");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            /*Log.d("Attachment: ",doctypestringfill);*/

                            for (int i = 1; i < (fetchOneAgentList.getAttachments().size()) - 4; i++) {

                                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                rowView = inflater.inflate(R.layout.doctypelinearlayout, null);
                                rowView.setId(View.generateViewId());
                                // Add the new row before the add field button.
                                linearLayout.addView(rowView, linearLayout.getChildCount() - 1);

                                Spinner doctypespinner = (Spinner) rowView.findViewById(R.id.doctypespinner);
                                doctypespinner.setId(View.generateViewId());

                                if(!fetchOneAgentList.getAttachments().get(i).getType().equals("USER_SIGNATURE")||
                                        !fetchOneAgentList.getAttachments().get(i).getType().equals("DISTRIBUTOR_SIGNATURE")||
                                        !fetchOneAgentList.getAttachments().get(i).getType().equals("CONTRACT")) {
                                    doctypestringfill = fetchOneAgentList.getAttachments().get(i).getType();
                                }
                                doctypeadapter = ArrayAdapter.createFromResource(EditAgentForm.this, R.array.doctype_spinner, android.R.layout.simple_spinner_item);
                                doctypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                doctypespinner.setAdapter(doctypeadapter);

                                if (doctypestringfill != null && doctypestringfill.equals("ID_PROOF")) {
                                    doctypestringfill = "ID_COPY";
                                }
                                if (doctypestringfill != null) {
                                    int spinnerPosition = doctypeadapter.getPosition(doctypestringfill);
                                    doctypespinner.setSelection(spinnerPosition);
                                    Integer id = fetchOneAgentList.getAttachments().get(i).getId();
                                    String name = fetchOneAgentList.getAttachments().get(i).getName();
                                    String file_name = fetchOneAgentList.getAttachments().get(i).getFileName();
                                    String type = fetchOneAgentList.getAttachments().get(i).getType();


                                    try {
                                        JSONObject attachmentobject = new JSONObject();
                                        attachmentobject.put("id",id);
                                        attachmentobject.put("name",name);
                                        attachmentobject.put("fileName",file_name);
                                        attachmentobject.put("type",type);
                                        attachmentArray2.put(i,attachmentobject);
                                        Log.d("AttachmentArray: ","Attach");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                Button uploadbtn = (Button) rowView.findViewById(R.id.uploadbtn);
                                uploadbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (doctypespinner.getSelectedItemPosition() == 0) {
                                            Toast.makeText(EditAgentForm.this, "Select Document Type", Toast.LENGTH_SHORT).show();
                                        } else {
                                            doctypeupload = doctypespinner.getSelectedItem().toString();
                                            if(doctypeupload.equals("ID_COPY"))
                                            {
                                                doctypeupload = "ID_PROOF";
                                            }
                                            getImageFromCamera();
                                        }
                                    }
                                });
                            }
                            for (int j = 0; j < fetchOneAgentList.getAttachments().size(); j++) {
                                if (fetchOneAgentList.getAttachments().get(j).getType().equals("DISTRIBUTOR_SIGNATURE")) {
                                    Integer file_id = fetchOneAgentList.getAttachments().get(j).getId();
                                    String name = fetchOneAgentList.getAttachments().get(j).getName();
                                    String file_name = fetchOneAgentList.getAttachments().get(j).getFileName();
                                    String type = fetchOneAgentList.getAttachments().get(j).getType();

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("id",file_id);
                                        jsonObject.put("name",name);
                                        jsonObject.put("fileName",file_name);
                                        jsonObject.put("type",type);
                                        attachmentArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
                                    Call<ResponseBody> responseBodyCall = web_interface.requestImagefromserver(file_id);
                                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            if (response.body() != null) {
                                                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                                Log.d("bitmap", "onResponse: " + bitmap);
                                                distImage.setImageBitmap(bitmap);
                                                distImage.setVisibility(View.VISIBLE);
                                            }
                                            // URI uri = new URI();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                        }
                                    });
                                }

                                if (fetchOneAgentList.getAttachments().get(j).getType().equals("USER_SIGNATURE")) {
                                    Integer file_id = fetchOneAgentList.getAttachments().get(j).getId();
                                    String name = fetchOneAgentList.getAttachments().get(j).getName();
                                    String file_name = fetchOneAgentList.getAttachments().get(j).getFileName();
                                    String type = fetchOneAgentList.getAttachments().get(j).getType();

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("id",file_id);
                                        jsonObject.put("name",name);
                                        jsonObject.put("fileName",file_name);
                                        jsonObject.put("type",type);
                                        attachmentArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
                                    Call<ResponseBody> responseBodyCall = web_interface.requestImagefromserver(file_id);
                                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            if (response.body() != null) {
                                                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                                Log.d("bitmap", "onResponse: " + bitmap);
                                                userImage.setImageBitmap(bitmap);
                                                userImage.setVisibility(View.VISIBLE);
                                            }
                                            // URI uri = new URI();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                        }
                                    });
                                }
                            }
/*    String doctypestring = fetchOneAgentList.getAttachments().get(i).getType();
                                addviewbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        rowView = inflater.inflate(R.layout.doctypelinearlayout, null);
                                        rowView.setId(View.generateViewId());
                                        // Add the new row before the add field button.
                                        linearLayout.addView(rowView, linearLayout.getChildCount() - 1);

                                        final Spinner doctypespinner = (Spinner)rowView.findViewById(R.id.doctypespinner);
                                        doctypespinner.setId(View.generateViewId());
                                        doctypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                if(position == 0)
                                                {

                                                }
                                                else
                                                {
                                                    doctype = doctypespinner.getSelectedItem().toString();
                                                    if(doctype.equals("ID_COPY"))
                                                    {
                                                        doctype = "ID_PROOF";
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                        Button uploadbtn = (Button)rowView.findViewById(R.id.uploadbtn);
                                        uploadbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(doctypespinner.getSelectedItemPosition() == 0)
                                                {
                                                    Toast.makeText(SignUpAgent.this, "Select Document Type", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    getImageFromCamera();
                                                }
                                            }
                                        });
                                    }
                                });

                            }
                            */
                            ArrayAdapter<CharSequence> titleadapter = ArrayAdapter.createFromResource(EditAgentForm.this, R.array.title_spinner, android.R.layout.simple_spinner_item);
                            titleadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            titlespinner.setAdapter(titleadapter);
                            if (titlestring != null) {
                                int spinnerPosition = titleadapter.getPosition(titlestring);
                                titlespinner.setSelection(spinnerPosition);
                            }

                            /*ArrayAdapter agentadapter = new ArrayAdapter(SignUpAgent.this,android.R.layout.simple_spinner_dropdown_item,
                                    strings);
                            authorityspinner.setAdapter(agentadapter);*/
                            String agentstring = fetchOneAgentList.getAuthority().getName();
                            if (agentstring != null && strings.size() != 0) {
                                int spinnerPosition = authorityadapter.getPosition(agentstring);
                                authorityspinner.setSelection(spinnerPosition);
                            }

                         /*   ArrayAdapter adapter = new ArrayAdapter(SignUpAgent.this,android.R.layout.simple_spinner_dropdown_item,
                                    warehousearray);
                            warehousespinner.setAdapter(adapter);*/

                            String warehousestring = fetchOneAgentList.getWarehouse().getName();
                            if (warehousestring != null && warehousearray.size() != 0) {
                                int spinnerPosition = warehouseadapter.getPosition(warehousestring);
                                warehousespinner.setSelection(spinnerPosition);
                            }

                            ArrayAdapter<CharSequence> idadapter = ArrayAdapter.createFromResource(EditAgentForm.this, R.array.id_spinner, android.R.layout.simple_spinner_item);
                            idadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            idspinner.setAdapter(idadapter);
                            if (idstring != null) {
                                int spinnerPosition = idadapter.getPosition(idstring);
                                idspinner.setSelection(spinnerPosition);
                                if (idspinner.getSelectedItem().equals("ID")) {
                                    //idstring = idspinner.getSelectedItem().toString();
                                    idnumtext.setVisibility(View.VISIBLE);
                                    passporttext.setVisibility(View.GONE);
                                    expirydatetext.setVisibility(View.GONE);
                                    calender.setVisibility(View.GONE);
                                    String idtext = fetchOneAgentList.getProfile().getIdNo();
                                    idnum.setText(idtext);
                                }
                                if (idspinner.getSelectedItem().equals("PASSPORT")) {
                                    idnumtext.setVisibility(View.GONE);
                                    passporttext.setVisibility(View.VISIBLE);
                                    expirydatetext.setVisibility(View.VISIBLE);
                                    calender.setVisibility(View.VISIBLE);
                                    String passportText = fetchOneAgentList.getProfile().getPassportNo();
                                    passport.setText(passportText);
                                    String expiryDateText = fetchOneAgentList.getProfile().getPassportExpiryDate();
                                    expirydate.setText(expiryDateText);
                                }
                            }

                            ArrayAdapter<CharSequence> provinceadapter = ArrayAdapter.createFromResource(EditAgentForm.this, R.array.province_spinner, android.R.layout.simple_spinner_item);
                            provinceadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            provincespinner.setAdapter(provinceadapter);
                            if (provincestring != null) {
                                int spinnerPosition = provinceadapter.getPosition(provincestring);
                                provincespinner.setSelection(spinnerPosition);
                            }


                            firstname.setText(fname);
                            lastname.setText(lname);
                            username.setText(usernamestring);
                            streetno.setText(streetnostring);
                            streetname.setText(streetnamestring);
                            city.setText(citystring);
                            suburb.setText(suburbstring);
                            postal_code.setText(postal_codestring);
                            email.setText(emailstring);
                            mobno.setText(mobnostring);
                            altmobno.setText(altmobnostring);

                        }
                    }
                }

                @Override
                public void onFailure(Call<FetchOneAgent> call, Throwable t) {
                    Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        addviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.doctypelinearlayout, null);
                rowView.setId(View.generateViewId());
                // Add the new row before the add field button.
                linearLayout.addView(rowView, linearLayout.getChildCount() - 1);

                final Spinner doctypespinner = (Spinner) rowView.findViewById(R.id.doctypespinner);
                doctypespinner.setId(View.generateViewId());
                doctypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {

                        } else {
                            doctype = doctypespinner.getSelectedItem().toString();
                            if (doctype.equals("ID_COPY")) {
                                doctype = "ID_PROOF";
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                Button uploadbtn = (Button) rowView.findViewById(R.id.uploadbtn);
                uploadbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (doctypespinner.getSelectedItemPosition() == 0) {
                            Toast.makeText(EditAgentForm.this, "Select Document Type", Toast.LENGTH_SHORT).show();
                        } else {
                            getImageFromCamera();
                        }
                    }
                });
            }
        });


        idnumtext = findViewById(R.id.idnumtext);
        passporttext = findViewById(R.id.passporttext);
        expirydatetext = findViewById(R.id.expirydatetext);
        // namestring = Pref.getFirstName(this);
        //  nametext.setText(namestring);

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditAgentForm.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                if (month < 10 && dayOfMonth < 10) {

                    String monthstring = "0" + month;
                    String dayofmonthString = "0" + dayOfMonth;
                    datestring = year + "-" + monthstring + "-" + dayofmonthString + "T00:00:00.000Z";
                } else if (month < 10 && dayOfMonth > 10) {

                    String monthstring = "0" + month;
                    datestring = year + "-" + monthstring + "-" + dayOfMonth + "T00:00:00.000Z";
                } else if (month > 10 && dayOfMonth < 10) {
                    String dayofmonthString = "0" + dayOfMonth;
                    datestring = year + "-" + month + "-" + dayofmonthString + "T00:00:00.000Z";
                } else {
                    datestring = year + "-" + month + "-" + dayOfMonth + "T00:00:00.000Z";
                }
                expirydate.setText(date);
            }
        };

        titlespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else {
                    title = titlespinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        authorityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //authority = authorityspinner.getSelectedItem().toString();
                Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
                Call<ResponseAuthority> responseAuthorityCall = web_interface.requestResponseAuthority();
                responseAuthorityCall.enqueue(new Callback<ResponseAuthority>() {
                    @Override
                    public void onResponse(Call<ResponseAuthority> call, Response<ResponseAuthority> response) {
                        if (response.isSuccessful() && response.code() == 200) {
                            List<Body> bodyList = new ArrayList<>();
                            if (response.body() != null) {
                                bodyList = response.body().getBody();
                                authorityId = bodyList.get(position).getId();
                                authorityname = bodyList.get(position).getName();
                                authority = bodyList.get(position).getAuthority();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseAuthority> call, Throwable t) {
                        Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        warehousespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    warehouse = warehousespinner.getSelectedItem().toString();
                Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
                Call<WarehouseResponse> warehouseResponseCall = web_interface.requestWarehouseResponse();
                warehouseResponseCall.enqueue(new Callback<WarehouseResponse>() {
                    @Override
                    public void onResponse(Call<WarehouseResponse> call, Response<WarehouseResponse> response) {
                        if (response.isSuccessful() && response.code() == 200) {
                            List<WarehouseBody> warehouseBodyList = new ArrayList<>();

                            if (response.body() != null) {
                                warehouseBodyList = response.body().getBody();
                                warehouseId = warehouseBodyList.get(position).getId();

                            }
                        } else {
                            Log.d("onResponse: ", "ERROR");
                            Toast.makeText(EditAgentForm.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WarehouseResponse> call, Throwable t) {
                        Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        idspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    idstring = idspinner.getSelectedItem().toString();
                    idnumtext.setVisibility(View.VISIBLE);
                    passporttext.setVisibility(View.GONE);
                    expirydatetext.setVisibility(View.GONE);
                    calender.setVisibility(View.GONE);
                } else if (position == 1) {
                    idstring = idspinner.getSelectedItem().toString();
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

        provincespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else {
                    province = provincespinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        doctypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else {
                    doctype = doctypespinner.getSelectedItem().toString();
                    if (doctype.equals("ID_COPY")) {
                        doctype = "ID_PROOF";
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doctypespinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(EditAgentForm.this, "Select Document Type", Toast.LENGTH_SHORT).show();
                } else {
                    getImageFromCamera();
                }
            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nextbtncount == 0) {
                    if (idspinner.getSelectedItem().toString().equals("ID") || idspinner.getSelectedItem().toString().equals("ID Type")) {
                        if (titlespinner.getSelectedItem().toString().equals("Title")) {
                            Toast.makeText(EditAgentForm.this, "Choose Title", Toast.LENGTH_SHORT).show();
                        } else if (firstname.getText().toString().length() == 0) {
                            firstname.setError(" First Name Required");
                            firstname.requestFocus();
                        } else if (lastname.getText().toString().length() == 0) {
                            lastname.setError("Last Name Required");
                            lastname.requestFocus();
                        } else if (username.getText().toString().length() == 0) {
                            username.setError("Username Required");
                            username.requestFocus();
                        } /*else if (password.getText().toString().length() == 0) {
                            password.setError("Password Required");
                            password.requestFocus();
                        } */else if (idspinner.getSelectedItem().equals("ID Type")) {
                            Toast.makeText(EditAgentForm.this, "Choose ID Type", Toast.LENGTH_SHORT).show();
                        }
                    /*else if(idspinner.getSelectedItem().equals("ID"))
                    {*/
                        else if (idnum.getText().toString().length() == 0) {
                            idnum.setError("ID Number Required");
                            idnum.requestFocus();
                            //}
                        } else {
                            nextbtncount++;
                            previousbtn.setVisibility(View.VISIBLE);
                            addresslinear.setVisibility(View.VISIBLE);
                            addresstext.setVisibility(View.VISIBLE);
                            basicdetailslinear.setVisibility(View.GONE);
                            basicdetailstext.setVisibility(View.GONE);
                        }
                    } else if (idspinner.getSelectedItem().toString().equals("PASSPORT")) {
                        if (titlespinner.getSelectedItem().toString().equals("Title")) {
                            Toast.makeText(EditAgentForm.this, "Choose Title", Toast.LENGTH_SHORT).show();
                        } else if (firstname.getText().toString().length() == 0) {
                            firstname.setError("First Name Required");
                            firstname.requestFocus();
                        } else if (lastname.getText().toString().length() == 0) {
                            lastname.setError("Last Name Required");
                            lastname.requestFocus();
                        } else if (username.getText().toString().length() == 0) {
                            username.setError("Username Required");
                            username.requestFocus();
                        } /*else if (password.getText().toString().length() == 0) {
                            password.setError("Password Required");
                            password.requestFocus();
                        }*/ else if (idspinner.getSelectedItem().equals("ID Type")) {
                            Toast.makeText(EditAgentForm.this, "Choose ID Type", Toast.LENGTH_SHORT).show();
                        } else if (passport.getText().toString().length() == 0) {
                            passport.setError("Passport Number Required");
                            passport.requestFocus();
                        } else if (expirydate.getText().toString().length() == 0) {
                            Toast.makeText(EditAgentForm.this, "Select Passport Expiry Date from Calender", Toast.LENGTH_SHORT).show();
                        } else {
                            nextbtncount++;
                            previousbtn.setVisibility(View.VISIBLE);
                            addresslinear.setVisibility(View.VISIBLE);
                            addresstext.setVisibility(View.VISIBLE);
                            basicdetailslinear.setVisibility(View.GONE);
                            basicdetailstext.setVisibility(View.GONE);
                        }
                    }
                } else if (nextbtncount == 1) {
                    if (streetno.getText().toString().length() == 0) {
                        streetno.setError("Street No. Required");
                        streetno.requestFocus();
                    } else if (streetname.getText().toString().length() == 0) {
                        streetname.setError("Street name Required");
                        streetname.requestFocus();
                    } else if (suburb.getText().toString().length() == 0) {
                        suburb.setError("Suburb Required");
                        suburb.requestFocus();
                    } else if (city.getText().toString().length() == 0) {
                        city.setError("City Required");
                        city.requestFocus();
                    } else if (provincespinner.getSelectedItem().equals("Province")) {
                        Toast.makeText(EditAgentForm.this, "Select Province!", Toast.LENGTH_SHORT).show();
                    } else if (postal_code.getText().toString().length() == 0) {
                        postal_code.setError("Postal Code Required");
                        postal_code.requestFocus();
                    } else {
                        nextbtncount++;
                        addresslinear.setVisibility(View.GONE);
                        addresstext.setVisibility(View.GONE);
                        contactinfolinear.setVisibility(View.VISIBLE);
                        contactinfotext.setVisibility(View.VISIBLE);
                    }
                } else if (nextbtncount == 2) {
                    /*if(ricauser.getText().toString().length() == 0)
                {
                    ricauser.setError("Rica User Required");
                    ricauser.requestFocus();
                }
                else if (ricapassword.getText().toString().length() == 0)
                {
                    ricapassword.setError("Rica Passsword Required");
                    ricapassword.requestFocus();
                }
                else if (ricagroup.getText().toString().length() == 0)
                {
                    ricagroup.setError("Rica Group Required");
                    ricagroup.requestFocus();
                }
                else*/
                    if (email.getText().toString().length() == 0) {
                        email.setError("Email Required");
                        email.requestFocus();
                    } else if (mobno.getText().toString().length() == 0) {
                        mobno.setError("Mobile No. Required");
                        mobno.requestFocus();
                    }
                    else if(mobno.getText().toString().length()!=10)
                    {
                        mobno.setError("Mobile No. is Incorrect");
                        mobno.requestFocus();
                    }
                    else if(altmobno.getText().toString().length()!=10 && altmobno.getText().toString().length()!=0)
                    {
                        altmobno.setError("Mobile No. is Incorrect");
                        altmobno.requestFocus();
                    }
                /*else if (altmobno.getText().toString().length() == 0)
                {
                    altmobno.setError("Alternative No. Required");
                    altmobno.requestFocus();
                }*/
                    else {
                        nextbtncount++;
                        contactinfolinear.setVisibility(View.GONE);
                        contactinfotext.setVisibility(View.GONE);
                        dailyratelinear.setVisibility(View.VISIBLE);
                        tableLayout.setVisibility(View.VISIBLE);
                        commision1text.setVisibility(View.VISIBLE);
    /*                    commision1linear.setVisibility(View.VISIBLE);
                        commision1text.setVisibility(View.VISIBLE);
                        commision2linear.setVisibility(View.VISIBLE);
                        commision2text.setVisibility(View.VISIBLE);
                        commision3linear.setVisibility(View.VISIBLE);
                        commision3text.setVisibility(View.VISIBLE);
                        commision3linear.setVisibility(View.VISIBLE);
                        commision3text.setVisibility(View.VISIBLE);
                        commision4linear.setVisibility(View.VISIBLE);
                        commision4text.setVisibility(View.VISIBLE);*/
                    }
                } else if (nextbtncount == 3) {
                    if (/*dailyrate.getText().length() == 0 && simcost.getText().length() == 0
                                &&*/activationcom1.getText().length() == 0 /*&&ogr1.getText().length() == 0*/
                            /*&&cib1.getText().length() == 0*/ && sims1.getText().length() == 0) {
                        //dailyrate.setText("0");
                        //simcost1.setText("0");
                        activationcom1.setError("Activation Commision Required");
                        activationcom1.requestFocus();
                        //ogr1.setText("0");
                        //cib1.setText("0");
                        sims1.setText("0");
                    }
                    if (/*dailyrate2.getText().length() == 0 && simcost2.getText().length() == 0
                                &&*/activationcom2.getText().length() == 0 /*&&ogr2.getText().length() == 0*/
                            /*&&cib2.getText().length() == 0*/ && sims2.getText().length() == 0) {
                        //dailyrate2.setText("0");
                        //simcost2.setText("0");
                        activationcom2.setError("Activation Commision Required");
                        activationcom2.requestFocus();
                        //ogr2.setText("0");
                        //cib2.setText("0");
                        sims2.setText("0");
                    }
                    if (/*dailyrate3.getText().length() == 0 && simcost3.getText().length() == 0
                                   &&*/activationcom3.getText().length() == 0 /*&&ogr3.getText().length() == 0*/
                            /*&&cib3.getText().length() == 0*/ && sims3.getText().length() == 0) {
                        //dailyrate3.setText("0");
                        //simcost3.setText("0");
                        activationcom3.setError("Activation Commision Required");
                        activationcom3.requestFocus();
                        //ogr3.setText("0");
                        //cib3.setText("0");
                        sims3.setText("0");
                    }
                    if (/*dailyrate4.getText().length() == 0 && simcost4.getText().length() == 0
                                   &&*/activationcom4.getText().length() == 0 /*&&ogr4.getText().length() == 0*/
                            /*&&cib4.getText().length() == 0*/ && sims4.getText().length() == 0) {
                        //dailyrate4.setText("0");
                        //simcost4.setText("0");
                        activationcom4.setError("Activation Commision Required");
                        activationcom4.requestFocus();
                        ogr4.setText("0");
                        //cib4.setText("0");
                        sims4.setText("0");
                    }

                    if (simcost.getText().length() == 0
                            || activationcom1.getText().length() == 0 || ogr1.getText().length() == 0
                            /*||cib1.getText().length() == 0*/ /*|| sims1.getText().length() == 0*/
                            /*|| simcost2.getText().length() == 0*/
                            || activationcom2.getText().length() == 0 || ogr2.getText().length() == 0
                            /*||cib2.getText().length() == 0 *//*|| sims2.getText().length() == 0*/
                            /*|| simcost3.getText().length() == 0*/
                            || activationcom3.getText().length() == 0 || ogr3.getText().length() == 0
                            /*||cib3.getText().length() == 0*/ /*|| sims3.getText().length() == 0*/
                            /*|| simcost4.getText().length() == 0*/
                            || activationcom4.getText().length() == 0 || ogr4.getText().length() == 0
                        /*||cib4.getText().length() == 0*/ /*|| sims4.getText().length() == 0*/) {
                     /*   if (dailyrate.getText().length() == 0) {
                            dailyrate.setError("Daily Rate Required");
                            dailyrate.requestFocus();
                        } else */
                        if (simcost.getText().length() == 0) {
                            simcost.requestFocus();
                        } else if (activationcom1.getText().length() == 0) {
                            activationcom1.setError("Activation Commision Required");
                            activationcom1.requestFocus();
                        } else if (ogr1.getText().length() == 0) {
                            ogr1.requestFocus();
                        } /*else if (cib1.getText().length() == 0) {
                            cib1.setError("CIB Required");
                            cib1.requestFocus();
                        }*/ else if (sims1.getText().length() == 0) {
                            sims1.setText("0");
                        }/* else if (dailyrate2.getText().length() == 0) {
                            dailyrate2.requestFocus();
                        } else if (simcost2.getText().length() == 0) {
                            simcost2.requestFocus();
                        } */ else if (activationcom2.getText().length() == 0) {
                            activationcom2.setError("Activation Commision Required");
                            activationcom2.requestFocus();
                        } else if (ogr2.getText().length() == 0) {
                            ogr2.requestFocus();
                        } /*else if (cib2.getText().length() == 0) {
                            cib2.setError("CIB Required");
                            cib2.requestFocus();
                        }*/ else if (sims2.getText().length() == 0) {
                            sims2.setText("0");
                        } /*else if (dailyrate3.getText().length() == 0) {
                            dailyrate3.requestFocus();
                        } else if (simcost3.getText().length() == 0) {
                            simcost3.requestFocus();
                        }*/ else if (activationcom3.getText().length() == 0) {
                            activationcom3.setError("Activation Commision Required");
                            activationcom3.requestFocus();
                        } else if (ogr3.getText().length() == 0) {
                            ogr3.requestFocus();
                        } /*else if (cib3.getText().length() == 0) {
                            cib3.setError("CIB Required");
                            cib3.requestFocus();
                        } */ else if (sims3.getText().length() == 0) {
                            sims3.setText("0");
                        } /*else if (dailyrate4.getText().length() == 0) {
                            dailyrate4.requestFocus();
                        } else if (simcost4.getText().length() == 0) {
                            simcost4.requestFocus();
                        }*/ else if (activationcom4.getText().length() == 0) {
                            activationcom4.setError("Activation Commision Required");
                            activationcom4.requestFocus();
                        } else if (ogr4.getText().length() == 0) {
                            ogr4.requestFocus();
                        } /*else if (cib4.getText().length() == 0) {
                            cib4.setError("CIB Required");
                            cib4.requestFocus();
                        }*/ else if (sims4.getText().length() == 0) {
                            sims4.setText("0");
                        }
                    } else {
                        nextbtn.setVisibility(View.GONE);
                        signupbtn.setVisibility(View.VISIBLE);
                        signedatlinear.setVisibility(View.VISIBLE);
                        doctypespinnerlinear.setVisibility(View.VISIBLE);
                        distributorlinear.setVisibility(View.VISIBLE);
                        agentlinear.setVisibility(View.VISIBLE);
                        dailyratelinear.setVisibility(View.GONE);
                        tableLayout.setVisibility(View.GONE);
                        commision1text.setVisibility(View.GONE);
/*
                        commision1linear.setVisibility(View.GONE);
                        commision1text.setVisibility(View.GONE);
                        commision2linear.setVisibility(View.GONE);
                        commision2text.setVisibility(View.GONE);
                        commision3linear.setVisibility(View.GONE);
                        commision3text.setVisibility(View.GONE);
                        commision3linear.setVisibility(View.GONE);
                        commision3text.setVisibility(View.GONE);
                        commision4linear.setVisibility(View.GONE);
                        commision4text.setVisibility(View.GONE);
*/
                    }
                }

            }
        });
        previousbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nextbtncount == 3) {
                    signupbtn.setVisibility(View.GONE);
                    nextbtn.setVisibility(View.VISIBLE);
                    nextbtncount--;
                    doctypespinnerlinear.setVisibility(View.GONE);
                    distributorlinear.setVisibility(View.GONE);
                    agentlinear.setVisibility(View.GONE);
                    signedatlinear.setVisibility(View.GONE);
                    dailyratelinear.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.VISIBLE);
                    commision1text.setVisibility(View.VISIBLE);
/*                    commision1linear.setVisibility(View.VISIBLE);
                    commision1text.setVisibility(View.VISIBLE);
                    commision2linear.setVisibility(View.VISIBLE);
                    commision2text.setVisibility(View.VISIBLE);
                    commision3linear.setVisibility(View.VISIBLE);
                    commision3text.setVisibility(View.VISIBLE);
                    commision3linear.setVisibility(View.VISIBLE);
                    commision3text.setVisibility(View.VISIBLE);
                    commision4linear.setVisibility(View.VISIBLE);
                    commision4text.setVisibility(View.VISIBLE);*/
                } else if (nextbtncount == 2) {
                    nextbtncount--;
  /*                  commision1linear.setVisibility(View.GONE);
                    commision1text.setVisibility(View.GONE);
                    commision2linear.setVisibility(View.GONE);
                    commision2text.setVisibility(View.GONE);
                    commision3linear.setVisibility(View.GONE);
                    commision3text.setVisibility(View.GONE);
                    commision3linear.setVisibility(View.GONE);
                    commision3text.setVisibility(View.GONE);
                    commision4linear.setVisibility(View.GONE);
                    commision4text.setVisibility(View.GONE);*/
                    tableLayout.setVisibility(View.GONE);
                    commision1text.setVisibility(View.GONE);
                    dailyratelinear.setVisibility(View.GONE);

                    contactinfolinear.setVisibility(View.VISIBLE);
                    contactinfotext.setVisibility(View.VISIBLE);
                } else if (nextbtncount == 1) {
                    nextbtncount--;
                    contactinfolinear.setVisibility(View.GONE);
                    contactinfotext.setVisibility(View.GONE);
                    addresslinear.setVisibility(View.VISIBLE);
                    addresstext.setVisibility(View.VISIBLE);
                } else if (nextbtncount == 0) {
                    previousbtn.setVisibility(View.GONE);
                    addresslinear.setVisibility(View.GONE);
                    addresstext.setVisibility(View.GONE);
                    basicdetailstext.setVisibility(View.VISIBLE);
                    basicdetailslinear.setVisibility(View.VISIBLE);
                }
            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(SignUpAgent.this, "No Functionalitily here for as of now....", Toast.LENGTH_SHORT).show();
                if (idspinner.getSelectedItem().toString().equals("ID") || idspinner.getSelectedItem().toString().equals("ID Type")) {
                    if (firstname.getText().toString().length() == 0) {
                        firstname.requestFocus();
                    } else if (lastname.getText().toString().length() == 0) {
                        lastname.requestFocus();
                    } else if (username.getText().toString().length() == 0) {
                        username.requestFocus();
                    } /*else if (password.getText().toString().length() == 0) {
                        password.requestFocus();
                    }*/ else if (idnum.getText().toString().length() == 0) {
                        idnum.requestFocus();
                    } else if (streetno.getText().toString().length() == 0) {
                        streetno.requestFocus();
                    } else if (streetname.getText().toString().length() == 0) {
                        streetname.requestFocus();
                    } else if (suburb.getText().toString().length() == 0) {
                        suburb.requestFocus();
                    } else if (city.getText().toString().length() == 0) {
                        city.requestFocus();
                    } else if (postal_code.getText().toString().length() == 0) {
                        postal_code.requestFocus();
                    }
   /*                 else if(ricauser.getText().toString().length() == 0)
                    {
                        ricauser.requestFocus();
                    }
                    else if (ricapassword.getText().toString().length() == 0)
                    {
                        ricapassword.requestFocus();
                    }
                    else if (ricagroup.getText().toString().length() == 0)
                    {
                        ricagroup.requestFocus();
                    }*/
                    else if (email.getText().toString().length() == 0) {
                        email.requestFocus();
                    } else if (mobno.getText().toString().length() == 0) {
                        mobno.requestFocus();
                    }
                   /* else if (altmobno.getText().toString().length() == 0)
                    {
                        altmobno.requestFocus();
                    }*/
                    else if (signedat.getText().toString().length() == 0) {
                        signedat.requestFocus();
                    } else if (titlespinner.getSelectedItem().toString().equals("Title")) {
                        Toast.makeText(EditAgentForm.this, "Choose Title", Toast.LENGTH_SHORT).show();
                    } else if (authorityspinner.getSelectedItem().toString().equals("Authority")) {
                        Toast.makeText(EditAgentForm.this, "Choose Role Type", Toast.LENGTH_SHORT).show();
                    } else if (warehousespinner.getSelectedItem().toString().equals("Warehouse")) {
                        Toast.makeText(EditAgentForm.this, "Choose Wareouse", Toast.LENGTH_SHORT).show();
                    } else if (idspinner.getSelectedItem().toString().equals("Id Type")) {
                        Toast.makeText(EditAgentForm.this, "Choose ID Type", Toast.LENGTH_SHORT).show();
                    } else if (provincespinner.getSelectedItem().toString().equals("Province")) {
                        Toast.makeText(EditAgentForm.this, "Select Province", Toast.LENGTH_SHORT).show();
                    } else if (doctypespinner.getSelectedItem().toString().equals("Choose DOC_TYPE")) {
                        Toast.makeText(EditAgentForm.this, "Select Document Type", Toast.LENGTH_SHORT).show();
                    } else {

                        if (/*dailyrate.getText().length() == 0 && simcost.getText().length() == 0
                                &&*/activationcom1.getText().length() == 0 /*&&ogr1.getText().length() == 0*/
                                /*&&cib1.getText().length() == 0*/ && sims1.getText().length() == 0) {
                            dailyrate.setText("0");
                            //simcost1.setText("0");
                            activationcom1.setError("Activation Commision Required");
                            activationcom1.requestFocus();
                            //ogr1.setText("0");
                            //cib1.setText("0");
                            sims1.setText("0");
                        }
                        if (/*dailyrate2.getText().length() == 0 && simcost2.getText().length() == 0
                                &&*/activationcom2.getText().length() == 0 /*&&ogr2.getText().length() == 0*/
                                /*&&cib2.getText().length() == 0*/ && sims2.getText().length() == 0) {
                            //dailyrate2.setText("0");
                            //simcost2.setText("0");
                            activationcom2.setError("Activation Commision Required");
                            activationcom2.requestFocus();
                            //ogr2.setText("0");
                            //cib2.setText("0");
                            sims2.setText("0");
                        }
                        if (/*dailyrate3.getText().length() == 0 && simcost3.getText().length() == 0
                                   &&*/activationcom3.getText().length() == 0 /*&&ogr3.getText().length() == 0*/
                                /*&&cib3.getText().length() == 0*/ && sims3.getText().length() == 0) {
                            //dailyrate3.setText("0");
                            //simcost3.setText("0");
                            activationcom3.setError("Activation Commision Required");
                            activationcom3.requestFocus();
                            //ogr3.setText("0");
                            //cib3.setText("0");
                            sims3.setText("0");
                        }
                        if (/*dailyrate4.getText().length() == 0 && simcost4.getText().length() == 0
                                   &&*/activationcom4.getText().length() == 0 /*&&ogr4.getText().length() == 0*/
                                /*&&cib4.getText().length() == 0*/ && sims4.getText().length() == 0) {
                            //dailyrate4.setText("0");
                            //simcost4.setText("0");
                            activationcom4.setError("Activation Commision Required");
                            activationcom4.requestFocus();
                            ogr4.setText("0");
                            //cib4.setText("0");
                            sims4.setText("0");
                        }
                        if (/*dailyrate.getText().length() == 0 || */simcost.getText().length() == 0
                                || activationcom1.getText().length() == 0 || ogr1.getText().length() == 0
                                /*||cib1.getText().length() == 0*/ /*|| sims1.getText().length() == 0*/
                                /*||dailyrate2.getText().length() == 0 || simcost2.getText().length() == 0*/
                                || activationcom2.getText().length() == 0 || ogr2.getText().length() == 0
                                /*||cib2.getText().length() == 0*/ /*|| sims2.getText().length() == 0*/
                                /*||dailyrate3.getText().length() == 0 || simcost3.getText().length() == 0*/
                                || activationcom3.getText().length() == 0 || ogr3.getText().length() == 0
                                /*||cib3.getText().length() == 0*/ /*|| sims3.getText().length() == 0*/
                                /*||dailyrate4.getText().length() == 0 || simcost4.getText().length() == 0*/
                                || activationcom4.getText().length() == 0 || ogr4.getText().length() == 0
                            /*||cib4.getText().length() == 0*/ /*|| sims4.getText().length() == 0*/) {
                            /*   if (dailyrate.getText().length() == 0) {
                                   dailyrate.requestFocus();
                               } else */
                            if (simcost.getText().length() == 0) {
                                simcost.requestFocus();
                            } else if (activationcom1.getText().length() == 0) {
                                activationcom1.requestFocus();
                            } else if (ogr1.getText().length() == 0) {
                                ogr1.requestFocus();
                            } /*else if (cib1.getText().length() == 0) {
                                   cib1.requestFocus();
                               }*/ else if (sims1.getText().length() == 0) {
                                sims1.setText("0");
                            } /*else if (dailyrate2.getText().length() == 0) {
                                   dailyrate2.requestFocus();
                               } else if (simcost2.getText().length() == 0) {
                                   simcost2.requestFocus();
                               }*/ else if (activationcom2.getText().length() == 0) {
                                activationcom2.requestFocus();
                            } else if (ogr2.getText().length() == 0) {
                                ogr2.requestFocus();
                            }/* else if (cib2.getText().length() == 0) {
                                   cib2.requestFocus();
                               }*/ else if (sims2.getText().length() == 0) {
                                sims2.setText("0");
                            } /*else if (dailyrate3.getText().length() == 0) {
                                   dailyrate3.requestFocus();
                               } else if (simcost3.getText().length() == 0) {
                                   simcost3.requestFocus();
                               }*/ else if (activationcom3.getText().length() == 0) {
                                activationcom3.requestFocus();
                            } else if (ogr3.getText().length() == 0) {
                                ogr3.requestFocus();
                            } /*else if (cib3.getText().length() == 0) {
                                   cib3.requestFocus();
                               }*/ else if (sims3.getText().length() == 0) {
                                sims3.setText("0");
                            } /*else if (dailyrate4.getText().length() == 0) {
                                   dailyrate4.requestFocus();
                               } else if (simcost4.getText().length() == 0) {
                                   simcost4.requestFocus();
                               }*/ else if (activationcom4.getText().length() == 0) {
                                activationcom4.requestFocus();
                            } else if (ogr4.getText().length() == 0) {
                                ogr4.requestFocus();
                            } /*else if (cib4.getText().length() == 0) {
                                   cib4.requestFocus();
                               }*/ else if (sims4.getText().length() == 0) {
                                sims4.setText("0");
                            }
                        } else {
                            contractagent(titlespinner.getSelectedItem().toString(), firstname.getText().toString(),
                                    lastname.getText().toString(), username.getText().toString(),
                                    /*password.getText().toString(),*/ authorityspinner.getSelectedItem().toString(),
                                    warehousespinner.getSelectedItem().toString(),
                                    idspinner.getSelectedItem().toString(), idnum.getText().toString(),
                                    streetno.getText().toString(), streetname.getText().toString(),
                                    suburb.getText().toString(), city.getText().toString(), provincespinner.getSelectedItem().toString(),
                                    postal_code.getText().toString(),
                                    email.getText().toString(), mobno.getText().toString(), altmobno.getText().toString(),
                                    network1.getText().toString(), dailyrate.getText().toString(),
                                    simcost.getText().toString(), activationcom1.getText().toString(),
                                    ogr1.getText().toString(),/*cib1.getText().toString()*/sims1.getText().toString(), network2.getText().toString(),
                                    /*dailyrate2.getText().toString(),simcost2.getText().toString(),*/activationcom2.getText().toString(),
                                    ogr2.getText().toString(),/*cib2.getText().toString(),*/sims2.getText().toString(), network3.getText().toString(),
                                    /*dailyrate3.getText().toString(),simcost3.getText().toString(),*/activationcom3.getText().toString(),
                                    ogr3.getText().toString(),/*cib3.getText().toString(),*/sims3.getText().toString(), network4.getText().toString(),
                                    /*dailyrate4.getText().toString(),simcost4.getText().toString(),*/activationcom4.getText().toString(),
                                    ogr4.getText().toString(),/*cib1.getText().toString(),*/sims4.getText().toString(), signedat.getText().toString());
                        }
                    }
                } else if (idspinner.getSelectedItem().toString().equals("PASSPORT")) {
                    if (firstname.getText().toString().length() == 0) {
                        firstname.requestFocus();
                    } else if (lastname.getText().toString().length() == 0) {
                        lastname.requestFocus();
                    } else if (username.getText().toString().length() == 0) {
                        username.requestFocus();
                    } /*else if (password.getText().toString().length() == 0) {
                        password.requestFocus();
                    }*/ else if (passport.getText().toString().length() == 0) {
                        passport.requestFocus();
                    } else if (expirydate.getText().toString().length() == 0) {
                        Toast.makeText(EditAgentForm.this, "Choose Expiry Date from Calender", Toast.LENGTH_SHORT).show();
                    } else if (streetno.getText().toString().length() == 0) {
                        streetno.requestFocus();
                    } else if (streetname.getText().toString().length() == 0) {
                        streetname.requestFocus();
                    } else if (suburb.getText().toString().length() == 0) {
                        suburb.requestFocus();
                    } else if (city.getText().toString().length() == 0) {
                        city.requestFocus();
                    } else if (postal_code.getText().toString().length() == 0) {
                        postal_code.requestFocus();
                    }
     /*               else if(ricauser.getText().toString().length() == 0)
                    {
                        ricauser.requestFocus();
                    }
                    else if (ricapassword.getText().toString().length() == 0)
                    {
                        ricapassword.requestFocus();
                    }
                    else if (ricagroup.getText().toString().length() == 0)
                    {
                        ricagroup.requestFocus();
                    }*/
                    else if (email.getText().toString().length() == 0) {
                        email.requestFocus();
                    } else if (mobno.getText().toString().length() == 0) {
                        mobno.requestFocus();
                    }
              /*      else if (altmobno.getText().toString().length() == 0)
                    {
                        altmobno.requestFocus();
                    }*/
                    else if (signedat.getText().toString().length() == 0) {
                        signedat.requestFocus();
                    } else if (titlespinner.getSelectedItem().toString().equals("Title")) {
                        Toast.makeText(EditAgentForm.this, "Choose Title", Toast.LENGTH_SHORT).show();
                    } else if (authorityspinner.getSelectedItem().toString().equals("Authority")) {
                        Toast.makeText(EditAgentForm.this, "Choose Role Type", Toast.LENGTH_SHORT).show();
                    } else if (warehousespinner.getSelectedItem().toString().equals("Warehouse")) {
                        Toast.makeText(EditAgentForm.this, "Choose Wareouse", Toast.LENGTH_SHORT).show();
                    } else if (idspinner.getSelectedItem().toString().equals("Id Type")) {
                        Toast.makeText(EditAgentForm.this, "Choose ID Type", Toast.LENGTH_SHORT).show();
                    } else if (provincespinner.getSelectedItem().toString().equals("Province")) {
                        Toast.makeText(EditAgentForm.this, "Select Province", Toast.LENGTH_SHORT).show();
                    } else if (doctypespinner.getSelectedItem().toString().equals("Choose DOC_TYPE")) {
                        Toast.makeText(EditAgentForm.this, "Select Document Type", Toast.LENGTH_SHORT).show();
                    } else {
                        if (/*dailyrate.getText().length() == 0 && */simcost.getText().length() == 0
                                && activationcom1.getText().length() == 0 /*&&ogr1.getText().length() == 0*/
                                /*&&cib1.getText().length() == 0*/ && sims1.getText().length() == 0) {
                            dailyrate.setText("0");
                            //simcost1.setText("0");
                            activationcom1.setError("Activation Commision Required");
                            activationcom1.requestFocus();
                            ogr1.setText("0");
                            //cib1.setText("0");
                            sims1.setText("0");
                        }
                        if (/*dailyrate2.getText().length() == 0 && simcost2.getText().length() == 0
                                    &&*/activationcom2.getText().length() == 0 && ogr2.getText().length() == 0
                                /*&&cib2.getText().length() == 0*/ && sims2.getText().length() == 0) {
                            //dailyrate2.setText("0");
                            //simcost2.setText("0");
                            activationcom2.setError("Activation Commision Required");
                            activationcom2.requestFocus();
                            ogr2.setText("0");
                            //cib2.setText("0");
                            sims2.setText("0");
                        }
                        if (/*dailyrate3.getText().length() == 0 && simcost3.getText().length() == 0
                                    &&*/activationcom3.getText().length() == 0 && ogr3.getText().length() == 0
                                /*&&cib3.getText().length() == 0*/ && sims3.getText().length() == 0) {
                            //dailyrate3.setText("0");
                            //simcost3.setText("0");
                            activationcom3.setError("Activation Commision Required");
                            activationcom3.requestFocus();
                            ogr3.setText("0");
                            //cib3.setText("0");
                            sims3.setText("0");
                        }
                        if (/*dailyrate4.getText().length() == 0 && simcost4.getText().length() == 0
                                    &&*/activationcom4.getText().length() == 0 && ogr4.getText().length() == 0
                                /*&&cib4.getText().length() == 0*/ && sims4.getText().length() == 0) {
                            //dailyrate4.setText("0");
                            //simcost4.setText("0");
                            activationcom4.setError("Activation Commision Required");
                            activationcom4.requestFocus();
                            ogr4.setText("0");
                            //cib4.setText("0");
                            sims4.setText("0");
                        }
                        if (/*dailyrate.getText().length() == 0 || */simcost.getText().length() == 0
                                || activationcom1.getText().length() == 0 || ogr1.getText().length() == 0
                                /*||cib1.getText().length() == 0*/ /*|| sims1.getText().length() == 0*/
                                /*||dailyrate2.getText().length() == 0 || simcost2.getText().length() == 0*/
                                || activationcom2.getText().length() == 0 || ogr2.getText().length() == 0
                                /*||cib2.getText().length() == 0*/ /*|| sims2.getText().length() == 0*/
                                /*||dailyrate3.getText().length() == 0 || simcost3.getText().length() == 0*/
                                || activationcom3.getText().length() == 0 || ogr3.getText().length() == 0
                                /* ||cib3.getText().length() == 0*/ /*|| sims3.getText().length() == 0*/
                                /*||dailyrate4.getText().length() == 0 || simcost4.getText().length() == 0*/
                                || activationcom4.getText().length() == 0 || ogr4.getText().length() == 0
                            /*||cib4.getText().length() == 0*/ /*|| sims4.getText().length() == 0*/) {
                              /*  if (dailyrate.getText().length() == 0) {
                                    dailyrate.requestFocus();
                                } else */
                            if (simcost.getText().length() == 0) {
                                simcost.requestFocus();
                            } else if (activationcom1.getText().length() == 0) {
                                activationcom1.requestFocus();
                            } else if (ogr1.getText().length() == 0) {
                                ogr1.requestFocus();
                            } /*else if (cib1.getText().length() == 0) {
                                    cib1.requestFocus();
                                }*/ else if (sims1.getText().length() == 0) {
                                sims1.setText("0");
                            } /*else if (dailyrate2.getText().length() == 0) {
                                    dailyrate2.requestFocus();
                                } else if (simcost2.getText().length() == 0) {
                                    simcost2.requestFocus();
                                }*/ else if (activationcom2.getText().length() == 0) {
                                activationcom2.requestFocus();
                            } else if (ogr2.getText().length() == 0) {
                                ogr2.requestFocus();
                            } /*else if (cib2.getText().length() == 0) {
                                    cib2.requestFocus();
                                }*/ else if (sims2.getText().length() == 0) {
                                sims2.setText("0");
                            } /*else if (dailyrate3.getText().length() == 0) {
                                    dailyrate3.requestFocus();
                                } else if (simcost3.getText().length() == 0) {
                                    simcost3.requestFocus();
                                }*/ else if (activationcom3.getText().length() == 0) {
                                activationcom3.requestFocus();
                            } else if (ogr3.getText().length() == 0) {
                                ogr3.requestFocus();
                            }/* else if (cib3.getText().length() == 0) {
                                    cib3.requestFocus();
                                }*/ else if (sims3.getText().length() == 0) {
                                sims3.setText("0");
                            } /*else if (dailyrate4.getText().length() == 0) {
                                    dailyrate4.requestFocus();
                                } else if (simcost4.getText().length() == 0) {
                                    simcost4.requestFocus();
                                }*/ else if (activationcom4.getText().length() == 0) {
                                activationcom4.requestFocus();
                            } else if (ogr4.getText().length() == 0) {
                                ogr4.requestFocus();
                            }/* else if (cib4.getText().length() == 0) {
                                    cib4.requestFocus();
                                }*/ else if (sims4.getText().length() == 0) {
                                sims4.setText("0");
                            }
                        } else {
                            contractagentPassport(titlespinner.getSelectedItem().toString(), firstname.getText().toString(),
                                    lastname.getText().toString(), username.getText().toString(),
                                    /*password.getText().toString(),*/ authorityspinner.getSelectedItem().toString(),
                                    warehousespinner.getSelectedItem().toString(),
                                    idspinner.getSelectedItem().toString(), passport.getText().toString(), expirydate.getText().toString(),
                                    streetno.getText().toString(), streetname.getText().toString(),
                                    suburb.getText().toString(), city.getText().toString(), provincespinner.getSelectedItem().toString(),
                                    postal_code.getText().toString(),
                                    email.getText().toString(), mobno.getText().toString(), altmobno.getText().toString(),
                                    network1.getText().toString(), dailyrate.getText().toString(),
                                    simcost.getText().toString(), activationcom1.getText().toString(),
                                    ogr1.getText().toString(),/*cib1.getText().toString(),*/sims1.getText().toString(), network2.getText().toString(),
                                    /*dailyrate2.getText().toString(),simcost2.getText().toString(),*/activationcom2.getText().toString(),
                                    ogr2.getText().toString(),/*cib2.getText().toString(),*/sims2.getText().toString(), network3.getText().toString(),
                                    /*dailyrate3.getText().toString(),simcost3.getText().toString(),*/activationcom3.getText().toString(),
                                    ogr3.getText().toString(),/*cib3.getText().toString(),*/sims3.getText().toString(), network4.getText().toString(),
                                    /*dailyrate4.getText().toString(),simcost4.getText().toString(),*/activationcom4.getText().toString(),
                                    ogr4.getText().toString(),/*cib1.getText().toString(),*/sims4.getText().toString(), signedat.getText().toString());
                        }
                    }
                }
            }

            ;
        });

        //buttonSign1=findViewById(R.id.signature1);
        //  buttonSign2=findViewById(R.id.signature2);
        buttonSign3 = findViewById(R.id.distributorbtn);
        //   buttonSign4=findViewById(R.id.signature3);
        //   buttonSign5=findViewById(R.id.signature4);
        buttonSign6 = findViewById(R.id.signaturebtn);
        root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        assetManager = getAssets();

        buttonSign3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: canvas for signature opens");
                count++;
                openDialog();
            }
        });
        buttonSign6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: canvas for signature opens");
                count1++;
                openDialog();
            }
        });
        initPlaces();
        editTextClick();
        suburbclick();
    }

    private void authorityAPICall() {
        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
        Call<ResponseAuthority> responseAuthorityCall = web_interface.requestResponseAuthority();
        responseAuthorityCall.enqueue(new Callback<ResponseAuthority>() {
            @Override
            public void onResponse(Call<ResponseAuthority> call, Response<ResponseAuthority> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    List<Body> bodyList = new ArrayList<>();
                    //strings = new ArrayList<>();
                    if (response.body() != null) {
                        bodyList = response.body().getBody();
                        for (int i = 0; i < bodyList.size(); i++) {
                            strings.add(bodyList.get(i).getAuthority());
                        }

                    }

                    authorityadapter = new ArrayAdapter(EditAgentForm.this, android.R.layout.simple_spinner_dropdown_item,
                            strings);
                    authorityspinner.setAdapter(authorityadapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseAuthority> call, Throwable t) {
                Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void warehouseAPICall() {

        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
        Call<WarehouseResponse> warehouseResponseCall = web_interface.requestWarehouseResponse();
        warehouseResponseCall.enqueue(new Callback<WarehouseResponse>() {
            @Override
            public void onResponse(Call<WarehouseResponse> call, Response<WarehouseResponse> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    List<WarehouseBody> warehouseBodyList = new ArrayList<>();
                    //warehousearray = new ArrayList<>();
                    if (response.body() != null) {
                        warehouseBodyList = response.body().getBody();
                        for (int i = 0; i < warehouseBodyList.size(); i++) {
                            warehousearray.add(warehouseBodyList.get(i).getName());
                        }
                    }
                    warehouseadapter = new ArrayAdapter(EditAgentForm.this, android.R.layout.simple_spinner_dropdown_item,
                            warehousearray);
                    warehousespinner.setAdapter(warehouseadapter);
                } else {
                    Log.d("onResponse: ", "ERROR");
                    Toast.makeText(EditAgentForm.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WarehouseResponse> call, Throwable t) {
                Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * This is the default function of Back Button press. In this according to the nextbtncount variable value, this will show the layout view to the user.
     */
    @Override
    public void onBackPressed() {


        if (nextbtncount == 3) {
            signupbtn.setVisibility(View.GONE);
            nextbtn.setVisibility(View.VISIBLE);
            nextbtncount--;
            doctypespinnerlinear.setVisibility(View.GONE);
            distributorlinear.setVisibility(View.GONE);
            agentlinear.setVisibility(View.GONE);
            signedatlinear.setVisibility(View.GONE);
            simcost.setVisibility(View.VISIBLE);
            //ogr.setVisibility(View.VISIBLE);
            dailyrate.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.VISIBLE);
            commision1text.setVisibility(View.VISIBLE);
/*            commision1linear.setVisibility(View.VISIBLE);
            commision1text.setVisibility(View.VISIBLE);
            commision2linear.setVisibility(View.VISIBLE);
            commision2text.setVisibility(View.VISIBLE);
            commision3linear.setVisibility(View.VISIBLE);
            commision3text.setVisibility(View.VISIBLE);
            commision3linear.setVisibility(View.VISIBLE);
            commision3text.setVisibility(View.VISIBLE);
            commision4linear.setVisibility(View.VISIBLE);
            commision4text.setVisibility(View.VISIBLE);*/
        } else if (nextbtncount == 2) {
            nextbtncount--;
/*            commision1linear.setVisibility(View.GONE);
            commision1text.setVisibility(View.GONE);
            commision2linear.setVisibility(View.GONE);
            commision2text.setVisibility(View.GONE);
            commision3linear.setVisibility(View.GONE);
            commision3text.setVisibility(View.GONE);
            commision3linear.setVisibility(View.GONE);
            commision3text.setVisibility(View.GONE);
            commision4linear.setVisibility(View.GONE);
            commision4text.setVisibility(View.GONE);*/
            tableLayout.setVisibility(View.GONE);
            commision1text.setVisibility(View.GONE);
            dailyratelinear.setVisibility(View.GONE);
            contactinfolinear.setVisibility(View.VISIBLE);
            contactinfotext.setVisibility(View.VISIBLE);
        } else if (nextbtncount == 1) {
            nextbtncount--;
            contactinfolinear.setVisibility(View.GONE);
            contactinfotext.setVisibility(View.GONE);
            addresslinear.setVisibility(View.VISIBLE);
            addresstext.setVisibility(View.VISIBLE);
        } else if (nextbtncount == 0) {
            previousbtn.setVisibility(View.GONE);
            addresslinear.setVisibility(View.GONE);
            addresstext.setVisibility(View.GONE);
            basicdetailstext.setVisibility(View.VISIBLE);
            basicdetailslinear.setVisibility(View.VISIBLE);
            nextbtncount--;
        } else if (nextbtncount == -1) {
            super.onBackPressed();
        }

    }

    /**
     * This function call the Google Places API for getting thr Suburb of the user who is signing up as agent.
     */
    private void suburbclick() {

        suburb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AUTOCOMPLETE_REQUEST_CODE_SUB = 2;

                List<Place.Field> field = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                        .setCountry("ZA")
                        .build(EditAgentForm.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_SUB);


            }
        });
    }

    /**
     * This function initializes the Google Places API.
     */
    private void initPlaces() {
        Places.initialize(this, getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
    }


    /**
     * This function call the Google Places API for getting thr City of the user who is signing up as agent.
     */
    private void editTextClick() {
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AUTOCOMPLETE_REQUEST_CODE = 1;

                List<Place.Field> field = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                        .setCountry("ZA")
                        .setTypeFilter(TypeFilter.CITIES)
                        .build(EditAgentForm.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


            }
        });
    }


    public void onDelete(View view) {

        String doc = doctypespinner.getSelectedItem().toString();
        if (doc.equals("Choose DOC_TYPE")) {

        } else {
            for (int i = 0; i < attachmentArray2.length(); i++) {
                try {
                    if (attachmentArray2.getJSONObject(i).get("type").equals(doc)) {
                        attachmentArray2.remove(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        linearLayout.removeView((View) view.getParent());
    }

    /**
     * This function opens the dialog box for getting the signature of the User on the canvas. it has two buttons. one is capture, used for
     * capturing & uploading it on the server & second is used to clear or reset the canvas.
     */
    private void openDialog() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.customs_sign_dialog, null);
        alert.setTitle("Signature");
        alert.setView(v);
        final AlertDialog dialog = alert.show();
        btnClear = v.findViewById(R.id.btnClear);
        signatureView = v.findViewById(R.id.signature_view);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();

            }
        });
        btnSave = v.findViewById(R.id.btnSave);
        dialog.setCanceledOnTouchOutside(true);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked save");
                Toast.makeText(EditAgentForm.this, "Signature Captured!", Toast.LENGTH_SHORT).show();
                if (!signatureView.isBitmapEmpty()) {
                    //getting bitmap from signature canvas
                    bitmap = signatureView.getSignatureBitmap();
                    Log.d(TAG, "onClick: bitmap:" + bitmap);
                    /**
                     method to save image and get saved path
                     */
                    path = saveImage(bitmap);

                    sendImageDataToServer();
                    dialog.dismiss();
                }
                //dialog.dismiss();
            }
        });
    }

    /**
     * This function saves the image in the local storage.
     *
     * @param bitmap This is the bitmap value of the image, passes as an arguent.
     * @return this function returns the path of the image on which path the image is stored.
     */
    private String saveImage(Bitmap bitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY /*iDyme folder*/);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
            Log.d("hhhhh", wallpaperDirectory.toString());
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            Toast.makeText(this, "Signature Captured Succesfully", Toast.LENGTH_SHORT).show();
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    /**
     * This function returns the API results of Contract API & Sign Up API by passing all the required values to this function as arguments.
     *
     * @param titlevalue     This is the title of the user who is signing up as an agent.
     * @param firstname      This is the first name of the user who is signing up as an agent.
     * @param lastname       This is the last name of the user who is signing up as an agent.
     * @param username       This is the username of the user who is signing up as an agent.
     * @param authorityvalue This is the authority type value of the user who is signing up as an agent.
     * @param warehousevalue This is the warehouse value of the user who is signing up as an agent.
     * @param idvalue        This is the ID Type of the user who is signing up as an agent.
     * @param passport       This is the passport number of the user who is signing up as an agent.
     * @param expirydate     This is the expiry date of the passport of the user who is signing up as an agent.
     * @param streetno       This is the street No of the user who is signing up as an agent.
     * @param streetname     This is the street Name of the user who is signing up as an agent.
     * @param suburb         This is the suburb of the user who is signing up as an agent.
     * @param city           This is the city of the user who is signing up as an agent.
     * @param province       This is the province of the user who is signing up as an agent.
     * @param postalcode     This is the postalcode of the user who is signing up as an agent.
     * @param email          This is the email of the user who is signing up as an agent.
     * @param mobno          This is the mobno of the user who is signing up as an agent.
     * @param altmobno       This is the altmobno of the user who is signing up as an agent.
     * @param signedat       This is the signedat (on which location he is signing up) of the user who is signing up as an agent.
     *                       <p>
     *                       also, we are having commisions,sims,cib values different for the differernt networks with common value of Daily rete
     *                       for all the networks.
     */
    private void contractagentPassport(String titlevalue, String firstname, String lastname, String username, /*String password,*/
                                       String authorityvalue, String warehousevalue, String idvalue,
                                       String passport, String expirydate, String streetno, String streetname, String suburb,
                                       String city, String province, String postalcode, String email, String mobno, String altmobno, String network1, String dailyRate,
                                       String simcost, String activationcom1, String ogr1,/*String cib1,*/ String sims1, String network2,
                                       String activationcom2, String ogr2, String sims2, String network3,
                                       String activationcom3, String ogr3, String sims3, String network4,
                                       String activationcom4, String ogr4, String sims4, String signedat) {

        if (dailyRate.equals("")) {
            dailyRate = "0";
        }
        String ricauser = null,ricapassword = null, ricagroup = null;
        progressBar.show();
        int parentId = Integer.parseInt(Pref.getUserId(this));

        float DailyRate1, SimCost1, ActivationCom1, Ogr1;
        float DailyRate2, SimCost2, ActivationCom2, Ogr2;
        float DailyRate3, SimCost3, ActivationCom3, Ogr3;
        float DailyRate4, SimCost4, ActivationCom4, Ogr4;
        simcost = "1";
        ogr1 = "0";
        ogr2 = "0";
        ogr3 = "0";
        ogr4 = "0";
        int Sims1, Sims2, Sims3, Sims4;

        DailyRate1 = Float.parseFloat(dailyRate);
        SimCost1 = Float.parseFloat(simcost);
        ActivationCom1 = Float.parseFloat(activationcom1);
        Ogr1 = Float.parseFloat(ogr1);
        //Cib1 = Float.parseFloat(cib1);
        Sims1 = sims1.trim().length() > 0? Integer.parseInt(sims1): 0;

        DailyRate2 = DailyRate1;
        SimCost2 = SimCost1;
        ActivationCom2 = Float.parseFloat(activationcom2);
        Ogr2 = Ogr1;
        //Cib2 = Float.parseFloat(cib2);
        Sims2 = sims2.trim().length() > 0? Integer.parseInt(sims2): 0;

        DailyRate3 = DailyRate1;
        SimCost3 = SimCost1;
        ActivationCom3 = Float.parseFloat(activationcom3);
        Ogr3 = Ogr1;
        //Cib3 = Float.parseFloat(cib3);

        Sims3 = sims3.trim().length() > 0? Integer.parseInt(sims3): 0;

        DailyRate4 = DailyRate1;
        SimCost4 = SimCost1;
        ActivationCom4 = Float.parseFloat(activationcom4);
        Ogr4 = Ogr1;
        //Cib4 = Float.parseFloat(cib4);

        Sims4 = sims4.trim().length() > 0? Integer.parseInt(sims4): 0;

        /*if(status.equals("Enabled"))
        {
            enableid = Boolean.valueOf("true");

        }
        else
        {

            enableid = Boolean.valueOf("false");

        }*/


        JSONObject profileobject = new JSONObject();
        try {
            profileobject.put("idType", idvalue);
            profileobject.put("passportNo", passport);
            profileobject.put("passportExpiryDate", datestring);
            profileobject.put("ricaUser", ricauser);
            profileobject.put("ricaPassword", ricapassword);
            profileobject.put("ricaGroup", ricagroup);
            profileobject.put("email", email);
            profileobject.put("mobileNo", mobno);
            profileobject.put("altMobileNo", altmobno);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject addressObject = new JSONObject();
        try {
            addressObject.put("streetNo", streetno);
            addressObject.put("streetName", streetname);
            addressObject.put("suburb", suburb);
            addressObject.put("city", city);
            addressObject.put("province", province);
            addressObject.put("postalCode", postalcode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject commisionsobject1 = new JSONObject();
        try {
            commisionsobject1.put("network", network1);
            commisionsobject1.put("dailyRate", DailyRate1);
            commisionsobject1.put("simCost", SimCost1);
            commisionsobject1.put("activationCom", ActivationCom1);
            commisionsobject1.put("ogr", Ogr1);
            //commisionsobject1.put("cib",Cib1);
            commisionsobject1.put("sims", Sims1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray commisionsArray = new JSONArray();
        commisionsArray.put(commisionsobject1);

        JSONObject commisionsobject2 = new JSONObject();
        try {
            commisionsobject2.put("network", network2);
            commisionsobject2.put("dailyRate", DailyRate2);
            commisionsobject2.put("simCost", SimCost2);
            commisionsobject2.put("activationCom", ActivationCom2);
            commisionsobject2.put("ogr", Ogr2);
            //commisionsobject2.put("cib",Cib2);
            commisionsobject2.put("sims", Sims2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        commisionsArray.put(commisionsobject2);

        JSONObject commisionsobject3 = new JSONObject();
        try {
            commisionsobject3.put("network", network3);
            commisionsobject3.put("dailyRate", DailyRate3);
            commisionsobject3.put("simCost", SimCost3);
            commisionsobject3.put("activationCom", ActivationCom3);
            commisionsobject3.put("ogr", Ogr3);
            //commisionsobject3.put("cib",Cib3);
            commisionsobject3.put("sims", Sims3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        commisionsArray.put(commisionsobject3);


        JSONObject commisionsobject4 = new JSONObject();
        try {
            commisionsobject4.put("network", network4);
            commisionsobject4.put("dailyRate", DailyRate4);
            commisionsobject4.put("simCost", SimCost4);
            commisionsobject4.put("activationCom", ActivationCom4);
            commisionsobject4.put("ogr", Ogr4);
            //commisionsobject4.put("cib",Cib4);
            commisionsobject4.put("sims", Sims4);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        commisionsArray.put(commisionsobject4);


        JSONObject authorityObject = new JSONObject();
        try {
            authorityObject.put("id", authorityId);
            authorityObject.put("name", authorityname);
            authorityObject.put("authority", authority);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("title", titlevalue);
            paramObject.put("firstName", firstname);
            paramObject.put("lastName", lastname);
            paramObject.put("username", username);
            //paramObject.put("password", password);
            paramObject.put("enabled", false);
            paramObject.put("authority", authorityObject);
            paramObject.put("attachments", attachmentArray);
            paramObject.put("profile", profileobject);
            paramObject.put("address", addressObject);
            paramObject.put("commissions", commisionsArray);
            paramObject.put("parentId", parentId);
            paramObject.put("warehouseId", warehouseId);
            paramObject.put("signedAt", signedat);

            RequestBody contractbody = RequestBody.create(MediaType.parse("application/json"), (paramObject).toString());
            Call<ContractResponse> signUpResponseCall = web_interface.requestContractResponse(contractbody);
            signUpResponseCall.enqueue(new Callback<ContractResponse>() {
                @Override
                public void onResponse(Call<ContractResponse> call, Response<ContractResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        if (response.body() != null) {
                            String success = response.body().getSuccess().toString();
                            if (success.equals("true")) {
                                progressBar.dismiss();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("id", response.body().getBody().getId());
                                    jsonObject.put("name", response.body().getBody().getName());
                                    jsonObject.put("fileName", response.body().getBody().getFileName());
                                    jsonObject.put("type", response.body().getBody().getType());
                                    attachmentArray2.put(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //Toast.makeText(SignUpAgent.this, response.code(), Toast.LENGTH_SHORT).show();

                                progressBar.show();
                                String sttsvalue = "";

                             /*   if(status.equals("Enabled"))
                                {
                                    sttsvalue="true";

                                }
                                else
                                {
                                    sttsvalue="false";
                                }*/
                                JSONObject profileobject = new JSONObject();
                                try {
                                    profileobject.put("id",fetchOneAgentList.getProfile().getId());
                                    profileobject.put("idType", idvalue);
                                    profileobject.put("passportNo", passport);
                                    profileobject.put("passportExpiryDate", datestring);
                                    profileobject.put("ricaUser", ricauser);
                                    profileobject.put("ricaPassword", ricapassword);
                                    profileobject.put("ricaGroup", ricagroup);
                                    profileobject.put("email", email);
                                    profileobject.put("mobileNo", mobno);
                                    profileobject.put("altMobileNo", altmobno);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JSONObject addressObject = new JSONObject();
                                try {
                                    addressObject.put("id",fetchOneAgentList.getAddress().getId());
                                    addressObject.put("streetNo", streetno);
                                    addressObject.put("streetName", streetname);
                                    addressObject.put("suburb", suburb);
                                    addressObject.put("city", city);
                                    addressObject.put("province", province);
                                    addressObject.put("postalCode", postalcode);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JSONObject commisionsobject1 = new JSONObject();
                                try {
                                    commisionsobject1.put("id",fetchOneAgentList.getCommissions().get(0).getId());
                                    commisionsobject1.put("network", network1);
                                    commisionsobject1.put("dailyRate", DailyRate1);
                                    commisionsobject1.put("simCost", SimCost1);
                                    commisionsobject1.put("activationCom", ActivationCom1);
                                    commisionsobject1.put("ogr", Ogr1);
                                    //                      commisionsobject1.put("cib",Cib1);
                                    commisionsobject1.put("sims", Sims1);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JSONArray commisionsArray = new JSONArray();
                                commisionsArray.put(commisionsobject1);

                                JSONObject commisionsobject2 = new JSONObject();
                                try {
                                    commisionsobject2.put("id",fetchOneAgentList.getCommissions().get(1).getId());
                                    commisionsobject2.put("network", network2);
                                    commisionsobject2.put("dailyRate", DailyRate2);
                                    commisionsobject2.put("simCost", SimCost2);
                                    commisionsobject2.put("activationCom", ActivationCom2);
                                    commisionsobject2.put("ogr", Ogr2);
                                    //                      commisionsobject2.put("cib",Cib2);
                                    commisionsobject2.put("sims", Sims2);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                commisionsArray.put(commisionsobject2);

                                JSONObject commisionsobject3 = new JSONObject();
                                try {
                                    commisionsobject3.put("id",fetchOneAgentList.getCommissions().get(2).getId());
                                    commisionsobject3.put("network", network3);
                                    commisionsobject3.put("dailyRate", DailyRate3);
                                    commisionsobject3.put("simCost", SimCost3);
                                    commisionsobject3.put("activationCom", ActivationCom3);
                                    commisionsobject3.put("ogr", Ogr3);
                                    //                      commisionsobject3.put("cib",Cib3);
                                    commisionsobject3.put("sims", Sims3);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                commisionsArray.put(commisionsobject3);


                                JSONObject commisionsobject4 = new JSONObject();
                                try {
                                    commisionsobject4.put("id",fetchOneAgentList.getCommissions().get(3).getId());
                                    commisionsobject4.put("network", network4);
                                    commisionsobject4.put("dailyRate", DailyRate4);
                                    commisionsobject4.put("simCost", SimCost4);
                                    commisionsobject4.put("activationCom", ActivationCom4);
                                    commisionsobject4.put("ogr", Ogr4);
                                    //                      commisionsobject4.put("cib",Cib4);
                                    commisionsobject4.put("sims", Sims4);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                commisionsArray.put(commisionsobject4);


                                JSONObject authorityObject = new JSONObject();
                                try {
                                    authorityObject.put("id", authorityId);
                                    authorityObject.put("name", authorityname);
                                    authorityObject.put("authority", authority);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

/*        JSONObject attachmentObject = new JSONObject();
        try {
            attachmentObject.put("id",imageid);
            attachmentObject.put("name",imagename);
            attachmentObject.put("fileName",imagefilename);
            attachmentObject.put("path",imagepath);
            attachmentObject.put("createdAt",imagecreatedat);
            attachmentObject.put("updatedAt",imageupdatedat);
            attachmentObject.put("type",doctype);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray attachmentArray = new JSONArray();
        attachmentArray.put(attachmentObject);*/

                                JSONArray paymentsArray = new JSONArray();
                                Web_Interface web_interface1 = RetrofitToken.getClient().create(Web_Interface.class);
                                JSONObject paramObject = new JSONObject();
                                try {
                                    paramObject.put("enabled", false);
                                    paramObject.put("profile", profileobject);
                                    paramObject.put("address", addressObject);
                                    paramObject.put("parentId", parentId);
                                    paramObject.put("title", titlevalue);
                                    paramObject.put("firstName", firstname);
                                    paramObject.put("lastName", lastname);
                                    paramObject.put("username", username);
                                    //paramObject.put("password", password);
                                    paramObject.put("authority", authorityObject);
                                    paramObject.put("warehouseId", warehouseId);
                                    paramObject.put("attachments", attachmentArray2);
                                    paramObject.put("paymentAccounts", paymentsArray);
                                    paramObject.put("commissions", commisionsArray);
                                    paramObject.put("signedAt", signedat);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                RequestBody signupBody = RequestBody.create(MediaType.parse("application/json"), (paramObject).toString());
                                Call<UpdateResponse> updateResponseCall = web_interface1.requestUpdateResponse(userid,signupBody);
                                updateResponseCall.enqueue(new Callback<UpdateResponse>() {
                                    @Override
                                    public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                                        if (response.isSuccessful() && response.code() == 200) {
                                            String success = response.body().getSuccess().toString();
                                            String message = response.body().getMessage();
                                            if (success.equals("true")) {
                                                progressBar.dismiss();
                                                Toasty.success(EditAgentForm.this, message, Toast.LENGTH_SHORT, true).show();
                                                Intent intent = new Intent(EditAgentForm.this, Stocks_dashboard.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
                                                progressBar.dismiss();
                                                Toast.makeText(EditAgentForm.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            progressBar.dismiss();
                                            Toast.makeText(EditAgentForm.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                @Override
                                public void onFailure(Call<UpdateResponse> call, Throwable t) {
                                    progressBar.dismiss();
                                    Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            progressBar.dismiss();
                            Toast.makeText(EditAgentForm.this, response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.dismiss();
                        Toast.makeText(EditAgentForm.this, response.code() + " Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.dismiss();
                    Toast.makeText(EditAgentForm.this, response.code() + " Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContractResponse> call, Throwable t) {
                progressBar.dismiss();
                Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    } catch (JSONException e) {
        e.printStackTrace();
    }
}

    /**
     * this functions gets the image from the camera having some properties of the clicked picture i.e. scaling, directory path.
     */

    private void getImageFromCamera() {

        new ImagePicker.Builder(EditAgentForm.this)
                .mode(ImagePicker.Mode.CAMERA)
                .compressLevel(ImagePicker.ComperesLevel.HARD)
                .directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.PNG)
                .scale(600, 600)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            filePath = mPaths.get(0);
            if (filePath != null) {
                sendImageDataToServer2();
            }
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                city.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_SUB) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                suburb.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        {

        }
    }

    /**
     * This function sends the image to the server for processing or uploading it on the server & get result success or failure.
     */

    private void sendImageDataToServer2() {

        if (filePath != null) {

            Utils.showProgress(this, "Image is uploading, please wait");
            Web_Interface webInterface = Ret.getClient().create(Web_Interface.class);
            List<MultipartBody.Part> parts = new ArrayList<>();
            List<String> files = new ArrayList<>(); //These are the uris for the files to be uploaded
            files.add(filePath);
            MediaType mediaType = MediaType.parse("multipart/form-data");//Based on the Postman logs,it's not specifying Content-Type, this is why I've made this empty content/mediaType
            MultipartBody.Part[] fileParts = new MultipartBody.Part[files.size()];
            for (int i = 0; i < files.size(); i++) {
                File file = new File(files.get(i));
                RequestBody fileBody = RequestBody.create(mediaType, file);
                //Setting the file name as an empty string here causes the same issue, which is sending the request successfully without saving the files in the backend, so don't neglect the file name parameter.
                fileParts[i] = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
                Log.d("filedata ", file.getName() + " " + fileBody);
            }
            Log.d("file2upload", Arrays.toString(fileParts) + "" + RetrofitToken.token);

            Call<UploadedFile> call = webInterface.requestUpdateProfilePic(fileParts, RetrofitToken.token);
            call.enqueue(new Callback<UploadedFile>() {
                @Override
                public void onResponse(Call<UploadedFile> call, Response<UploadedFile> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        //if code is 200 and response is successfull means the agent is login successfully
                        //now setting flag to true to day started

                        String message1 = response.body().getMessage();
                        imageid = response.body().getBody().getId();
                        imagename = response.body().getBody().getName();
                        imagefilename = response.body().getBody().getFileName();
                        imagepath = response.body().getBody().getPath();
                        imagecreatedat = response.body().getBody().getCreatedAt();
                        imageupdatedat = response.body().getBody().getUpdatedAt();

                        for (int i = 0; i < attachmentArray2.length(); i++) {
                            //String type = fetchOneAgentList.getAttachments().get(i).getType();
                            //String sign = fetchOneAgentList.getAttachments().get(i).getType();
                            try {
                                if (attachmentArray2.getJSONObject(i).get("type").equals(doctypeupload)) {

                                    JSONObject attachmentObject = new JSONObject();
                                    attachmentObject = attachmentArray2.getJSONObject(i);
                                    attachmentObject.put("id", imageid);
                                    attachmentObject.put("name", imagename);
                                    attachmentObject.put("fileName", response.body().getBody().getFileName());
                                    attachmentObject.put("type", doctypestringfill);
                                    attachmentArray2.put(i, attachmentObject);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                   /*     JSONObject attachmentObject = new JSONObject();
                        try {
                            attachmentObject.put("id", imageid);
                            attachmentObject.put("name", imagename);
                            attachmentObject.put("fileName", imagefilename);
                            attachmentObject.put("type", doctype);


                            attachmentArray2.put(attachmentObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        //now we will send this image id and store name from spinner using retrofit
                        Utils.stopProgress();
                        Toasty.success(getApplicationContext(), message1).show();


                    } else {

                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.d("AgentLoginActivity", jObjError.getString("message"));
                            String message1 = response.body().getMessage();
                            Toast.makeText(getApplicationContext(), message1 + "here", Toast.LENGTH_LONG).show();

                            //stopping progress
                            Utils.stopProgress();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage() + "error", Toast.LENGTH_LONG).show();
                            //stopping progress
                            Utils.stopProgress();

                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadedFile> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    if (t instanceof SocketTimeoutException) {
                        String message = "Socket Time out. Please try again.";
                        Log.d("Response is: ", message);
                    }

                }
            });


        }
    }

    /**
     * This function sends the image to the server for processing or uploading it on the server & get result success or failure.
     */

    private void sendImageDataToServer() {

        if (path != null) {

            Utils.showProgress(this, "Image is uploading, please wait");
            Web_Interface webInterface = Ret.getClient().create(Web_Interface.class);
            List<MultipartBody.Part> parts = new ArrayList<>();
            List<String> files = new ArrayList<>(); //These are the uris for the files to be uploaded
            files.add(path);
            MediaType mediaType = MediaType.parse("multipart/form-data");//Based on the Postman logs,it's not specifying Content-Type, this is why I've made this empty content/mediaType
            MultipartBody.Part[] fileParts = new MultipartBody.Part[files.size()];
            for (int i = 0; i < files.size(); i++) {
                File file = new File(files.get(i));
                RequestBody fileBody = RequestBody.create(mediaType, file);
                //Setting the file name as an empty string here causes the same issue, which is sending the request successfully without saving the files in the backend, so don't neglect the file name parameter.
                fileParts[i] = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
                Log.d("filedata ", file.getName() + " " + fileBody);
            }
            Log.d("file2upload", Arrays.toString(fileParts) + "" + RetrofitToken.token);

            Call<UploadedFile> call = webInterface.requestUpdateProfilePic(fileParts, RetrofitToken.token);
            call.enqueue(new Callback<UploadedFile>() {
                @Override
                public void onResponse(Call<UploadedFile> call, Response<UploadedFile> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        //if code is 200 and response is successfull means the agent is login successfully
                        //now setting flag to true to day started

                        String message1 = response.body().getMessage();
                        imageid = response.body().getBody().getId();
                        imagename = response.body().getBody().getName();
                        imagefilename = response.body().getBody().getFileName();

                        imagepath = response.body().getBody().getPath();
                        imagecreatedat = response.body().getBody().getCreatedAt();
                        imageupdatedat = response.body().getBody().getUpdatedAt();
                            if (attachmentArray.length() != 0) {
                                if (count > 0) {
                                    for (int i = 0; i < attachmentArray.length(); i++) {
                                        //String type = fetchOneAgentList.getAttachments().get(i).getType();
                                        //String sign = fetchOneAgentList.getAttachments().get(i).getType();
                                        try {
                                            if (attachmentArray.getJSONObject(i).get("type").equals("DISTRIBUTOR_SIGNATURE")) {

                                                count = 0;
                                                JSONObject attachmentObject = new JSONObject();
                                                attachmentObject = attachmentArray.getJSONObject(i);
                                                attachmentObject.put("id", imageid);
                                                attachmentObject.put("name", imagename);
                                                attachmentObject.put("fileName", response.body().getBody().getFileName());
                                                attachmentObject.put("type", "DISTRIBUTOR_SIGNATURE");
                                                attachmentArray.put(i, attachmentObject);
                                                attachmentArray2.put(i, attachmentObject);


                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (count1 > 0)
                                {
                                    for (int i = 0; i < attachmentArray.length(); i++) {
                                        //String type = fetchOneAgentList.getAttachments().get(i).getType();
                                        //String sign = fetchOneAgentList.getAttachments().get(i).getType();
                                        try {
                                            if (attachmentArray.getJSONObject(i).get("type").equals("USER_SIGNATURE")) {

                                                count1 = 0;
                                                JSONObject attachmentObject = new JSONObject();
                                                attachmentObject = attachmentArray.getJSONObject(i);
                                                attachmentObject.put("id", imageid);
                                                attachmentObject.put("name", imagename);
                                                attachmentObject.put("fileName", response.body().getBody().getFileName());
                                                attachmentObject.put("type", "USER_SIGNATURE");
                                                attachmentArray.put(i, attachmentObject);
                                                attachmentArray2.put(i, attachmentObject);


                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                            }
                            }


                        /*JSONObject attachmentObject = new JSONObject();
                        try {
                            attachmentObject.put("id", imageid);
                            attachmentObject.put("name", imagename);
                            attachmentObject.put("fileName", response.body().getBody().getFileName());
                            if (count > 0) {
                                attachmentObject.put("type", "DISTRIBUTOR_SIGNATURE");
                                Bitmap myBitmap = BitmapFactory.decodeFile(path);
                                distImage.setVisibility(View.VISIBLE);
                                distImage.setImageBitmap(myBitmap);

                                count = 0;
                            } else if (count1 > 0) {
                                attachmentObject.put("type", "USER_SIGNATURE");
                                Bitmap myBitmap = BitmapFactory.decodeFile(path);
                                userImage.setVisibility(View.VISIBLE);
                                userImage.setImageBitmap(myBitmap);
                                count1 = 0;
                            }
                            attachmentArray.put(attachmentObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
*/
                        //now we will send this image id and store name from spinner using retrofit
                        Utils.stopProgress();
                        Toasty.success(getApplicationContext(), message1).show();


                    } else {

                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.d("AgentLoginActivity", jObjError.getString("message"));
                            String message1 = response.body().getMessage();
                            Toast.makeText(getApplicationContext(), message1 + "here", Toast.LENGTH_LONG).show();

                            //stopping progress
                            Utils.stopProgress();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage() + "error", Toast.LENGTH_LONG).show();
                            //stopping progress
                            Utils.stopProgress();

                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadedFile> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    if (t instanceof SocketTimeoutException) {
                        String message = "Socket Time out. Please try again.";
                        Log.d("Response is: ", message);
                    }

                }
            });


        }

    }

    /**
     * This function returns the API results of Contract API & Sign Up API by passing all the required values to this function as arguments.
     *
     * @param titlevalue     This is the title of the user who is signing up as an agent.
     * @param firstname      This is the first name of the user who is signing up as an agent.
     * @param lastname       This is the last name of the user who is signing up as an agent.
     * @param username       This is the username of the user who is signing up as an agent.
     * @param authorityvalue This is the authority type value of the user who is signing up as an agent.
     * @param warehousevalue This is the warehouse value of the user who is signing up as an agent.
     * @param idvalue        This is the ID Type of the user who is signing up as an agent.
     * @param idnum          This is the ID number of the user who is signing up as an agent.
     * @param streetno       This is the street No of the user who is signing up as an agent.
     * @param streetname     This is the street Name of the user who is signing up as an agent.
     * @param suburb         This is the suburb of the user who is signing up as an agent.
     * @param city           This is the city of the user who is signing up as an agent.
     * @param province       This is the province of the user who is signing up as an agent.
     * @param postalcode     This is the postalcode of the user who is signing up as an agent.
     * @param email          This is the email of the user who is signing up as an agent.
     * @param mobno          This is the mobno of the user who is signing up as an agent.
     * @param altmobno       This is the altmobno of the user who is signing up as an agent.
     * @param signedat       This is the signedat (on which location he is signing up) of the user who is signing up as an agent.
     *                       <p>
     *                       also, we are having commisions,sims,cib values different for the differernt networks with common value of Daily rete
     *                       for all the networks.
     */
    private void contractagent(String titlevalue, String firstname, String lastname, String username, /*String password,*/ String authorityvalue, String warehousevalue,
                               String idvalue, String idnum, String streetno, String streetname, String suburb, String city, String province, String postalcode,
                               String email, String mobno, String altmobno, String network1, String dailyRate,
                               String simcost, String activationcom1, String ogr1,/*String cib1,*/ String sims1, String network2, /*String dailyRate2,*/
                               String activationcom2, String ogr2, String sims2, String network3, /*String dailyRate3,*/
                               String activationcom3, String ogr3, String sims3, String network4, /*String dailyRate4,*/
                               String activationcom4, String ogr4, String sims4, String signedat) {

        if (dailyRate.equals("")) {
            dailyRate = "0";
        }
        float DailyRate1, SimCost1, ActivationCom1, Ogr1;
        float DailyRate2, SimCost2, ActivationCom2, Ogr2;
        float DailyRate3, SimCost3, ActivationCom3, Ogr3;
        float DailyRate4, SimCost4, ActivationCom4, Ogr4;
        simcost = "1";
        ogr1 = "0";/*simcost2 = "1";ogr2 = "0";simcost3 = "1";ogr3 = "0";simcost4 = "1";ogr4 = "0";*/
        int Sims1, Sims2, Sims3, Sims4;

        String ricauser = null, ricapassword = null,ricagroup = null;
        progressBar.show();
        int parentId = Integer.parseInt(Pref.getUserId(this));

        DailyRate1 = Float.parseFloat(dailyRate);
        SimCost1 = Float.parseFloat(simcost);
        ActivationCom1 = Float.parseFloat(activationcom1);
        Ogr1 = Float.parseFloat(ogr1);
        //Cib1 = Float.parseFloat(cib1);
        Sims1 = sims1.trim().length() > 0? Integer.parseInt(sims1): 0;

        DailyRate2 = DailyRate1;
        SimCost2 = SimCost1;
        ActivationCom2 = Float.parseFloat(activationcom2);
        Ogr2 = Ogr1;
        //Cib2 = Float.parseFloat(cib2);
        Sims2 = sims2.trim().length() > 0? Integer.parseInt(sims2): 0;

        DailyRate3 = DailyRate1;
        SimCost3 = SimCost1;
        ActivationCom3 = Float.parseFloat(activationcom3);
        Ogr3 = Ogr1;
        //Cib3 = Float.parseFloat(cib3);
        Sims3 = sims3.trim().length() > 0? Integer.parseInt(sims3): 0;

        DailyRate4 = DailyRate1;
        SimCost4 = SimCost1;
        ActivationCom4 = Float.parseFloat(activationcom4);
        Ogr4 = Ogr1;
        //Cib4 = Float.parseFloat(cib4);
        Sims4 = sims4.trim().length() > 0? Integer.parseInt(sims4): 0;

   /*     if(status.equals("Enabled"))
            {
                enableid = Boolean.valueOf("true");

            }
        else
            {
                enableid = Boolean.valueOf("false");
            }*/
        JSONObject profileobject = new JSONObject();
        try {
            profileobject.put("idType", idvalue);
            profileobject.put("idNo", idnum);
            profileobject.put("ricaUser", ricauser);
            profileobject.put("ricaPassword", ricapassword);
            profileobject.put("ricaGroup", ricagroup);
            profileobject.put("email", email);
            profileobject.put("mobileNo", mobno);
            profileobject.put("altMobileNo", altmobno);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject addressObject = new JSONObject();
        try {
            addressObject.put("streetNo", streetno);
            addressObject.put("streetName", streetname);
            addressObject.put("suburb", suburb);
            addressObject.put("city", city);
            addressObject.put("province", province);
            addressObject.put("postalCode", postalcode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject commisionsobject1 = new JSONObject();
        try {
            commisionsobject1.put("network", network1);
            commisionsobject1.put("dailyRate", DailyRate1);
            commisionsobject1.put("simCost", SimCost1);
            commisionsobject1.put("activationCom", ActivationCom1);
            commisionsobject1.put("ogr", Ogr1);
            //      commisionsobject1.put("cib",Cib1);
            commisionsobject1.put("sims", Sims1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray commisionsArray = new JSONArray();
        commisionsArray.put(commisionsobject1);

        JSONObject commisionsobject2 = new JSONObject();
        try {
            commisionsobject2.put("network", network2);
            commisionsobject2.put("dailyRate", DailyRate2);
            commisionsobject2.put("simCost", SimCost1);
            commisionsobject2.put("activationCom", ActivationCom2);
            commisionsobject2.put("ogr", Ogr2);
            //commisionsobject2.put("cib",Cib2);
            commisionsobject2.put("sims", Sims2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        commisionsArray.put(commisionsobject2);

        JSONObject commisionsobject3 = new JSONObject();
        try {
            commisionsobject3.put("network", network3);
            commisionsobject3.put("dailyRate", DailyRate3);
            commisionsobject3.put("simCost", SimCost3);
            commisionsobject3.put("activationCom", ActivationCom3);
            commisionsobject3.put("ogr", Ogr3);
            //commisionsobject3.put("cib",Cib3);
            commisionsobject3.put("sims", Sims3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        commisionsArray.put(commisionsobject3);


        JSONObject commisionsobject4 = new JSONObject();
        try {
            commisionsobject4.put("network", network4);
            commisionsobject4.put("dailyRate", DailyRate4);
            commisionsobject4.put("simCost", SimCost4);
            commisionsobject4.put("activationCom", ActivationCom4);
            commisionsobject4.put("ogr", Ogr4);
            //commisionsobject4.put("cib",Cib4);
            commisionsobject4.put("sims", Sims4);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        commisionsArray.put(commisionsobject4);


        JSONObject authorityObject = new JSONObject();
        try {
            authorityObject.put("id", authorityId);
            authorityObject.put("name", authorityname);
            authorityObject.put("authority", authority);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("title", titlevalue);
            paramObject.put("firstName", firstname);
            paramObject.put("lastName", lastname);
            paramObject.put("username", username);
            //paramObject.put("password", password);
            paramObject.put("enabled", false);
            paramObject.put("authority", authorityObject);
            paramObject.put("attachments", attachmentArray);
            paramObject.put("profile", profileobject);
            paramObject.put("address", addressObject);
            paramObject.put("commissions", commisionsArray);
            paramObject.put("parentId", parentId);
            paramObject.put("warehouseId", warehouseId);
            paramObject.put("signedAt", signedat);

            //paramObject.put("paymentAccounts",paymentsArray);

            RequestBody signupBody = RequestBody.create(MediaType.parse("application/json"), (paramObject).toString());
            Call<ContractResponse> signUpResponseCall = web_interface.requestContractResponse(signupBody);
            signUpResponseCall.enqueue(new Callback<ContractResponse>() {
                @Override
                public void onResponse(Call<ContractResponse> call, Response<ContractResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        if (response.body() != null) {
                            String success = response.body().getSuccess().toString();

                            if (success.equals("true")) {
                                progressBar.dismiss();
                                //String message = response.body().getMessage();
                                //Toast.makeText(SignUpAgent.this, message, Toast.LENGTH_SHORT).show();

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("id", response.body().getBody().getId());
                                    jsonObject.put("name", response.body().getBody().getName());
                                    jsonObject.put("fileName", response.body().getBody().getFileName());
                                    jsonObject.put("type", response.body().getBody().getType());
                                    attachmentArray2.put(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //Toast.makeText(SignUpAgent.this, response.code(), Toast.LENGTH_SHORT).show();

                                progressBar.show();
                                String sttsvalue = "";

                              /*  if(status.equals("Enabled"))
                                {
                                    sttsvalue="true";

                                }
                                else
                                {
                                    sttsvalue="false";
                                }*/
                                JSONObject profileobject = new JSONObject();
                                try {
                                    profileobject.put("id",fetchOneAgentList.getProfile().getId());
                                    profileobject.put("idType", idvalue);
                                    profileobject.put("idNo", idnum);
                                    profileobject.put("ricaUser", ricauser);
                                    profileobject.put("ricaPassword", ricapassword);
                                    profileobject.put("ricaGroup", ricagroup);
                                    profileobject.put("email", email);
                                    profileobject.put("mobileNo", mobno);
                                    profileobject.put("altMobileNo", altmobno);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JSONObject addressObject = new JSONObject();
                                try {
                                    addressObject.put("id",fetchOneAgentList.getAddress().getId());
                                    addressObject.put("streetNo", streetno);
                                    addressObject.put("streetName", streetname);
                                    addressObject.put("suburb", suburb);
                                    addressObject.put("city", city);
                                    addressObject.put("province", province);
                                    addressObject.put("postalCode", postalcode);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JSONObject commisionsobject1 = new JSONObject();
                                try {
                                    commisionsobject1.put("id",fetchOneAgentList.getCommissions().get(0).getId());
                                    commisionsobject1.put("network", network1);
                                    commisionsobject1.put("dailyRate", DailyRate1);
                                    commisionsobject1.put("simCost", SimCost1);
                                    commisionsobject1.put("activationCom", ActivationCom1);
                                    commisionsobject1.put("ogr", Ogr1);
                                    //                      commisionsobject1.put("cib",Cib1);
                                    commisionsobject1.put("sims", Sims1);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JSONArray commisionsArray = new JSONArray();
                                commisionsArray.put(commisionsobject1);

                                JSONObject commisionsobject2 = new JSONObject();
                                try {
                                    commisionsobject2.put("id",fetchOneAgentList.getCommissions().get(1).getId());
                                    commisionsobject2.put("network", network2);
                                    commisionsobject2.put("dailyRate", DailyRate2);
                                    commisionsobject2.put("simCost", SimCost2);
                                    commisionsobject2.put("activationCom", ActivationCom2);
                                    commisionsobject2.put("ogr", Ogr2);
                                    //                    commisionsobject2.put("cib",Cib2);
                                    commisionsobject2.put("sims", Sims2);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                commisionsArray.put(commisionsobject2);

                                JSONObject commisionsobject3 = new JSONObject();
                                try {
                                    commisionsobject3.put("id",fetchOneAgentList.getCommissions().get(2).getId());
                                    commisionsobject3.put("network", network3);
                                    commisionsobject3.put("dailyRate", DailyRate3);
                                    commisionsobject3.put("simCost", SimCost3);
                                    commisionsobject3.put("activationCom", ActivationCom3);
                                    commisionsobject3.put("ogr", Ogr3);
                                    //                  commisionsobject3.put("cib",Cib3);
                                    commisionsobject3.put("sims", Sims3);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                commisionsArray.put(commisionsobject3);


                                JSONObject commisionsobject4 = new JSONObject();
                                try {
                                    commisionsobject4.put("id",fetchOneAgentList.getCommissions().get(3).getId());
                                    commisionsobject4.put("network", network4);
                                    commisionsobject4.put("dailyRate", DailyRate4);
                                    commisionsobject4.put("simCost", SimCost4);
                                    commisionsobject4.put("activationCom", ActivationCom4);
                                    commisionsobject4.put("ogr", Ogr4);
                                    //                commisionsobject4.put("cib",Cib4);
                                    commisionsobject4.put("sims", Sims4);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                commisionsArray.put(commisionsobject4);


                                JSONObject authorityObject = new JSONObject();
                                try {
                                    authorityObject.put("id", authorityId);
                                    authorityObject.put("name", authorityname);
                                    authorityObject.put("authority", authority);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

/*        JSONObject attachmentObject = new JSONObject();
        try {
            attachmentObject.put("id",imageid);
            attachmentObject.put("name",imagename);
            attachmentObject.put("fileName",imagefilename);
            attachmentObject.put("path",imagepath);
            attachmentObject.put("createdAt",imagecreatedat);
            attachmentObject.put("updatedAt",imageupdatedat);
            attachmentObject.put("type",doctype);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray attachmentArray = new JSONArray();
        attachmentArray.put(attachmentObject);*/

                                JSONArray paymentsArray = new JSONArray();
                                Web_Interface web_interface1 = RetrofitToken.getClient().create(Web_Interface.class);
                                JSONObject paramObject = new JSONObject();
                                try {
                                    paramObject.put("id",userid);
                                    paramObject.put("customerId",customerid);
                                    paramObject.put("title", titlevalue);
                                    paramObject.put("firstName", firstname);
                                    paramObject.put("lastName", lastname);
                                    paramObject.put("username", username);
                                    paramObject.put("enabled", false);
                                    paramObject.put("authority", authorityObject);
                                    paramObject.put("attachments", attachmentArray2);
                                    paramObject.put("parentId", parentId);
                                    paramObject.put("warehouseId", warehouseId);
                                    paramObject.put("balance",fetchOneAgentList.getBalance());
                                    paramObject.put("createdAt",fetchOneAgentList.getCreatedAt());
                                    paramObject.put("updatedAt",fetchOneAgentList.getUpdatedAt());
                                    paramObject.put("profile", profileobject);
                                    paramObject.put("address", addressObject);
                                    //paramObject.put("password", password);
                                    paramObject.put("paymentAccounts", paymentsArray);
                                    paramObject.put("commissions", commisionsArray);
                                    paramObject.put("name",namestring);
                                    paramObject.put("signedAt", signedat);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                RequestBody signupBody = RequestBody.create(MediaType.parse("application/json"), (paramObject).toString());
                                Call<UpdateResponse> updateResponseCall = web_interface1.requestUpdateResponse(userid,signupBody);
                                updateResponseCall.enqueue(new Callback<UpdateResponse>() {
                                    @Override
                                    public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                                        if (response.isSuccessful() && response.code() == 200) {
                                            String success = response.body().getSuccess().toString();
                                            if (success.equals("true")) {
                                                progressBar.dismiss();
                                                String message = response.body().getMessage();
                                                Toasty.success(EditAgentForm.this, message, Toast.LENGTH_SHORT, true).show();
                                                Intent intent = new Intent(EditAgentForm.this, Stocks_dashboard.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
                                                // resolved exception
                                                progressBar.dismiss();
                                                Toast.makeText(EditAgentForm.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // resolved exception
                                            progressBar.dismiss();
                                            if (response.body() != null) {
                                                Toast.makeText(EditAgentForm.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UpdateResponse> call, Throwable t) {
                                        progressBar.dismiss();
                                        Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });


                            } else {
                                progressBar.dismiss();
                                Toast.makeText(EditAgentForm.this, response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.dismiss();
                            Toast.makeText(EditAgentForm.this, response.code() + " Error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.dismiss();
                        Toast.makeText(EditAgentForm.this, response.code() + " Error", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ContractResponse> call, Throwable t) {
                    progressBar.dismiss();
                    Toast.makeText(EditAgentForm.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function returns the warehouse list from the server on API call.
     */


    /**
     * This function returns the authority type list from the server on API call.
     */

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
        Intent i=new Intent(this, NetworkError.class);
        startActivity(i);
    }
}
