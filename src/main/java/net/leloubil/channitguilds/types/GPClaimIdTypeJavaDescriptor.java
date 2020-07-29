package net.leloubil.channitguilds.types;

import me.ryanhamshire.GriefPrevention.Claim;
import net.leloubil.channitguilds.ChannitGuildsPlugin;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GPClaimIdTypeJavaDescriptor extends AbstractTypeDescriptor<List> {


    public static final GPClaimIdTypeJavaDescriptor INSTANCE = new GPClaimIdTypeJavaDescriptor();

    protected GPClaimIdTypeJavaDescriptor() {
        super(List.class);
    }

    @Override
    public List fromString(String string) {
        return null; // ???
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(List value, Class<X> type, WrapperOptions options) {
        if(value == null) return null;
        if(String.class.isAssignableFrom(type)){
            return (X) value.stream().map(c -> ((Claim) c).getID().toString()).collect(Collectors.joining(","));
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> List wrap(X value, WrapperOptions options) {
        if(value == null) return null;
        if(value instanceof String){
            String[] splitted = ((String) value).split(",");
            if(splitted.length == 1 && splitted[0].equals("")) return new ArrayList<Claim>();
            return Stream.of(splitted).map(l -> ChannitGuildsPlugin.gp().dataStore.getClaim(Long.parseLong(l))).collect(Collectors.toList());
        }
        throw unknownWrap(value.getClass());
    }
}
