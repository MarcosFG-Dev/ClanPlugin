package com.clan.utils;

import com.clan.Clan;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Gerenciador de mensagens - pra nÃ£o ficar hardcoded igual animal
 */
public class MessageManager {

    private static MessageManager instance;
    private final FileConfiguration config;

    private MessageManager() {
        this.config = Clan.getInstance().getConfig();
    }

    // Singleton porque sÃ³ precisa de um
    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    /**
     * Pega uma mensagem da config e jÃ¡ formata as cores
     * 
     * @param path Caminho na config
     * @return Mensagem formatada
     */
    public String getMessage(String path) {
        String message = config.getString("messages." + path, "");
        return ColorUtil.color(message);
    }

    /**
     * Pega mensagem com prefixo
     * 
     * @param path Caminho na config
     * @return Mensagem com prefixo formatado
     */
    public String getMessageWithPrefix(String path) {
        return getPrefix() + getMessage(path);
    }

    /**
     * Pega o prefixo do plugin
     * 
     * @return Prefixo formatado
     */
    public String getPrefix() {
        return getMessage("prefix");
    }

    /**
     * Pega uma configuraÃ§Ã£o inteira
     * 
     * @param path         Caminho na config
     * @param defaultValue Valor padrÃ£o se nÃ£o achar
     * @return Valor da config
     */
    public int getInt(String path, int defaultValue) {
        return config.getInt("settings." + path, defaultValue);
    }

    /**
     * Pega uma configuraÃ§Ã£o booleana
     * 
     * @param path         Caminho na config
     * @param defaultValue Valor padrÃ£o
     * @return Valor da config
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean("settings." + path, defaultValue);
    }

    /**
     * Recarrega as configuraÃ§Ãµes
     */
    public void reload() {
        Clan.getInstance().reloadConfig();
    }
}

