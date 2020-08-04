package com.example.smarttrashapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttrashapp.R;
import com.example.smarttrashapp.interFaces.itemClickListner;

public class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView cansNumber, dateofoperation;
    public itemClickListner listner;

    public HistoryHolder(View itemView) {
        super(itemView);
        cansNumber = itemView.findViewById(R.id.can_number_TV);
        dateofoperation = itemView.findViewById(R.id.date_tv);

    }

    public void setItemClickListner(itemClickListner listner) {
        this.listner = listner;

    }

    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(), false);

    }

}
