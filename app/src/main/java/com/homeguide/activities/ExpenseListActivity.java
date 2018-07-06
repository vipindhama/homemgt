package com.homeguide.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homeguide.R;
import com.homeguide.adapters.ExpenseAdapter;
import com.homeguide.database.DbHelper;
import com.homeguide.database.ExternalDatabase;
import com.homeguide.interfaces.OnMyItemClickListener;
import com.homeguide.models.MyExpense;
import com.homeguide.utilities.AppController;
import com.homeguide.utilities.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseListActivity extends BaseActivity implements OnMyItemClickListener {

    private final String TAG = "ExpenseList";

    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private List<MyExpense> myExpenseList,myExpenseOriginalList;
    private List<MyExpense> expenseDeletePositions;
    private TextView textViewTotalExp, textViewError;
    private TextView textViewFromDate, textViewToDate;
    private RelativeLayout relativeLayoutProgressBar;
    private RelativeLayout relativeLayoutFromDate, relativeLayoutToDate;
    private DatePickerDialog frpmDatePicker, toDatePicker;
    private String fromDate, toDate;
    private String monthTwoDigits = "";
    private String dayTwoDigits = "";
    private DbHelper dbHelper;
    private ExternalDatabase externalDatabase;
    private Menu menu;
    private SearchView searchView;
    private AppBarLayout appBarLayout,searchAppBarLayout;
    private int counter, expenseEditPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

    }

    private void initViews() {
        //dbHelper = new DbHelper(this);
        if(Utility.verifyStoragePermissions(this)){
            String root="";
            if(Utility.isExternalStorageAvailable()){
                root = Environment.getExternalStoragePublicDirectory("").toString();
            }

            root = root+"/Home Expense Guide/Database";
            externalDatabase = new ExternalDatabase(this,root);
        }

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
       // searchAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_search);
        recyclerView = findViewById(R.id.recycler_view);
        textViewTotalExp = findViewById(R.id.text_total_expenses);
        textViewError = findViewById(R.id.text_error);
        textViewFromDate = findViewById(R.id.text_from_date);
        textViewToDate = findViewById(R.id.text_to_date);
        relativeLayoutProgressBar = findViewById(R.id.relative_progress);
        relativeLayoutFromDate = findViewById(R.id.relative_from_date);
        relativeLayoutToDate = findViewById(R.id.relative_to_date);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar calendarPre = Calendar.getInstance();
        calendarPre.add(Calendar.DAY_OF_MONTH, -(day - 1));
        int yearPre = calendarPre.get(Calendar.YEAR);
        int monthPre = calendarPre.get(Calendar.MONTH);
        int dayPre = calendarPre.get(Calendar.DAY_OF_MONTH);

        if (month < 10) {
            monthTwoDigits = "0" + (month + 1);
        } else {
            monthTwoDigits = "" + (month + 1);
        }

        if (day < 10) {
            dayTwoDigits = "0" + day;
        } else {
            dayTwoDigits = "" + day;
        }
        toDate = year + "-" + monthTwoDigits + "-" + dayTwoDigits;

        if (monthPre < 10) {
            monthTwoDigits = "0" + (monthPre + 1);
        } else {
            monthTwoDigits = "" + (monthPre + 1);
        }

        if (dayPre < 10) {
            dayTwoDigits = "0" + dayPre;
        } else {
            dayTwoDigits = "" + dayPre;
        }
        fromDate = yearPre + "-" + monthTwoDigits + "-" + dayTwoDigits;

        textViewFromDate.setText(dayPre + " " + Utility.getMonthName(monthPre) + " " + yearPre);
        textViewToDate.setText(day + " " + Utility.getMonthName(month) + " " + year);

        frpmDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int yr, int mon, int dy) {
                textViewFromDate.setText(dy + " " + Utility.getMonthName(mon) + " " + yr);
                if (mon < 10) {
                    monthTwoDigits = "0" + (mon + 1);
                } else {
                    monthTwoDigits = "" + (mon + 1);
                }

                if (dy < 10) {
                    dayTwoDigits = "0" + dy;
                } else {
                    dayTwoDigits = "" + dy;
                }

                fromDate = year + "-" + monthTwoDigits + "-" + dayTwoDigits;
                setExpense();
            }
        }, yearPre, monthPre, dayPre);
        frpmDatePicker.setMessage("Select Start Date");


        toDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int yr, int mon, int dy) {
                textViewToDate.setText(dy + " " + Utility.getMonthName(mon) + " " + yr);
                if (mon < 10) {
                    monthTwoDigits = "0" + (mon + 1);
                } else {
                    monthTwoDigits = "" + (mon + 1);
                }

                if (dy < 10) {
                    dayTwoDigits = "0" + dy;
                } else {
                    dayTwoDigits = "" + dy;
                }

                toDate = year + "-" + monthTwoDigits + "-" + dayTwoDigits;
                setExpense();
            }
        }, year, month, day);
        toDatePicker.setMessage("Select End Date");

        relativeLayoutFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frpmDatePicker.show();
            }
        });

        relativeLayoutToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePicker.show();
            }
        });

        textViewTotalExp.setText("Total Expenses: " + String.format("%.02f", externalDatabase.getTotalExpenses(fromDate, toDate)) + " Rs");
        // showProgress(true);

        expenseDeletePositions = new ArrayList<>();
        myExpenseList = externalDatabase.getExpenses(fromDate, toDate);
        myExpenseOriginalList = externalDatabase.getExpenses(fromDate, toDate);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        expenseAdapter = new ExpenseAdapter(this, myExpenseList);
        expenseAdapter.setOnMyItemClickListener(this);
        recyclerView.setAdapter(expenseAdapter);

        //showProgress(false);
        if (myExpenseList.size() > 0) {
            showError(false, null);
        } else {
            showError(true, "No expenses.");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(ExpenseListActivity.this, CreateExpenseActivity.class);
                intent.putExtra("flag", "add");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppController.isExpenseAdded) {
            setExpense();
            AppController.isExpenseAdded = false;
        }
    }

    private void setExpense() {
        myExpenseList.clear();
        myExpenseOriginalList.clear();
        List<MyExpense> newList = externalDatabase.getExpenses(fromDate, toDate);
        for (MyExpense item : newList){
            myExpenseList.add(item);
            myExpenseOriginalList.add(item);
        }

        //showProgress(false);
        if (myExpenseList.size() > 0) {
            showError(false, null);
            expenseAdapter.notifyDataSetChanged();
        } else {
            showError(true, "No expenses.");
        }

        textViewTotalExp.setText("Total Expenses: " + String.format("%.02f", externalDatabase.getTotalExpenses(fromDate, toDate)) + " Rs");
    }

    private void showProgress(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.GONE);
            relativeLayoutProgressBar.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            relativeLayoutProgressBar.setVisibility(View.GONE);
        }
    }

    private void showError(boolean show, String msg) {
        if (show) {
            recyclerView.setVisibility(View.GONE);
            relativeLayoutProgressBar.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
            textViewError.setText(msg);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            relativeLayoutProgressBar.setVisibility(View.GONE);
            textViewError.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                searchView.clearFocus();
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                callSearch(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if(counter > 0){
                counter = 0;
                getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
                expenseAdapter.setItemLongPressedStarted(false);
                expenseAdapter.unSelectAllSelectedViews();
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(false);
                appBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }else{
                super.onBackPressed();
            }
            return true;
        } else if (id == R.id.action_delete) {
            if (expenseDeletePositions.size() == 1)
                showDeleteDialog("Are you sure want to delete this item?");
            else
                showDeleteDialog("Are you sure want to delete selected items?");

        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(ExpenseListActivity.this, CreateExpenseActivity.class);
            intent.putExtra("flag", "edit");
            intent.putExtra("id", "" + myExpenseList.get(expenseEditPosition).getId());
            startActivityForResult(intent, 1);
        }else if (id == R.id.action_drive) {
            Intent intent = new Intent(ExpenseListActivity.this, BackupDataActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void callSearch(String query){
        Log.i(TAG,"Query "+query);

        myExpenseList.clear();

        float totalPrice = 0f;

        for(MyExpense item : myExpenseOriginalList){
            if(item.getName().toLowerCase().contains(query.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                    item.getExpenseType().toLowerCase().contains(query.toLowerCase())){
                myExpenseList.add(item);
                totalPrice = totalPrice+item.getCost();
            }
        }

        textViewTotalExp.setText("Total Expenses: " + String.format("%.02f", totalPrice) + " Rs");

        expenseAdapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        // alertDialogBuilder.setTitle("Oops...No internet");
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        for (MyExpense myExpense : expenseDeletePositions) {
                            externalDatabase.deleteExpenseItem(myExpense.getId());
                            myExpenseList.remove(myExpense);
                            Log.i(TAG,"Removed "+myExpense.getName());
                           // expenseAdapter.notifyItemRemoved(myExpenseList.indexOf(myExpense));
                        }
                        expenseAdapter.clearSelectedMap();
                        expenseAdapter.notifyDataSetChanged();
                        counter = 0;
                        getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
                        expenseAdapter.setItemLongPressedStarted(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        menu.getItem(0).setVisible(false);
                        menu.getItem(1).setVisible(false);
                        appBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        textViewTotalExp.setText("Total Expenses: " + String.format("%.02f", externalDatabase.getTotalExpenses(fromDate, toDate)) + " Rs");
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public void onItemLongClicked(int position) {
        if (counter == 0) {
            counter++;
            getSupportActionBar().setTitle("" + counter);
           // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            expenseAdapter.setItemLongPressedStarted(true);
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.teal_300));
            expenseEditPosition = position;
            Log.i(TAG,"id "+myExpenseList.get(position).getId()+" maxid "+externalDatabase.getExpensesId());
        } else {
            counter++;
            menu.getItem(0).setVisible(false);
            getSupportActionBar().setTitle("" + counter);

        }

        expenseDeletePositions.add(myExpenseList.get(position));
        Log.i(TAG,"size "+expenseDeletePositions.size());
    }

    @Override
    public void onItemLongUnClicked(int position) {
        if (counter > 1) {
            counter--;
            getSupportActionBar().setTitle("" + counter);

        } else {
            counter = 0;
            getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
            expenseAdapter.setItemLongPressedStarted(false);
           // getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        // Integer obj = expenseDeletePositions.get(position);
        expenseDeletePositions.remove(myExpenseList.get(position));
        Log.i(TAG,"size "+expenseDeletePositions.size());
    }

    @Override
    public void onCounterIncreased() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data != null) {
            // fetch the message String
            MyExpense myExpense = externalDatabase.getExpense(data.getStringExtra("id"));
            MyExpense preItem = myExpenseList.get(expenseEditPosition);
            preItem.setName(myExpense.getName());
            preItem.setExpenseBy(myExpense.getExpenseBy());
            preItem.setDescription(myExpense.getDescription());
            preItem.setExpenseType(myExpense.getExpenseType());
            preItem.setDate(myExpense.getDate());
            preItem.setCost(myExpense.getCost());
            preItem.setQty(myExpense.getQty());
            preItem.setClicked(false);
            counter = 0;
            expenseAdapter.clearSelectedMap(expenseEditPosition);
            expenseAdapter.notifyItemChanged(expenseEditPosition);
            getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
            expenseAdapter.setItemLongPressedStarted(false);
          //  getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            textViewTotalExp.setText("Total Expenses: " + String.format("%.02f", externalDatabase.getTotalExpenses(fromDate, toDate)) + " Rs");
        }

    }

    @Override
    public void onBackPressed(){
        if(counter > 0){
            counter = 0;
            getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
            expenseAdapter.setItemLongPressedStarted(false);
            expenseAdapter.unSelectAllSelectedViews();
          //  getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }else{
            super.onBackPressed();
        }
    }
}