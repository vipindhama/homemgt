package com.homeguide.models;

/**
 * Created by Shweta on 21-03-2018.
 */

public class MyIncome {
    private int id;
    private String incomePerson,incomeDate,incomeSource;
    private float income;
    private boolean isClicked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIncomePerson() {
        return incomePerson;
    }

    public void setIncomePerson(String incomePerson) {
        this.incomePerson = incomePerson;
    }

    public String getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(String incomeDate) {
        this.incomeDate = incomeDate;
    }

    public String getIncomeSource() {
        return incomeSource;
    }

    public void setIncomeSource(String incomeSource) {
        this.incomeSource = incomeSource;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
