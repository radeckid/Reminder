package com.damrad.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ArchiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        TextView archiveTV = findViewById(R.id.archiveTV);

        archiveTV.setText(new FilesOperation().getArchivedText(getApplicationContext()));
    }

    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent(ArchiveActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
