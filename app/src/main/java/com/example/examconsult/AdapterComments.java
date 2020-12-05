package com.example.examconsult;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class AdapterComments extends RecyclerView.Adapter<AdapterComments.TasksViewHolder> {
    private List<Comments> comments;
    private Comments comment;
    DBHelper dbHelper;
    Context ctx;
    int user_id;
    int forum_author_id;

    public AdapterComments(ArrayList<Comments> notes, Context ctx, int user_id, int forum_author_id) {
        this.forum_author_id = forum_author_id;
        this.user_id = user_id;
        this.ctx = ctx;
        this.comments = notes;
        dbHelper = new DBHelper(ctx);
    }


    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder notesViewHolder, int i) {
        comment = comments.get(i);
        silentUpdate = true;
        notesViewHolder.textViewDescription.setText(comment.getComment());
        notesViewHolder.textViewTitle.setText(String.valueOf(comment.getAuthor_id()));
        notesViewHolder.textViewDayOfWeek.setText(comment.getDate());
        notesViewHolder.main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getAuthor_id().equals(String.valueOf(user_id)) || forum_author_id == user_id) {
                    createDialog(comment.getId(),v.getContext());
                } else {
                    Toast.makeText(ctx, "Вы не автор комментария и не владелец форума", (int) 0.1).show();
                }
                return false;
            }
        });
        //notesViewHolder.textViewDayOfWeek.setText(getDayAsString(note.getDayOfWeek() + 1));
    }

    void createDialog(final int comment_id,Context ctxx) {
        final AlertDialog aboutDialog = new AlertDialog.Builder(
                ctxx).setMessage("Вы действительно хотите удалить комментарий?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteComment(comment_id);
                        Toast.makeText(ctx, "Комментарий удалён", (int) 0.1).show();
                    }
                }).create();

        aboutDialog.show();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private boolean silentUpdate;

    class TasksViewHolder extends RecyclerView.ViewHolder {
        // init
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewDayOfWeek;
        ConstraintLayout main;


        public TasksViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.main);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDayOfWeek = itemView.findViewById(R.id.textViewDayOfWeek);
        }

    }

}