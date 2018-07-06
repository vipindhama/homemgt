package com.homeguide.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.homeguide.models.MyExpense;
import com.homeguide.models.MyIncome;
import com.homeguide.models.MySummary;
import com.homeguide.utilities.Utility;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Shweta on 31-01-2018.
 */

public class ExternalDatabase extends SQLiteOpenHelper {

    private final String TAG = "Dbhelper";

    public static final String DATABASE_NAME = "home_guide.db";
    public static final String ID = "ID";
    public static final String NAME = "name";
    public static final String EXPENSE_BY = "expenseBy";
    public static final String DESCRIPTION = "description";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String EXPENSE_DATE = "expenseDate";
    public static final String COST = "cost";
    public static final String QTY = "qty";
    public static final String INCOME = "income";
    public static final String INCOME_PERSON_NAME = "incomePersonName";
    public static final String INCOME_SOURCE = "incomeSource";
    public static final String INCOME_DATE = "incomeDate";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String COUNTER = "updatedAt";
    private Context context;

    public ExternalDatabase(Context context, String path)
    {
        super(context, path+"/"+DATABASE_NAME, null, 3);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table MyExpense " +
                        "(ID INTEGER NOT NULL, " +
                        " name TEXT NOT NULL, " +
                        " expenseBy TEXT, " +
                        " description TEXT, " +
                        " expenseType TEXT, " +
                        " expenseDate TEXT, " +
                        " cost REAL, " +
                        " qty INTEGER, " +
                        " createdAt TEXT, " +
                        " updatedAt TEXT)"
        );

        db.execSQL(
                "create table MyIncome " +
                        "(ID INTEGER NOT NULL, " +
                        " income REAL NOT NULL, " +
                        " incomePersonName TEXT NOT NULL, " +
                        " incomeSource TEXT, " +
                        " incomeDate TEXT, " +
                        " createdAt TEXT, " +
                        " updatedAt TEXT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          db.execSQL("DROP TABLE IF EXISTS MyIncome");
       /* db.execSQL(
                "alter table MyExpense add expenseBy TEXT"
        );*/

        db.execSQL(
                "create table MyIncome " +
                        "(ID INTEGER NOT NULL, " +
                        " income REAL NOT NULL, " +
                        " incomePersonName TEXT NOT NULL, " +
                        " incomeSource TEXT, " +
                        " incomeDate TEXT, " +
                        " createdAt TEXT, " +
                        " updatedAt TEXT)"
        );
    }

    public boolean insertExpenseItem(MyExpense item, String createdAt, String updatedAt)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(NAME, item.getName());
        contentValues.put(EXPENSE_BY, item.getExpenseBy());
        contentValues.put(DESCRIPTION, item.getDescription());
        contentValues.put(EXPENSE_TYPE, item.getExpenseType());
        contentValues.put(EXPENSE_DATE, item.getDate());
        contentValues.put(COST, item.getCost());
        contentValues.put(QTY, item.getQty());
        contentValues.put(CREATED_AT, createdAt);
        contentValues.put(UPDATED_AT, updatedAt);
        db.insert("MyExpense", null, contentValues);
        return true;
    }

    public boolean insertIncomeItem(MyIncome item, String createdAt, String updatedAt)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(INCOME, item.getIncome());
        contentValues.put(INCOME_PERSON_NAME, item.getIncomePerson());
        contentValues.put(INCOME_DATE, item.getIncomeDate());
        contentValues.put(INCOME_SOURCE, item.getIncomeSource());
        contentValues.put(CREATED_AT, createdAt);
        contentValues.put(UPDATED_AT, updatedAt);
        db.insert("MyIncome", null, contentValues);
        return true;
    }

    public ArrayList<MyExpense> getExpenses(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from MyExpense";
        Cursor res =  db.rawQuery(query, null);
        // res.moveToFirst();
        ArrayList<MyExpense> itemList=new ArrayList<>();
        MyExpense item = null;
        if(res.moveToFirst()){
            do{
                item = new MyExpense();
                item.setId(res.getInt(res.getColumnIndex(ID)));
                item.setExpenseBy(res.getString(res.getColumnIndex(EXPENSE_BY)));
                item.setName(res.getString(res.getColumnIndex(NAME)));
                item.setDescription(res.getString(res.getColumnIndex(DESCRIPTION)));
                item.setExpenseType(res.getString(res.getColumnIndex(EXPENSE_TYPE)));
                item.setDate(res.getString(res.getColumnIndex(EXPENSE_DATE)));
                item.setCost(res.getFloat(res.getColumnIndex(COST)));
                item.setQty(res.getInt(res.getColumnIndex(QTY)));
                itemList.add(item);
            }while (res.moveToNext());
        }
        return itemList;
    }

    public MyExpense getExpense(String id){
        Log.i(TAG,"id "+id);
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from MyExpense where "+ID+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{id});
        Log.i(TAG,"size  "+res.getCount());
        MyExpense item = null;
        if(res.moveToFirst()){
            item = new MyExpense();
            item.setId(res.getInt(res.getColumnIndex(ID)));
            item.setName(res.getString(res.getColumnIndex(NAME)));
            item.setExpenseBy(res.getString(res.getColumnIndex(EXPENSE_BY)));
            item.setDescription(res.getString(res.getColumnIndex(DESCRIPTION)));
            item.setExpenseType(res.getString(res.getColumnIndex(EXPENSE_TYPE)));
            item.setDate(res.getString(res.getColumnIndex(EXPENSE_DATE)));
            item.setCost(res.getFloat(res.getColumnIndex(COST)));
            item.setQty(res.getInt(res.getColumnIndex(QTY)));
        }
        return item;
    }

    public ArrayList<MyExpense> getExpenses(String fromDate,String toDate){

        Log.i(TAG,fromDate+" "+toDate);

        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from MyExpense where date("+EXPENSE_DATE+") >= date(?) and " +
                "date("+EXPENSE_DATE+") <= date(?) order by "+EXPENSE_DATE+" desc";
        Cursor res =  db.rawQuery(query, new String[]{fromDate,toDate});
        // res.moveToFirst();
        ArrayList<MyExpense> itemList=new ArrayList<>();
        MyExpense item = null;
        if(res.moveToFirst()){
            do{
                item = new MyExpense();
                item.setId(res.getInt(res.getColumnIndex(ID)));
                item.setName(res.getString(res.getColumnIndex(NAME)));
                item.setExpenseBy(res.getString(res.getColumnIndex(EXPENSE_BY)));
                item.setDescription(res.getString(res.getColumnIndex(DESCRIPTION)));
                item.setExpenseType(res.getString(res.getColumnIndex(EXPENSE_TYPE)));
                item.setDate(res.getString(res.getColumnIndex(EXPENSE_DATE)));
                item.setCost(res.getFloat(res.getColumnIndex(COST)));
                item.setQty(res.getInt(res.getColumnIndex(QTY)));
                itemList.add(item);
            }while (res.moveToNext());
        }
        return itemList;
    }

    public ArrayList<MyExpense> getExpensesByType(String fromDate,String toDate,String type){

        Log.i(TAG,fromDate+" "+toDate);

        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from MyExpense where date("+EXPENSE_DATE+") >= date(?) and " +
                "date("+EXPENSE_DATE+") <= date(?) and "+EXPENSE_TYPE+" = ? order by "+EXPENSE_DATE+" desc";
        Cursor res =  db.rawQuery(query, new String[]{fromDate,toDate,type});
        // res.moveToFirst();
        ArrayList<MyExpense> itemList=new ArrayList<>();
        MyExpense item = null;
        if(res.moveToFirst()){
            do{
                item = new MyExpense();
                item.setId(res.getInt(res.getColumnIndex(ID)));
                item.setName(res.getString(res.getColumnIndex(NAME)));
                item.setExpenseBy(res.getString(res.getColumnIndex(EXPENSE_BY)));
                item.setDescription(res.getString(res.getColumnIndex(DESCRIPTION)));
                item.setExpenseType(res.getString(res.getColumnIndex(EXPENSE_TYPE)));
                item.setDate(res.getString(res.getColumnIndex(EXPENSE_DATE)));
                item.setCost(res.getFloat(res.getColumnIndex(COST)));
                item.setQty(res.getInt(res.getColumnIndex(QTY)));
                itemList.add(item);
            }while (res.moveToNext());
        }
        return itemList;
    }

    public ArrayList<MyIncome> getIncomes(String fromDate,String toDate){

        Log.i(TAG,fromDate+" "+toDate);

        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from MyIncome where date("+INCOME_DATE+") >= date(?) and " +
                "date("+INCOME_DATE+") <= date(?) order by "+INCOME_DATE+" desc";
        Cursor res =  db.rawQuery(query, new String[]{fromDate,toDate});
        // res.moveToFirst();
        ArrayList<MyIncome> itemList=new ArrayList<>();
        MyIncome item = null;
        if(res.moveToFirst()){
            do{
                item = new MyIncome();
                item.setId(res.getInt(res.getColumnIndex(ID)));
                item.setIncome(res.getFloat(res.getColumnIndex(INCOME)));
                item.setIncomePerson(res.getString(res.getColumnIndex(INCOME_PERSON_NAME)));
                item.setIncomeDate(res.getString(res.getColumnIndex(INCOME_DATE)));
                item.setIncomeSource(res.getString(res.getColumnIndex(INCOME_SOURCE)));
                itemList.add(item);
            }while (res.moveToNext());
        }
        return itemList;
    }

    public MyIncome getIncome(String id){
        Log.i(TAG,"id "+id);
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from MyIncome where "+ID+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{id});
        Log.i(TAG,"size  "+res.getCount());
        MyIncome item = null;
        if(res.moveToFirst()){
            item = new MyIncome();
            item.setId(res.getInt(res.getColumnIndex(ID)));
            item.setIncome(res.getFloat(res.getColumnIndex(INCOME)));
            item.setIncomePerson(res.getString(res.getColumnIndex(INCOME_PERSON_NAME)));
            item.setIncomeDate(res.getString(res.getColumnIndex(INCOME_DATE)));
            item.setIncomeSource(res.getString(res.getColumnIndex(INCOME_SOURCE)));
        }
        return item;
    }

    public ArrayList<MySummary> getSummary(String fromDate, String toDate){

        Log.i(TAG,fromDate+" "+toDate);

        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select "+EXPENSE_TYPE+",sum(cost) as total from MyExpense where date("+EXPENSE_DATE+") >= date(?) and " +
                "date("+EXPENSE_DATE+") <= date(?) group by "+EXPENSE_TYPE;
        Cursor res =  db.rawQuery(query, new String[]{fromDate,toDate});
        // res.moveToFirst();
        ArrayList<MySummary> itemList=new ArrayList<>();
        MySummary item = null;
        if(res.moveToFirst()){
            do{
                item = new MySummary();
                item.setName(res.getString(res.getColumnIndex(EXPENSE_TYPE)));
                item.setValue(res.getFloat(res.getColumnIndex("total")));
                itemList.add(item);
                Log.i(TAG,res.getString(res.getColumnIndex(EXPENSE_TYPE))+" "+res.getFloat(res.getColumnIndex("total")));
            }while (res.moveToNext());
        }
        return itemList;
    }


    public float getTotalExpenses(String fromDate,String toDate){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+COST+") as totalExp from MyExpense where date("+EXPENSE_DATE+") >= date(?) and " +
                "date("+EXPENSE_DATE+") <= date(?)";
        Cursor res =  db.rawQuery(query, new String[]{fromDate,toDate});
        res.moveToFirst();
        float sum = res.getFloat(res.getColumnIndex("totalExp"));
        Log.i(TAG,"Sum "+sum);
        return sum;
    }

    public float getTotalIncome(String fromDate,String toDate){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+INCOME+") as totalIncome from MyIncome where date("+INCOME_DATE+") >= date(?) and " +
                "date("+INCOME_DATE+") <= date(?)";
        Cursor res =  db.rawQuery(query, new String[]{fromDate,toDate});
        res.moveToFirst();
        float sum = res.getFloat(res.getColumnIndex("totalIncome"));
        Log.i(TAG,"Sum "+sum);
        return sum;
    }

    public int getExpensesId(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select max(ID) as maxID from MyExpense";
        Cursor res =  db.rawQuery(query, null);
        res.moveToFirst();
        int id = res.getInt(res.getColumnIndex("maxID"));
        Log.i(TAG,"max id  "+id);
        return ++id;
    }

    public int getIncomeId(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select max(ID) as maxID from MyIncome";
        Cursor res =  db.rawQuery(query, null);
        res.moveToFirst();
        int id = res.getInt(res.getColumnIndex("maxID"));
        Log.i(TAG,"max id  "+id);
        return ++id;
    }

    public ArrayList<String> getExpenseTypes(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select DISTINCT "+EXPENSE_TYPE+" from MyExpense";
        Cursor res =  db.rawQuery(query, null);
        // res.moveToFirst();
        ArrayList<String> itemList=new ArrayList<>();
        if(res.moveToFirst()){
            do{
                itemList.add(res.getString(res.getColumnIndex(EXPENSE_TYPE)));
            }while (res.moveToNext());
        }
        return itemList;
    }

    public ArrayList<String> getExpenseByNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select DISTINCT "+EXPENSE_BY+" from MyExpense";
        Cursor res =  db.rawQuery(query, null);
        // res.moveToFirst();
        ArrayList<String> itemList=new ArrayList<>();
        if(res.moveToFirst()){
            do{
                if(res.getString(res.getColumnIndex(EXPENSE_BY)) != null && !res.getString(res.getColumnIndex(EXPENSE_BY)).equals("null"))
                    itemList.add(res.getString(res.getColumnIndex(EXPENSE_BY)));
            }while (res.moveToNext());
        }
        return itemList;
    }

    public boolean isExpenseTypeExist(String type){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select count(*) as counter from MyExpense where "+EXPENSE_TYPE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{type});
        boolean exist = false;
        if(res.moveToFirst()){
            if(res.getInt(res.getColumnIndex("counter")) > 0)
                exist = true;
        }
        return exist;
    }

    public boolean isExpenseByExist(String by){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select count(*) as counter from MyExpense where "+EXPENSE_BY+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{by});
        boolean exist = false;
        if(res.moveToFirst()){
            if(res.getInt(res.getColumnIndex("counter")) > 0)
                exist = true;
        }
        return exist;
    }

    public boolean updateExpenseData(MyExpense item,String updatedAt)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, item.getName());
        contentValues.put(EXPENSE_BY, item.getExpenseBy());
        contentValues.put(DESCRIPTION, item.getDescription());
        contentValues.put(EXPENSE_TYPE, item.getExpenseType());
        contentValues.put(EXPENSE_DATE, item.getDate());
        contentValues.put(COST, item.getCost());
        contentValues.put(QTY, item.getQty());
        contentValues.put(UPDATED_AT, updatedAt);
        db.update("MyExpense", contentValues, ID+" = ? ", new String[]{String.valueOf(item.getId())});
        db.close();
        return true;
    }

    public boolean updateIncomeData(MyIncome item,String updatedAt)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INCOME, item.getIncome());
        contentValues.put(INCOME_PERSON_NAME, item.getIncomePerson());
        contentValues.put(INCOME_SOURCE, item.getIncomeSource());
        contentValues.put(INCOME_DATE, item.getIncomeDate());
        contentValues.put(UPDATED_AT, updatedAt);
        db.update("MyIncome", contentValues, ID+" = ? ", new String[]{String.valueOf(item.getId())});
        db.close();
        return true;
    }

    public void deleteExpenseItem(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("MyExpense", ID+"=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteIncomeItem(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("MyIncome", ID+"=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean dropAndCreateBookingTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS MyExpense");
        db.execSQL("DROP TABLE IF EXISTS MyIncome");
        onCreate(db);
        return true;
    }


}