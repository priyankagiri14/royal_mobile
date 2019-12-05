package com.app.mobile.royal.Driver.SignUpAgent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.app.mobile.royal.R;

public class SignUpAgent2 extends AppCompatActivity {

    EditText ricauser,ricapassword,ricagroup,
            email,mobno,altmobno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_agent2);


        ricauser = findViewById(R.id.ricauser);
        ricapassword = findViewById(R.id.ricapassword);
        ricagroup = findViewById(R.id.ricagroup);

        email = findViewById(R.id.email);
        mobno = findViewById(R.id.mobno);
        altmobno = findViewById(R.id.altmobno);

    }
}
