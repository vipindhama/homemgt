package com.homeguide.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.homeguide.R;
import com.homeguide.database.DbHelper;
import com.homeguide.utilities.ConnectionDetector;
import com.homeguide.utilities.Constants;
import com.homeguide.utilities.DialogAndToast;

public class RegisterActivity extends AppCompatActivity {

    private EditText editFullName,editEmail;
    private Button btnStart;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DbHelper dbHelper;
    private ProgressDialog progressDialog;
    private String fullName,email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper=new DbHelper(this);

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        sharedPreferences=getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();

        editFullName=(EditText)findViewById(R.id.edit_full_name);
        editEmail=(EditText)findViewById(R.id.edit_email);

        if(!sharedPreferences.getBoolean(Constants.IS_DATABASE_CREATED,false)){
           // createDatabase();
        }

        btnStart=(Button)findViewById(R.id.btn_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    public void attemptRegister(){
        fullName=editFullName.getText().toString();
        email=editEmail.getText().toString();
        password="12345";

        View focus=null;
        boolean cancel=false;


        if(TextUtils.isEmpty(email)){
            focus=editEmail;
            cancel=true;
            editEmail.setError(getResources().getString(R.string.email_required));
        }

        if(TextUtils.isEmpty(fullName)){
            focus=editFullName;
            cancel=true;
            editFullName.setError(getResources().getString(R.string.full_name_required));
        }

        if(cancel){
            focus.requestFocus();
            return;
        }else {
                if(ConnectionDetector.isNetworkAvailable(this)) {
                    progressDialog.setMessage(getResources().getString(R.string.creating_account));
                    editor.putString(Constants.FULL_NAME,fullName);
                    editor.putString(Constants.EMAIL,email);
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.commit();
                    DialogAndToast.showToast("Account created",RegisterActivity.this);
                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    DialogAndToast.showDialog(getResources().getString(R.string.no_internet),this);
                }

        }
    }


    public void showProgress(Boolean show){
        if(show){
            progressDialog.show();;
        }else{
            progressDialog.hide();
        }
    }
}
