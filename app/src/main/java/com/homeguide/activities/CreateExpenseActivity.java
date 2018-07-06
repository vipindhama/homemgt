package com.homeguide.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.homeguide.R;
import com.homeguide.adapters.SingleItemAdapter;
import com.homeguide.database.DbHelper;
import com.homeguide.database.ExternalDatabase;
import com.homeguide.interfaces.OnSingleItemClickListener;
import com.homeguide.models.MyExpense;
import com.homeguide.utilities.AppController;
import com.homeguide.utilities.DialogAndToast;
import com.homeguide.utilities.Utility;

import java.util.Calendar;
import java.util.List;

public class CreateExpenseActivity extends AppCompatActivity implements OnSingleItemClickListener{

    private EditText editTextName,editTextExpenseBy,editTextDesc,editTextType,editTextPrice,editTextQty,editTextDate;
    private DatePickerDialog datePickerDialog;
    private String monthTwoDigits = "";
    private String dayTwoDigits = "";
    private String expenseDate;
    private DbHelper dbHelper;
    private String flag;
    private boolean isEdit;
    private MyExpense myExpense;
    private ExternalDatabase externalDatabase;
    private RecyclerView recyclerView,recyclerViewExpenseBy;
    private List<String> expenseTypeList,expenseByList;
    private List<String> expenseTypeOriginalList,expenseByOriginalList;
    private SingleItemAdapter expenseTypeAdapter,expenseByAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
    }

    private void initViews(){
       // dbHelper = new DbHelper(this);
        if(Utility.verifyStoragePermissions(this)){
            String root="";
            if(Utility.isExternalStorageAvailable()){
                root = Environment.getExternalStoragePublicDirectory("").toString();
            }

            root = root+"/Home Expense Guide/Database";
            externalDatabase = new ExternalDatabase(this,root);
        }
        flag = getIntent().getStringExtra("flag");
        editTextName = findViewById(R.id.edit_name);
        editTextExpenseBy = findViewById(R.id.edit_expense_by);
        editTextDesc = findViewById(R.id.edit_desc);
        editTextType = findViewById(R.id.edit_type);
        editTextPrice = findViewById(R.id.edit_price);
        editTextQty = findViewById(R.id.edit_qty);
        editTextDate = findViewById(R.id.edit_date);
        Button btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.recycler_view_expense_type);
        recyclerViewExpenseBy = findViewById(R.id.recycler_view_expense_by);

        expenseTypeList = externalDatabase.getExpenseTypes();
        expenseTypeOriginalList = externalDatabase.getExpenseTypes();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        expenseTypeAdapter = new SingleItemAdapter(this, expenseTypeList,"expenseType");
        expenseTypeAdapter.setOnSingleItemClickListener(this);
        recyclerView.setAdapter(expenseTypeAdapter);

        expenseByList = externalDatabase.getExpenseByNames();
        expenseByOriginalList = externalDatabase.getExpenseByNames();

//        Log.i("CreateExpense","Size "+expenseByList.size()+" "+expenseByList.get(0));
//        Log.i("CreateExpense","Size "+expenseByOriginalList.size()+" "+expenseByOriginalList.get(0));
        recyclerViewExpenseBy.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        recyclerViewExpenseBy.setLayoutManager(linearLayoutManager1);
        expenseByAdapter = new SingleItemAdapter(this, expenseByList,"expenseBy");
        expenseByAdapter.setOnSingleItemClickListener(this);
        recyclerViewExpenseBy.setAdapter(expenseByAdapter);

        if(flag.equals("edit")){
            btnAdd.setText("Update");
            setTitle("Update Expense");
            isEdit = true;
            myExpense = externalDatabase.getExpense(getIntent().getStringExtra("id"));
            editTextName.setText(myExpense.getName());
            editTextExpenseBy.setText(myExpense.getExpenseBy());
            editTextDesc.setText(myExpense.getDescription());
            editTextType.setText(myExpense.getExpenseType());
            editTextPrice.setText(""+myExpense.getCost());
            editTextQty.setText(""+myExpense.getQty());
            editTextDate.setText(myExpense.getDate());
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


        editTextType.addTextChangedListener(expenseTypeWatcher);

        editTextType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    recyclerView.setVisibility(View.VISIBLE);
                else
                    recyclerView.setVisibility(View.GONE);
            }
        });

        editTextExpenseBy.addTextChangedListener(expenseByWatcher);

        editTextExpenseBy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    recyclerViewExpenseBy.setVisibility(View.VISIBLE);
                else
                    recyclerViewExpenseBy.setVisibility(View.GONE);
            }
        });

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

    private void attemptAdd(){
        String name = editTextName.getText().toString();
        String expenseBy = editTextExpenseBy.getText().toString();
        String desc = editTextDesc.getText().toString();
        String type = editTextType.getText().toString();
        String price = editTextPrice.getText().toString();
        String qty = editTextQty.getText().toString();
        String date = editTextDate.getText().toString();

        if(TextUtils.isEmpty(name)){
            DialogAndToast.showDialog("Please enter expense name",this);
            return;
        }

        if(TextUtils.isEmpty(expenseBy)){
            DialogAndToast.showDialog("Please enter expense by",this);
            return;
        }

        if(TextUtils.isEmpty(desc)){
            DialogAndToast.showDialog("Please enter expense description",this);
            return;
        }

        if(TextUtils.isEmpty(type)){
            DialogAndToast.showDialog("Please enter expense type",this);
            return;
        }

        if(TextUtils.isEmpty(price)){
            DialogAndToast.showDialog("Please enter expense price",this);
            return;
        }

        if(TextUtils.isEmpty(qty)){
            DialogAndToast.showDialog("Please enter expense qty",this);
            return;
        }

        if(TextUtils.isEmpty(date)){
            DialogAndToast.showDialog("Please enter expense date",this);
            return;
        }

        if(!externalDatabase.isExpenseTypeExist(type)){
            expenseTypeOriginalList.add(0,type);
            expenseTypeList.add(0,type);
            expenseTypeAdapter.notifyItemInserted(0);
        }

        if(!externalDatabase.isExpenseByExist(expenseBy)){
            expenseByOriginalList.add(0,expenseBy);
            expenseByList.add(0,expenseBy);
            expenseByAdapter.notifyItemInserted(0);
        }


        MyExpense item = new MyExpense();
        item.setName(name);
        item.setExpenseBy(expenseBy);
        item.setQty(Integer.parseInt(qty));
        item.setDescription(desc);
        item.setExpenseType(type);
        item.setDate(date);
        item.setCost(Float.parseFloat(price));
        String timeStamp = Utility.getTimeStamp();

        if(isEdit){
            item.setId(myExpense.getId());
            externalDatabase.updateExpenseData(item,timeStamp);
            DialogAndToast.showToast("Expense updated",this);
            Intent intent=new Intent();
            intent.putExtra("id",""+item.getId());
            setResult(1,intent);
            finish();
        }else{
            item.setId(externalDatabase.getExpensesId());
            externalDatabase.insertExpenseItem(item,timeStamp,timeStamp);
            DialogAndToast.showToast("Expense added",this);
            editTextName.setText("");
            editTextExpenseBy.removeTextChangedListener(expenseByWatcher);
            editTextType.removeTextChangedListener(expenseTypeWatcher);
            editTextExpenseBy.setText("");
            editTextDesc.setText("");
            editTextType.setText("");
            editTextPrice.setText("");
            editTextQty.setText("");
            editTextDate.setText(Utility.getTimeStampWithoutTime());
            editTextExpenseBy.addTextChangedListener(expenseByWatcher);
            editTextType.addTextChangedListener(expenseTypeWatcher);
            AppController.isExpenseAdded = true;
           // finish();
        }
    }

    @Override
    public void onItemClicked(int position,String type) {
        if(type.equals("expenseType")){
            editTextType.setText(expenseTypeList.get(position));
            recyclerView.setVisibility(View.GONE);
        }else{
            editTextExpenseBy.setText(expenseByList.get(position));
            recyclerViewExpenseBy.setVisibility(View.GONE);
        }

    }

    TextWatcher expenseTypeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String type = s.toString();
            expenseTypeList.clear();
            for(String str : expenseTypeOriginalList){
                if(str.contains(type)){
                    expenseTypeList.add(str);
                }
            }

            if(expenseTypeList.size() == 0){
                if(recyclerView.getVisibility() == View.VISIBLE)
                    recyclerView.setVisibility(View.GONE);
            }else {
                if(recyclerView.getVisibility() == View.GONE)
                    recyclerView.setVisibility(View.VISIBLE);
            }

            expenseTypeAdapter.notifyDataSetChanged();
        }
    };

    TextWatcher expenseByWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String type = s.toString();
            expenseByList.clear();
            for(String str : expenseByOriginalList){
                if(str.contains(type)){
                    expenseByList.add(str);
                }
            }

            if(expenseByList.size() == 0){
                if(recyclerViewExpenseBy.getVisibility() == View.VISIBLE)
                    recyclerViewExpenseBy.setVisibility(View.GONE);
            }else {
                if(recyclerViewExpenseBy.getVisibility() == View.GONE)
                    recyclerViewExpenseBy.setVisibility(View.VISIBLE);
            }

            expenseByAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onBackPressed(){
        if(recyclerView.getVisibility() == View.VISIBLE)
            recyclerView.setVisibility(View.GONE);
        else if(recyclerViewExpenseBy.getVisibility() == View.VISIBLE)
            recyclerViewExpenseBy.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }
}
