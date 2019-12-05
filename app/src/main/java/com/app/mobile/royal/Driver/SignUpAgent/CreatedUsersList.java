package com.app.mobile.royal.Driver.SignUpAgent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mobile.royal.Agent.NetworkError;
import com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent.Body;
import com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent.FetchAgent;
import com.app.mobile.royal.NetworkStateReceiver;
import com.app.mobile.royal.R;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Web_Interface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatedUsersList extends AppCompatActivity implements UsersViewHolder.OnItemListener, NetworkStateReceiver.NetworkStateReceiverListener {

    private static final String TAG = "HomeFragment";
    private String message;
    private List<String> list;
    private List<Integer> agentid;
    RecyclerView agentlistview;
    LinearLayoutManager layoutManager;
    private int imageid;
    List<com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent.Body> list1;
    List<com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent.Body> agent = new ArrayList<>();
    TextView noagents;
    ProgressDialog progressBar;
    private int pageno =0;
    private int itemcount = 10;
    Web_Interface webInterface;
    CreatedUsersAdapter createdUsersAdapter;
    private Boolean isLoading = true;
    private int pastVisibleItems,visibleItemCount,totalItemCount,previousTotal = 0;
    private int viewThreshold = 10;
    NetworkStateReceiver networkStateReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_users_list);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        agentlistview = findViewById(R.id.userslist);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(agentlistview.VERTICAL);
        agentlistview.setLayoutManager(layoutManager);
        fetchagent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getLocation();
        progressBar.dismiss();
    }

    /**
     * This function return the Agent Fetch API results according to the its response code. It may be a failure or success.
     */


    private void fetchagent() {
        progressBar.show();
        webInterface = RetrofitToken.getClient().create(Web_Interface.class);
        Call<FetchAgent> call = webInterface.requestfetchagent(RetrofitToken.token,itemcount,pageno);
        call.enqueue(new Callback<FetchAgent>() {
            @Override
            public void onResponse(Call<FetchAgent> call, Response<FetchAgent> response) {
                if (response.isSuccessful() && response.code() == 200) {


                    assert response.body() != null;
                    agent = response.body().getBody();
                    list1 = new ArrayList<>();
                    list = new ArrayList<>();
                    agentid = new ArrayList<>();
                    for (int i = 0; i < agent.size(); i++) {
                        list1.add(response.body().getBody().get(i));
                        agentid.add(agent.get(i).getId());
                        list.add(agent.get(i).getName());
                        progressBar.dismiss();
                    }

                    Log.d("agentname", list.toString() + "\n" + "agentid" + agentid.toString());
                    populateListView(list1);
                    if (agentlistview.getAdapter().getItemCount() == 0) {
                        noagents.setVisibility(View.VISIBLE);
//                                        buttonCapture.setVisibility(View.INVISIBLE);
//                                        buttonStartDay.setVisibility(View.INVISIBLE);
//                                        progressBar.dismiss();
                    }
                } else {

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Log.d("AgentLoginActivity", jObjError.getString("message"));
                        Toasty.info(getApplicationContext(), message + "cameraa try").show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        //stopping progress
                    }
                }
            }

            @Override
            public void onFailure(Call<FetchAgent> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
                Toasty.info(getApplicationContext(), t.getLocalizedMessage() + "camerahere").show();

            }
        });

        agentlistview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if(dy>0)
                {
                    if(isLoading)
                    {
                        if(totalItemCount>previousTotal)
                        {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if(!isLoading&&(totalItemCount-visibleItemCount)<=(pastVisibleItems+viewThreshold))
                    {
                        pageno++;
                        performPagination();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void performPagination()
    {
            Call<FetchAgent> call = webInterface.requestfetchagent(RetrofitToken.token,itemcount,pageno);
        call.enqueue(new Callback<FetchAgent>() {
            @Override
            public void onResponse(Call<FetchAgent> call, Response<FetchAgent> response) {
                if (response.isSuccessful() && response.code() == 200) {


                    if(response.body().getBody() != null)
                    {
                        List<Body> bodyList = response.body().getBody();
                        createdUsersAdapter.addData(bodyList);
//                        Toast.makeText(CreatedUsersList.this, "Page "+pageno+" is loaded", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(CreatedUsersList.this, "No more Data Available", Toast.LENGTH_SHORT).show();
                    }

   /*                 assert response.body() != null;
                    agent = response.body().getBody();
                    list1 = new ArrayList<>();
                    list = new ArrayList<>();
                    agentid = new ArrayList<>();
                    for (int i = 0; i < agent.size(); i++) {
                        list1.add(response.body().getBody().get(i));
                        agentid.add(agent.get(i).getId());
                        list.add(agent.get(i).getName());
                        progressBar.dismiss();
                    }

                    Log.d("agentname", list.toString() + "\n" + "agentid" + agentid.toString());
                    populateListView(list1);
                    if (agentlistview.getAdapter().getItemCount() == 0) {
                        noagents.setVisibility(View.VISIBLE);
//                                        buttonCapture.setVisibility(View.INVISIBLE);
//                                        buttonStartDay.setVisibility(View.INVISIBLE);
//                                        progressBar.dismiss();
                    }*/
                } else {

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Log.d("AgentLoginActivity", jObjError.getString("message"));
                        Toasty.info(getApplicationContext(), message + "cameraa try").show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        //stopping progress
                    }
                }
            }

            @Override
            public void onFailure(Call<FetchAgent> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
                Toasty.info(getApplicationContext(), t.getLocalizedMessage() + "camerahere").show();

            }
        });
    }
    /**
     * This function populates the list got from the ERP (Array List)
     *
     * @param list This  is the list of the Body class of the Agents Get Class.
     */

    private void populateListView(List<Body> list) {

        createdUsersAdapter = new CreatedUsersAdapter(CreatedUsersList.this, list,this);
        createdUsersAdapter.notifyDataSetChanged();
        agentlistview.setItemViewCacheSize(list.size());
        agentlistview.setAdapter(createdUsersAdapter);

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Position is"+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {
        Intent i=new Intent(this,NetworkError.class);
        startActivity(i);
    }
}
