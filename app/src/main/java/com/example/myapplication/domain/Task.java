package com.example.myapplication.domain;

/*
 Définition de ce qu'est une "tâche" à réaliser
 */
public class Task {
    // Une tâche possède un titre décrivant la tâche
    String title;
    // Une tâche possède un état ("fait" ou "à faire")
    TaskState state;

    // Méthode permettant de créer une tâche
    public Task(String title, TaskState state) {
        this.title = title;
        this.state = state;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public TaskState getState() {
        return state;
    }
}
