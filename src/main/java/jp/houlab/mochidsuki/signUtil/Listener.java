package jp.houlab.mochidsuki.signUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.Command;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static jp.houlab.mochidsuki.signUtil.Main.config;
import static jp.houlab.mochidsuki.signUtil.Main.plugin;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            switch (event.getClickedBlock().getType()) {
                case ACACIA_HANGING_SIGN:
                case ACACIA_WALL_SIGN:
                case BIRCH_HANGING_SIGN:
                case BIRCH_WALL_SIGN:
                case DARK_OAK_HANGING_SIGN:
                case DARK_OAK_WALL_SIGN:
                case JUNGLE_HANGING_SIGN:
                case JUNGLE_WALL_SIGN:
                case SPRUCE_HANGING_SIGN:
                case SPRUCE_WALL_SIGN:
                case CRIMSON_HANGING_SIGN:
                case CRIMSON_WALL_SIGN:
                case WARPED_HANGING_SIGN:
                case WARPED_WALL_SIGN:
                case ACACIA_SIGN:
                case BIRCH_SIGN:
                case DARK_OAK_SIGN:
                case JUNGLE_SIGN:
                case SPRUCE_SIGN:
                case CRIMSON_SIGN:
                case WARPED_SIGN:
                case CHERRY_HANGING_SIGN:
                case CHERRY_WALL_SIGN:
                case MANGROVE_HANGING_SIGN:
                case MANGROVE_WALL_SIGN:
                case OAK_SIGN:
                case OAK_WALL_SIGN:
                case ACACIA_WALL_HANGING_SIGN:
                case BIRCH_WALL_HANGING_SIGN:
                case DARK_OAK_WALL_HANGING_SIGN:
                case JUNGLE_WALL_HANGING_SIGN:
                case SPRUCE_WALL_HANGING_SIGN:
                case BAMBOO_HANGING_SIGN:
                case BAMBOO_WALL_HANGING_SIGN:
                case BAMBOO_SIGN:
                case BAMBOO_WALL_SIGN:
                case CHERRY_SIGN:
                case CHERRY_WALL_HANGING_SIGN:
                case MANGROVE_SIGN:
                case MANGROVE_WALL_HANGING_SIGN:
                case CRIMSON_WALL_HANGING_SIGN:
                case OAK_HANGING_SIGN:
                case OAK_WALL_HANGING_SIGN:
                case WARPED_WALL_HANGING_SIGN:{
                    Sign sign = (Sign)event.getClickedBlock().getState();
                    for(String string : config.getConfigurationSection("Sign").getKeys(false)) {
                        checkSign(sign.getSide(Side.BACK), string, player);
                        checkSign(sign.getSide(Side.FRONT), string, player);
                        event.setCancelled(true);
                    }
                    break;
                }

            }

        }
    }
    private void checkSign(SignSide side,String word,Player player) {
        for (int i = 0; i < 4;i++){
            if(PlainTextComponentSerializer.plainText().serialize(side.line(i)).equals(word) && config.getString("Sign."+word+".tag") != null) {
                for(String string : config.getConfigurationSection("Sign").getKeys(false)){
                    //tag
                    String tag = config.getString("Sign."+string+".tag");
                    if(tag != null && player.getScoreboardTags().contains(tag)) {
                        player.removeScoreboardTag(tag);
                    }

                    //Item
                    for(String item : config.getConfigurationSection("Sign."+string+".item").getKeys(false)){
                        Material material = Material.matchMaterial(item);
                        if(material != null) {
                            player.getInventory().remove(material);
                        }
                    }
                }
                player.addScoreboardTag(config.getString("Sign."+word+".tag"));

                player.removeScoreboardTag("JetPack");

                List<String> opts = config.getStringList("Sign."+word+".opt");
                if(config.getConfigurationSection("Sign."+word).contains("opt") && !opts.isEmpty()) {
                    for(String string : opts){
                        switch (string){
                            case "JetPack"->player.addScoreboardTag("JetPack");
                        }
                    }
                }

                for(String item : config.getConfigurationSection("Sign."+word+".item").getKeys(false)){
                    Material material = Material.matchMaterial(item);
                    if(material != null) {
                        ItemStack itemStack = new ItemStack(material);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.displayName(Component.text(config.getString("Sign."+word+".item."+item+".name")).color(NamedTextColor.NAMES.value(config.getString("Sign."+word+".item."+item+".color","WHITE").toLowerCase(Locale.ROOT))));
                        List<String> lore = config.getStringList("Sign."+word+".lore");
                        List<Component> loreComponents = new ArrayList<>();
                        if(!lore.isEmpty()){
                            for(String string : lore){
                                loreComponents.add(Component.text(string));
                            }
                            itemMeta.lore(loreComponents);
                        }

                        if(config.getConfigurationSection("Sign."+word+".item."+item+".enchantment") != null) {
                            Registry<Enchantment> enchantmentRegistry = Bukkit.getRegistry(Enchantment.class);
                            itemMeta.addEnchant(enchantmentRegistry.get(new NamespacedKey("minecraft",config.getString("Sign."+word+".item."+item+".enchantment.id"))),config.getInt("Sign."+word+".item."+item+".enchantment.level"),false);

                        }
                        itemStack.setItemMeta(itemMeta);
                        player.getInventory().addItem(itemStack);
                    }
                }
            }
        }
    }
}
