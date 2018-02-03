package br.com.tinycraft.arenax1.commands;

import br.com.tinycraft.arenax1.Language;
import br.com.tinycraft.arenax1.commands.annotation.CommandArena;
import br.com.tinycraft.arenax1.controller.ArenaController;
import br.com.tinycraft.arenax1.controller.InviteController;
import br.com.tinycraft.arenax1.controller.UserController;
import br.com.tinycraft.arenax1.entity.Arena;
import br.com.tinycraft.arenax1.entity.Invite;
import br.com.tinycraft.arenax1.entity.User;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author Willian
 */
@SuppressWarnings("unused")
public class CommandX1 {

    private final InviteController inviteController;
    private final Language language;
    private final ArenaController arenaController;
    private final UserController userController;
    private final ArenaExecutor arenaExecutor;

    public CommandX1(InviteController inviteController,
                     Language language,
                     ArenaController arenaController,
                     UserController userController,
                     ArenaExecutor arenaExecutor) {
        this.inviteController = inviteController;
        this.language = language;
        this.arenaController = arenaController;
        this.userController = userController;
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

        if (inviteController.createInvite(author, target)) {
            author.sendMessage(language.getMessage("InviteMessageAuthor"));
            target.sendMessage(language.getMessage("InviteMessageTarget", author.getName()));
        } else author.sendMessage(language.getMessage("ErrorPlayerAlreadyHasInvite"));
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

        Invite invite = inviteController.getPendentInvite(author, target);

        if (invite == null) target.sendMessage(language.getMessage("ErrorNoInviteFound"));
        else {
            inviteController.inviteAccepted(invite);
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

        Invite invite = inviteController.getPendentInvite(author, target);

        if (invite == null) author.sendMessage(language.getMessage("ErrorNoInviteFound"));
        else {
            inviteController.inviteRejected(invite);
            author.sendMessage(language.getMessage("InviteRejectedTarget"));
            invite.getAuthor().sendMessage(language.getMessage("InviteRejectedAuthor",
                    invite.getTarget().getName()
            ));
        }
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

    @CommandArena(command = "test",
            superCommand = "arenax1",
            args = 1,
            usage = "")
    public void test(Player author, String[] args) {
        Random random = new Random();

        for (int i = 0; i < 400; i++) {
            User user = new User();
            user.setUuid(UUID.randomUUID());
            user.setLastName("ASD " + random.nextInt(100));
            user.setLoses(random.nextInt(5000));
            user.setWins(random.nextInt(5000));
            try {
                userController.getUserService().save(user);
            } catch (DataBaseException e) {
                e.printStackTrace();
            }
        }
    }

    @CommandArena(command = "ranking",
            superCommand = "arenax1",
            args = {1, 2},
            usage = "§a/x1 ranking ?[page] - Ver o ranking dos 100 melhores jogadores")
    public void ranking(Player author, String[] args) {
        int page = 0;
        int viewSize = 10;

        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                author.sendMessage(language.getMessage("RankingPageInvalid"));
                return;
            }
        }

        List<User> ranking = userController.getRanking();

        if (ranking.isEmpty()) {
            author.sendMessage(language.getMessage("NoRanking"));
            return;
        }

        if ((page - 1) * viewSize > ranking.size()) {
            page = ranking.size() / 10;
        }

        int current = page * viewSize;

        author.sendMessage(language.getMessage("RankingDisplayHeader", page + 1, (ranking.size() / viewSize) + 1));

        for (int i = 0; i < viewSize; i++) {
            if (ranking.size() - 1 < current + i) break;
            User user = ranking.get(current + i);
            author.sendMessage(language.getMessage("RankingDisplayBody",
                    current + i,
                    user.getLastName(),
                    user.getRate(),
                    user.getWins(),
                    user.getLoses()));
        }
    }

    @CommandArena(command = "create",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm create [ArenaName]")
    public void create(Player author, String[] args) {
        if (arenaController.createArena(args[1], author.getWorld().getName())) author.sendMessage("§aArena created with successful!");
        else author.sendMessage("§cErro: Arena already exists");
    }

    @CommandArena(command = "remove",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm remove [Name]")
    public void remove(Player author, String[] args) {
        if (arenaController.removeArena(args[1])) author.sendMessage("§aArena removed with sucessful!");
        else author.sendMessage("§cError! Arena not found.");

    }

    @CommandArena(command = "pos1",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "§a/x1adm pos1 [Name]")
    public void pos1(Player author, String[] args) {
        Arena arena = arenaController.getArena(args[1]);

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
            usage = "§a/x1adm pos2 [Name]")
    public void pos2(Player author, String[] args) {
        Arena arena = arenaController.getArena(args[1]);

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
            usage = "§a/x1adm setlobby [Name]")
    public void setlobby(Player author, String[] args) {
        Arena arena = arenaController.getArena(args[1]);

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
            usage = "§a/x1adm setbox [Name]")
    public void setBox(Player author, String[] args) {
        Arena arena = arenaController.getArena(args[1]);

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

        for (Arena arena : arenaController.getArenas())
            author.sendMessage("§a " + arena.getName() + (arena.isCompleted() ? " enabled." : " §cno spawn position"));
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
