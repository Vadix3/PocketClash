package com.example.pocketclash;

public class Skill {
    private String name = "";
    private int damage = 0;
    private int points = 0;
    private int type = 0; // 0 = low damage, 1 = high damage, 2 = heal

    public Skill() {
    }

    public Skill(String name, int damage, int points, int type) {
        this.name = name;
        this.damage = damage;
        this.points = points;
        this.type = type;
    }

    public Skill(Skill skill) { // Copy constructor
        this.name = skill.name;
        this.damage = skill.damage;
        this.points = skill.points;
        this.type = skill.type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public int getPoints() {
        return points;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Damage: " + damage + " Type: " + type + " Points: " + points;
    }
}
