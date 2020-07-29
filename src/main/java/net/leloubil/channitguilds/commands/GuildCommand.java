package net.leloubil.channitguilds.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.leloubil.channitguilds.DatabaseLink;
import net.leloubil.channitguilds.entities.ChannitGuild;
import net.leloubil.channitguilds.entities.ChannitPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.transaction.Transactional;

@SuppressWarnings("ConstantConditions")
@CommandAlias("guild|g|guilds")
public class GuildCommand extends BaseCommand {

    @Dependency
    private DatabaseLink databaseLink;

    @Subcommand("create")
    @Syntax("[guildName]")
    @Description("Creates a new guild")
    @Transactional
    public void onCreate(@Conditions("noguild") @Flags("") ChannitPlayer player, String guildName){
        if(databaseLink.getGuildByName(guildName).isPresent()){
            player.getPlayer().getPlayer().sendMessage("This guild name is already used");
            return;
        }
        ChannitGuild guild = new ChannitGuild(player, guildName);
        player.getPlayer().getPlayer().sendMessage("The guild " + guildName + " was successfully created !");
    }

    @Subcommand("invite") @Syntax("[player]")
    @Description("Invites a player into the guild")
    @Transactional
    public void onInvite(@Conditions("hasguild") ChannitPlayer player, @Flags("other") @Conditions("noguild") ChannitPlayer other){
        OfflinePlayer otherp = other.getPlayer();
        if(otherp.isOnline()){
            otherp.getPlayer().sendMessage("You have been invited to join the " + player.getGuild().getName() + " guild by " + player.getPlayer().getName());
        }
        other.setLastInvite(player.getGuild());
        player.getPlayer().getPlayer().sendMessage("Invitation sent");
    }

    @Subcommand("leave") @Syntax("[player]")
    @Description("Leaves a guild")
    @Transactional
    public void onLeave(@Conditions("hasguild") ChannitPlayer player){
        player.setGuild(null);
        player.getPlayer().getPlayer().sendMessage("Guild leaved");
    }

    @Subcommand("info") @Syntax("")
    @Description("Lists info of the guild")
    public void onInfo(@Conditions("hasguild") ChannitPlayer player, ChannitGuild guild){
        player.getPlayer().getPlayer()
                .sendMessage("name : " + guild.getName());
    }

    @HelpCommand @Syntax("[command]")
    public void onHelp(CommandSender sender, CommandHelp command){
        command.showHelp();
    }


}
