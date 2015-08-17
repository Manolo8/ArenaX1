package br.com.tinycraft.arenax1.arena;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;

import org.bukkit.entity.Player;

/**
 *
 * @author Willian
 */
public class Arena
{

    private final String name;
    private final String world;
    private Location pos1;
    private Location pos2;
    private List<Player> players;
    private Player winner;
    private Player loser;
    private int remainingTime;
    private boolean isOcurring;
    private boolean isWaitingToStart;

    public Arena(String name, String world, Location pos1, Location pos2)
    {
        this.name = name;
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.remainingTime = 0;
        this.players = new ArrayList();
        this.isOcurring = false;
        this.isWaitingToStart = false;
        this.loser = null;
        this.winner = null;
    }

    public String getName()
    {
        return name;
    }

    public Player getWinner()
    {
        return this.winner;
    }

    public Player getLoser()
    {
        return this.loser;
    }

    public String getWorld()
    {
        return world;
    }

    public Location getPos1()
    {
        return pos1;
    }

    public Location getPos2()
    {
        return pos2;
    }

    public int getRemainingTime()
    {
        return this.remainingTime;
    }

    public boolean isEnded()
    {
        this.remainingTime--;
        return this.remainingTime <= 0;
    }

    public boolean isOcurring()
    {
        return this.isOcurring;
    }

    public boolean isWaitinigToStart()
    {
        return this.isWaitingToStart;
    }

    public boolean isInArena(Player player, boolean remove)
    {
        Iterator<Player> i = players.iterator();

        Player winnern = null;
        boolean is = false;

        while (i.hasNext())
        {
            Player loop = i.next();
            if (player.equals(loop))
            {
                is = true;
                if (remove)
                {
                    i.remove();
                }
            } else
            {
                winnern = loop;
            }
        }

        if(remove)
        {
            this.winner = winnern;
        }
        
        return is;
    }

    public boolean isInArena(Player player)
    {
        return isInArena(player, false);
    }

    public boolean isCompleted()
    {
        return this.pos1 != null && this.pos2 != null;
    }

    public List<Player> getPlayers()
    {
        return this.players;
    }

    public void setWinner(Player player)
    {
        this.winner = player;
    }

    public void setLoser(Player player)
    {
        this.loser = player;
    }

    public boolean setPos1(Location pos1)
    {
        this.pos1 = pos1;
        return isCompleted();
    }

    public boolean setPos2(Location pos2)
    {
        this.pos2 = pos2;
        return isCompleted();
    }

    public void setOcurring(boolean ocurring)
    {
        this.isOcurring = ocurring;
    }

    public void setWaitinigToStart(boolean waiting)
    {
        this.isWaitingToStart = waiting;
    }

    public void setRemainingTime(int time)
    {
        this.remainingTime = time;
    }

    public void addPlayer(Player player)
    {
        this.players.add(player);
    }

    public void removePlayer(Player player)
    {
        isInArena(player, true);
    }

    public void removePlayers()
    {
        players.clear();
    }
}
