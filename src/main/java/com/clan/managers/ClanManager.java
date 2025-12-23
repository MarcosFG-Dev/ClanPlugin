package com.clan.managers;

import com.clan.Clan;
import com.clan.model.ClanData;
import com.clan.model.ClanWar;
import com.clan.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

// O cérebro do sistema de clans - aqui que a mágica acontece
public class ClanManager {

    private static ClanManager instance;
    private final Map<String, ClanData> clans; // Tag -> ClanData
    private final Map<UUID, String> invites; // Convites pendentes (PlayerUUID -> ClanTag)
    private final List<ClanWar> activeWars; // Guerras rolando no momento
    private final File file;
    private FileConfiguration config;

    private ClanManager() {
        this.clans = new HashMap<>();
        this.invites = new HashMap<>();
        this.activeWars = new ArrayList<>();
        this.file = new File(Clan.getPlugin(Clan.class).getDataFolder(), "clans.yml");
    }

    // Singleton porque só precisa de uma instância dessa bagaça
    public static ClanManager getInstance() {
        if (instance == null) {
            instance = new ClanManager();
        }
        return instance;
    }

    // === GERENCIAMENTO DE CLANS ===

    public ClanData createClan(String tag, String name, UUID owner) {
        ClanData newClan = new ClanData(tag, name, owner);
        clans.put(tag.toLowerCase(), newClan);
        saveClans(); // Salva na hora pra não perder
        return newClan;
    }

    public void deleteClan(String tag) {
        clans.remove(tag.toLowerCase());
        // Remove qualquer guerra envolvendo esse clan
        activeWars.removeIf(war -> war.involves(tag));
        saveClans();
    }

    public boolean exists(String tag) {
        return clans.containsKey(tag.toLowerCase());
    }

    public ClanData getClan(String tag) {
        return clans.get(tag.toLowerCase());
    }

    // Pega o clan de um jogador específico
    public ClanData getClanByPlayer(UUID playerUUID) {
        for (ClanData clan : clans.values()) {
            if (clan.isMember(playerUUID)) {
                return clan;
            }
        }
        return null; // Esse cara não tem clan, coitado
    }

    public Collection<ClanData> getAllClans() {
        return clans.values();
    }

    // === SISTEMA DE CONVITES ===

    public void invitePlayer(UUID playerUUID, String clanTag) {
        invites.put(playerUUID, clanTag);
        // Remove o convite depois de 60 segundos (pra não ficar lixo)
        Bukkit.getScheduler().runTaskLater(Clan.getPlugin(Clan.class), () -> {
            if (hasInvite(playerUUID, clanTag)) {
                invites.remove(playerUUID);
                Player p = Bukkit.getPlayer(playerUUID);
                if (p != null) {
                    p.sendMessage(ColorUtil.color("&eO convite para o clan " + clanTag + " expirou. Perdeu playboy!"));
                }
            }
        }, 1200L); // 60 segundos
    }

    public boolean hasInvite(UUID playerUUID, String clanTag) {
        return invites.containsKey(playerUUID) && invites.get(playerUUID).equalsIgnoreCase(clanTag);
    }

    public void acceptInvite(UUID playerUUID) {
        if (!invites.containsKey(playerUUID))
            return;
        String tag = invites.get(playerUUID);
        ClanData clan = getClan(tag);
        if (clan != null) {
            clan.addMember(playerUUID);
            saveClans();
        }
        invites.remove(playerUUID);
    }

    // === SISTEMA DE GUERRAS - AQUI A PORRADA COME ===

    public ClanWar startWar(String attackerTag, String defenderTag, long durationMinutes) {
        // Verifica se já tem guerra rolando entre esses clans
        for (ClanWar war : activeWars) {
            if (war.isActive() && war.involves(attackerTag) && war.involves(defenderTag)) {
                return null; // Já tem treta rolando
            }
        }

        ClanWar war = new ClanWar(attackerTag, defenderTag, durationMinutes * 60 * 1000);
        activeWars.add(war);

        // Agenda o fim da guerra
        Bukkit.getScheduler().runTaskLater(Clan.getPlugin(Clan.class), () -> {
            endWar(war);
        }, durationMinutes * 60 * 20L); // Converte minutos pra ticks

        return war;
    }

    public void endWar(ClanWar war) {
        if (!war.isActive())
            return;
        war.setActive(false);

        // Anuncia quem ganhou essa bagaça
        String winner = war.getWinner();
        String message = ColorUtil.color("&e&l[GUERRA] &fA guerra entre &c" + war.getAttackerTag() +
                " &fe &c" + war.getDefenderTag() + " &fterminou!");

        if (winner.equals("EMPATE")) {
            message += ColorUtil.color(
                    " &7Resultado: &eEMPATE &7(" + war.getAttackerKills() + " x " + war.getDefenderKills() + ")");
        } else {
            message += ColorUtil.color(" &aVencedor: &6" + winner + " &7(" +
                    (winner.equals(war.getAttackerTag()) ? war.getAttackerKills() : war.getDefenderKills()) +
                    " kills)");
        }

        Bukkit.broadcastMessage(message);
        activeWars.remove(war);
    }

    public ClanWar getWar(String tag1, String tag2) {
        for (ClanWar war : activeWars) {
            if (war.isActive() && war.involves(tag1) && war.involves(tag2)) {
                return war;
            }
        }
        return null;
    }

    public ClanWar getActiveWar(String tag) {
        for (ClanWar war : activeWars) {
            if (war.isActive() && war.involves(tag)) {
                return war;
            }
        }
        return null;
    }

    // Registra um kill durante a guerra
    public void recordWarKill(UUID killer, UUID victim) {
        ClanData killerClan = getClanByPlayer(killer);
        ClanData victimClan = getClanByPlayer(victim);

        if (killerClan == null || victimClan == null)
            return;

        ClanWar war = getWar(killerClan.getTag(), victimClan.getTag());
        if (war != null && war.isActive()) {
            if (war.getAttackerTag().equalsIgnoreCase(killerClan.getTag())) {
                war.addAttackerKill();
            } else {
                war.addDefenderKill();
            }

            // Notifica o matador
            Player killerPlayer = Bukkit.getPlayer(killer);
            if (killerPlayer != null) {
                killerPlayer.sendMessage(ColorUtil.color("&a[GUERRA] &fKill registrado! Placar: &e" +
                        war.getAttackerKills() + " &fx &e" + war.getDefenderKills()));
            }
        }
    }

    // === PERSISTÊNCIA - SALVAR E CARREGAR ===

    public void loadClans() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        clans.clear();

        if (config.contains("clans")) {
            for (String key : config.getConfigurationSection("clans").getKeys(false)) {
                String path = "clans." + key;
                String tag = key;
                String name = config.getString(path + ".name");
                UUID owner = UUID.fromString(config.getString(path + ".owner"));

                ClanData clan = new ClanData(tag, name, owner);
                clan.setKills(config.getInt(path + ".kills"));
                clan.setDeaths(config.getInt(path + ".deaths"));
                clan.setBank(config.getDouble(path + ".bank"));
                clan.setFriendlyFire(config.getBoolean(path + ".friendlyFire"));
                clan.setDescription(config.getString(path + ".description", "Sem descrição."));

                for (String m : config.getStringList(path + ".members")) {
                    clan.addMember(UUID.fromString(m));
                }
                for (String o : config.getStringList(path + ".officers")) {
                    clan.promote(UUID.fromString(o));
                }
                clans.put(tag.toLowerCase(), clan);
            }
        }
    }

    public void saveClans() {
        config.set("clans", null); // Limpa tudo antes
        for (ClanData clan : clans.values()) {
            String path = "clans." + clan.getTag().toLowerCase();
            config.set(path + ".name", clan.getName());
            config.set(path + ".owner", clan.getOwner().toString());
            config.set(path + ".description", clan.getDescription());
            config.set(path + ".kills", clan.getKills());
            config.set(path + ".deaths", clan.getDeaths());
            config.set(path + ".bank", clan.getBank());
            config.set(path + ".friendlyFire", clan.isFriendlyFire());

            List<String> userList = new ArrayList<>();
            for (UUID uuid : clan.getMembers())
                userList.add(uuid.toString());
            config.set(path + ".members", userList);

            List<String> officerList = new ArrayList<>();
            for (UUID uuid : clan.getOfficers())
                officerList.add(uuid.toString());
            config.set(path + ".officers", officerList);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

