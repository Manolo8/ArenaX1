package br.com.tinycraft.arenax1.executor;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.Language;
import br.com.tinycraft.arenax1.controller.ArenaController;
import br.com.tinycraft.arenax1.controller.UserController;
import br.com.tinycraft.arenax1.entity.Arena;
import br.com.tinycraft.arenax1.entity.Invite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author AB
 */
public class ArenaExecutor implements Runnable {

    private final ArenaController arenaController;
    private final UserController userController;
    private final Language language;
    private final List<Arena> runningArenas;
    private final int defaultRemainingTime;
    private final int startWaitTime;
    private final int endingTime;

    public ArenaExecutor(ArenaController arenaController, UserController userController, ArenaConfig config, Language language) {
        this.arenaController = arenaController;
        this.userController = userController;
        this.language = language;
        this.defaultRemainingTime = config.getDefaultRemainingTime();
        this.startWaitTime = config.getStartWaitTime();
        this.endingTime = config.getEndindTime();

        this.runningArenas = new ArrayList<>();
    }

    public boolean createX1(Invite invite) {
        Arena duel = arenaController.getAvailableArena();

        if (duel == null) {
            return false;
        }

        duel.setRemainingTime(defaultRemainingTime);
        duel.startArena(invite);

        runningArenas.add(duel);
        return true;
    }

    public Arena getPlayerArena(Player player) {
        for (Arena arena : runningArenas) if (arena.isInArena(player)) return arena;
        return null;
    }

    public void playerDeath(Player player) {
        Arena arena = getPlayerArena(player);

        if (arena == null) return;

        arena.setRemainingTime(endingTime);
        arena.setLoser(player);

        if (arena.isTie()) {
            arena.getWinner().sendMessage(language.getMessage("WinnerMessageDeath"));
            return;
        }

        if (arena.getWinner() != null) arena.getWinner().sendMessage(language.getMessage("WinnerMessage", endingTime));
    }

    @Override
    public void run() {
        Iterator<Arena> i = this.runningArenas.iterator();

        while (i.hasNext()) {
            Arena arena = i.next();

            if (arena.isWaitinigToStart()) {
                int waitTime = defaultRemainingTime - arena.getRemainingTime();
                if (waitTime >= startWaitTime) {
                    arena.sendMessage(language.getMessage("DuelStartMessage"));
                    arena.setWaitinigToStart(false);
                } else {
                    arena.sendMessage(language.getMessage("WaitMessage",
                            this.startWaitTime - waitTime));
                    arena.isEnded();
                }
                continue;
            }

            if (arena.isEnded()) {
                i.remove();

                if (arena.getWinner() == null) {
                    arena.sendMessage(language.getMessage("TimeOver"));
                    arena.close();
                    continue;
                }

                if (arena.isTie())
                    Bukkit.broadcastMessage(language.getMessage("BroadCastMessageTie",
                            arena.getWinner().getName(), arena.getLoser().getName()));
                else Bukkit.broadcastMessage(language.getMessage("BroadCastMessage",
                        arena.getWinner().getName(), arena.getLoser().getName()));

                userController.updateByArenaStatus(arena);

                arena.close();
            }
        }
    }
}
