package com.clan.comandos;

import com.clan.gui.ClanGUI;
import com.clan.managers.ClanManager;
import com.clan.model.ClanData;
import com.clan.model.ClanWar;
import com.clan.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class clanCommand implements CommandExecutor {

    private final ClanManager clanManager = ClanManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.color("&cComando apenas para jogadores."));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            openClanMenu(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "criar":
                handleCreate(player, args);
                break;
            case "convidar":
            case "invite":
                handleInvite(player, args);
                break;
            case "aceitar":
            case "entrar":
            case "join":
                handleJoin(player, args);
                break;
            case "sair":
            case "leave":
                handleLeave(player);
                break;
            case "expulsar":
            case "kick":
                handleKick(player, args);
                break;
            case "ff":
            case "pvp":
                handleFriendlyFire(player);
                break;
            case "remover":
            case "disband":
                handleDelete(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "guerra":
            case "war":
                handleWar(player, args);
                break;
            case "aceitar_guerra":
                handleAcceptWar(player, args);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ColorUtil.color("&e&lSistema de Clans - Ajuda"));
        player.sendMessage(ColorUtil.color("&6/clan criar <tag> <nome> &f- Cria um novo clan."));
        player.sendMessage(ColorUtil.color("&6/clan convidar <player> &f- Convida um jogador."));
        player.sendMessage(ColorUtil.color("&6/clan aceitar <tag> &f- Aceita um convite."));
        player.sendMessage(ColorUtil.color("&6/clan guerra <tag> &f- Declara guerra."));
        player.sendMessage(ColorUtil.color("&6/clan expular <player> &f- Remove um membro."));
        player.sendMessage(ColorUtil.color("&6/clan sair &f- Sai do clan atual."));
        player.sendMessage(ColorUtil.color("&6/clan ff &f- Alterna o Fogo Amigo (PvP)."));
        player.sendMessage(ColorUtil.color("&6/clan info [tag] &f- Informações do clan."));
        player.sendMessage(ColorUtil.color("&6/clan remover &f- Deleta seu clan."));
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ColorUtil.color("&cUse: /clan criar <tag> <nome>"));
            return;
        }
        if (clanManager.getClanByPlayer(player.getUniqueId()) != null) {
            player.sendMessage(ColorUtil.color("&cVocê já tem um clan."));
            return;
        }
        String tag = args[1];
        String name = args[2];
        if (tag.length() > 5) {
            player.sendMessage(ColorUtil.color("&cTag muito longa (max 5 chars)."));
            return;
        }
        if (clanManager.exists(tag)) {
            player.sendMessage(ColorUtil.color("&cEssa tag já existe."));
            return;
        }
        clanManager.createClan(tag, name, player.getUniqueId());
        player.sendMessage(ColorUtil.color("&aClan criado com sucesso!"));
    }

    private void handleInvite(Player player, String[] args) {
        ClanData clan = clanManager.getClanByPlayer(player.getUniqueId());
        if (clan == null || !clan.isOfficer(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&cApenas líderes e oficiais podem convidar."));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ColorUtil.color("&cUse: /clan convidar <player>"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ColorUtil.color("&cJogador offline ou não encontrado."));
            return;
        }
        if (clanManager.getClanByPlayer(target.getUniqueId()) != null) {
            player.sendMessage(ColorUtil.color("&cO jogador já possui um clan."));
            return;
        }

        clanManager.invitePlayer(target.getUniqueId(), clan.getTag());
        player.sendMessage(ColorUtil.color("&aConvite enviado para " + target.getName()));
        target.sendMessage(
                ColorUtil.color("&eVocê foi convidado para o clan &6" + clan.getName() + " &e[" + clan.getTag() + "]"));
        target.sendMessage(ColorUtil.color("&eDigite &f/clan aceitar " + clan.getTag() + " &epara entrar."));
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.color("&cUse: /clan aceitar <tag>"));
            return;
        }
        String tag = args[1];
        if (!clanManager.hasInvite(player.getUniqueId(), tag)) {
            player.sendMessage(ColorUtil.color("&cVocê não tem convite para este clan."));
            return;
        }
        clanManager.acceptInvite(player.getUniqueId());
        player.sendMessage(ColorUtil.color("&aVocê entrou no clan " + tag + "!"));

        ClanData clan = clanManager.getClan(tag);
        notifyClan(clan, ColorUtil.color("&e" + player.getName() + " entrou no clan!"));
    }

    private void handleKick(Player player, String[] args) {
        ClanData clan = clanManager.getClanByPlayer(player.getUniqueId());
        if (clan == null || !clan.isOfficer(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&cVocê não tem permissão."));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ColorUtil.color("&cUse: /clan expulsar <player>"));
            return;
        }
        // In reality, you'd want to handle offline players by Name lookup or UUID cache
        // keeping it simple for online players per requirement "professional plugin"
        // usually handles offline too
        // but for this scope, let's try to find member
        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[1]);
        UUID targetUUID = null;
        String targetName = args[1];

        if (target != null) {
            targetUUID = target.getUniqueId();
        } else {
            // Look in clan members
            for (UUID memberId : clan.getMembers()) {
                if (Bukkit.getOfflinePlayer(memberId).getName() != null &&
                        Bukkit.getOfflinePlayer(memberId).getName().equalsIgnoreCase(args[1])) {
                    targetUUID = memberId;
                    break;
                }
            }
        }

        if (targetUUID == null || !clan.isMember(targetUUID)) {
            player.sendMessage(ColorUtil.color("&cJogador não encontrado no clan."));
            return;
        }

        if (clan.getOwner().equals(targetUUID)) {
            player.sendMessage(ColorUtil.color("&cVocê não pode expulsar o dono."));
            return;
        }

        clan.removeMember(targetUUID);
        clanManager.saveClans();
        player.sendMessage(ColorUtil.color("&cJogador " + targetName + " expulso."));
        if (target != null)
            target.sendMessage(ColorUtil.color("&cVocê foi expulso do clan."));
    }

    private void handleFriendlyFire(Player player) {
        ClanData clan = clanManager.getClanByPlayer(player.getUniqueId());
        if (clan == null || !clan.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&cApenas o dono pode alterar o PvP."));
            return;
        }
        boolean newState = !clan.isFriendlyFire();
        clan.setFriendlyFire(newState);
        clanManager.saveClans();
        player.sendMessage(ColorUtil.color("&aFogo amigo " + (newState ? "ATIVADO" : "DESATIVADO")));
    }

    private void handleLeave(Player player) {
        ClanData clan = clanManager.getClanByPlayer(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(ColorUtil.color("&cVocê não está em um clan."));
            return;
        }
        if (clan.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&cDono não pode sair. Use /clan remover."));
            return;
        }
        clan.removeMember(player.getUniqueId());
        clanManager.saveClans();
        player.sendMessage(ColorUtil.color("&aVocê saiu do clan."));
        notifyClan(clan, ColorUtil.color("&c" + player.getName() + " saiu do clan."));
    }

    private void handleDelete(Player player) {
        ClanData clan = clanManager.getClanByPlayer(player.getUniqueId());
        if (clan == null || !clan.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&cApenas o dono pode deletar."));
            return;
        }
        clanManager.deleteClan(clan.getTag());
        player.sendMessage(ColorUtil.color("&cClan deletado."));
        Bukkit.broadcastMessage(ColorUtil.color("&eO clan " + clan.getName() + " foi desfeito!"));
    }

    private void handleInfo(Player player, String[] args) {
        ClanData clan;
        if (args.length > 1) {
            clan = clanManager.getClan(args[1]);
        } else {
            clan = clanManager.getClanByPlayer(player.getUniqueId());
        }

        if (clan == null) {
            player.sendMessage(ColorUtil.color("&cClan não encontrado."));
            return;
        }

        player.sendMessage(ColorUtil.color("&8&m--------------------------"));
        player.sendMessage(ColorUtil.color(" &6&l" + clan.getName() + " &7(" + clan.getTag() + ")"));
        player.sendMessage(ColorUtil.color(" &fDescrição: &7" + clan.getDescription()));
        player.sendMessage(ColorUtil.color(" &fDono: &7" + Bukkit.getOfflinePlayer(clan.getOwner()).getName()));
        player.sendMessage(ColorUtil.color(" &fKDR: &7" + clan.getKills() + "/" + clan.getDeaths()));
        player.sendMessage(ColorUtil.color(" &fPvP: " + (clan.isFriendlyFire() ? "&cAtivado" : "&aDesativado")));
        player.sendMessage(ColorUtil.color(" &fMembros: &a" + clan.getMembers().size()));
        player.sendMessage(ColorUtil.color("&8&m--------------------------"));
    }

    private void notifyClan(ClanData clan, String message) {
        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null)
                p.sendMessage(message);
        }
    }

    private void handleWar(Player player, String[] args) {
        ClanData attackerClan = clanManager.getClanByPlayer(player.getUniqueId());
        if (attackerClan == null || !attackerClan.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&cApenas o dono do clan pode declarar guerra."));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ColorUtil.color("&cUse: /clan guerra <tag>"));
            return;
        }

        String targetTag = args[1];
        ClanData defenderClan = clanManager.getClan(targetTag);

        if (defenderClan == null) {
            player.sendMessage(ColorUtil.color("&cClan não encontrado."));
            return;
        }

        if (attackerClan.getTag().equalsIgnoreCase(defenderClan.getTag())) {
            player.sendMessage(ColorUtil.color("&cVocê não pode declarar guerra ao seu próprio clan!"));
            return;
        }

        ClanWar existingWar = clanManager.getWar(attackerClan.getTag(), defenderClan.getTag());
        if (existingWar != null) {
            player.sendMessage(ColorUtil.color("&cJá existe uma guerra ativa entre esses clans!"));
            return;
        }

        // Send war request
        Player defenderOwner = Bukkit.getPlayer(defenderClan.getOwner());
        if (defenderOwner == null) {
            player.sendMessage(ColorUtil.color("&cO dono do clan inimigo precisa estar online."));
            return;
        }

        player.sendMessage(ColorUtil.color("&ePedido de guerra enviado para " + defenderClan.getName() + "!"));
        defenderOwner.sendMessage(ColorUtil.color("&c&l[GUERRA] &e" + attackerClan.getName() + " declarou guerra!"));
        defenderOwner.sendMessage(
                ColorUtil.color("&eUse &f/clan aceitar_guerra " + attackerClan.getTag() + " &epara aceitar."));
        defenderOwner.sendMessage(ColorUtil.color("&7Duração: 10 minutos"));
    }

    private void handleAcceptWar(Player player, String[] args) {
        ClanData defenderClan = clanManager.getClanByPlayer(player.getUniqueId());
        if (defenderClan == null || !defenderClan.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color("&cApenas o dono do clan pode aceitar guerras."));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ColorUtil.color("&cUse: /clan aceitar_guerra <tag>"));
            return;
        }

        String attackerTag = args[1];
        ClanData attackerClan = clanManager.getClan(attackerTag);

        if (attackerClan == null) {
            player.sendMessage(ColorUtil.color("&cClan não encontrado."));
            return;
        }

        ClanWar war = clanManager.startWar(attackerClan.getTag(), defenderClan.getTag(), 10);
        if (war == null) {
            player.sendMessage(ColorUtil.color("&cNão foi possível iniciar a guerra."));
            return;
        }

        Bukkit.broadcastMessage(
                ColorUtil.color("&c&l[GUERRA] &e" + attackerClan.getName() + " &fvs &e" + defenderClan.getName()));
        Bukkit.broadcastMessage(ColorUtil.color("&7A guerra começou! Duração: 10 minutos"));

        notifyClan(attackerClan, ColorUtil.color("&aA guerra começou! Boa sorte!"));
        notifyClan(defenderClan, ColorUtil.color("&aA guerra começou! Boa sorte!"));
    }

    private void openClanMenu(Player player) {
        ClanGUI.openMainMenu(player);
    }
}
