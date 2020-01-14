package com.example.examconsult;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Forum> forums;
    private Context mainContext;
    private int user;

    RecycleAdapter(Context context, List<Forum> forums,int user_id) {
        this.forums = forums;
        this.inflater = LayoutInflater.from(context);
        mainContext = context;
        user = user_id;
    }

    @NonNull
    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position) {
        DBHelper helper = new DBHelper(mainContext);
        final Forum forum = forums.get(position);
        holder.title.setText(forum.getTitle());
        holder.description.setText(forum.getDesc());
        holder.author.setText(helper.getAuthor(forum.getAuthor_id()));
        holder.date.setText(forum.getCreated_at());
        holder.main.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, VideoMaterial.class);
                intent.putExtra("id_user",user);
                intent.putExtra("forum",forum.getId());
                mainContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView title, description ,author,date;
        final LinearLayout main;
        ViewHolder(View view){
            super(view);
            main = view.findViewById(R.id.main);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            author = view.findViewById(R.id.author);
            date = view.findViewById(R.id.date);
        }
    }
}
