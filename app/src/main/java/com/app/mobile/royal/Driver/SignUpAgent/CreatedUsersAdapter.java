package com.app.mobile.royal.Driver.SignUpAgent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent.Body;
import com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent.FetchAgent;
import com.app.mobile.royal.FetchOneAgent.CommentsResponse;
import com.app.mobile.royal.R;
import com.app.mobile.royal.Web_Services.RetrofitToken;
import com.app.mobile.royal.Web_Services.Web_Interface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatedUsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent.Body item);
    }

    private List<FetchAgent> batchesGetLists;
    private List<com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent.Body> bodyList;
    private Context context;
    private UsersViewHolder.OnItemListener onItemListener;


    public CreatedUsersAdapter(Context context, List<Body> bodyList, UsersViewHolder.OnItemListener onItemListener) {

        this.context = context;
        this.bodyList = bodyList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.created_users_list_item, parent, false);

        return new UsersViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.fnametext.setText(bodyList.get(position).getName());
        holder.idtext.setText(bodyList.get(position).getProfile().getIdNo());
        holder.usernametext.setText(bodyList.get(position).getUsername());
        holder.mobnotext.setText(bodyList.get(position).getProfile().getMobileNo());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = bodyList.get(position).getId();
                Web_Interface web_interface = RetrofitToken.getClient().create(Web_Interface.class);
                Call<CommentsResponse> commentsResponseCall = web_interface.requestCommentsresponse(id,"USER");
                commentsResponseCall.enqueue(new Callback<CommentsResponse>() {
                    @Override
                    public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {
                        if(response.isSuccessful() && response.code() == 200)
                        {
                            if(response.body()!= null && response.body().getSuccess())
                            {
                                if(response.body()!=null && response.body().getBody().size() !=0) {
                                    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
                                    alertDialog.setMessage(response.body().getBody().get(0).getMessage());
                                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Go to Sign Up Form", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String id = String.valueOf(bodyList.get(position).getId());
                                            Intent intent = new Intent(context, EditAgentForm.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("id", id);
                                            context.startActivity(intent);
                                        }
                                    });
                                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            alertDialog.cancel();
                                        }
                                    });
                                    alertDialog.show();
                                }
                                else
                                {
                                    Toast.makeText(context, "No Comments Received", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentsResponse> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


                //String id = pojos.getData().get(i).getId().toString();
      /*
                intent.putExtra("title", bodyList.get(position).getId());
                intent.putExtra("firstname", bodyList.get(position).getFirstName());
                intent.putExtra("lastname", bodyList.get(position).getLastName());
                intent.putExtra("username", bodyList.get(position).getUsername());
                intent.putExtra("password", bodyList.get(position).getpass);
                intent.putExtra("streetno", bodyList.get(position).);
                intent.putExtra("username", bodyList.get(position).getUsername());
                intent.putExtra("username", bodyList.get(position).getUsername());
                intent.putExtra("username", bodyList.get(position).getUsername());*/

                //intent.putExtra("ID",id);

            }
        });
    }

    @Override
    public int getItemCount() {
        return bodyList.size();
    }

    public void addData(List<Body> data) {
        for (Body body : data) {
            bodyList.add(body);
        }
        notifyDataSetChanged();
    }
}
