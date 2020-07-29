package net.leloubil.channitguilds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.leloubil.channitguilds.entities.ChannitGuild;
import net.leloubil.channitguilds.entities.ChannitPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public final class ChannitGuildsPlugin extends JavaPlugin {

    @Getter
    private ConfigManager configManager;

    @Getter
    private DatabaseLink databaseLink;

    @Getter
    private PaperCommandManager paperCommandManager;

    private static ChannitGuildsPlugin instance;

    @Override
    public void onEnable() {
        if(instance == null) instance = this;
        else {
            setEnabled(false);
            return;
        }
        configManager = new ConfigManager(this);
        paperCommandManager = new PaperCommandManager(this);
        databaseLink = new DatabaseLink(configManager);
        initPlugin();
        log.info("inited");
    }

    private void initPlugin() {
        configManager.loadConfiguration();
        databaseLink.initDatabase();
        initCommands();
    }

    private void initCommands() {
        paperCommandManager.enableUnstableAPI("help");
        paperCommandManager.registerDependency(DatabaseLink.class,databaseLink);
        paperCommandManager.getCommandContexts().registerIssuerAwareContext(ChannitPlayer.class, c -> {
            if("false".equalsIgnoreCase(c.getFlagValue("other","false"))){
                return databaseLink.getChannitPlayer(c.getPlayer());
            }
            return databaseLink.getChannitPlayer(c.getFirstArg());
        });
        paperCommandManager.getCommandContexts().registerIssuerAwareContext(ChannitGuild.class, c -> {
            return databaseLink.getChannitPlayer(c.getPlayer()).getGuild();
        });

        paperCommandManager.getCommandConditions().addCondition(ChannitPlayer.class,"noguild",(c,exec, value) -> {
            if(value.getGuild() != null){
                throw new ConditionFailedException( (value.getUuid() == exec.getPlayer().getUniqueId() ? "You are " : value.getPlayer().getName() + " is ") + "already in a guild");
            }
        });
        paperCommandManager.getCommandConditions().addCondition(ChannitPlayer.class,"hasguild",(c,exec, value) -> {
            if(value.getGuild() == null){
                throw new ConditionFailedException((value.getUuid() == exec.getPlayer().getUniqueId() ? "You are " : value.getPlayer().getName() + " is ") + "not in a guild");
            }
        });
        Reflections reflections = new Reflections("net.leloubil.channitguilds.commands");
        reflections.getSubTypesOf(BaseCommand.class).forEach(c -> {
            try {
                paperCommandManager.registerCommand(c.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

    public static DatabaseLink db(){
        return instance.getDatabaseLink();
    }

    public static GriefPrevention gp(){
        return instance.gprev();
    }

    private GriefPrevention gprev() {
        return getPlugin(GriefPrevention.class);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
