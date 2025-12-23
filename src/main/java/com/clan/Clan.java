package com.clan;

import com.clan.utils.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principal do plugin Clan System
 * Sistema completo de gerenciamento de clans com GUI, guerras e hierarquia
 * 
 * @author Pedro
 * @version 1.0.0
 */
public class Clan extends JavaPlugin {

    private static Clan instance;

    @Override
    public void onEnable() {
        instance = this;

        // Carrega e salva configuração padrão
        saveDefaultConfig();

        // Mensagens de inicialização
        getLogger().info("========================================");
        getLogger().info("Clan System v" + getDescription().getVersion());
        getLogger().info("Desenvolvido por: " + getDescription().getAuthors());
        getLogger().info("========================================");

        // Inicializa o gerenciador de mensagens
        MessageManager.getInstance();

        // Inicializa o gerenciador e carrega dados
        com.clan.managers.ClanManager.getInstance().loadClans();
        getLogger().info("Sistema de clans carregado!");

        // Registra comandos
        if (getCommand("clan") != null) {
            getCommand("clan").setExecutor(new com.clan.comandos.clanCommand());
            getLogger().info("Comandos registrados!");
        } else {
            getLogger().severe("ERRO: Comando /clan não encontrado no plugin.yml!");
        }

        // Registra eventos
        getServer().getPluginManager().registerEvents(new com.clan.listeners.ClanInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new com.clan.listeners.ClanPlayerListener(), this);
        getLogger().info("Listeners registrados!");

        getLogger().info("Plugin iniciado com sucesso!");
    }

    @Override
    public void onDisable() {
        // Salva todos os dados antes de desligar
        if (com.clan.managers.ClanManager.getInstance() != null) {
            com.clan.managers.ClanManager.getInstance().saveClans();
            getLogger().info("Dados dos clans salvos!");
        }

        getLogger().info("Clan System desativado. Até logo!");
    }

    public static Clan getInstance() {
        return instance;
    }
}
