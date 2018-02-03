package br.com.tinycraft.arenax1.executor;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.arena.Arena;
import br.com.tinycraft.arenax1.arena.ArenaManager;
import br.com.tinycraft.arenax1.invite.Invite;
import br.com.tinycraft.arenax1.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author AB
 */
public class ArenaExecutor implements Runnable {

    private final ArenaManager arenaManager;
    private final Language language;
    private final List<Arena> runningArenas;
    private final int defaultRemainingTime;
    private final int startWaitTime;
    private final int endingTime;

    public ArenaExecutor(ArenaManager arenaManager, ArenaConfig config, Language language) {
        this.arenaManager = arenaManager;
        this.language = language;
        this.defaultRemainingTime = config.getDefaultRemainingTime();
        this.startWaitTime = config.getStartWaitTime();
        this.endingTime = config.getEndindTime();

        this.runningArenas = new ArrayList<>();
    }

    public boolean createX1(Invite invite) {
        Arena duel = arenaManager.getAvailableArena();

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

                arena.close();
            }
        }
    }
}
