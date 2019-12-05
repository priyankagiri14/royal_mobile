package com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent;
import android.app.Activity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobile.royal.R;

import java.util.List;

public class Agentattendance_Adapter extends RecyclerView.Adapter<Agentattendance_Adapter.MyViewHolder> {

    private List<FetchAgent> batchesGetLists;
    private List<Body> bodyList;
    private Activity context;
    SparseBooleanArray array = new SparseBooleanArray();
    private CallbackInterface mCallback;


    public interface CallbackInterface{
        /**
         * Callback invoked when clicked
         * @param position - the position
         */
        void onHandleSelection(int position);
    }

    public Agentattendance_Adapter(Activity context, List<Body> bodyList) {

        this.context = context;
        this.bodyList = bodyList;

        try{
            mCallback = (CallbackInterface) context;
        }catch(ClassCastException ex){
            //.. should log the error or throw and exception
            Log.e("MyAdapter","Must implement the CallbackInterface in the Activity", ex);
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.driver_attendance,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.agent_list.setText(bodyList.get(position).getName());
        holder.button.setVisibility(View.INVISIBLE);

                if(array.get(position))
                {
                    bodyList.get(position).setIschecked(true);
                    holder.checkBox.setChecked(true);

                }
                else
                {
                    bodyList.get(position).setIschecked(false);
                    holder.checkBox.setChecked(false);
                }

    }

    @Override
    public int getItemCount() {
        return bodyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView agent_list;
        CheckBox checkBox;
        ImageView button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            agent_list = (TextView) itemView.findViewById(R.id.agent_text);
            checkBox = (CheckBox) itemView.findViewById(R.id.agent_checkbox);
            button = (ImageView) itemView.findViewById(R.id.agent_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(checkBox.isChecked())
                    {
                        checkBox.setChecked(false);
                        bodyList.get(getAdapterPosition()).setIschecked(false);
                    }
                    else
                    {
                        checkBox.setChecked(true);
                        bodyList.get(getAdapterPosition()).setIschecked(true);

                    }
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    array.put(getAdapterPosition(), true);
                    if (isChecked) {
                        bodyList.get(getAdapterPosition()).setIschecked(true);
                        button.setVisibility(View.VISIBLE);
                        //holder.checkBox.setChecked(true);

                    } else {
                        bodyList.get(getAdapterPosition()).setIschecked(false);
                        button.setVisibility(View.INVISIBLE);
                        //holder.checkBox.setChecked(false);
                    }
                    //notifyDataSetChanged();
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mCallback != null) {
                        mCallback.onHandleSelection(getAdapterPosition());
                    }
                }
            });
        }
    }
}
