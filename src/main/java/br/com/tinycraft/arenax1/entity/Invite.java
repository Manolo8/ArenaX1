package br.com.tinycraft.arenax1.entity;

import org.bukkit.entity.Player;

/**
 * @author AB
 */
public class Invite {
    private final Player author;
    private final Player target;
    private int remainingTime;
    private boolean accepted;
    private int acceptedWait;

    public Invite(Player author, Player target, int remainingTime, int acceptedWait) {
        this.author = author;
        this.target = target;
        this.remainingTime = remainingTime;
        this.accepted = false;
        this.acceptedWait = acceptedWait;
    }

    public boolean isEnded() {
        remainingTime--;
        return remainingTime <= 0;
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean value) {
        this.accepted = value;
    }

    public boolean isAcceptedWait() {
        acceptedWait--;
        return acceptedWait < 1;
    }

    public int getAcceptedWaitTime() {
        return this.acceptedWait;
    }

    public Player getAuthor() {
        return author;
    }

    public Player getTarget() {
        return target;
    }

    public boolean authorMatch(Player player) {
        return author.equals(player);
    }

    public boolean targetMatch(Player player) {
        return target.equals(player);
    }

    public boolean anyMatch(Player player) {
        return authorMatch(player) || targetMatch(player);
    }

    public boolean removeOffline() {
        if (!author.isOnline()) return true;
        if (!target.isOnline()) return true;
        return false;
    }

    public boolean isExpired() {
        return isEnded() && !isAccepted();
    }

    public void sendMessage(String... messages) {
        if (messages.length == 0) return;
        if (author.isOnline()) author.sendMessage(messages[0]);
        if (target.isOnline()) target.sendMessage(messages.length == 2 ? messages[1] : messages[0]);
    }
}
