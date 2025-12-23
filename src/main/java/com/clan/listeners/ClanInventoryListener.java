package com.clan.listeners;

import com.clan.gui.ClanGUI;
import com.clan.managers.ClanManager;
import com.clan.model.ClanData;
import com.clan.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

// Listener pra quando o cara clica no inventário da GUI
public class ClanInventoryListener implements Listener {

    private final ClanManager clanManager = ClanManager.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        // Se não é um menu do clan, passa reto mano
        if (!title.contains("Clan") && !title.contains("Membros") && !title.contains("Guerra")
                && !title.contains("Configurações")) {
            return;
        }

        event.setCancelled(true); // Cancela pra não roubar item

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return; // Clicou no nada
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        String itemName = clicked.getItemMeta().getDisplayName();

        // Menu principal
        if (title.contains("Menu de Clans")) {
            handleMainMenu(player, clicked, itemName);
        }
        // Menu de membros
        else if (title.contains("Membros")) {
            handleMembersMenu(player, clicked, itemName, title);
        }
        // Menu de guerra
        else if (title.contains("Guerra")) {
            handleWarMenu(player, clicked, itemName);
        }
        // Menu de configurações - aqui que vem a mágica!
        else if (title.contains("Configurações")) {
            handleSettingsMenu(player, clicked, itemName);
        }
    }

    // Lida com cliques no menu principal
    private void handleMainMenu(Player player, ItemStack clicked, String itemName) {
        if (itemName.contains("Fechar")) {
            player.closeInventory();
        } else if (itemName.contains("Membros")) {
            ClanGUI.openMembersMenu(player, 0);
        } else if (itemName.contains("Configurações")) {
            ClanGUI.openSettingsMenu(player); // Bora pro menu de config
        } else if (itemName.contains("Guerra")) {
            ClanGUI.openWarMenu(player);
        } else if (itemName.contains("Seu Clan")) {
            ClanGUI.openMembersMenu(player, 0);
        }
    }

    // Lida com cliques no menu de membros
    private void handleMembersMenu(Player player, ItemStack clicked, String itemName, String title) {
        if (itemName.contains("Voltar")) {
            ClanGUI.openMainMenu(player);
        } else if (itemName.contains("Anterior")) {
            int currentPage = extractPageNumber(title);
            ClanGUI.openMembersMenu(player, currentPage - 2);
        } else if (itemName.contains("Próxima")) {
            int currentPage = extractPageNumber(title);
            ClanGUI.openMembersMenu(player, currentPage);
        } else if (clicked.getType() == Material.SKULL_ITEM) {
            // Sistema de expulsão - clicou na cabeça de alguém
            ClanData clan = clanManager.getClanByPlayer(player.getUniqueId());
            if (clan != null && clan.isOfficer(player.getUniqueId())) {
                String targetName = itemName.replace("&a", "");
                @SuppressWarnings("deprecation")
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if (target != null && clan.isMember(target.getUniqueId())) {
                    if (!clan.getOwner().equals(target.getUniqueId())) {
                        clan.removeMember(target.getUniqueId());
                        clanManager.saveClans();
                        player.sendMessage(ColorUtil.color("&c" + targetName + " foi expulso. Tchau!"));
                        ClanGUI.openMembersMenu(player, 0);
                    } else {
                        player.sendMessage(ColorUtil.color("&cNão pode expulsar o dono, ô animal!"));
                    }
                }
            }
        }
    }

    // Lida com cliques no menu de guerra
    private void handleWarMenu(Player player, ItemStack clicked, String itemName) {
        if (itemName.contains("Fechar")) {
            player.closeInventory();
        }
    }

    // Lida com cliques no menu de configurações - aqui que o bicho pega!
    private void handleSettingsMenu(Player player, ItemStack clicked, String itemName) {
        if (itemName.contains("Voltar")) {
            ClanGUI.openMainMenu(player); // Volta pro menu principal
            return;
        }

        // Se clicou no toggle de fogo amigo
        if (itemName.contains("Fogo Amigo")) {
            ClanData clan = clanManager.getClanByPlayer(player.getUniqueId());

            // Só dono pode mexer nessa porra
            if (clan == null || !clan.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(ColorUtil.color("&cEita! Só o dono pode mudar isso aí, parça."));
                player.closeInventory();
                return;
            }

            // Aqui é onde a mágica acontece - inverte o status
            boolean novoStatus = !clan.isFriendlyFire();
            clan.setFriendlyFire(novoStatus);
            clanManager.saveClans(); // Salva pra não perder essa belezura

            // Avisa o cara que deu bom
            if (novoStatus) {
                player.sendMessage(ColorUtil.color("&c&l⚔ Fogo amigo ATIVADO! &cAgora é tudo na porrada mesmo!"));
            } else {
                player.sendMessage(ColorUtil.color("&a&l✔ Fogo amigo DESATIVADO! &aPaz e amor no clan!"));
            }

            // Reabre o menu pra ver a mudança na hora
            ClanGUI.openSettingsMenu(player);
        }
    }

    // Extrai o número da página do título
    private int extractPageNumber(String title) {
        try {
            String[] parts = title.split("Página ");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].trim());
            }
        } catch (Exception e) {
            // Se der erro, retorna 1 mesmo
        }
        return 1;
    }
}
