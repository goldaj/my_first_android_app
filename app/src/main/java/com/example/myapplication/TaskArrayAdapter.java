package com.example.myapplication;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.domain.Task;
import com.example.myapplication.domain.TaskState;

import java.util.List;

/*
 Adaptateur permettant d'associer les données de la base de données
 avec les différents objets visuels (boutons, textes, etc...) de notre écran
 */
public class TaskArrayAdapter extends ArrayAdapter<Task> {

    // Création d'un adaptateur
    public TaskArrayAdapter(@NonNull Context context, @NonNull List<Task> objects) {
        super(context, 0, objects);
    }

    // Méthode qui fait le lien entre la base de données et l'affichage
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }

        // Dans la base de données on aura une liste de tâches.
        // On indique ici qu'on va executer les opérations sur chaque tâches trouvée
        Task currentTask = getItem(position);

        // Association du titre de la tâche dans la base de données avec celui de l'écran
        TextView title = currentItemView.findViewById(R.id.task_title);
        title.setText(currentTask.getTitle());

        Button doneButton = currentItemView.findViewById(R.id.task_done);
        Button undoButton = currentItemView.findViewById(R.id.task_undo);
        // Si le statut de la tâche dans la base de données est "fait"
        if (currentTask.getState() == TaskState.DONE) {
            // Alors on n'affiche pas le bouton permettant de passer la tâche à "fait" (puisqu'elle est déjà faite)
            doneButton.setVisibility(View.INVISIBLE);
            // Mais on affiche le bouton permettant de la repasser à "à faire"
            undoButton.setVisibility(View.VISIBLE);
            // On rajoute le nécessaire pour que le texte soit barré, puisqu'il s'agit d'une tâche déjà faite
            title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        // Sinon, c'est que la tâche n'est pas encore faite
        else {
            // Alors on rend le bouton "à faire" invisible (puisque la tâche est déjà à faire)
            undoButton.setVisibility(View.INVISIBLE);
            // Et on rend le bouton "fait" visible
            doneButton.setVisibility(View.VISIBLE);
            // On fait le nécessaire pour s'assurer que le texte ne soit pas barré
            title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        return currentItemView;
    }
}
