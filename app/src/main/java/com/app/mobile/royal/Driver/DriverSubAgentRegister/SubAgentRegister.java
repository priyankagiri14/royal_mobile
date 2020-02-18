package com.app.mobile.royal.Driver.DriverSubAgentRegister;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.mobile.royal.Driver.DriverSubAgentRegister.model.SubAgentResponseModel;
import com.app.mobile.royal.Driver.Driver_Dashboard.Stocks_dashboard;
import com.app.mobile.royal.R;
import com.app.mobile.royal.Web_Services.MyApp;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Utils.Pref;
import com.app.mobile.royal.Web_Services.Web_Interface;
import com.app.mobile.royal.utils.Utils;
import com.mapbox.api.geocoding.v5.models.CarmenContext;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import info.androidhive.fontawesome.FontTextView;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubAgentRegister extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SubAgentRegister";
    private Spinner idspinner, sp_country,sp_country_nationality;
    String id_type;
    private LinearLayout ll_passport, ll_id_no, ll_dl_document, ll_passport_document;
    private String passport_path = null;
    private String residence_path = null;
    private String dl_path = null, dl_base64_path = null;
    private String filePath = "", picturePathPassport = "", picturePathResidence = "", picturePathDL = "";
    private EditText et_first_name, et_last_name, et_id_no, et_phone_no, et_passport_no, et_passport_ex, et_country_code,
            et_areaCode, etAddress, et_street, et_city, et_postalcode,et_mobile_no, et_id_country_code;
    private List<String> mPath;
    List<CountryModel> countries = new ArrayList<>();
    List<CountryModel> nationality_array_list = new ArrayList<>();
    private TextView tv_passport, tv_passport_path, tv_dl_doc, tv_dl_doc_path, tv_residence, tv_residence_path;
    private String region_1;
    private Button btnSubAgentRegister;
    private String id_no;
    private int mYear, mMonth, mDay;
    private String passport_no;
    private Button bt_scan_id;
    private String passport_exp;
    private Bitmap selectedImage;
    private ImageView img_passport_doc, img_proof_residence, img_dl_doc;
    FontTextView img_datePicker;
    private String residence_base64_path = null;
    private String passport_base64_path = null;
    private String id_country_code;
    private String address_country_code, type_id;
    private DatePickerDialog.OnDateSetListener dateSetListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_agent_register);
        initViews();
    }

    /**
     * initialising views
     */
    private void initViews() {
        Log.d(TAG, "initViews: called");
        img_datePicker = (FontTextView) findViewById(R.id.img_date_picker);
        img_passport_doc = (ImageView) findViewById(R.id.img_passport_doc);
        img_dl_doc = (ImageView) findViewById(R.id.img_dl_doc);
        img_proof_residence = (ImageView) findViewById(R.id.img_proof_residence);
        btnSubAgentRegister = findViewById(R.id.btnSubAgentRegister);
        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);
        et_id_no = (EditText) findViewById(R.id.et_id_no);
        et_id_country_code = findViewById(R.id.et_id_country_code);
        et_passport_no = (EditText) findViewById(R.id.et_passport_no);
        et_passport_ex = (EditText) findViewById(R.id.et_passport_ex);
        et_phone_no = (EditText) findViewById(R.id.et_phone_no);
        et_mobile_no = findViewById(R.id.et_mobile_no);
        et_country_code = (EditText) findViewById(R.id.et_country_code);
        et_areaCode = (EditText) findViewById(R.id.et_areaCode);
        etAddress = (EditText) findViewById(R.id.etAddress);
        et_street = (EditText) findViewById(R.id.et_street);
        et_city = (EditText) findViewById(R.id.et_city);
        et_postalcode = (EditText) findViewById(R.id.et_postalcode);
        tv_passport = (TextView) findViewById(R.id.tv_passport);
        tv_passport_path = (TextView) findViewById(R.id.tv_passport_path);
        tv_dl_doc = (TextView) findViewById(R.id.tv_dl_doc);
        tv_dl_doc_path = (TextView) findViewById(R.id.tv_dl_doc_path);
        tv_residence = (TextView) findViewById(R.id.tv_residence);
        tv_residence_path = (TextView) findViewById(R.id.tv_residence_path);
        ll_id_no = findViewById(R.id.ll_id_no);
        ll_passport = findViewById(R.id.ll_passport);
        ll_dl_document = findViewById(R.id.ll_dl_document);
        ll_passport_document = findViewById(R.id.ll_passport_document);
        sp_country = findViewById(R.id.sp_country);
        sp_country_nationality = findViewById(R.id.sp_country_nationality);

        // now showing all the countries using this spinner
        showNationalitySpinner();

        // country Spinner currently not using
        String[] isoCountryCodes = Locale.getISOCountries();
        for (String countryCode : isoCountryCodes) {
            Locale locale = new Locale("", countryCode);
            String iso = locale.getISO3Country();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry();
            if (!"".equals(iso) && !"".equals(name) && !"".equals(code)) {
                countries.add(new CountryModel(iso, code, name));
            }
            Log.d(TAG, "initViews: couuntry name:" + code);
        }
        Log.d(TAG, "initViews: countrysize:" + countries.size());
        // now set adapter
        ArrayAdapter<CountryModel> country_spinner_adapter = new ArrayAdapter<>(SubAgentRegister.this,
                R.layout.support_simple_spinner_dropdown_item, countries);
        sp_country.setAdapter(country_spinner_adapter);
        sp_country.setSelection(246);
        sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String country_name = countries.get(position).getName();
                id_country_code = countries.get(position).getCode(); // 2digit country code
                Log.d(TAG, "onItemSelected: country:code:" + id_country_code + " name:" + country_name);
                // setting country code edit text
               int dialing_code = PhoneNumberUtil.createInstance(getApplicationContext()).getCountryCodeForRegion(id_country_code+"");
                Log.d(TAG, "onItemSelected: dialing code:"+dialing_code);
                et_country_code.setText(new StringBuilder().append(dialing_code) );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // id type spinner
        idspinner = findViewById(R.id.idspinner);
        idspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_type = parent.getSelectedItem().toString();
                if (id_type.equalsIgnoreCase("id")) {
                    Log.d(TAG, "onItemSelected: idType:" + id_type);
                    ll_passport.setVisibility(View.GONE);
                    ll_id_no.setVisibility(View.VISIBLE);
                    // for photos pick layout
                        type_id = id_type;
                    ll_dl_document.setVisibility(View.VISIBLE);
                    ll_passport_document.setVisibility(View.GONE);
                }
                if (id_type.equalsIgnoreCase("passport")) {
                    Log.d(TAG, "onItemSelected: idType:" + id_type);
                    type_id = id_type;
                    ll_id_no.setVisibility(View.GONE);
                    ll_passport.setVisibility(View.VISIBLE);
                    // for photos pick layout
                    ll_dl_document.setVisibility(View.GONE);
                    ll_passport_document.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // implementing mapbox address fetch
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapBoxAddressPicker();
            }
        });


        // button submit on click listener
        btnSubAgentRegister.setOnClickListener(this);
        tv_passport.setOnClickListener(this);
        tv_dl_doc.setOnClickListener(this);
        tv_residence.setOnClickListener(this);
        img_datePicker.setOnClickListener(this);
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
        ArrayAdapter<CountryModel> nationality_spinner_adapter = new ArrayAdapter<>(SubAgentRegister.this,
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
                // setting value to country code in contact section
                et_id_country_code.setText(new StringBuilder().append(dialing_code) );
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

    /**
     *
     * @param path - images path
     * @return base64 string
     */
    public  String getBase64FromFile(String path)
    {
        Bitmap bmp = null;
        ByteArrayOutputStream baos = null;
        byte[] baat = null;
        String encodeString = null;
        try
        {
            bmp = BitmapFactory.decodeFile(path);
            baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            baat = baos.toByteArray();
            encodeString = Base64.encodeToString(baat, Base64.DEFAULT);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return encodeString;
    }


    /**
     * method for initiating address picker
     */
    private void startMapBoxAddressPicker() {
        Log.d(TAG, "startMapBoxAddressPicker: calleed");
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

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /**
         * for place address
         */
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            PlaceAutocomplete.clearRecentHistory(this);

            if (feature != null) {

                Log.d(TAG, "onActivityResult json: " + feature.toJson());

                List<CarmenContext> featureList = feature.context();
                Log.d(TAG, "onActivityResult: featre:" + featureList);
                String place_name = feature.text();

                assert featureList != null;
                if (featureList.size() > 0) {
                    for (int i = 0; i < featureList.size(); i++) {
                        if (featureList.get(i).id().contains("postcode")) {
                            et_postalcode.setText(featureList.get(i).text());
                        }

                        if (featureList.get(i).id().contains("place")) {
                            et_street.setText(featureList.get(i).text());
                            etAddress.setText(new StringBuilder().append(place_name).append(", ").append(featureList.get(i).text()));
                        }

                        if (featureList.get(i).id().contains("region")) {
                            region_1 = featureList.get(i).text();
                        }
                        if (featureList.get(i).id().contains("country")) {
                            // getting short code from country
                            address_country_code = featureList.get(i).shortCode();
                            Log.d(TAG, "onActivityResult: address countrycode:"+address_country_code);
                            if (region_1 != null) {
                                et_city.setText(new StringBuilder().append(region_1).append(", ").append(featureList.get(i).text()));
                            } else {
                                Toast.makeText(this, "Address not found, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                }

            }

        }


        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {

            if(filePath.equals("1"))
            {       //for passport proof
                mPath = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
                img_passport_doc.setImageBitmap(BitmapFactory.decodeFile(mPath.get(0)));
                img_passport_doc.setVisibility(View.VISIBLE);
                passport_path=mPath.get(0);

                    passport_base64_path = getBase64FromFile(passport_path);
                Log.d(TAG, "onActivityResult: "+passport_path+"\n"+"base 64:"+passport_base64_path);
                /*DIR_PATH=mPath.get(0)+""; // A valid file path
                File file = new File(DIR_PATH);
                String getDirectoryPath = file.getParent(); // Only return path if physical file exist else return null
                Log.d(TAG,getDirectoryPath);*/

            }
            else if(filePath.equals("2"))
            {
                //for driving license proof
                mPath = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
                img_dl_doc.setImageBitmap(BitmapFactory.decodeFile(mPath.get(0)));
                img_dl_doc.setVisibility(View.VISIBLE);
                dl_path=mPath.get(0);

                    dl_base64_path = getBase64FromFile(dl_path);

                Log.d(TAG, "onActivityResult: "+dl_path+"\n"+"base 64:"+dl_base64_path);
            }
            else if(filePath.equals("3"))
            {
                //for proof residence
                mPath = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
                img_proof_residence.setImageBitmap(BitmapFactory.decodeFile(mPath.get(0)));
                img_proof_residence.setVisibility(View.VISIBLE);
                residence_path=mPath.get(0);

                    residence_base64_path = getBase64FromFile(residence_path);

                Log.d(TAG, "onActivityResult: "+residence_path+"\n"+"base 64:"+residence_base64_path);
            }
        }
    }


    /**
     * register button on click
     * @param v - view id for different button clicks
     */
    @Override
    public void onClick(View v) {
        //we will validate the fields
        String fname = et_first_name.getText().toString().trim();
        String lname = et_last_name.getText().toString().trim();
        String id_mobile_no = et_mobile_no.getText().toString().trim();
        id_no = et_id_no.getText().toString().trim();
        passport_no = et_passport_no.getText().toString();
        passport_exp = et_passport_ex.getText().toString();
        String phone = et_phone_no.getText().toString().trim();
        String country_code = et_id_country_code.getText().toString().trim();
        String area_code = et_areaCode.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String street = et_street.getText().toString().trim();
        String city = et_city.getText().toString().trim();
        String postal_code = et_postalcode.getText().toString().trim();
        Log.d(TAG, "onClick: passportdate:"+passport_exp);


        /**
         *  button register click
         */
        if (v.getId() == R.id.btnSubAgentRegister) {

            /**
             * checking if type is id
             */
            if (ll_id_no.getVisibility() == View.VISIBLE) {

                if (TextUtils.isEmpty(fname)) {
                    et_first_name.setError("First Name Required*");
                    et_first_name.requestFocus();
                } else if (TextUtils.isEmpty(lname)) {
                    et_last_name.setError("Last Name Required*");
                    et_last_name.requestFocus();
                }
                else if (TextUtils.isEmpty(id_mobile_no)) {
                    et_mobile_no.setError("Mobile Number Required");
                    et_mobile_no.requestFocus();
                }
                else if (id_mobile_no.length() != 10) {
                    Toast.makeText(this, "Mobile number should be of 10 characters long", Toast.LENGTH_LONG).show();
                    et_id_no.requestFocus();
                }
                else if (TextUtils.isEmpty(id_no)) {
                    et_id_no.setError("Identification Number Required");
                    et_id_no.requestFocus();
                }
                else if (id_no.length() != 13) {
                    Toast.makeText(this, "Identification Number should be of 13 characters long", Toast.LENGTH_LONG).show();
                    et_id_no.requestFocus();
                }
                else if (TextUtils.isEmpty(country_code)) {
                    et_country_code.setError("Country Code Required*");
                    et_country_code.requestFocus();
                }
                /*else if (TextUtils.isEmpty(country_code)) {
                    et_country_code.setError("Country Code Required*");
                    et_country_code.requestFocus();
                } else if (TextUtils.isEmpty(area_code)) {
                    et_areaCode.setError("Area Code Required*");
                    et_areaCode.requestFocus();
                }*/ else if (TextUtils.isEmpty(phone)) {
                    et_phone_no.setError("Contact Number Required*");
                    et_phone_no.requestFocus();
                } else if (TextUtils.isEmpty(address)) {
                    etAddress.setError("Address Required*");
                    etAddress.requestFocus();
                } else if (TextUtils.isEmpty(street)) {
                    et_street.setError("Street Required*");
                    et_street.requestFocus();
                } else if (TextUtils.isEmpty(city)) {
                    et_city.setError("City Required*");
                    et_city.requestFocus();
                } else if (TextUtils.isEmpty(postal_code)) {
                    et_postalcode.setError("Postal Code Required*");
                    et_postalcode.requestFocus();
                } else if (dl_path == null) {
                    //means if user hasn't selected any image
                    Toast.makeText(this, "Please upload Driving License Image*", Toast.LENGTH_SHORT).show();
                    tv_dl_doc_path.setText(R.string.no_file_chosen);
                    tv_dl_doc_path.setVisibility(View.VISIBLE);

                } else if (residence_path == null) {
                    //means if user hasn't selected any image
                    Toast.makeText(this, "Please upload Residence Image*", Toast.LENGTH_SHORT).show();
                    tv_residence_path.setText(R.string.no_file_chosen);
                    tv_residence_path.setVisibility(View.VISIBLE);
                }
                else {
                    String areaCode = phone.substring(0,2);
                    Log.d(TAG, "onClick: areacode:"+areaCode);
                    String agent_name = Pref.getFirstName(MyApp.getContext());
                    String contact_number = phone.substring(2);
                    Log.d(TAG, "onClick: contact number:"+contact_number);
                    sendSignupResponseToServer(agent_name,id_mobile_no,fname,lname,id_country_code,type_id,id_no,
                            passport_no,passport_exp,country_code,areaCode,contact_number,
                            address,address_country_code,postal_code,street,city,
                            passport_base64_path,dl_base64_path,residence_base64_path);
                }
            }

            /**
             * checking if type is passport
             */
            if (ll_passport.getVisibility() == View.VISIBLE) {

                if (TextUtils.isEmpty(fname)) {
                    et_first_name.setError("First Name Required*");
                    et_first_name.requestFocus();
                } else if (TextUtils.isEmpty(lname)) {
                    et_last_name.setError("Last Name Required*");
                    et_last_name.requestFocus();
                }
                else if (TextUtils.isEmpty(id_mobile_no)) {
                    et_mobile_no.setError("Mobile Number Required");
                    et_mobile_no.requestFocus();
                }
                else if (id_mobile_no.length() != 10) {
                    Toast.makeText(this, "Mobile number should be of 10 characters long", Toast.LENGTH_LONG).show();
                    et_id_no.requestFocus();
                }
                else if (TextUtils.isEmpty(passport_no) || TextUtils.isEmpty(passport_exp)) {
                    Toast.makeText(this, "Passport Fields Cannot Be Empty", Toast.LENGTH_LONG).show();

                }
                else if (TextUtils.isEmpty(country_code)) {
                    et_country_code.setError("Country Code Required*");
                    et_country_code.requestFocus();
                }
                /*else if (TextUtils.isEmpty(country_code)) {
                    et_country_code.setError("Country Code Required*");
                    et_country_code.requestFocus();
                } else if (TextUtils.isEmpty(area_code)) {
                    et_areaCode.setError("Area Code Required*");
                    et_areaCode.requestFocus();
                } */else if (TextUtils.isEmpty(phone)) {
                    et_phone_no.setError("Phone Number Required*");
                    et_phone_no.requestFocus();
                } else if (TextUtils.isEmpty(address)) {
                    etAddress.setError("Address Required*");
                    etAddress.requestFocus();
                } else if (TextUtils.isEmpty(street)) {
                    et_street.setError("Street Required*");
                    et_street.requestFocus();
                } else if (TextUtils.isEmpty(city)) {
                    et_city.setError("City Required*");
                    et_city.requestFocus();
                } else if (TextUtils.isEmpty(postal_code)) {
                    et_postalcode.setError("Postal Code Required*");
                    et_postalcode.requestFocus();
                } else if (passport_path == null) {
                    //means if user hasn't selected any image
                    Toast.makeText(this, "Please upload Passport Image*", Toast.LENGTH_SHORT).show();
                    tv_passport_path.setText(R.string.no_file_chosen);
                    tv_passport_path.setVisibility(View.VISIBLE);

                } else if (residence_path == null) {
                    //means if user hasn't selected any image
                    Toast.makeText(this, "Please upload Residence Image*", Toast.LENGTH_SHORT).show();
                    tv_residence_path.setText(R.string.no_file_chosen);
                    tv_residence_path.setVisibility(View.VISIBLE);

                } else {
                    // now removing area code from contact_no_edit_text to get only contact number
                    String contact_number = phone.substring(2);
                    Log.d(TAG, "onClick: contact number:"+contact_number);
                    //check if passport fields
                    //send data to presenter class
                    Log.d(TAG, "DATA SENT TO SUB AGENT PRESENTER");
                    // extracting two digit area code from contact_no_edit_text
                        String areaCode = phone.substring(1,2);
                    Log.d(TAG, "onClick: areacode:"+areaCode);
                    String agent_name = Pref.getFirstName(MyApp.getContext());
                    sendSignupResponseToServer(agent_name,id_mobile_no,fname,lname,id_country_code,type_id,id_no,
                            passport_no,passport_exp,country_code,areaCode,contact_number,
                            address,address_country_code,postal_code,street,city,
                            passport_base64_path,dl_base64_path,residence_base64_path);
                }
            }
        }

        /**
         * date_picker image view click
         */
        else if (v.getId() == R.id.img_date_picker) {
                final Calendar c = Calendar.getInstance();

                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                showDate();


            }

        /**
         * choose passport text click
         */
        else if (v.getId() == R.id.tv_passport) {

                Log.d(TAG, "START IMAGE CHOOSER FOR PASSPORT");
                showImageChooser();
                tv_passport_path.setVisibility(View.GONE);
                filePath = "1";

            } else if (v.getId() == R.id.tv_dl_doc) {
                Log.d(TAG, "START IMAGE CHOOSER FOR DRIVING LICENSE");
                showImageChooser();
                tv_dl_doc_path.setVisibility(View.GONE);
                filePath = "2";


            } else if (v.getId() == R.id.tv_residence) {
                Log.d(TAG, "START IMAGE CHOOSER FOR RESIDENCE");
                showImageChooser();
                tv_residence_path.setVisibility(View.GONE);
                filePath = "3";

            }

            }

    /**
     * sending params to server
     * @param agent_name - agent name of logged in agent
     * @param id_mobile_no - mobile number 10 digits
     * @param fname - fname of sub agent
     * @param lname - lname of sub_agent
     * @param id_country_code - country code two digit(ZA) fetched from id_nationality spinner
     * @param type_id - id type- PASSPORT/ID
     * @param id_no - identification number of 13 digits
     * @param passport_no - passport number
     * @param passport_exp - passport expiry
     * @param country_code - country code in numbers (27) for S.A
     * @param area_code - area code
     * @param contact_number - contact number of sub_agent
     * @param address - address of sub_agent fetched from mapbox
     * @param address_country_code - country code two digit(ZA) fetched mapbox
     * @param postal_code - postal code fetched from mapbox
     * @param street - suburb fetched from mapbox
     * @param city - city fetched from mapbox
     * @param passport_base64_path - base64 passport string
     * @param dl_base64_path - base64 driving_license string
     * @param residence_base64_path - base64 address_proof string
     */
    private void sendSignupResponseToServer(String agent_name, String id_mobile_no, String fname, String lname,
                                            String id_country_code, String type_id, String id_no, String passport_no,
                                            String passport_exp, String country_code, String area_code,
                                            String contact_number, String address, String address_country_code,
                                            String postal_code, String street, String city,
                                            String passport_base64_path, String dl_base64_path, String residence_base64_path) {
        Log.d(TAG, "sendSignupResponseToServer: called");

        Utils.showProgress(this,"Please wait...");
        Log.d(TAG, "checkIfValidRica: called");

        Web_Interface webInterface = RetrofitToken.getClient().create(Web_Interface.class);
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("agent",agent_name);
            jsonObject.put("msisdn",id_mobile_no);
            jsonObject.put("firstName",fname);
            jsonObject.put("lastName",lname);
            jsonObject.put("idCountryCode",id_country_code); //za
            jsonObject.put("idType",type_id);
            jsonObject.put("idNo",id_no);
            jsonObject.put("passportNo",passport_no);
            jsonObject.put("passportExpiryDate",passport_exp);
            jsonObject.put("mobileCountryCode",country_code); //27
            jsonObject.put("mobileAreaCode",area_code);
            jsonObject.put("mobileNo",contact_number);
            jsonObject.put("address",address);
            jsonObject.put("countryCode",address_country_code); //za
            jsonObject.put("postalCode",postal_code);
            jsonObject.put("suburb",street);
            jsonObject.put("city",city);
            jsonObject.put("passportDocument",passport_base64_path);
            jsonObject.put("idDocument",dl_base64_path);
            jsonObject.put("proofOfResidence",residence_base64_path);

            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),(jsonObject).toString()) ;
            Call<SubAgentResponseModel> call = webInterface.requestSUbAgentSignUpResponse(requestBody);
            call.enqueue(new Callback<SubAgentResponseModel>() {
                @Override
                public void onResponse(Call<SubAgentResponseModel> call, Response<SubAgentResponseModel> response) {
                    if(response.isSuccessful() && response.code()==200) {
                        Utils.stopProgress();
                        String message = response.body().getMessage();
                        String success = response.body().getSuccess().toString();
                        if (success.equals("true")) {
                            Log.d(TAG, "onResponse: success true");
                            Toasty.success(getApplicationContext(), message).show();
                                Intent intent = new Intent(SubAgentRegister.this, Stocks_dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                        } else {
                                Toasty.info(getApplicationContext(),message).show();
                                Log.d("offlineRica", "onResponse: message: " + response.body().getMessage());
                            }
                        }

                    else{
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
                public void onFailure(Call<SubAgentResponseModel> call, Throwable t) {
                    Log.d(TAG,t.getLocalizedMessage());
                    Toasty.info(getApplicationContext(),t.getLocalizedMessage()).show();
                    Utils.stopProgress();

                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * showing date for passport type
     */
    private void showDate() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(SubAgentRegister.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month+1;
        String date = dayOfMonth+"/"+month+"/"+year;
        et_passport_ex.setText(date);
        Log.d(TAG, "onDateSet: "+et_passport_ex.getText());
        }
        };
    }

    private void validateDrivingLicense(String dl_no) {
        if (TextUtils.isEmpty(dl_no)) {
            //ll_id_no.setError("Driving License No. Required*");

        } else {

            passport_no = "test123";
            passport_exp = "test123";
            Log.d(TAG, "PASSPORT NO=> " + passport_no);
            Log.d(TAG, "PASSPORT EXP=> " + passport_exp);

        }
    }


    private void showImageChooser() {
        //picking image from gallery and camera

        Log.d(TAG, "IMAGE CHOOSER STARTED");
        new ImagePicker.Builder(SubAgentRegister.this)
                .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                .compressLevel(ImagePicker.ComperesLevel.HARD)
                .extension(ImagePicker.Extension.PNG)
                .scale(600, 600)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();
    }
}



