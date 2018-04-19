package br.com.tinycraft.arenax1.entity;

import java.util.UUID;

public class User {

    private Object uuid;
    private String lastName;
    private int wins;
    private int loses;
    private boolean isNew;
    private boolean save;

    public User() {
        isNew = true;
        wins = 0;
        loses = 0;
        save = false;
    }

    public Object getUuid() {
        return uuid;
    }

    public void setUuid(Object uuid) {
        this.uuid = uuid;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (this.lastName != null && lastName != null && this.lastName.equals(lastName)) return;
        this.lastName = lastName;
        save = true;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        save = true;
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        save = true;
        this.loses = loses;
    }

    public double getRate() {
        return (double) (wins < 1 ? 1 : wins) / (double) (loses < 1 ? 1 : loses);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) return false;
        return ((User) obj).getUuid().equals(uuid);
    }
}