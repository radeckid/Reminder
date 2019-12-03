package com.damrad.reminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private ArrayList<Note> list;
    private OnClickInterface onClickInterface;

    NoteAdapter(ArrayList<Note> list) {
        this.list = list;
    }

    static class NoteHolder extends RecyclerView.ViewHolder {

        private TextView dateMonthTV;
        private TextView dateDayTV;
        private TextView timeTV;
        private TextView descriptionTitleTV;
        private TextView descriptionBodyTV;

        NoteHolder(@NonNull View itemView, OnClickInterface onClickInterface) {
            super(itemView);
            timeTV = itemView.findViewById(R.id.timeTV);
            descriptionTitleTV = itemView.findViewById(R.id.descriptionTitleTV);
            descriptionBodyTV = itemView.findViewById(R.id.descriptionBodyTV);
            dateMonthTV = itemView.findViewById(R.id.dateMonthTV);
            dateDayTV = itemView.findViewById(R.id.dateDayTV);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onClickInterface.setOnClick(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new NoteHolder(view, onClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        holder.dateMonthTV.setText(list.get(position).getMonth().trim());
        holder.dateDayTV.setText(list.get(position).getDay().trim());
        holder.timeTV.setText(list.get(position).getTime().trim());
        holder.descriptionTitleTV.setText(list.get(position).getTitle().trim());
        holder.descriptionBodyTV.setText(list.get(position).getBody().trim());
    }

    public void setOnItemClickListener(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }

    public Note getItemAt(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Note> list) {
        this.list = list;
    }
}