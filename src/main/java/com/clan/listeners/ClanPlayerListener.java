package com.clan.listeners;

import com.clan.managers.ClanManager;
import com.clan.model.ClanData;
import com.clan.utils.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

// Listener pra eventos de jogadores - aqui que a coisa fica interessante
public class ClanPlayerListener implements Listener {

    private final ClanManager clanManager = ClanManager.getInstance();

    // Adiciona a tag do clan no chat - pra todo mundo ver quem é foda
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ClanData clan = clanManager.getClanByPlayer(player.getUniqueId());

        if (clan != null) {
            String format = event.getFormat();
            // Bota a tag antes do nome do cara
            event.setFormat(ColorUtil.color("&7[" + clan.getTag() + "]&r ") + format);
        }
    }

    // Sistema de fogo amigo - se tá desativado, não pode bater nos brother
    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return; // Não é PvP, foda-se
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        ClanData victimClan = clanManager.getClanByPlayer(victim.getUniqueId());
        ClanData attackerClan = clanManager.getClanByPlayer(attacker.getUniqueId());

        if (victimClan != null && attackerClan != null) {
            // Se são do mesmo clan
            if (victimClan.getTag().equalsIgnoreCase(attackerClan.getTag())) {
                if (!victimClan.isFriendlyFire()) {
                    event.setCancelled(true); // Cancela o dano
                    attacker.sendMessage(ColorUtil.color("&cO fogo amigo tá desativado, não bate nos brother!"));
                }
            }
        }
    }

    // Quando alguém morre - registra kills e mortes
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            // Registra kill na guerra se tiver uma rolando
            clanManager.recordWarKill(killer.getUniqueId(), victim.getUniqueId());

            // Atualiza as estatísticas dos clans
            ClanData killerClan = clanManager.getClanByPlayer(killer.getUniqueId());
            ClanData victimClan = clanManager.getClanByPlayer(victim.getUniqueId());

            if (killerClan != null) {
                killerClan.setKills(killerClan.getKills() + 1); // +1 kill pro clan
            }
            if (victimClan != null) {
                victimClan.setDeaths(victimClan.getDeaths() + 1); // +1 morte pro clan (F)
            }

            clanManager.saveClans(); // Salva as mudanças
        }
    }
}

