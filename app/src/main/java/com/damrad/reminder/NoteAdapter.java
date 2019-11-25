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

        private TextView timeTV;
        private TextView descriptionTV;
        private TextView dateTV;

        NoteHolder(@NonNull View itemView, OnClickInterface onClickInterface) {
            super(itemView);
            timeTV = itemView.findViewById(R.id.timeTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            dateTV = itemView.findViewById(R.id.dateTV);

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
//        holder.nameTV.setText(list.get(position).getDate());
//
//        if (holder.nameTV.getText().equals("")) {
//            holder.nameTV.setVisibility(View.GONE);
//        } else {
//            holder.nameTV.setVisibility(View.VISIBLE);
//        }
//
//        holder.bodyTV.setText(list.get(position).getDescription());
//        holder.dateTV.setText(list.get(position).getDate());
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

}