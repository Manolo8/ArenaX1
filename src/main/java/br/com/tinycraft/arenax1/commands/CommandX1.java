package br.com.tinycraft.arenax1.commands;

import br.com.tinycraft.arenax1.arena.Arena;
import br.com.tinycraft.arenax1.arena.ArenaManager;
import br.com.tinycraft.arenax1.commands.annotation.CommandArena;
import br.com.tinycraft.arenax1.gui.GUI;
import br.com.tinycraft.arenax1.invite.Invite;
import br.com.tinycraft.arenax1.invite.InviteManager;
import br.com.tinycraft.arenax1.language.Language;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Willian
 */
public class CommandX1
{

    private final InviteManager inviteManager;
    private final Language language;
    private final GUI gui;
    private final ArenaManager arenaManager;

    public CommandX1(InviteManager inviteManager, Language language, GUI gui, ArenaManager arenaManager)
    {
        this.inviteManager = inviteManager;
        this.language = language;
        this.gui = gui;
        this.arenaManager = arenaManager;
    }

    @CommandArena(command = "duel",
            superCommand = "arenax1",
            args = 2,
            usage = "§a/x1 duel [nick] - duel with an player")
    public void duel(Player author, String[] args)
    {
        Player target = Bukkit.getPlayer(args[1]);

        if (checkPlayers(author, target))
        {
            return;
        }

        if (inviteManager.createInvite(author, target))
        {
            author.sendMessage(language.getMessage("InviteMessageAuthor"));
        } else
        {
            author.sendMessage(language.getMessage("ErrorPlayerAlreadyHasInvite"));
        }
    }

    @CommandArena(command = "accept",
            superCommand = "arenax1",
            args = 2,
            usage = "§a/x1 accept [nick] - Accept player duel")
    public void accept(Player author, String[] args)
    {
        Player target = Bukkit.getPlayer(args[1]);

        if (checkPlayers(author, target))
        {
            return;
        }

        Invite invite = inviteManager.getPendentInvite(author, target);
        if (invite == null)
        {
            author.sendMessage(language.getMessage("ErrorNoInviteFound"));
        } else
        {
            inviteManager.inviteAccepted(invite);
            author.sendMessage(language.getMessage("InviteAcceptedTarget"));
        }
    }

    @CommandArena(command = "reject",
            superCommand = "arenax1",
            args = 2,
            usage = "§a/x1 reject [nick] - Reject player duel")
    public void reject(Player author, String[] args)
    {
        Player target = Bukkit.getPlayer(args[1]);

        if (checkPlayers(author, target))
        {
            return;
        }

        if (target == null)
        {
            author.sendMessage(language.getMessage("ErrorNoInviteFound"));
            return;
        }

        Invite invite = inviteManager.getPendentInvite(author, target);
        if (invite == null)
        {
            author.sendMessage(language.getMessage("ErrorNoInviteFound"));
        } else
        {
            inviteManager.inviteRejected(invite);
            author.sendMessage(language.getMessage("InviteRejectedTarget"));
        }
    }

    @CommandArena(command = "gui",
            superCommand = "arenax1",
            args = 1,
            usage = "§a/x1 guit")
    public void gui(Player author, String[] args)
    {
        gui.openGui(author);
    }

    @CommandArena(command = "create",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm create [ArenaName]")
    public void create(Player author, String[] args)
    {
        if (arenaManager.createArena(args[1], author.getWorld().getName()))
        {
            author.sendMessage("§aArena created with successful!");
        } else
        {
            author.sendMessage("§cErro: Arena already exists");
        }
    }

    @CommandArena(command = "remove",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm remove [ArenaName]")
    public void remove(Player author, String[] args)
    {
        if (arenaManager.removeArena(args[1]))
        {
            author.sendMessage("§aArena removed with sucessful!");
        } else
        {
            author.sendMessage("§cError! Arena not found.");
        }
    }

    @CommandArena(command = "pos1",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm pos1 [ArenaName]")
    public void pos1(Player author, String[] args)
    {
        Arena arena = arenaManager.getArena(args[1]);

        if (arena == null)
        {
            author.sendMessage("§cError! Arena not found.");
        } else
        {
            author.sendMessage("§aPosition 1 has been setted!");
            if (arena.setPos1(author.getLocation()))
            {
                author.sendMessage("§a" + arena.getName() + " is ok now!");
            } else
            {
                author.sendMessage("§cNow set the second position!");
            }
        }
    }

    @CommandArena(command = "pos2",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm pos2 [ArenaName]")
    public void pos2(Player author, String[] args)
    {
        Arena arena = arenaManager.getArena(args[1]);

        if (arena == null)
        {
            author.sendMessage("§cError! Arena not found.");
        } else
        {
            author.sendMessage("§aPosition 2 has been setted!");
            if (arena.setPos2(author.getLocation()))
            {
                author.sendMessage("§a" + arena.getName() + " is ok now!");
            } else
            {
                author.sendMessage("§cNow set the first position");
            }
        }
    }

    @CommandArena(command = "setlobby",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 1,
            usage = "§a/x1adm setlobby")
    public void setlobby(Player author, String[] args)
    {
        author.sendMessage("§aLobby setted!");
        arenaManager.setArenaLobby(author.getLocation());
    }

    @CommandArena(command = "list",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 1,
            usage = "§a/x1adm list")
    public void list(Player author, String[] args)
    {
        author.sendMessage("§a -> Arenas:");
        Iterator<Arena> i = arenaManager.getArenas().iterator();

        while (i.hasNext())
        {
            Arena arena = i.next();
            author.sendMessage("§a " + arena.getName() + (arena.isCompleted() ? " enabled." : " §cno spawn position"));
        }
    }

    public boolean checkPlayers(Player author, Player target)
    {
        if (target == null)
        {
            author.sendMessage(language.getMessage("ErrorCommandPlayerNotFound"));
            return true;
        } else if (author.getName().equalsIgnoreCase(target.getName()))
        {
            author.sendMessage(language.getMessage("ErrorCommandCantChallengeYourself"));
            return true;
        }
        return false;
    }
}
