package com.homeguide.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homeguide.R;
import com.homeguide.adapters.ExpenseAdapter;
import com.homeguide.adapters.SummaryAdapter;
import com.homeguide.database.DbHelper;
import com.homeguide.database.ExternalDatabase;
import com.homeguide.models.MyExpense;
import com.homeguide.models.MySummary;
import com.homeguide.utilities.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment {

    private final String TAG = "ExpenseList";

    private RecyclerView recyclerView;
    private SummaryAdapter summaryAdapter;
    private List<MySummary> mySummaryList;
    private TextView textViewTotalExp, textViewTotalInc,textViewError;
    private TextView textViewFromDate, textViewToDate;
    private RelativeLayout relativeLayoutProgressBar;
    private RelativeLayout relativeLayoutFromDate, relativeLayoutToDate;
    private DatePickerDialog frpmDatePicker, toDatePicker;
    private String fromDate, toDate;
    private String monthTwoDigits = "";
    private String dayTwoDigits = "";
    private DbHelper dbHelper;
    private ExternalDatabase externalDatabase;
    private View rootView;


    public SummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_summary, container, false);
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
        textViewTotalInc = rootView.findViewById(R.id.text_total_incomes);
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
                setSummary();
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
                setSummary();
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

        textViewTotalInc.setText("Total Income: " + String.format("%.02f", externalDatabase.getTotalIncome(fromDate, toDate)) + " Rs");
        textViewTotalExp.setText("Total Expenses: " + String.format("%.02f", externalDatabase.getTotalExpenses(fromDate, toDate)) + " Rs");
        // showProgress(true);
       // mySummaryList = new ArrayList<>();
        mySummaryList = externalDatabase.getSummary(fromDate, toDate);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        summaryAdapter = new SummaryAdapter(getActivity(), mySummaryList);
      //  summaryAdapter.setOnMyItemClickListener(this);
        recyclerView.setAdapter(summaryAdapter);

        //showProgress(false);
        if (mySummaryList.size() > 0) {
            showError(false, null);
        } else {
            showError(true, "No summary.");
        }
    }

    private void setSummary(){
        mySummaryList.clear();
        List<MySummary> newList = externalDatabase.getSummary(fromDate, toDate);
        for (MySummary item : newList){
            mySummaryList.add(item);
        }

        //showProgress(false);
        if (mySummaryList.size() > 0) {
            showError(false, null);
            summaryAdapter.notifyDataSetChanged();
        } else {
            showError(true, "No summary.");
        }

        textViewTotalInc.setText("Total Income: " + String.format("%.02f", externalDatabase.getTotalIncome(fromDate, toDate)) + " Rs");
        textViewTotalExp.setText("Total Expenses: " + String.format("%.02f", externalDatabase.getTotalExpenses(fromDate, toDate)) + " Rs");

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

}
