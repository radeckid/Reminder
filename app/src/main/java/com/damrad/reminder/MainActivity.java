package com.damrad.reminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notes;
    private Button dateBtn;
    private Button timeBtn;

    private FilesOperation file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new FilesOperation(getApplicationContext());

        notes = file.loadDataToArray();

        recyclerView = findViewById(R.id.recyclerView);
        noteAdapter = new NoteAdapter(notes);

        setOnSwipe();
        setRecyclerView();

        FloatingActionButton fab = findViewById(R.id.fab);

        noteAdapter.setOnItemClickListener(position -> createDialog("EDIT", position));

        fab.setOnClickListener(v -> createDialog("ADD", -1));
    }

    private void setOnSwipe() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                createNotificationOnTime(null, null, null, noteAdapter.getItemAt(viewHolder.getAdapterPosition()).getUniqueID(), "DELETE");

                Note delNote = noteAdapter.getItemAt(viewHolder.getAdapterPosition());

                file.deleteNote(delNote);

                notes.remove(delNote);
                noteAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(noteAdapter);
    }

    private void createDialog(String dialogType, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, viewGroup, false);

        EditText descriptionTitleET = dialogView.findViewById(R.id.dialogDescriptionTitleET);
        EditText descriptionBodyET = dialogView.findViewById(R.id.dialogDescriptionBodyET);
        Button addBtn = dialogView.findViewById(R.id.dialogAddBtn);
        timeBtn = dialogView.findViewById(R.id.dialogTimePicker);
        dateBtn = dialogView.findViewById(R.id.dialogDatePicker);

        Calendar calendar;
        int delNoteID;

        if (dialogType.equals("EDIT") && position != -1) {
            Note note = noteAdapter.getItemAt(position);
            delNoteID = note.getUniqueID();

            descriptionTitleET.setText(note.getTitle());
            descriptionBodyET.setText(note.getBody());
            timeBtn.setText(note.getTime());

            calendar = note.getCalendar();
            String date = calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR);
            dateBtn.setText(date);

            addBtn.setText(getString(R.string.save));
        } else {
            calendar = Calendar.getInstance();
            delNoteID = -1;
        }

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        dateBtn.setOnClickListener(v -> getDateFromPicker(calendar));
        timeBtn.setOnClickListener(v -> getTimeFromPicker(calendar));

        addBtn.setOnClickListener(v -> {
            if (descriptionTitleET.getText().length() <= 0) {
                descriptionTitleET.setError(getString(R.string.can_not_be_empty));
                return;
            } else if (descriptionBodyET.getText().length() <= 0) {
                descriptionBodyET.setError(getString(R.string.can_not_be_empty));
                return;
            } else if (dateBtn.getText().equals(getString(R.string.choice_date))) {
                dateBtn.setError(getString(R.string.choice_date_first));
                return;
            } else if (timeBtn.getText().equals(getString(R.string.choice_time))) {
                timeBtn.setError(getString(R.string.choice_time_first));
                return;
            }
            if (dialogType.equals("EDIT") && delNoteID != -1) {
                createNotificationOnTime(null, null, null, delNoteID, "DELETE");

                Note deleteNote = noteAdapter.getItemAt(position);

                file.deleteNote(deleteNote);

                notes.remove(deleteNote);
                noteAdapter.notifyDataSetChanged();
            }

            String descTitleS = descriptionTitleET.getText().toString().trim();
            String descBodyS = descriptionBodyET.getText().toString().trim();

            int noteID = createID();

            Note noteForAdd = new Note(descTitleS, descBodyS, noteID, calendar);

            notes.add(noteForAdd);

            file.saveNoteToFile(notes, true);

            noteAdapter.notifyDataSetChanged();

            createNotificationOnTime(noteForAdd.getCalendar(), noteForAdd.getTitle(), noteForAdd.getBody(), noteForAdd.getUniqueID(), "ADD");
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        });
    }

    private void createNotificationOnTime(Calendar calendar, String title, String body, int id, String operationType) {
        Intent intent = new Intent(this, NotificationBroadcast.class);
        intent.putExtra("titleExtra", title);
        intent.putExtra("bodyExtra", body);
        intent.putExtra("ID", id);
        intent.putExtra("OperationType", operationType);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (calendar == null && title == null && body == null) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(0);
        }

        assert calendar != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void getDateFromPicker(Calendar calendar) {
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "." + month + "." + year;
            dateBtn.setText(date);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void getTimeFromPicker(Calendar calendar) {
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    timeBtn.setText(time);
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                }, mHour, mMinute, true);

        timePickerDialog.show();
    }

    private int createID() {
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(now));
    }
}
