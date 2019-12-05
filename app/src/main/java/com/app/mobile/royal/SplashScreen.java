package com.app.mobile.royal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.mobile.royal.Agent.Agent_Mainactivity;
import com.app.mobile.royal.Agent_Login.RefreshToken;
import com.app.mobile.royal.Driver.Driver_Dashboard.Stocks_dashboard;
import com.app.mobile.royal.Navigation_main.Navigation_Main;
import com.app.mobile.royal.Web_Services.MyApp;
import com.app.mobile.royal.Web_Services.RefreshAccessToken;
import com.app.mobile.royal.Web_Services.Utils.Pref;
import com.app.mobile.royal.Web_Services.Web_Interface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.mobile.royal.SharedPreference.SharedPref.AGENT;
import static com.app.mobile.royal.SharedPreference.SharedPref.DRIVER;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT=1000;
    public static SharedPreferences mSharedPreferences,aSharedPreferences;
    String accessToken,expirytime;

    /**
     * Thus method is initializing all the design components which will be used further for some functionalty.
     * This function will check for the token stored in the phone.
     * if the token is available & have expiry time then it will jump to the activity according to the token type.
     * if the token is available & does not have expiry time then it will refresh the token with 200 response from server &
     * jump to the activity according to the token type.
     * if the token is not available then it will move to the logi screen of the app to put the login credentials to validate for
     * the user session
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSharedPreferences = getSharedPreferences("Driver", Context.MODE_PRIVATE);
                aSharedPreferences = getSharedPreferences("Agent",Context.MODE_PRIVATE);

                // mSharedPreferences = getSharedPreferences("Agent", Context.MODE_PRIVATE);
                if(mSharedPreferences.contains(DRIVER)){
                    Web_Interface web_interface = RefreshAccessToken.getClient().create(Web_Interface.class);
                    Call<RefreshToken> refreshTokenCall = web_interface.requestRefreshToken();
                    refreshTokenCall.enqueue(new Callback<RefreshToken>() {
                        @Override
                        public void onResponse(Call<RefreshToken> call, Response<RefreshToken> response) {
                            if (response.isSuccessful() && response.code() == 200) {
                                if (response.body() != null) {
                                    accessToken = response.body().getAccessToken();
                                    expirytime = response.body().getExpiresIn().toString();
                                    String token = "Bearer " + accessToken;
                                    Pref.putToken(MyApp.getContext(), token);
                                    Log.d("onResponse: ", token);
                                    Log.d("onResponse: ", expirytime);
                                    Intent intent = new Intent(SplashScreen.this, Stocks_dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else
                            {
                                Toast.makeText(SplashScreen.this, "Your Session has Expired.. Please Login again..!", Toast.LENGTH_SHORT).show();
                                mSharedPreferences = getSharedPreferences("Driver", Context.MODE_PRIVATE);
                                if (mSharedPreferences.contains("Driver")) {
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    Intent i = new Intent(SplashScreen.this, Navigation_Main.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();

                                }
                                else
                                {
                                    Intent i = new Intent(SplashScreen.this, Navigation_Main.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshToken> call, Throwable t) {
                            Intent i = new Intent(SplashScreen.this, Navigation_Main.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                           /* Toast.makeText(SplashScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);*/
                        }
                    });

                }
                else if(aSharedPreferences.contains(AGENT)){

                    Web_Interface web_interface = RefreshAccessToken.getClient().create(Web_Interface.class);
                    Call<RefreshToken> refreshTokenCall = web_interface.requestRefreshToken();
                    refreshTokenCall.enqueue(new Callback<RefreshToken>() {
                        @Override
                        public void onResponse(Call<RefreshToken> call, Response<RefreshToken> response) {
                            if (response.isSuccessful() && response.code() == 200) {
                                if (response.body() != null) {
                                    accessToken = response.body().getAccessToken();
                                    expirytime = response.body().getExpiresIn().toString();
                                    String token = "Bearer " + accessToken;
                                    Pref.putToken(MyApp.getContext(), token);
                                    Log.d("onResponse: ", token);
                                    Log.d("onResponse: ", expirytime);
                                    Intent intent = new Intent(SplashScreen.this, Agent_Mainactivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else
                            {
                                Toast.makeText(SplashScreen.this, "Your Session has Expired.. Please Login again..!", Toast.LENGTH_SHORT).show();
                                mSharedPreferences = getSharedPreferences("Driver", Context.MODE_PRIVATE);
                                if (mSharedPreferences.contains("Driver")) {
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    Intent i = new Intent(SplashScreen.this, Navigation_Main.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();

                                }
                                else
                                {
                                    Intent i = new Intent(SplashScreen.this, Navigation_Main.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshToken> call, Throwable t) {
                            Intent i = new Intent(SplashScreen.this, Navigation_Main.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                           /* Toast.makeText(SplashScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);*/
                        }
                    });

                }
                else
                {
                    Intent intent = new Intent(SplashScreen.this,Navigation_Main.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
