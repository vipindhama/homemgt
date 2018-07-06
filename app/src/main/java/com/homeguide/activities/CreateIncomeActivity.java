package com.homeguide.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.homeguide.R;
import com.homeguide.database.DbHelper;
import com.homeguide.database.ExternalDatabase;
import com.homeguide.models.MyIncome;
import com.homeguide.utilities.AppController;
import com.homeguide.utilities.DialogAndToast;
import com.homeguide.utilities.Utility;

import java.util.Calendar;

public class CreateIncomeActivity extends AppCompatActivity {

    private EditText editTextIncome,editTextIncomeBy,editTextIncomeSource,editTextDate;
    private Button btnAdd;
    private DatePickerDialog datePickerDialog;
    private String monthTwoDigits = "";
    private String dayTwoDigits = "";
    private DbHelper dbHelper;
    private String flag;
    private boolean isEdit;
    private ExternalDatabase externalDatabase;
    private MyIncome myIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_income);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
    }

    private void initViews() {
        // dbHelper = new DbHelper(this);
        if (Utility.verifyStoragePermissions(this)) {
            String root = "";
            if (Utility.isExternalStorageAvailable()) {
                root = Environment.getExternalStoragePublicDirectory("").toString();
            }

            root = root + "/Home Expense Guide/Database";
            externalDatabase = new ExternalDatabase(this, root);
        }
        flag = getIntent().getStringExtra("flag");
        editTextIncome = findViewById(R.id.edit_income);
        editTextIncomeBy = findViewById(R.id.edit_income_by);
        editTextIncomeSource = findViewById(R.id.edit_income_source);
        editTextDate = findViewById(R.id.edit_date);
        btnAdd = findViewById(R.id.btn_add);

        if(flag.equals("edit")){
            btnAdd.setText("Update");
            setTitle("Update Income");
            isEdit = true;
            myIncome = externalDatabase.getIncome(getIntent().getStringExtra("id"));
            editTextIncome.setText(String.format("%.02f",myIncome.getIncome()));
            editTextIncomeBy.setText(myIncome.getIncomePerson());
            editTextIncomeSource.setText(myIncome.getIncomeSource());
            editTextDate.setText(myIncome.getIncomeDate());
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        if(month < 10){
            monthTwoDigits = "0"+(month+1);
        }else{
            monthTwoDigits = ""+(month+1);
        }

        if(day < 10){
            dayTwoDigits = "0"+day;
        }else{
            dayTwoDigits = ""+day;
        }

        editTextDate.setText(year+"-"+monthTwoDigits+"-"+dayTwoDigits);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if(month < 10){
                    monthTwoDigits = "0"+(month+1);
                }else{
                    monthTwoDigits = ""+(month+1);
                }

                if(dayOfMonth < 10){
                    dayTwoDigits = "0"+dayOfMonth;
                }else{
                    dayTwoDigits = ""+dayOfMonth;
                }

                editTextDate.setText(year+"-"+monthTwoDigits+"-"+dayTwoDigits);

            }
        },year,month,day);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAdd();
            }
        });
    }

    private void attemptAdd() {
        String income = editTextIncome.getText().toString();
        String incomeBy = editTextIncomeBy.getText().toString();
        String incomeSource = editTextIncomeSource.getText().toString();
        String incomeDate = editTextDate.getText().toString();

        if(TextUtils.isEmpty(income)){
            DialogAndToast.showDialog("Please enter income",this);
            return;
        }

        if(TextUtils.isEmpty(incomeBy)){
            DialogAndToast.showDialog("Please enter income by",this);
            return;
        }

        if(TextUtils.isEmpty(incomeSource)){
            DialogAndToast.showDialog("Please enter income source",this);
            return;
        }

        if(TextUtils.isEmpty(incomeDate)){
            DialogAndToast.showDialog("Please enter income date",this);
            return;
        }

        MyIncome item = new MyIncome();
        item.setIncome(Float.parseFloat(income));
        item.setIncomePerson(incomeBy);
        item.setIncomeSource(incomeSource);
        item.setIncomeDate(incomeDate);
        String timeStamp = Utility.getTimeStamp();
        if(isEdit){
            item.setId(myIncome.getId());
            externalDatabase.updateIncomeData(item,timeStamp);
            DialogAndToast.showToast("Income updated",this);
            Intent intent=new Intent();
            intent.putExtra("id",""+item.getId());
            setResult(1,intent);
            finish();
        }else{
            item.setId(externalDatabase.getIncomeId());
            externalDatabase.insertIncomeItem(item,timeStamp,timeStamp);
            DialogAndToast.showToast("Income added",this);
            editTextIncome.setText("");
            editTextIncomeBy.setText("");
            editTextIncomeSource.setText("");
            editTextDate.setText(Utility.getTimeStampWithoutTime());
            AppController.isIncomeAdded = true;
        }
    }

}
