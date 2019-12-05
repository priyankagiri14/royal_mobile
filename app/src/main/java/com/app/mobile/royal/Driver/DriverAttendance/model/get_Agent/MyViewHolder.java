package com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobile.royal.R;

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView agent_list;
    CheckBox checkBox;
    OnItemListener onItemListener;
    public MyViewHolder(@NonNull View itemView, OnItemListener onItemListener ) {
        super(itemView);

        agent_list = (TextView) itemView.findViewById(R.id.agent_text);
        checkBox = (CheckBox)itemView.findViewById(R.id.agent_checkbox);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition());
    }

    public interface OnItemListener{
        void onItemClick(int position);
    }
}
