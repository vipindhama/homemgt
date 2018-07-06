package com.homeguide.fragments;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homeguide.R;
import com.homeguide.activities.BackupDataActivity;
import com.homeguide.activities.CreateExpenseActivity;
import com.homeguide.activities.ExpenseListActivity;
import com.homeguide.activities.MainActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseListFragment extends Fragment implements OnMyItemClickListener{

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
    private View rootView;

    private OnMyItemClickListener onMyItemClickListener;

    public void setOnMyItemClickListener(OnMyItemClickListener onMyItemClickListener) {
        this.onMyItemClickListener = onMyItemClickListener;
    }

    public ExpenseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_expense_list, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        //dbHelper = new DbHelper(this);
        if(Utility.verifyStoragePermissions(getActivity())){
            String root="";
            if(Utility.isExternalStorageAvailable()){
                root = Environment.getExternalStoragePublicDirectory("").toString();
            }

            root = root+"/Home Expense Guide/Database";
            externalDatabase = new ExternalDatabase(getActivity(),root);
        }

       // appBarLayout = (AppBarLayout) rootView.findViewById(R.id.appbar);
       // searchAppBarLayout = (AppBarLayout) rootView.findViewById(R.id.appbar_search);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        textViewTotalExp = rootView.findViewById(R.id.text_total_expenses);
        textViewError = rootView.findViewById(R.id.text_error);
        textViewFromDate = rootView.findViewById(R.id.text_from_date);
        textViewToDate = rootView.findViewById(R.id.text_to_date);
        relativeLayoutProgressBar = rootView.findViewById(R.id.relative_progress);
        relativeLayoutFromDate = rootView.findViewById(R.id.relative_from_date);
        relativeLayoutToDate = rootView.findViewById(R.id.relative_to_date);

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

        frpmDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

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


        toDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        expenseAdapter = new ExpenseAdapter(getActivity(), myExpenseList);
        expenseAdapter.setOnMyItemClickListener(this);
        recyclerView.setAdapter(expenseAdapter);

        //showProgress(false);
        if (myExpenseList.size() > 0) {
            showError(false, null);
        } else {
            showError(true, "No expenses.");
        }
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


    public void callSearch(String query){
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

    public void clearSelection(){
        expenseAdapter.setItemLongPressedStarted(false);
        expenseAdapter.unSelectAllSelectedViews();
    }

    public void deleteItems(){
        if (expenseDeletePositions.size() == 1)
            showDeleteDialog("Are you sure want to delete this item?");
        else
            showDeleteDialog("Are you sure want to delete selected items?");
    }

    public void editItem(){
        Intent intent = new Intent(getActivity(), CreateExpenseActivity.class);
        intent.putExtra("flag", "edit");
        intent.putExtra("id", "" + myExpenseList.get(expenseEditPosition).getId());
        startActivityForResult(intent, 1);
    }

    private void showDeleteDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                    //    getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
                        expenseAdapter.setItemLongPressedStarted(false);
                    //    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        onMyItemClickListener.onItemLongUnClicked(0);
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
          //  getSupportActionBar().setTitle("" + counter);
          //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            expenseAdapter.setItemLongPressedStarted(true);
            onMyItemClickListener.onItemLongClicked(position);
            expenseEditPosition = position;
            Log.i(TAG,"id "+myExpenseList.get(position).getId()+" maxid "+externalDatabase.getExpensesId());
        } else {
            counter++;
            onMyItemClickListener.onItemLongClicked(position);
           // getSupportActionBar().setTitle("" + counter);

        }

        expenseDeletePositions.add(myExpenseList.get(position));
        Log.i(TAG,"size "+expenseDeletePositions.size());
    }

    @Override
    public void onItemLongUnClicked(int position) {
        if (counter > 1) {
            counter--;
           // getSupportActionBar().setTitle("" + counter);
            onMyItemClickListener.onItemLongUnClicked(position);

        } else {
            counter = 0;
          //  getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
            expenseAdapter.setItemLongPressedStarted(false);
            onMyItemClickListener.onItemLongUnClicked(position);
         //   getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Integer obj = expenseDeletePositions.get(position);
        expenseDeletePositions.remove(myExpenseList.get(position));
        Log.i(TAG,"size "+expenseDeletePositions.size());
    }

    @Override
    public void onCounterIncreased() {

    }

    public int getCounter(){
      return counter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            //getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));

            onMyItemClickListener.onItemLongUnClicked(0);
            expenseAdapter.setItemLongPressedStarted(false);
            textViewTotalExp.setText("Total Expenses: " + String.format("%.02f", externalDatabase.getTotalExpenses(fromDate, toDate)) + " Rs");
        }
    }
}
