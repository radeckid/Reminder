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
        noteAdapter = new NoteAdapter(notes);

        Bundle cameIntent = getIntent().getExtras();

        if (cameIntent != null) {
            if (cameIntent.size() >= 3) {
                String title = cameIntent.getString("saveTitle");
                String body = cameIntent.getString("saveBody");
                String timeDate = cameIntent.getString("saveTimeDate");
                int id = cameIntent.getInt("ID");

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(body);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Archiwizuj", (dialog, which) -> {

                    file.deleteNote(id);
                    noteAdapter.setList(file.loadDataToArray());
                    noteAdapter.notifyDataSetChanged();

                });
                alertDialog.show();

                new FilesOperation().saveToArchive(getApplicationContext(), title, body, timeDate);
            }
        }

        Button archiveBtn = findViewById(R.id.archiveBtn);

        recyclerView = findViewById(R.id.recyclerView);

        setOnSwipe();
        setRecyclerView();

        FloatingActionButton fab = findViewById(R.id.fab);

        noteAdapter.setOnItemClickListener(position -> createDialog("EDIT", position));

        fab.setOnClickListener(v -> createDialog("ADD", -1));

        archiveBtn.setOnClickListener(v -> {
            Intent archiveIntent = new Intent(MainActivity.this, ArchiveActivity.class);
            startActivity(archiveIntent);
            finish();
        });
    }

    private void setOnSwipe() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note delNote = noteAdapter.getItemAt(viewHolder.getAdapterPosition());

                createNotificationOnTime(null, delNote.getTitle(), null, null, noteAdapter.getItemAt(viewHolder.getAdapterPosition()).getUniqueID(), "DELETE");

                file.deleteNote(delNote.getUniqueID());
                noteAdapter.setList(file.loadDataToArray());
                notes.remove(delNote);
                noteAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
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
            if (descriptionBodyET.getText().length() <= 0) {
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
                Note deleteNote = noteAdapter.getItemAt(position);

                createNotificationOnTime(null, deleteNote.getTitle(), null, null, delNoteID, "DELETE");

                file.deleteNote(deleteNote.getUniqueID());
                notes.remove(deleteNote);
                noteAdapter.setList(file.loadDataToArray());
                noteAdapter.notifyDataSetChanged();
            }

            String descTitleS = descriptionTitleET.getText().toString().trim();
            String descBodyS = descriptionBodyET.getText().toString().trim();

            int noteID = createID();

            Note noteForAdd = new Note(descTitleS, descBodyS, noteID, calendar);

            notes.add(noteForAdd);

            file.saveNoteToFile(notes, true);
            noteAdapter.setList(file.loadDataToArray());
            noteAdapter.notifyDataSetChanged();

            String timeDate = "Data: " + noteForAdd.getDay() + " " + noteForAdd.getMonth() + " " + noteForAdd.getYearNr() + "\t\nCzas: " + noteForAdd.getTime();

            createNotificationOnTime(noteForAdd.getCalendar(), noteForAdd.getTitle(), noteForAdd.getBody(), timeDate, noteForAdd.getUniqueID(), "ADD");
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        });
    }

    private void createNotificationOnTime(Calendar calendar, String title, String body, String timeDate, int id, String operationType) {
        Intent intent = new Intent(this, NotificationBroadcast.class);
        intent.putExtra("titleExtra", title);
        intent.putExtra("bodyExtra", body);
        intent.putExtra("timeExtra", timeDate);
        intent.putExtra("ID", id);
        intent.putExtra("OperationType", operationType);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (calendar == null && body == null) {
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
                    calendar.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
                }, mHour, mMinute, true);

        timePickerDialog.show();
    }

    private int createID() {
        Calendar calendar = Calendar.getInstance();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(calendar.getTime()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
