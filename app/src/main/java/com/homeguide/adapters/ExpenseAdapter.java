package com.homeguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homeguide.R;
import com.homeguide.interfaces.OnMyItemClickListener;
import com.homeguide.models.MyExpense;
import com.homeguide.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Shweta on 27-01-2018.
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final List<MyExpense> rowItems;
    private List<RelativeLayout> relativeLayoutsList;
    private Map<Integer,RelativeLayout> relativeLayoutMap;
    private boolean isItemLongPressedStarted;
    private Context context;

    public void setItemLongPressedStarted(boolean itemLongPressedStarted) {
        isItemLongPressedStarted = itemLongPressedStarted;
    }

    private OnMyItemClickListener onMyItemClickListener;

    public void setOnMyItemClickListener(OnMyItemClickListener onMyItemClickListener) {
        this.onMyItemClickListener = onMyItemClickListener;
    }

    public ExpenseAdapter(Context context, List<MyExpense> rowItems) {
        this.rowItems = rowItems;
        this.context = context;
        relativeLayoutsList=new ArrayList<>();
        relativeLayoutMap = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_expense_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       MyExpense item = rowItems.get(position);
        holder.textName.setText(item.getName() +" ("+item.getExpenseBy()+")");
        holder.textViewDesc.setText(item.getDescription());
        holder.textViewPrice.setText(String.format("%.02f",item.getCost()) +" Rs");
        String[] date = item.getDate().split("-");
        holder.textViewDate.setText(date[2]+" "+ Utility.getMonthName(Integer.parseInt(date[1])-1)+" "+date[0]);
        holder.textViewQty.setText("Qty "+item.getQty());
        relativeLayoutsList.add(holder.relativeLayout);
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        public TextView textName,textViewDesc,textViewPrice,textViewQty,textViewDate;
        private RelativeLayout relativeLayout;
        public ViewHolder(View view) {
            super(view);
            textName = (TextView) view.findViewById(R.id.text_name);
            textViewDesc = (TextView) view.findViewById(R.id.text_desc);
            textViewPrice = (TextView) view.findViewById(R.id.text_price_label);
            textViewQty = (TextView) view.findViewById(R.id.text_quantity);
            textViewDate = (TextView) view.findViewById(R.id.text_date);
            relativeLayout = (RelativeLayout) view;
            relativeLayout.setOnClickListener(this);
            relativeLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MyExpense item=rowItems.get(getAdapterPosition());
            if(isItemLongPressedStarted){
                if(item.isClicked()){
                    onMyItemClickListener.onItemLongUnClicked(getAdapterPosition());
                    relativeLayout.setBackgroundResource(R.drawable.grey_border_bottom_background);
                    item.setClicked(false);
                    relativeLayoutMap.remove(getAdapterPosition());
                }
                else {
                    onMyItemClickListener.onItemLongClicked(getAdapterPosition());
                    relativeLayout.setBackgroundResource(R.drawable.list_item_selected_solid_background);
                    item.setClicked(true);
                    relativeLayoutMap.put(getAdapterPosition(),relativeLayout);
                }
            }
        }


        @Override
        public boolean onLongClick(View v) {
            MyExpense item=rowItems.get(getAdapterPosition());
            if(item.isClicked()){
                onMyItemClickListener.onItemLongUnClicked(getAdapterPosition());
                relativeLayout.setBackgroundResource(R.drawable.grey_border_bottom_background);
                item.setClicked(false);
                relativeLayoutMap.remove(getAdapterPosition());
            }
            else {
                onMyItemClickListener.onItemLongClicked(getAdapterPosition());
                relativeLayout.setBackgroundResource(R.drawable.list_item_selected_solid_background);
                item.setClicked(true);
                relativeLayoutMap.put(getAdapterPosition(),relativeLayout);
            }

            relativeLayout.setPressed(false);
            return true;
        }
    }

    public void unSelectAllSelectedViews(){
        Set set = relativeLayoutMap.keySet();
        Iterator keyIterator = set.iterator();
        while (keyIterator.hasNext()){
            int position = (int)keyIterator.next();
            relativeLayoutMap.get(position).setBackgroundResource(R.drawable.grey_border_bottom_background);
            rowItems.get(position).setClicked(false);
        }
    }

    public void clearSelectedMap(){
        relativeLayoutMap.clear();
    }

    public void clearSelectedMap(int position){
        relativeLayoutMap.get(position).setBackgroundResource(R.drawable.grey_border_bottom_background);
        relativeLayoutMap.clear();
    }
}
