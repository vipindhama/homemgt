package com.homeguide.fragments;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homeguide.R;
import com.homeguide.activities.CreateIncomeActivity;
import com.homeguide.adapters.ExpenseAdapter;
import com.homeguide.adapters.IncomeAdapter;
import com.homeguide.database.DbHelper;
import com.homeguide.database.ExternalDatabase;
import com.homeguide.interfaces.OnMyItemClickListener;
import com.homeguide.models.MyExpense;
import com.homeguide.models.MyIncome;
import com.homeguide.utilities.AppController;
import com.homeguide.utilities.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeListFragment extends Fragment implements OnMyItemClickListener{

    private final String TAG = "IncomeList";

    private RecyclerView recyclerView;
    private IncomeAdapter incomeAdapter;
    private List<MyIncome> myIncomeList,myIncomeOriginalList;
    private List<MyIncome> incomeDeletePositions;
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
    private int counter, incomeEditPosition;
    private View rootView;

    private OnMyItemClickListener onMyItemClickListener;

    public void setOnMyItemClickListener(OnMyItemClickListener onMyItemClickListener) {
        this.onMyItemClickListener = onMyItemClickListener;
    }


    public IncomeListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_income_list, container, false);
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
                setIncome();
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
                setIncome();
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

        textViewTotalExp.setText("Total Income: " + String.format("%.02f", externalDatabase.getTotalIncome(fromDate, toDate)) + " Rs");
        // showProgress(true);

        incomeDeletePositions = new ArrayList<>();
        myIncomeList = externalDatabase.getIncomes(fromDate, toDate);
        myIncomeOriginalList = externalDatabase.getIncomes(fromDate, toDate);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        incomeAdapter = new IncomeAdapter(getActivity(), myIncomeList);
        incomeAdapter.setOnMyItemClickListener(this);
        recyclerView.setAdapter(incomeAdapter);

        //showProgress(false);
        if (myIncomeList.size() > 0) {
            showError(false, null);
        } else {
            showError(true, "No incomes.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppController.isIncomeAdded) {
            setIncome();
            AppController.isIncomeAdded = false;
        }
    }

    private void setIncome() {
        myIncomeList.clear();
        myIncomeOriginalList.clear();
        List<MyIncome> newList = externalDatabase.getIncomes(fromDate, toDate);
        for (MyIncome item : newList){
            myIncomeList.add(item);
            myIncomeOriginalList.add(item);
        }

        //showProgress(false);
        if (myIncomeList.size() > 0) {
            showError(false, null);
            incomeAdapter.notifyDataSetChanged();
        } else {
            showError(true, "No incomes.");
        }

        textViewTotalExp.setText("Total Income: " + String.format("%.02f", externalDatabase.getTotalExpenses(fromDate, toDate)) + " Rs");
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


    public void clearSelection(){
        incomeAdapter.setItemLongPressedStarted(false);
        incomeAdapter.unSelectAllSelectedViews();
    }

    public void deleteItems(){
        if (incomeDeletePositions.size() == 1)
            showDeleteDialog("Are you sure want to delete this item?");
        else
            showDeleteDialog("Are you sure want to delete selected items?");
    }

    public void editItem(){
        Intent intent = new Intent(getActivity(), CreateIncomeActivity.class);
        intent.putExtra("flag", "edit");
        intent.putExtra("id", "" + myIncomeList.get(incomeEditPosition).getId());
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
                        for (MyIncome myIncome : incomeDeletePositions) {
                            externalDatabase.deleteIncomeItem(myIncome.getId());
                            myIncomeList.remove(myIncome);
                            Log.i(TAG,"Removed "+myIncome.getIncome());
                            // expenseAdapter.notifyItemRemoved(myExpenseList.indexOf(myExpense));
                        }
                        incomeAdapter.clearSelectedMap();
                        incomeAdapter.notifyDataSetChanged();
                        counter = 0;
                        //    getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
                        incomeAdapter.setItemLongPressedStarted(false);
                        onMyItemClickListener.onItemLongUnClicked(0);
                        textViewTotalExp.setText("Total Income: " + String.format("%.02f", externalDatabase.getTotalIncome(fromDate, toDate)) + " Rs");
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    @Override
    public void onItemLongClicked(int position) {
        if (counter == 0) {
            counter++;
            //  getSupportActionBar().setTitle("" + counter);
            //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            incomeAdapter.setItemLongPressedStarted(true);
            onMyItemClickListener.onItemLongClicked(position);
            incomeEditPosition = position;
            Log.i(TAG,"id "+myIncomeList.get(position).getId()+" maxid "+externalDatabase.getIncomeId());
        } else {
            counter++;
            onMyItemClickListener.onItemLongClicked(position);
            // getSupportActionBar().setTitle("" + counter);

        }

        incomeDeletePositions.add(myIncomeList.get(position));
        Log.i(TAG,"size "+incomeDeletePositions.size());
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
            incomeAdapter.setItemLongPressedStarted(false);
            onMyItemClickListener.onItemLongUnClicked(position);
            //   getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Integer obj = expenseDeletePositions.get(position);
        incomeDeletePositions.remove(myIncomeList.get(position));
        Log.i(TAG,"size "+incomeDeletePositions.size());
    }

    @Override
    public void onItemClicked(int position) {

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
            MyIncome myIncome = externalDatabase.getIncome(data.getStringExtra("id"));
            MyIncome preItem = myIncomeList.get(incomeEditPosition);
            preItem.setIncome(myIncome.getIncome());
            preItem.setIncomePerson(myIncome.getIncomePerson());
            preItem.setIncomeSource(myIncome.getIncomeSource());
            preItem.setIncomeDate(myIncome.getIncomeDate());
            preItem.setClicked(false);
            counter = 0;
            incomeAdapter.clearSelectedMap(incomeEditPosition);
            incomeAdapter.notifyItemChanged(incomeEditPosition);
            //getSupportActionBar().setTitle(getString(R.string.title_activity_expense_list));
            incomeAdapter.setItemLongPressedStarted(false);
            onMyItemClickListener.onItemLongUnClicked(0);
            textViewTotalExp.setText("Total Income: " + String.format("%.02f", externalDatabase.getTotalIncome(fromDate, toDate)) + " Rs");
        }
    }

}
