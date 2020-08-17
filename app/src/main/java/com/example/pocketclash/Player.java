package com.example.pocketclash;

public class Player {
    private int health = 0;
    private String name = "";
    private Skill[] skills = null;

    //More properties
    private int numOfTurns = 0;


    public Player() {
    }

    public Player(int health, String name) {
        this.health = health;
        this.name = name;
    }

    public void setSkills(Skill[] skills) {
        this.skills = skills;
    }

    public Skill[] getSkills() {
        return skills;
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
        return name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Skill lowSkill() {
        return this.skills[0];
    }

    public Skill highSkill() {
        return this.skills[1];
    }

    public Skill heal() {
        return this.skills[2];
    }

    public void setName(String name) {
        this.name = name;
    }
}
