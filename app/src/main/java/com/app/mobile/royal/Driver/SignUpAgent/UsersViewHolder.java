package com.app.mobile.royal.Driver.SignUpAgent;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobile.royal.R;

public class UsersViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

    TextView fnametext, idtext, usernametext, mobnotext;
    UsersViewHolder.OnItemListener onItemListener;

    public UsersViewHolder(@NonNull View itemView, UsersViewHolder.OnItemListener onItemListener) {
        super(itemView);

        fnametext = (TextView) itemView.findViewById(R.id.fnametext);
        idtext = (TextView) itemView.findViewById(R.id.idtext);
        usernametext = (TextView) itemView.findViewById(R.id.usernametext);
        mobnotext = (TextView) itemView.findViewById(R.id.mobnotext);

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