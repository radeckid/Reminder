package com.damrad.reminder;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
class FilesOperation {

    private final String dir;

    private Context context;

    FilesOperation() {
        dir = null;
    }

    FilesOperation(Context context) {
        this.context = context;

        String fileName = "/notes_data.bin";
        dir = context.getApplicationContext().getFilesDir().getPath() + fileName;
        File file = new File(dir);

        if (!file.exists()) {
            ArrayList<Note> array = new ArrayList<Note>();
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(dir));
                outputStream.writeObject(array);
                outputStream.close();
            } catch (IOException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    boolean saveNoteToFile(ArrayList<Note> arrayList, boolean confirmation) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(dir));
            outputStream.writeObject(arrayList);
            outputStream.close();
            return true;
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        } finally {
            if (confirmation) {
                Toast.makeText(context, context.getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
            }
        }

    }

    ArrayList<Note> loadDataToArray() {

        ArrayList<Note> array = new ArrayList<Note>();

        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(dir));

            array = (ArrayList<Note>) inputStream.readObject();

            inputStream.close();
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return array;
    }

    void deleteNote(int uniqueID) {

        ArrayList<Note> array = (ArrayList<Note>) loadDataToArray().clone();

        for (Note note : array) {
            if (note.getUniqueID() == uniqueID) {
                array.remove(note);
                break;
            }
        }

        saveNoteToFile(array, false);
    }

    void saveToArchive(Context context, String title, String body, String timeDate) {
        try {
            OutputStreamWriter outputStream = new OutputStreamWriter(context.getApplicationContext().openFileOutput("archive.txt", Context.MODE_APPEND));
            outputStream.write(context.getString(R.string.Title) + title);
            outputStream.write("\n");
            outputStream.write(context.getString(R.string.Body) + body);
            outputStream.write("\n");
            outputStream.write(timeDate);
            outputStream.write("\n");
            outputStream.write("\n");
            outputStream.close();
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    String getArchivedText(Context context) {
        StringBuilder archived = new StringBuilder();

        try {
            InputStreamReader inputStream = new InputStreamReader(context.getApplicationContext().openFileInput("archive.txt"));

            int data = inputStream.read();

            while (data != -1) {
                archived.append((char) data);
                data = inputStream.read();
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return archived.toString().trim();
    }

}