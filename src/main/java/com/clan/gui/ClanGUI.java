package com.clan.gui;

import com.clan.managers.ClanManager;
import com.clan.model.ClanData;
import com.clan.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// Sistema de GUI avançado - aqui que fica bonito pra caralho
public class ClanGUI {

    private static final int ITEMS_PER_PAGE = 28; // Quantos membros por página

    // Abre o menu principal do clan
    public static void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ColorUtil.color("&8&lMenu de Clans"));

        ClanData clan = ClanManager.getInstance().getClanByPlayer(player.getUniqueId());

        // Vidro decorativo pra ficar chique
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        // Coloca vidro nas bordas
        for (int i : new int[] { 0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53 }) {
            inv.setItem(i, glass);
        }

        if (clan != null) {
            // Informações do clan
            ItemStack info = createItem(Material.PAPER, ColorUtil.color("&6&lSeu Clan: " + clan.getName()),
                    Arrays.asList(
                            ColorUtil.color("&7Tag: &f" + clan.getTag()),
                            ColorUtil.color("&7Membros: &a" + clan.getMembers().size()),
                            ColorUtil.color("&7KDR: &c" + clan.getKills() + "&7/&c" + clan.getDeaths()),
                            "",
                            ColorUtil.color("&eClique para ver membros")));
            inv.setItem(4, info);

            // Botão de membros
            ItemStack members = createSkull(player.getName(), ColorUtil.color("&a&lMembros do Clan"), Arrays.asList(
                    ColorUtil.color("&7Total: &f" + clan.getMembers().size()),
                    "",
                    ColorUtil.color("&eClique para ver lista")));
            inv.setItem(20, members);

            // Configurações
            ItemStack settings = createItem(Material.REDSTONE_COMPARATOR, ColorUtil.color("&c&lConfigurações"),
                    Arrays.asList(
                            ColorUtil.color("&7Fogo Amigo: " + (clan.isFriendlyFire() ? "&aAtivado" : "&cDesativado")),
                            "",
                            ColorUtil.color("&eClique para gerenciar")));
            inv.setItem(22, settings);

            // Botão de guerra
            ItemStack war = createItem(Material.DIAMOND_SWORD, ColorUtil.color("&4&lSistema de Guerra"), Arrays.asList(
                    ColorUtil.color("&7Declare guerra contra"),
                    ColorUtil.color("&7outros clans!"),
                    "",
                    ColorUtil.color("&eClique para iniciar")));
            inv.setItem(24, war);

        } else {
            // Cara não tem clan
            ItemStack create = createItem(Material.NETHER_STAR, ColorUtil.color("&a&lCriar Clan"), Arrays.asList(
                    ColorUtil.color("&7Você não possui um clan."),
                    "",
                    ColorUtil.color("&eUse: &f/clan criar <tag> <nome>")));
            inv.setItem(22, create);
        }

        // Botão de fechar
        ItemStack close = createItem(Material.BARRIER, ColorUtil.color("&c&lFechar"), Arrays.asList());
        inv.setItem(49, close);

        player.openInventory(inv);
    }

    // Menu de membros com paginação
    public static void openMembersMenu(Player player, int page) {
        ClanData clan = ClanManager.getInstance().getClanByPlayer(player.getUniqueId());
        if (clan == null)
            return;

        List<UUID> members = new ArrayList<>(clan.getMembers());
        int totalPages = (int) Math.ceil(members.size() / (double) ITEMS_PER_PAGE);
        page = Math.max(0, Math.min(page, totalPages - 1));

        Inventory inv = Bukkit.createInventory(null, 54, ColorUtil.color("&8Membros - Página " + (page + 1)));

        // Vidro verde decorativo
        ItemStack greenGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemMeta glassMeta = greenGlass.getItemMeta();
        glassMeta.setDisplayName(" ");
        greenGlass.setItemMeta(glassMeta);

        for (int i = 0; i < 9; i++)
            inv.setItem(i, greenGlass);
        for (int i = 45; i < 54; i++)
            inv.setItem(i, greenGlass);

        // Membros
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, members.size());

        int slot = 9;
        for (int i = startIndex; i < endIndex; i++) {
            UUID memberUUID = members.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(memberUUID);
            String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Desconhecido";

            List<String> lore = new ArrayList<>();
            lore.add(ColorUtil.color("&7Status: " + (offlinePlayer.isOnline() ? "&aOnline" : "&cOffline")));

            if (clan.getOwner().equals(memberUUID)) {
                lore.add(ColorUtil.color("&6&lDONO"));
            } else if (clan.isOfficer(memberUUID)) {
                lore.add(ColorUtil.color("&e&lOFICIAL"));
            } else {
                lore.add(ColorUtil.color("&7Membro"));
            }

            if (clan.isOfficer(player.getUniqueId()) && !memberUUID.equals(player.getUniqueId())) {
                lore.add("");
                lore.add(ColorUtil.color("&cClique para expulsar"));
            }

            ItemStack skull = createSkull(name, ColorUtil.color("&a" + name), lore);
            inv.setItem(slot++, skull);
        }

        // Navegação
        if (page > 0) {
            ItemStack prev = createItem(Material.ARROW, ColorUtil.color("&e← Página Anterior"), Arrays.asList());
            inv.setItem(45, prev);
        }

        if (page < totalPages - 1) {
            ItemStack next = createItem(Material.ARROW, ColorUtil.color("&ePróxima Página →"), Arrays.asList());
            inv.setItem(53, next);
        }

        // Botão de voltar
        ItemStack back = createItem(Material.BARRIER, ColorUtil.color("&c&lVoltar"), Arrays.asList());
        inv.setItem(49, back);

        player.openInventory(inv);
    }

    // Menu de configurações - aqui que a brincadeira começa menó
    public static void openSettingsMenu(Player player) {
        ClanData clan = ClanManager.getInstance().getClanByPlayer(player.getUniqueId());
        if (clan == null)
            return; // Ué, cadê o clan? Voltou pra casa?

        Inventory inv = Bukkit.createInventory(null, 27, ColorUtil.color("&c&lConfigurações do Clan"));

        // Vidro laranja pra deixar bonitinho
        ItemStack orangeGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        ItemMeta glassMeta = orangeGlass.getItemMeta();
        glassMeta.setDisplayName(" ");
        orangeGlass.setItemMeta(glassMeta);

        for (int i : new int[] { 0, 1, 7, 8, 9, 17, 18, 19, 25, 26 }) {
            inv.setItem(i, orangeGlass);
        }

        // Toggle de fogo amigo - a estrela do show caralho
        boolean ffStatus = clan.isFriendlyFire();
        Material ffMaterial = ffStatus ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK;
        String ffColor = ffStatus ? "&c" : "&a";
        String ffStatusText = ffStatus ? "ATIVADO" : "DESATIVADO";

        List<String> ffLore = new ArrayList<>();
        ffLore.add(ColorUtil.color("&7Status: " + ffColor + ffStatusText));
        ffLore.add("");
        if (ffStatus) {
            ffLore.add(ColorUtil.color("&cOs brother podem trocar porrada!"));
            ffLore.add(ColorUtil.color("&7Clique para desativar"));
        } else {
            ffLore.add(ColorUtil.color("&aPaz e amor no clan ✌"));
            ffLore.add(ColorUtil.color("&7Clique para ativar"));
        }

        // Só o dono muda essa porra
        if (!clan.getOwner().equals(player.getUniqueId())) {
            ffLore.add("");
            ffLore.add(ColorUtil.color("&c⚠ Só o dono pode alterar"));
        }

        ItemStack ffToggle = createItem(ffMaterial,
                ColorUtil.color("&e&lFogo Amigo (PvP)"),
                ffLore);
        inv.setItem(13, ffToggle);

        // Botão de voltar
        ItemStack back = createItem(Material.BARRIER, ColorUtil.color("&c&lVoltar"), Arrays.asList());
        inv.setItem(22, back);

        player.openInventory(inv);
    }

    // Menu de guerra
    public static void openWarMenu(Player player) {
        ClanData clan = ClanManager.getInstance().getClanByPlayer(player.getUniqueId());
        if (clan == null)
            return;

        Inventory inv = Bukkit.createInventory(null, 27, ColorUtil.color("&4&lSistema de Guerra"));

        // Vidro vermelho pra tema de guerra
        ItemStack redGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta glassMeta = redGlass.getItemMeta();
        glassMeta.setDisplayName(" ");
        redGlass.setItemMeta(glassMeta);

        for (int i : new int[] { 0, 1, 7, 8, 9, 17, 18, 19, 25, 26 }) {
            inv.setItem(i, redGlass);
        }

        ItemStack info = createItem(Material.BOOK, ColorUtil.color("&e&lComo Funciona"), Arrays.asList(
                ColorUtil.color("&71. Use &f/clan guerra <tag>"),
                ColorUtil.color("&72. O outro clan precisa aceitar"),
                ColorUtil.color("&73. Guerra dura 10 minutos"),
                ColorUtil.color("&74. Clan com mais kills vence"),
                "",
                ColorUtil.color("&cPvP é liberado durante guerra!")));
        inv.setItem(13, info);

        ItemStack close = createItem(Material.BARRIER, ColorUtil.color("&cFechar"), Arrays.asList());
        inv.setItem(22, close);

        player.openInventory(inv);
    }

    // Cria um item com nome e lore
    private static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (!lore.isEmpty())
            meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Cria uma cabeça de jogador
    private static ItemStack createSkull(String playerName, String displayName, List<String> lore) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(playerName);
        meta.setDisplayName(displayName);
        if (!lore.isEmpty())
            meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }
}
