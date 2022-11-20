package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.TaskContract;
import com.example.myapplication.db.TaskDbHelper;
import com.example.myapplication.domain.Task;
import com.example.myapplication.domain.TaskState;

import java.util.ArrayList;

/*
 Il s'agit ici du fichier qui va contenir toute la logique de l'application.
 C'est à dire, qui va définir ce qu'il se passe lorsqu'on touche l'écran/clic sur un bouton
 ou toute autre interraction entre l'utilisateur et l'application.
*/
public class MainActivity extends AppCompatActivity {
    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private TaskArrayAdapter mAdapter;

    // Méthode servant à initialiser toutes les informations qui vont bien lors du lancement de l'application.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelper = new TaskDbHelper(this);
        mTaskListView = findViewById(R.id.list_todo);
        // Affichage à l'écran de l'application. Sans ça, rien ne s'affiche.
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Méthode qui va servir à sauvegarder dans la base de données qu'on a bien rajouté la tâche.
       Sans ça, lorsqu'on redémarre l'application on perdrait toutes nous tâches.
    */
    private void addInDataBase(String title) {
        ContentValues values = new ContentValues();
        // Rajout du nom de la tâche avec la valeur sélectionnée par l'utilisateur
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, title);
        // On créé toujours la tâche à l'état "à faire"
        values.put(TaskContract.TaskEntry.COL_TASK_STATE, String.valueOf(TaskState.TODO));
        // Sauvegarde de la nouvelle tâche en base de données
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Méthode générique qui sert à plusieurs endroits pour mettre à jour la base de données et enregistrer les informations
    private void updateInDataBase(String title, TaskState state) {
        ContentValues values = new ContentValues();
        // Rajout du nom de la tâche avec la valeur sélectionnée par l'utilisateur
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, title);
        // On créé/modifie la tâche avec l'état actuel de la tâche
        values.put(TaskContract.TaskEntry.COL_TASK_STATE, String.valueOf(state));

        // Enregistrement en base de données
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.updateWithOnConflict(TaskContract.TaskEntry.TABLE,
                values,
                TaskContract.TaskEntry.COL_TASK_TITLE + "= ?" ,
                new String[]{title},
                SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    // Méthode qui va gérer comment on affiche les différents éléments à l'écran
    // en fonctionn des informations qu'on a en base de données.
    private void updateUI() {
        ArrayList<Task> taskList = new ArrayList<>();

        // Récupération des tâches existantes dans la base de données
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{ TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE, TaskContract.TaskEntry.COL_TASK_STATE },
                null, null, null, null, null);

        // Pour toutes les tâches récupérées de la base de données, on défini les valeurs du titre et de l'état
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            int state = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_STATE);
            taskList.add(new Task(cursor.getString(idx), TaskState.valueOf(cursor.getString(state))));
        }

        // Création de l'affichage à l'écran du téléphone
        if (mAdapter == null) {
            mAdapter = new TaskArrayAdapter(this, taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        // On referme toujours la base de données parès l'avoir ouverte
        cursor.close();
        db.close();
    }

    // Méthode qui va servir à rajouter une tâche lorsqu'on clic sur le bouton d'ajout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_task) {
            final EditText taskEditText = new EditText(this);
            // Affichage d'une popup pour saisir la nouvelle tâche
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(taskEditText)
                    .create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode qui se lance lorsqu'on utilise le bouton indiquant qu'une tâche est faite.
    public void doneTask(View view) {
        View parent = (View) view.getParent();
        TextView task = parent.findViewById(R.id.task_title);
        // On affiche le texte de façon à ce qu'il soit barré
        task.setPaintFlags(task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // On rend le bouton "fait" invisible. Puisqu'on vient de cliquer dessus, c'est que la tâche est déjà faite.
        Button doneButton = parent.findViewById(R.id.task_done);
        doneButton.setVisibility(View.INVISIBLE);
        // À la place, on fait apparaître le bouton "à faire", au cas où on s'est trompé et qu'on souhaite annuler la précédente action
        Button undoButton = parent.findViewById(R.id.task_undo);
        undoButton.setVisibility(View.VISIBLE);

        String taskName = String.valueOf(task.getText());

        // On enregistre en base de données. Sinon en redémarrant l'application on perdrait le statut de toutes nos tâches.
        updateInDataBase(taskName, TaskState.DONE);
    }

    // Méthode qui se lance lorsqu'on utilise le bouton indiquant qu'une tâche est à faire.
    public void undoTask(View view) {
        View parent = (View) view.getParent();
        TextView task = parent.findViewById(R.id.task_title);
        //On affiche le texte de façon à ce qu'il ne soit plus barré s'il l'était
        task.setPaintFlags(task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        // On rend le bouton "à faire" invisible. Puisqu'on vient de cliquer dessus, c'est que la tâche est déjà faite.
        Button undoButton = parent.findViewById(R.id.task_undo);
        undoButton.setVisibility(View.INVISIBLE);
        // À la place, on fait apparaître le bouton "fait", au cas où on souhaite annuler la précédente action
        Button doneButton = parent.findViewById(R.id.task_done);
        doneButton.setVisibility(View.VISIBLE);

        String taskName = String.valueOf(task.getText());

        // On enregistre en base de données. Sinon en redémarrant l'application on perdrait le statut de toutes nos tâches.
        updateInDataBase(taskName, TaskState.TODO);
    }

    // Méthode qui se lance lorsqu'on supprimer une tâche
    public void deleteTask(View view) {
        // On supprime la tâche de l'affichage du téléphone
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());

        /* On supprime la tâche de la base de données.
           Sinon en redémarrant le téléphone on afficherait à nouveau les tâches qu'on avait supprimé.
        */
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }
}