package com.homeguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homeguide.R;
import com.homeguide.interfaces.OnSingleItemClickListener;

import java.util.List;

/**
 * Created by Shweta on 28-01-2018.
 */

public class SingleItemAdapter extends RecyclerView.Adapter<SingleItemAdapter.ViewHolder> {

    private final List<String> rowItems;
    private Context context;
    private String type;

    private OnSingleItemClickListener onSingleItemClickListener;

    public void setOnSingleItemClickListener(OnSingleItemClickListener onSingleItemClickListener) {
        this.onSingleItemClickListener = onSingleItemClickListener;
    }

    public SingleItemAdapter(Context context, List<String> rowItems,String type) {
        this.rowItems = rowItems;
        this.context = context;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_type_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textName.setText(rowItems.get(position));
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textName;
        private RelativeLayout relativeLayout;
        public ViewHolder(View view) {
            super(view);
            textName = (TextView) view.findViewById(R.id.text_name);
            relativeLayout = (RelativeLayout)view;
            relativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSingleItemClickListener.onItemClicked(getAdapterPosition(),type);
        }
    }
}
