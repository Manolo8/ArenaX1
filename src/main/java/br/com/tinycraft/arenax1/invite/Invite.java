package br.com.tinycraft.arenax1.invite;

import org.bukkit.entity.Player;

/**
 *
 * @author AB
 */
public class Invite 
{
    private final Player author;
    private final Player target;
    private int remainingTime;
    private boolean accepted;
    private int acceptedWait;

    public Invite(Player author, Player target, int remainingTime, int acceptedWait)
    {
        this.author = author;
        this.target = target;
        this.remainingTime = remainingTime;
        this.accepted = false;
        this.acceptedWait = acceptedWait;
    }
    
    public void setAccepted(boolean value)
    {
        this.accepted = value;
    }
    
    public boolean isEnded()
    {
        remainingTime--;
        return remainingTime <= 0;
    }
    
    public boolean isAccepted()
    {
        return this.accepted;
    }
    
    public boolean isAcceptedWait()
    {
        acceptedWait--;
        return acceptedWait <= 0;
    }

    public int getAcceptedWaitTime()
    {
        return this.acceptedWait;
    }
    
    public Player getAuthor()
    {
        return author;
    }

    public Player getTarget()
    {
        return target;
    }
}
