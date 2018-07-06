package com.homeguide.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.homeguide.R;
import com.homeguide.database.DbHelper;
import com.homeguide.utilities.AppController;
import com.homeguide.utilities.ConnectionDetector;
import com.homeguide.utilities.Constants;
import com.homeguide.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText editTextEmailID,editTextPassword;
    private TextView textForgotPassword;
    private ImageView imageBack;
    private Button btnLogin,btnSignUp;
    private ProgressDialog progressDialog;
    private DbHelper dbHelper;
    private String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper=new DbHelper(this);

        sharedPreferences=getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editTextEmailID=(EditText)findViewById(R.id.edit_email_phone);
        editTextPassword=(EditText)findViewById(R.id.edit_password);
        textForgotPassword=(TextView)findViewById(R.id.text_forgot_password);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.logging_user));

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                //startActivity(intent);
            }
        });

        //dbHelper.dropAndCreateAllTable();
        //createDatabase();



        btnLogin=(Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        btnSignUp=(Button)findViewById(R.id.btn_register);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void attemptLogin(){
        email=editTextEmailID.getText().toString();
        password=editTextPassword.getText().toString();
        View focus=null;
        boolean cancel=false;

        if(TextUtils.isEmpty(password)){
            editTextPassword.setError(getResources().getString(R.string.password_required));
            focus=editTextPassword;
            cancel=true;
        }

        if(TextUtils.isEmpty(email)){
            editTextEmailID.setError(getResources().getString(R.string.email_required));
            focus=editTextEmailID;
            cancel=true;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()){
            editTextEmailID.setError(getResources().getString(R.string.valid_email));
            focus=editTextEmailID;
            cancel=true;
        }

        if(cancel){
            focus.requestFocus();
            return;
        }else {
            if(ConnectionDetector.isNetworkAvailable(this)) {
                progressDialog.setMessage(getResources().getString(R.string.logging_user));
                //showProgress(true);
                editor.putString(Constants.FULL_NAME,"Vipin Dhama");
                editor.putString(Constants.EMAIL,email);
                editor.putString(Constants.MOBILE_NO,"9718181697");
                editor.putString(Constants.LOCATION,"Delhi");
                editor.putBoolean(Constants.IS_LOGGED_IN,true);
                editor.commit();
                DialogAndToast.showToast("Account created",LoginActivity.this);
                Intent intent=new Intent(LoginActivity.this,ExpenseListActivity.class);
                startActivity(intent);
                finish();

            }else {
                DialogAndToast.showDialog(getResources().getString(R.string.no_internet),this);
            }

        }
    }

    public void volleyRequest(){
        Map<String,String> params=new HashMap<>();
        if(email.contains("@"))
            params.put("username",email.split("@")[0]);
        else
            params.put("username",email);
        params.put("password",password);
        String url=getResources().getString(R.string.url)+"/Users/login";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,new JSONObject(),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress(false);
                try {
                    // JSONObject jsonObject=response.getJSONObject("response");
                    if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                        JSONObject dataObject=response.getJSONObject("data");
                        editor.putString(Constants.FULL_NAME,dataObject.getString("name"));
                        editor.putString(Constants.EMAIL,dataObject.getString("email"));
                        editor.putInt(Constants.MOBILE_NO,dataObject.getInt("mobile"));
                        editor.putString(Constants.LOCATION,dataObject.getString("city"));
                        editor.putInt(Constants.USER_TYPE_ID,dataObject.getInt("user_type_id"));
                        editor.putString(Constants.USERNAME,dataObject.getString("username"));
                        editor.putString(Constants.ROLE,dataObject.getString("role"));
                        editor.putString(Constants.ACTIVATE_KEY,dataObject.getString("activate_key"));
                        editor.putString(Constants.GUID,dataObject.getString("guid"));
                        editor.putString(Constants.FORGOT_PASSWORD_REQUEST_TIME,dataObject.getString("forgot_password_request_time"));
                        editor.putString(Constants.STATUS,dataObject.getString("status"));
                        editor.putString(Constants.TOKEN,dataObject.getString("token"));
                        editor.putString(Constants.CREATED,dataObject.getString("created"));
                        editor.putString(Constants.MODIFIED,dataObject.getString("modified"));
                        editor.putBoolean(Constants.IS_LOGGED_IN,true);
                        editor.commit();
                    }else {
                        DialogAndToast.showDialog(response.getString("message"),LoginActivity.this);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),LoginActivity.this);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                showProgress(false);
                DialogAndToast.showToast(getResources().getString(R.string.connection_error),LoginActivity.this);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void showProgress(Boolean show){
        if(show){
            progressDialog.show();;
        }else{
            progressDialog.hide();
        }
    }
}
