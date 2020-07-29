package net.leloubil.channitguilds.entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "channitguilds_players")
@Data
@NoArgsConstructor
public class ChannitPlayer {

    @Id @Type(type = "uuid-char")
    private UUID uuid;

    @Transient
    private OfflinePlayer player;

    @ManyToOne
    @JoinColumn(name="guild_name")
    private ChannitGuild guild;

    @OneToOne
    @JoinColumn(name="last_invite")
    private ChannitGuild lastInvite;

    public ChannitPlayer(OfflinePlayer p) {
        this.uuid = p.getUniqueId();
        this.player = p;
    }
}
