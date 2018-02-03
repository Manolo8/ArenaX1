package br.com.tinycraft.arenax1.entity;

import br.com.tinycraft.arenax1.entity.itf.ArenaStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Willian
 */
public class Arena implements ArenaStatus {

    private String name;
    private String world;
    private Location pos1;
    private Location pos2;
    private Location lobby;
    private Location box;
    private List<Player> players;
    private Player winner;
    private Player loser;
    private int remainingTime;
    private boolean isOccurring;
    private boolean isWaitingToStart;
    private boolean tie;

    public Arena() {
    }

    public Arena(String name, String world, Location pos1, Location pos2, Location lobby, Location box) {
        this.name = name;
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.lobby = lobby;
        this.box = box;
        this.remainingTime = 0;
        this.players = new ArrayList<>();
        this.isOccurring = false;
        this.isWaitingToStart = false;
        this.loser = null;
        this.winner = null;
        this.tie = false;
    }

    public String getName() {
        return name;
    }

    public Player getWinner() {
        return this.winner;
    }

    public Player getLoser() {
        return this.loser;
    }

    public void setLoser(Player loser) {
        if (this.getWinner() != null) {
            tie = true;
            return;
        }

        this.loser = loser;
        for (Player player : players) {
            if (player.equals(loser)) continue;
            this.winner = player;
        }
    }

    public String getWorld() {
        return world;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location getLobby() {
        if (lobby != null) return lobby;
        return Bukkit.getWorld(getWorld()).getSpawnLocation();
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public boolean isTie() {
        return tie;
    }

    public void setTie(boolean tie) {
        this.tie = tie;
    }

    public Location getBox() {
        return box;
    }

    public void setBox(Location box) {
        this.box = box;
    }

    public boolean isReady() {
        return !isOccurring() && isCompleted();
    }

    public int getRemainingTime() {
        return this.remainingTime;
    }

    public void setRemainingTime(int time) {
        this.remainingTime = time;
    }

    public boolean isEnded() {
        this.remainingTime--;
        return this.remainingTime < 1;
    }

    public boolean isOccurring() {
        return this.isOccurring;
    }

    public void setOccurring(boolean occurring) {
        this.isOccurring = occurring;
    }

    public boolean isWaitinigToStart() {
        return this.isWaitingToStart;
    }

    public void setWaitinigToStart(boolean waiting) {
        this.isWaitingToStart = waiting;
    }

    public boolean isInArena(Player player) {
        return players.contains(player);
    }

    public boolean removeFromArena(Player player) {
        return players.remove(player);
    }

    public boolean isCompleted() {
        return this.pos1 != null && this.pos2 != null;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public boolean setPos1(Location pos1) {
        this.pos1 = pos1;
        return isCompleted();
    }

    public boolean setPos2(Location pos2) {
        this.pos2 = pos2;
        return isCompleted();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void close() {
        setLoser(null);
        this.winner = null;
        setOccurring(false);
        setWaitinigToStart(false);

        for (Player player : getPlayers()) {
            player.teleport(getLobby());
        }

        players.clear();
    }

    public void sendMessage(String message) {
        for (Player player : getPlayers()) player.sendMessage(message);
    }

    public void startArena(Invite invite) {
        setOccurring(true);
        setWaitinigToStart(true);
        setTie(false);

        addPlayer(invite.getAuthor());
        addPlayer(invite.getTarget());

        invite.getAuthor().teleport(getPos1());
        invite.getTarget().teleport(getPos2());
    }
}
