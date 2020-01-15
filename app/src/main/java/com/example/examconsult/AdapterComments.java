package com.example.examconsult;

import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class AdapterComments extends RecyclerView.Adapter<AdapterComments.TasksViewHolder>{
    private List<Comments> comments;
    private OnTaskClickListener onTaskClickListener;
    private Comments comment;
    DBHelper dbHelper;


    public AdapterComments(ArrayList<Comments> notes) {
        this.comments = notes;
    }

    public interface OnTaskClickListener {
        void onTaskClick(int position);
        void onLongClick(int position);
    }

    public void setOnTaskClickListener(OnTaskClickListener onTaskClickListener) {
        this.onTaskClickListener = onTaskClickListener;
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
        //notesViewHolder.textViewDayOfWeek.setText(getDayAsString(note.getDayOfWeek() + 1));
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

        CheckBox completed;


        public TasksViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDayOfWeek = itemView.findViewById(R.id.textViewDayOfWeek);
        }
    }
}