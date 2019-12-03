package com.damrad.reminder;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
class FilesOperation {

    private final String dir;

    private Context context;

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

    void deleteNote(Note note) {

        ArrayList<Note> array = (ArrayList<Note>) loadDataToArray().clone();

        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getUniqueID() == note.getUniqueID()) {
                array.remove(i);
            }
        }

        saveNoteToFile(array, false);
    }

}