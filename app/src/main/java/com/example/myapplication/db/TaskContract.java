package com.example.myapplication.db;

import android.provider.BaseColumns;

/*
 Contrat définissant la structure de notre base de données
 */
public class TaskContract {
    // Le nom de la base de données
    public static final String DB_NAME = "com.golda.todolist.db";
    // La version
    public static final int DB_VERSION = 1;
    public static class TaskEntry implements BaseColumns {
        // Le nom de la table dans notre base de données qui va servir à stocker les tâches à faire
        public static final String TABLE = "tasks";

        // La ligne de la base de données servant à stocker la tâche à faire
        public static final String COL_TASK_TITLE = "title";
        // La ligne de la base de données servant à indiquer si la tâche est faite ou non
        public static final String COL_TASK_STATE = "state";
    }
}