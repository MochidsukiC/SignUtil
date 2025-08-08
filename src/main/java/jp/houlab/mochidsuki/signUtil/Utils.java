package jp.houlab.mochidsuki.signUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static jp.houlab.mochidsuki.signUtil.Main.config;

public class Utils {
    public static void initializeSkillItem(Player player) {
        for(String section:config.getConfigurationSection("Sign").getKeys(false)) {
            if(player.getScoreboardTags().contains(config.getString("Sign."+section+".tag"))){
                removeAllSkillItem(player);
                addSkillItem(player,section);

            }
        }
    }

    public static void initializeSkillCoolDown(Player player) {
        for(String section:config.getConfigurationSection("Sign").getKeys(false)) {
            for(String item : config.getConfigurationSection("Sign."+section+".item").getKeys(false)){
            player.setCooldown(Material.matchMaterial(item),0);
            }
        }
    }
    public static void initializeSkillCoolDownMax(Player player) {
        for(String section:config.getConfigurationSection("Sign").getKeys(false)) {
            for(String item : config.getConfigurationSection("Sign."+section+".item").getKeys(false)){
                player.setCooldown(Material.matchMaterial(item),config.getInt("Sign."+section+".item."+item+".cooldown"));
            }
        }
    }

    public static void removeAllSkillItem(Player player) {
        //Item
        for(String string : config.getConfigurationSection("Sign").getKeys(false)) {
            for (String item : config.getConfigurationSection("Sign." + string + ".item").getKeys(false)) {
                Material material = Material.matchMaterial(item);
                if (material != null) {
                    player.getInventory().remove(material);
                }
            }
        }
    }
    public static void removeAllSkillTags(Player player) {
        for(String string : config.getConfigurationSection("Sign").getKeys(false)){
            //tag
            String tag = config.getString("Sign."+string+".tag");
            if(tag != null && player.getScoreboardTags().contains(tag)) {
                player.removeScoreboardTag(tag);
            }
            config.getStringList("Sign."+string+".subTag").forEach(
                    player.getScoreboardTags()::remove);
        }

        player.removeScoreboardTag("JetPack");


    }

    public static void addSkillItem(Player player,String word) {
        for(String item : config.getConfigurationSection("Sign."+word+".item").getKeys(false)){
            Material material = Material.matchMaterial(item);
            if(material != null) {
                ItemStack itemStack = new ItemStack(material);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(Component.text(config.getString("Sign."+word+".item."+item+".name")).color(NamedTextColor.NAMES.value(config.getString("Sign."+word+".item."+item+".color","WHITE").toLowerCase(Locale.ROOT))));
                List<String> lore = config.getStringList("Sign."+word+".item."+item+".lore");
                List<Component> loreComponents = new ArrayList<>();
                if(!lore.isEmpty()){
                    for(String string : lore){
                        loreComponents.add(LegacyComponentSerializer.legacySection().deserialize(string));
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

    public static void addSkillTag(Player player,String word){
        player.addScoreboardTag(config.getString("Sign."+word+".tag"));


        List<String> opts = config.getStringList("Sign."+word+".opt");
        if(config.getConfigurationSection("Sign."+word).contains("opt") && !opts.isEmpty()) {
            for(String string : opts){
                switch (string){
                    case "JetPack"->player.addScoreboardTag("JetPack");
                }
            }
        }
    }
}
