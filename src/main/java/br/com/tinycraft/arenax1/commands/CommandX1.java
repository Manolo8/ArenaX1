package br.com.tinycraft.arenax1.commands;

import br.com.tinycraft.arenax1.arena.Arena;
import br.com.tinycraft.arenax1.arena.ArenaManager;
import br.com.tinycraft.arenax1.commands.annotation.CommandArena;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import br.com.tinycraft.arenax1.invite.Invite;
import br.com.tinycraft.arenax1.invite.InviteManager;
import br.com.tinycraft.arenax1.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Willian
 */
@SuppressWarnings("unused")
public class CommandX1 {

    private final InviteManager inviteManager;
    private final Language language;
    private final ArenaManager arenaManager;
    private final ArenaExecutor arenaExecutor;

    public CommandX1(InviteManager inviteManager, Language language, ArenaManager arenaManager, ArenaExecutor arenaExecutor) {
        this.inviteManager = inviteManager;
        this.language = language;
        this.arenaManager = arenaManager;
        this.arenaExecutor = arenaExecutor;
    }

    @CommandArena(command = "duel",
            superCommand = "arenax1",
            args = 2,
            usage = "§a/x1 duel [nick] - duel with an player")
    public void duel(Player author, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);

        if (checkPlayers(author, target)) {
            return;
        }

        if (inviteManager.createInvite(author, target)) {
            author.sendMessage(language.getMessage("InviteMessageAuthor"));
            target.sendMessage(language.getMessage("InviteMessageTarget", author.getName()));
        } else author.sendMessage(language.getMessage("ErrorPlayerAlreadyHasInvite"));
    }

    @CommandArena(command = "box",
            superCommand = "arenax1",
            args = 2,
            usage = "§a/x1 box [nick] - go to an box to see the fight")
    public void box(Player author, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            author.sendMessage(language.getMessage("ErrorCommandPlayerNotFound"));
            return;
        }

        Arena arena = arenaExecutor.getPlayerArena(target);

        if (arena == null) {
            author.sendMessage(language.getMessage("ErrorPlayerNotInDuel"));
            return;
        }

        author.teleport(arena.getBox());
    }

    @CommandArena(command = "accept",
            superCommand = "arenax1",
            args = 2,
            usage = "§a/x1 accept [nick] - Accept player duel")
    public void accept(Player target, String[] args) {
        Player author = Bukkit.getPlayer(args[1]);

        if (checkPlayers(target, author)) {
            return;
        }

        if (author == null) {
            target.sendMessage(language.getMessage("ErrorNoInviteFound"));
            return;
        }

        Invite invite = inviteManager.getPendentInvite(author, target);

        if (invite == null) target.sendMessage(language.getMessage("ErrorNoInviteFound"));
        else {
            inviteManager.inviteAccepted(invite);
            target.sendMessage(language.getMessage("InviteAcceptedTarget"));
            author.sendMessage(language.getMessage("InviteAcceptedAuthor"));
        }
    }

    @CommandArena(command = "reject",
            superCommand = "arenax1",
            args = 2,
            usage = "§a/x1 reject [nick] - Reject player duel")
    public void reject(Player target, String[] args) {
        Player author = Bukkit.getPlayer(args[1]);

        if (checkPlayers(target, author)) return;

        if (author == null) {
            target.sendMessage(language.getMessage("ErrorNoInviteFound"));
            return;
        }

        Invite invite = inviteManager.getPendentInvite(author, target);

        if (invite == null) author.sendMessage(language.getMessage("ErrorNoInviteFound"));
        else {
            inviteManager.inviteRejected(invite);
            author.sendMessage(language.getMessage("InviteRejectedTarget"));
            invite.getAuthor().sendMessage(language.getMessage("InviteRejectedAuthor",
                    invite.getTarget().getName()
            ));
        }
    }

    @CommandArena(command = "create",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm create [ArenaName]")
    public void create(Player author, String[] args) {
        if (arenaManager.createArena(args[1], author.getWorld().getName())) author.sendMessage("§aArena created with successful!");
        else author.sendMessage("§cErro: Arena already exists");
    }

    @CommandArena(command = "remove",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm remove [ArenaName]")
    public void remove(Player author, String[] args) {
        if (arenaManager.removeArena(args[1])) author.sendMessage("§aArena removed with sucessful!");
        else author.sendMessage("§cError! Arena not found.");

    }

    @CommandArena(command = "pos1",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm pos1 [ArenaName]")
    public void pos1(Player author, String[] args) {
        Arena arena = arenaManager.getArena(args[1]);

        if (arena == null) author.sendMessage("§cError! Arena not found.");
        else {
            author.sendMessage("§aPosition 1 has been setted!");
            if (arena.setPos1(author.getLocation())) {
                author.sendMessage("§a" + arena.getName() + " is ok now!");
            } else {
                author.sendMessage("§cNow set the second position!");
            }
        }
    }

    @CommandArena(command = "pos2",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm pos2 [ArenaName]")
    public void pos2(Player author, String[] args) {
        Arena arena = arenaManager.getArena(args[1]);

        if (arena == null)
            author.sendMessage("§cError! Arena not found.");
        else {
            author.sendMessage("§aPosition 2 has been setted!");
            if (arena.setPos2(author.getLocation())) {
                author.sendMessage("§a" + arena.getName() + " is ok now!");
            } else {
                author.sendMessage("§cNow set the first position");
            }
        }
    }

    @CommandArena(command = "setlobby",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm setlobby [ArenaName]")
    public void setlobby(Player author, String[] args) {
        Arena arena = arenaManager.getArena(args[1]);

        if (arena == null)
            author.sendMessage("§cError! Arena not found.");
        else {
            arena.setLobby(author.getLocation());
            author.sendMessage("§aLobby setted!");
        }
    }

    @CommandArena(command = "setbox",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm setbox [ArenaName]")
    public void setBox(Player author, String[] args) {
        Arena arena = arenaManager.getArena(args[1]);

        if (arena == null)
            author.sendMessage("§cError! Arena not found.");
        else {
            arena.setBox(author.getLocation());
            author.sendMessage("§aBox setted!");
        }
    }

    @CommandArena(command = "list",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 1,
            usage = "§a/x1adm list")
    public void list(Player author, String[] args) {
        author.sendMessage("§a -> Arenas:");

        for (Arena arena : arenaManager.getArenas()) {
            author.sendMessage("§a " + arena.getName() + (arena.isCompleted() ? " enabled." : " §cno spawn position"));
        }
    }

    private boolean checkPlayers(Player author, Player target) {
        if (target == null) {
            author.sendMessage(language.getMessage("ErrorCommandPlayerNotFound"));
            return true;
        } else if (author.equals(target)) {
            author.sendMessage(language.getMessage("ErrorCommandCantChallengeYourself"));
            return true;
        }
        return false;
    }
}
