package com.example.examconsult;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Forum> forums;
    private Context mainContext;
    private int user;
    private DBHelper dbHelper;
    RecycleAdapter(Context context, List<Forum> forums, int user_id) {
        this.forums = forums;
        this.inflater = LayoutInflater.from(context);
        mainContext = context;
        user = user_id;
        dbHelper = new DBHelper(context);
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
        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, ForumArticle.class);
                intent.putExtra("id_user", user);
                intent.putExtra("forum", forum.getId());
                intent.putExtra("forum_author_id", forum.getAuthor_id());
                mainContext.startActivity(intent);
            }
        });
        holder.main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (forum.getAuthor_id() == user) {
                    createDialog(forum, v.getContext(),user);
                } else {
                    Toast.makeText(mainContext, "Вы не автор форума", (int) 0.1).show();
                }
                return false;
            }
        });
    }

    void createDialog(final Forum forum, Context ctxx, final int user_id) {
        final AlertDialog aboutDialog = new AlertDialog.Builder(
                ctxx).setMessage("Выберите операцию")
                .setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mainContext, add_forum.class);
                        intent.putExtra("isEdit",true);
                        intent.putExtra("id_user",user_id);
                        intent.putExtra("title",forum.getTitle());
                        intent.putExtra("desc",forum.getDesc());
                        intent.putExtra("id",forum.getId());
                        intent.putExtra("category",forum.getCategory());
                        mainContext.startActivity(intent);
                    }
                }).setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteForum(String.valueOf(forum.getId()));
                Toast.makeText(mainContext, "Форум удалён", (int) 0.1).show();
            }
        }).create();
        aboutDialog.show();
    }

    @Override
    public int getItemCount() {
        return forums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title, description, author, date;
        final LinearLayout main;

        ViewHolder(View view) {
            super(view);
            main = view.findViewById(R.id.main);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            author = view.findViewById(R.id.author);
            date = view.findViewById(R.id.date);
        }
    }
}
