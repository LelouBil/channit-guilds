package net.leloubil.channitguilds.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.ryanhamshire.GriefPrevention.Claim;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "channitguilds_guild")
@Data
@NoArgsConstructor
public class ChannitGuild {

    @Id
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name="owner_uuid")
    private ChannitPlayer owner;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL)
    private List<ChannitPlayer> members = new ArrayList<>();

    @Type(type = "net.leloubil.channitguilds.types.GPClaimIdType")
    private List<Claim> guildWideClaims = new ArrayList<>();

    public ChannitGuild(ChannitPlayer owner, String name) {
        this.owner = owner;
        this.name = name;
        this.owner.setGuild(this);
    }
}
