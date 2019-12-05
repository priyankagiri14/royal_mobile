package com.app.mobile.royal;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

import com.app.mobile.royal.Agent.ScanBatch;
import com.app.mobile.royal.Agent.Sim_allocation;

public class RicaTab extends TabActivity{

    TextView tabtext;

    /**
     * Thus method is initializing all the design components which will be used further for some functionalty.
     * This class is a tab activity with minimum of two tabs. There is tab host & activities having tab host values with properties like
     * indicator, current tab color value etc.
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rica_tab);

        tabtext=findViewById(R.id.tab_tv_title);


        TabHost tabHost=findViewById(android.R.id.tabhost);
        TabHost.TabSpec spec;
        Intent intent;

        spec=tabHost.newTabSpec("Boxno");
        spec.setIndicator("RICA");
        intent=new Intent(this, Sim_allocation.class);
        spec.setContent(intent);
        tabHost.addTab(spec);


        spec=tabHost.newTabSpec("Pending stock");
        spec.setIndicator("RICA Batch");
        intent=new Intent(this, ScanBatch.class);
        spec.setContent(intent);
        tabHost.addTab(spec);
       // tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#FFFFFF"));




        tabHost.setCurrentTab(0);
        if(tabHost.getCurrentTab() == 0)
        {
            tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#9E5700")); // unselected
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#FFFD6E"));
        }

        TextView tv = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title); //Unselected Tabs
        //tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FF0000")); // unselected
        tv.setTextColor(Color.parseColor("#FFFD6E"));
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FFA73B")); // unselected
                    tv.setTextColor(Color.parseColor("#FFFD6E"));
                }

                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#9E5700")); // selected
                TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.parseColor("#FFFD6E"));
                // display the name of the tab whenever a tab is changed
                // Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();


            }
        });
    }


}