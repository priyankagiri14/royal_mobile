package com.app.mobile.royal.AgentsList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.mobile.royal.R;

import java.util.List;

public class AgentsListAdapter extends BaseAdapter {

    private List<AgentsListResponse> batchesGetLists;
    private List<Body> bodyList;
    private Context context;
    int selectedPosition = 0;
    int unselectedposition = -1;


    public AgentsListAdapter(Context context, List<Body> bodyList) {
        this.context = context;
        this.bodyList = bodyList;

    }

    class MyViewHolder {
        public TextView agentslisttext;
        public RadioButton agentslistcheckbox;

        MyViewHolder(View view) {
            agentslisttext = (TextView) view.findViewById(R.id.agents_list_text);
            agentslistcheckbox = (RadioButton) view.findViewById(R.id.agent_list_checkbox);
        }
    }



    @Override
    public int getCount() {
        return bodyList.size();
    }

    @Override
    public Object getItem(int position) {
        return bodyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View row = view;
        AgentsListAdapter.MyViewHolder holder = null;

        if (row == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.agents_list_item, parent, false);
            holder = new AgentsListAdapter.MyViewHolder(row);
            row.setTag(holder);
        }
        else
        {
            holder = (AgentsListAdapter.MyViewHolder) row.getTag();
        }
        //batchesgetcheckbox.setChecked(fa);

        holder.agentslisttext.setText(bodyList.get(position).getName());
        /*holder.agentslistcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    bodyList.get(position).setIschecked(true);
                }
                else
                {
                    bodyList.get(position).setIschecked(false);
                }
            }
        });*/

        holder.agentslistcheckbox.setChecked(position == selectedPosition);
        holder.agentslistcheckbox.setTag(position);
        holder.agentslistcheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(unselectedposition !=-1)
                {
                    bodyList.get(unselectedposition).setIschecked(false);
                    notifyDataSetChanged();
                }
                selectedPosition = (Integer)view.getTag();
                bodyList.get(selectedPosition).setIschecked(true);
                notifyDataSetChanged();
                unselectedposition = selectedPosition;
            }
        });
        return row;
    }
}
