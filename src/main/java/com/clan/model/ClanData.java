package com.clan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Modelo de dados do clan pra não virar bagunça
public class ClanData {
    private String tag;
    private String name;
    private String description;
    private UUID owner; // O patrão do bagulho
    private List<UUID> members; // Os manos do clan
    private List<UUID> officers; // Os moderadores que acham que mandam
    private int kills; // Quantos inimigos viraram pó
    private int deaths; // Quantas vezes tomaram ferro
    private double bank; // Grana do clan (se tiver economia)
    private boolean friendlyFire; // Se os cara pode se matar ou não
    private long creationDate; // Quando essa bagaça foi criada

    public ClanData(String tag, String name, UUID owner) {
        this.tag = tag;
        this.name = name;
        this.description = "Sem descrição, preguiça define.";
        this.owner = owner;
        this.members = new ArrayList<>();
        this.officers = new ArrayList<>();
        this.members.add(owner); // O dono já entra de cara né porra
        this.kills = 0;
        this.deaths = 0;
        this.bank = 0.0;
        this.friendlyFire = false; // Por padrão não pode matar os brother
        this.creationDate = System.currentTimeMillis();
    }

    // Getters - pra pegar as parada
    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<UUID> getOfficers() {
        return officers;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public double getBank() {
        return bank;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public long getCreationDate() {
        return creationDate;
    }

    // Setters - pra mudar as parada
    public void setDescription(String description) {
        this.description = description;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    // Métodos úteis pra não ficar repetindo código
    public void addMember(UUID uuid) {
        if (!members.contains(uuid))
            members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
        officers.remove(uuid); // Se era oficial, perdeu o cargo também kkkkk
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    // Promove um cara pra oficial
    public void promote(UUID uuid) {
        if (members.contains(uuid) && !officers.contains(uuid)) {
            officers.add(uuid);
        }
    }

    // Rebaixa o cara de oficial
    public void demote(UUID uuid) {
        officers.remove(uuid);
    }

    // Verifica se o cara é oficial ou dono
    public boolean isOfficer(UUID uuid) {
        return officers.contains(uuid) || owner.equals(uuid);
    }

    // Calcula o KDR (Kill/Death Ratio) - quanto maior, mais foda
    public double getKDR() {
        if (deaths == 0)
            return kills; // Se nunca morreu, é brabo demais
        return (double) kills / deaths;
    }
}

