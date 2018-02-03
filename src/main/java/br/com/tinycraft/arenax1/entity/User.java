package br.com.tinycraft.arenax1.entity;

import java.util.UUID;

public class User {

    private UUID uuid;
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
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
        if (wins < 1 || loses < 1) return 1;
        return (double) wins / (double) loses;
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
}