package com.app.mobile.royal.Driver.UpdateDriverRicaGroupName;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.app.mobile.royal.Driver.Driver_Dashboard.Stocks_dashboard;
import com.app.mobile.royal.R;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Utils.Pref;
import com.app.mobile.royal.Web_Services.Web_Interface;
import com.app.mobile.royal.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDriverRicaGroup extends AppCompatActivity {

    private static final String TAG = "UpdateDriverRicaGroup" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_driver_rica_group);
        initViews();
    }

    private void initViews() {
        Button btn_submit_rica = findViewById(R.id.btn_submit_rica);
        EditText et_rica_username = findViewById(R.id.et_rica_username);
        EditText et_rica_user_password = findViewById(R.id.et_rica_user_password);
        EditText et_rica_group_name = findViewById(R.id.et_rica_group_name);
        CheckBox cb_accept_rica_agreement = findViewById(R.id.cb_accept_rica_agreement);
        TextView tv_accept_agreement = findViewById(R.id.tv_accept_agreement);
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
                    Toast.makeText(UpdateDriverRicaGroup.this, "Please accept the agreement", Toast.LENGTH_SHORT).show();
                } else {
                    sendUpdateRicaDetailsToServer(username, password, group_name);
                }

            }

        });

    }


    private void sendUpdateRicaDetailsToServer(String username, String password, String group_name) {

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
                        Utils.stopProgress();
                        try {
                            JSONObject jObjSuccess = new JSONObject(response.body().string());
                            Log.d(TAG, jObjSuccess.getString("message"));
                            Toasty.success(getApplicationContext(), jObjSuccess.getString("message")).show();

                            // redirecting driver to stock dashboard
                            Intent intent = new Intent(UpdateDriverRicaGroup.this, Stocks_dashboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

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
                    Toast.makeText(UpdateDriverRicaGroup.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    }

