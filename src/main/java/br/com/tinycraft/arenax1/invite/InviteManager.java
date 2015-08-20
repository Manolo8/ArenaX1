package br.com.tinycraft.arenax1.invite;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.language.Language;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author AB
 */
public class InviteManager extends BukkitRunnable
{

    private final ArenaExecutor arenaExecutor;
    private final Language language;
    private final int defaultTime;
    private final int acceptedWait;
    private final List<Invite> invites;

    public InviteManager(ArenaExecutor arenaExecutor, ArenaConfig config, Language language)
    {
        this.arenaExecutor = arenaExecutor;
        this.defaultTime = config.getDefaultExpireTime();
        this.acceptedWait = config.getDefaultAcceptedWait();
        this.language = language;
        invites = new ArrayList();
    }

    /**
     *
     * @param author
     * @param target
     * @0 if not have invites
     * @1 if author invite target
     * @2 if target invite author
     * @return 0, 1 or 2
     */
    public int getInviteRelation(Player author, Player target)
    {
        int value = 0;
        Iterator<Invite> i = invites.iterator();
        while (i.hasNext())
        {
            Invite invite = i.next();

            if (invite.getTarget().getName().equalsIgnoreCase(author.getName())
                    && invite.getAuthor().getName().equalsIgnoreCase(target.getName()))
            {
                value = 1;
            } else if (invite.getAuthor().getName().equalsIgnoreCase(author.getName())
                    && invite.getTarget().getName().equalsIgnoreCase(target.getName()))
            {
                value = 2;
            }
        }
        return value;
    }

    public Invite getPendentInvite(Player target, Player author)
    {
        Iterator<Invite> i = invites.iterator();
        while (i.hasNext())
        {
            Invite invite = i.next();

            if (invite.getAuthor().getName().equalsIgnoreCase(author.getName())
                    && invite.getTarget().getName().equalsIgnoreCase(target.getName()))
            {
                return invite;
            }
        }
        return null;
    }

    public boolean createInvite(Player author, Player target)
    {
        if (getPendentInvite(author, target) != null)
        {
            return false;
        } else if (getPendentInvite(target, author) != null)
        {
            return false;
        }

        invites.add(new Invite(author, target, defaultTime, acceptedWait));
        target.sendMessage(language.getMessage("InviteMessageTarget", author.getName()));
        return true;
    }

    public void inviteAccepted(Invite invite)
    {
        clearPlayerInvites(invite);
        invite.setAccepted(true);
        invite.getAuthor().sendMessage(language.getMessage("InviteAcceptedAuthor"));
    }

    public void inviteRejected(Invite invite)
    {
        invites.remove(invite);
        invite.getAuthor().sendMessage(language.getMessage("InviteRejectedAuthor",
                invite.getTarget().getName()
        ));
    }

    public void clearPlayerInvites(Invite invite)
    {
        Iterator<Invite> i = invites.iterator();

        String author = invite.getAuthor().getName();
        String target = invite.getTarget().getName();

        while (i.hasNext())
        {
            Invite invi = i.next();

            if (!invi.equals(invite))
            {
                String authori = invi.getAuthor().getName();
                String targeti = invi.getTarget().getName();

                if (author.equals(authori)
                        || author.equals(targeti)
                        || target.equals(authori)
                        || target.equals(targeti))
                {
                    i.remove();
                }
            }
        }
    }

    @Override
    public void run()
    {
        Iterator<Invite> i = invites.iterator();

        while (i.hasNext())
        {
            Invite invite = i.next();

            if (!invite.getAuthor().isOnline())
            {
                invite.getTarget().sendMessage(
                        language.getMessage("InviteCancelledExit"));
                i.remove();
                continue;
            } else if (!invite.getTarget().isOnline())
            {
                invite.getAuthor().sendMessage(
                        language.getMessage("InviteCancelledExit"));
                i.remove();
                continue;
            }

            if (invite.isEnded() && !invite.isAccepted())
            {
                invite.getAuthor().sendMessage(
                        language.getMessage("InviteExpiredAuthor",
                                invite.getTarget().getName()));
                invite.getTarget().sendMessage(
                        language.getMessage("InviteExpiredTarget",
                                invite.getAuthor().getName()));
                i.remove();
            } else if (invite.isAccepted())
            {
                if (invite.isAcceptedWait())
                {
                    if (arenaExecutor.createX1(invite.getAuthor(), invite.getTarget()))
                    {
                        invite.getAuthor().sendMessage(
                                language.getMessage("InviteAcceptedStarting"));
                        invite.getTarget().sendMessage(
                                language.getMessage("InviteAcceptedStarting"));
                    } else
                    {
                        invite.getAuthor().sendMessage(
                                language.getMessage("InviteAcceptedNoArena"));
                        invite.getTarget().sendMessage(
                                language.getMessage("InviteAcceptedNoArena"));
                    }

                    i.remove();
                } else
                {
                    invite.getAuthor().sendMessage(
                            language.getMessage("InviteAcceptedWaiting",
                                    invite.getAcceptedWaitTime()));
                    invite.getTarget().sendMessage(
                            language.getMessage("InviteAcceptedWaiting",
                                    invite.getAcceptedWaitTime()));
                }
            }
        }
    }
}
