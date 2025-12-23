package com.clan.utils;

import org.bukkit.ChatColor;

/**
 * UtilitÃ¡rio pra converter cores & em Â§
 * Porque escrever Â§ Ã© chato pra caralho
 */
public class ColorUtil {

    /**
     * Converte cÃ³digos de cor & em Â§
     * 
     * @param message Mensagem com cÃ³digos &
     * @return Mensagem formatada com Â§
     */
    public static String color(String message) {
        if (message == null)
            return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Converte mÃºltiplas mensagens de uma vez
     * 
     * @param messages Array de mensagens
     * @return Array formatado
     */
    public static String[] color(String... messages) {
        String[] colored = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            colored[i] = color(messages[i]);
        }
        return colored;
    }

    /**
     * Remove todas as cores de uma mensagem
     * 
     * @param message Mensagem colorida
     * @return Mensagem sem cores (pra que nÃ©, mas vai que)
     */
    public static String stripColor(String message) {
        if (message == null)
            return "";
        return ChatColor.stripColor(message);
    }
}

