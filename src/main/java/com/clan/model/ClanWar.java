package com.clan.model;

// Modelo de guerra entre clans - aqui a porrada come solta
public class ClanWar {
    private String attackerTag; // Quem começou a treta
    private String defenderTag; // Quem vai se defender (ou tomar ferro)
    private int attackerKills; // Kills do atacante
    private int defenderKills; // Kills do defensor
    private long startTime; // Quando a guerra começou
    private long duration; // Quanto tempo essa merda vai durar (em milissegundos)
    private boolean active; // Se tá rolando ainda

    public ClanWar(String attackerTag, String defenderTag, long duration) {
        this.attackerTag = attackerTag;
        this.defenderTag = defenderTag;
        this.attackerKills = 0;
        this.defenderKills = 0;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
        this.active = true; // Bora pra guerra!
    }

    // Getters básicos
    public String getAttackerTag() {
        return attackerTag;
    }

    public String getDefenderTag() {
        return defenderTag;
    }

    public int getAttackerKills() {
        return attackerKills;
    }

    public int getDefenderKills() {
        return defenderKills;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Adiciona kill pro atacante
    public void addAttackerKill() {
        this.attackerKills++;
    }

    // Adiciona kill pro defensor
    public void addDefenderKill() {
        this.defenderKills++;
    }

    // Verifica se a guerra já passou do tempo
    public boolean isExpired() {
        return System.currentTimeMillis() - startTime >= duration;
    }

    // Quanto tempo falta pra acabar essa bagaça
    public long getRemainingTime() {
        long remaining = duration - (System.currentTimeMillis() - startTime);
        return remaining > 0 ? remaining : 0;
    }

    // Descobre quem ganhou essa porra
    public String getWinner() {
        if (attackerKills > defenderKills)
            return attackerTag;
        if (defenderKills > attackerKills)
            return defenderTag;
        return "EMPATE"; // Ninguém ganhou, todo mundo perdeu tempo kkkk
    }

    // Verifica se um clan tá envolvido nessa guerra
    public boolean involves(String tag) {
        return attackerTag.equalsIgnoreCase(tag) || defenderTag.equalsIgnoreCase(tag);
    }
}

