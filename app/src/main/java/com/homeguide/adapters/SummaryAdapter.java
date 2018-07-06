package com.homeguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homeguide.R;
import com.homeguide.models.MySummary;

import java.util.List;

public class SummaryAdapter  extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    private final List<MySummary> rowItems;
    private Context context;
    private String type;

    public SummaryAdapter(Context context, List<MySummary> rowItems) {
        this.rowItems = rowItems;
        this.context = context;
        //this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_summary_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MySummary mySummary = rowItems.get(position);
        holder.textName.setText(mySummary.getName());
        holder.textValue.setText(String.format("%.02f", mySummary.getValue()));
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textName,textValue;
       // private RelativeLayout relativeLayout;
        public ViewHolder(View view) {
            super(view);
            textName = (TextView) view.findViewById(R.id.text_name);
            textValue = (TextView) view.findViewById(R.id.text_value);
           // relativeLayout = (RelativeLayout)view;
        }

        @Override
        public void onClick(View v) {
           //onSingleItemClickListener.onItemClicked(getAdapterPosition(),type);
        }
    }
}
