package br.com.tinycraft.arenax1.controller;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.entity.Invite;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import br.com.tinycraft.arenax1.Language;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author AB
 */
public class InviteController implements Runnable {

    private final ArenaExecutor arenaExecutor;
    private final Language language;
    private final int defaultTime;
    private final int acceptedWait;
    private final List<Invite> invites;

    public InviteController(ArenaExecutor arenaExecutor, ArenaConfig config, Language language) {
        this.arenaExecutor = arenaExecutor;
        this.defaultTime = config.getDefaultExpireTime();
        this.acceptedWait = config.getDefaultAcceptedWait();
        this.language = language;
        invites = new ArrayList<>();
    }

    public boolean authorHasOwnInvites(Player author) {
        for (Invite invite : invites) if (invite.authorMatch(author)) return true;
        return false;
    }

    public boolean createInvite(Player author, Player target) {
        if (authorHasOwnInvites(author)) return false;
        invites.add(new Invite(author, target, defaultTime, acceptedWait));
        return true;
    }

    public Invite getPendentInvite(Player author, Player target) {
        for (Invite invite : invites) {
            if (invite.authorMatch(author) && invite.targetMatch(target)) return invite;
        }
        return null;
    }

    public void inviteAccepted(Invite invite) {
        clearPlayerInvites(invite);
        invite.setAccepted(true);
    }

    public void inviteRejected(Invite invite) {
        invites.remove(invite);
    }

    private void clearPlayerInvites(Invite invite) {
        Iterator<Invite> i = invites.iterator();

        Player author = invite.getAuthor();
        Player target = invite.getTarget();

        while (i.hasNext()) {
            Invite loop = i.next();
            if (loop.equals(invite)) continue;
            if (invite.anyMatch(author) || invite.anyMatch(target)) i.remove();
        }
    }

    @Override
    public void run() {
        Iterator<Invite> i = invites.iterator();

        while (i.hasNext()) {
            Invite invite = i.next();

            if (invite.removeOffline()) {
                invite.sendMessage(language.getMessage("InviteCancelledExit"));
                i.remove();
                continue;
            }

            if (invite.isExpired()) {
                invite.sendMessage(language.getMessage("InviteExpiredAuthor", invite.getTarget().getName()),
                        language.getMessage("InviteExpiredTarget", invite.getAuthor().getName()));
                i.remove();
                continue;
            }

            if (!invite.isAccepted()) continue;

            if (invite.isAcceptedWait()) {
                if (arenaExecutor.createX1(invite)) invite.sendMessage(language.getMessage("InviteAcceptedStarting"));
                else invite.sendMessage(language.getMessage("InviteAcceptedNoArena"));
                i.remove();
                continue;
            }

            invite.sendMessage(language.getMessage("InviteAcceptedWaiting", invite.getAcceptedWaitTime()));
        }
    }
}
