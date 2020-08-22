package com.example.pocketclash;

public class Score implements Comparable {
    private int numOfTurns = 0;

    private MyLocation location;

    public Score() {
    }

    public Score(int numOfTurns) {
        this.numOfTurns = numOfTurns;
    }

    public Score(int numOfTurns, MyLocation location) {
        this.numOfTurns = numOfTurns;
        this.location = location;
    }

    public int getNumOfTurns() {
        return numOfTurns;
    }

    public void setNumOfTurns(int numOfTurns) {
        this.numOfTurns = numOfTurns;
    }

    public MyLocation getLocation() {
        return location;
    }

    public void setLocation(MyLocation location) {
        this.location = location;
    }

    @Override
    public int compareTo(Object o) {
        Score temp = (Score) o;
        return this.numOfTurns - temp.numOfTurns;
    }

    @Override
    public String toString() {
        return "Number of turns: " + this.numOfTurns;
    }


}
