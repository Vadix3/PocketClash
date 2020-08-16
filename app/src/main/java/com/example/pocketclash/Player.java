package com.example.pocketclash;

import android.widget.ProgressBar;

public class Player {
    private int health = 0;
    private String Name = "";


    //More properties
    private int numOfTurns = 0;


    public Player() {
    }

    public Player(int health, String name) {
        this.health = health;
        Name = name;
    }

    public int getNumOfTurns() {
        return numOfTurns;
    }

    public void setNumOfTurns(int numOfTurns) {
        this.numOfTurns = numOfTurns;
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
