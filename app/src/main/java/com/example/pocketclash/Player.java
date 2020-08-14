package com.example.pocketclash;

import android.widget.ProgressBar;

public class Player {
    int health;
    String Name;
    ProgressBar progressBar;
    //More properties
    public Player() {
    }

    public Player(int health, String name) {
        this.health = health;
        Name = name;
    }

    public int getHealth() {
        return health;
    }

    public String getName() {
        return Name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setName(String name) {
        Name = name;
    }
}
