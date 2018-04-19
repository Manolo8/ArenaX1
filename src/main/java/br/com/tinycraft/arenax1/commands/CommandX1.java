package br.com.tinycraft.arenax1.commands;

import br.com.tinycraft.arenax1.Language;
import br.com.tinycraft.arenax1.commands.annotation.CommandArena;
import br.com.tinycraft.arenax1.controller.ArenaController;
import br.com.tinycraft.arenax1.controller.InviteController;
import br.com.tinycraft.arenax1.controller.UserController;
import br.com.tinycraft.arenax1.entity.Arena;
import br.com.tinycraft.arenax1.entity.Invite;
import br.com.tinycraft.arenax1.entity.User;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

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

    @CommandArena(command = "CommandChallenge",
            superCommand = "arenax1",
            args = 2,
            usage = "CommandChallengeUsage")
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

    @CommandArena(command = "CommandAccept",
            superCommand = "arenax1",
            args = 2,
            usage = "CommandAcceptUsage")
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

    @CommandArena(command = "CommandReject",
            superCommand = "arenax1",
            args = 2,
            usage = "CommandRejectUsage")
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

    @CommandArena(command = "CommandBox",
            superCommand = "arenax1",
            args = 2,
            usage = "CommandBoxUsage")
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

    @CommandArena(command = "CommandStatus",
            superCommand = "arenax1",
            args = {1, 2},
            usage = "CommandStatusUsage")
    public void status(Player author, String[] args) {
        Player target = author;
        if (args.length == 2) target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            author.sendMessage(language.getMessage("ErrorCommandPlayerNotFound"));
            return;
        }

        User user = userController.findOne(target);

        int rankPosition = userController.findRankPosition(user);

        String rank;
        if (rankPosition == -1) {
            rank = language.getMessage("PlayerStatusNoRank");
        } else {
            rank = language.getMessage("PlayerStatusRank", rankPosition + 1);
        }

        author.sendMessage(language.getMessage("PlayerStatus",
                rank,
                user.getRate(),
                user.getWins(),
                user.getLoses()));
    }

    @CommandArena(command = "CommandRanking",
            superCommand = "arenax1",
            args = {1, 2},
            usage = "CommandRankingUsage")
    public boolean ranking(Player author, String[] args) {
        int page = 0;
        int viewSize = 10;

        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
                page--;
                if (page < 0 || page > 10) throw new NumberFormatException();
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        List<User> ranking = userController.getRanking();

        if (ranking.isEmpty()) {
            author.sendMessage(language.getMessage("NoRanking"));
            return true;
        }

        if ((page - 1) * viewSize > ranking.size()) {
            page = ranking.size() / viewSize;
        }

        int current = page * viewSize;
        int totalPages = ranking.size() / viewSize;

        author.sendMessage(language.getMessage("RankingDisplayHeader",
                (page == 0 ? 1 : page + 1),
                (totalPages == 0 ? 1 : totalPages)));

        for (int i = 0; i < viewSize; i++) {
            if (ranking.size() - 1 < current + i) break;
            User user = ranking.get(current + i);
            author.sendMessage(language.getMessage("RankingDisplayBody",
                    current + i + 1,
                    user.getLastName(),
                    user.getRate(),
                    user.getWins(),
                    user.getLoses()));
        }

        return true;
    }

    @CommandArena(command = "CommandCreate",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "CommandCreateUsage")
    public void create(Player author, String[] args) {
        if (arenaController.createArena(args[1],
                author.getWorld().getName())) author.sendMessage(language.getMessage("ArenaCreated", args[1]));
        else author.sendMessage(language.getMessage("ArenaAlreadyExists"));
    }

    @CommandArena(command = "CommandRemove",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "CommandRemoveUsage")
    public void remove(Player author, String[] args) {
        if (arenaController.removeArena(args[1]))
            author.sendMessage(language.getMessage("ArenaRemoved", args[1]));
        else author.sendMessage(language.getMessage("ArenaNotFound"));

    }

    @CommandArena(command = "CommandPos1",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "CommandPos1Usage")
    public void pos1(Player author, String[] args) {
        Arena arena = arenaController.getArena(args[1]);

        if (arena == null) author.sendMessage(language.getMessage("ArenaNotFound"));
        else {
            author.sendMessage(language.getMessage("PropertySet"));
            if (arena.setPos1(author.getLocation())) {
                author.sendMessage(language.getMessage("ArenaCompleted", arena.getName()));
            } else {
                author.sendMessage(language.getMessage("SetSecondPosition"));
            }
        }
    }

    @CommandArena(command = "CommandPos2",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "CommandPos2Usage")
    public void pos2(Player author, String[] args) {
        Arena arena = arenaController.getArena(args[1]);

        if (arena == null)
            author.sendMessage(language.getMessage("ArenaNotFound"));
        else {
            author.sendMessage(language.getMessage("PropertySet"));
            if (arena.setPos2(author.getLocation())) {
                author.sendMessage(language.getMessage("ArenaCompleted", arena.getName()));
            } else {
                author.sendMessage(language.getMessage("SetFirstPosition"));
            }
        }
    }

    @CommandArena(command = "CommandSetLobby",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "CommandSetLobbyUsage")
    public void setlobby(Player author, String[] args) {
        Arena arena = arenaController.getArena(args[1]);

        if (arena == null)
            author.sendMessage("§cErro! Arena nao encontrada.");
        else {
            arena.setLobby(author.getLocation());
            author.sendMessage(language.getMessage("PropertySet"));
        }
    }

    @CommandArena(command = "CommandSetBox",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 2,
            usage = "CommandSetBoxUsage")
    public void setBox(Player author, String[] args) {
        Arena arena = arenaController.getArena(args[1]);

        if (arena == null)
            author.sendMessage(language.getMessage("ArenaNotFound"));
        else {
            arena.setBox(author.getLocation());
            author.sendMessage(language.getMessage("PropertySet"));
        }
    }

    @CommandArena(command = "CommandList",
            superCommand = "arenax1adm",
            permission = "arenax1.adm",
            args = 1,
            usage = "CommandListUsage")
    public void list(Player author, String[] args) {
        author.sendMessage("§a -> Arenas:");

        for (Arena arena : arenaController.getArenas())
            author.sendMessage(language.getMessage("ArenaListBody",
                    arena.getName(),
                    language.getMessage((arena.isCompleted() ? "Completed" : "NotCompleted"))));
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
