package net.leloubil.channitguilds.types;

import me.ryanhamshire.GriefPrevention.Claim;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BigIntTypeDescriptor;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor;

import java.util.List;

public class GPClaimIdType extends AbstractSingleColumnStandardBasicType<List> {

    public GPClaimIdType() {
        super(LongVarcharTypeDescriptor.INSTANCE,GPClaimIdTypeJavaDescriptor.INSTANCE);
    }

    public static final GPClaimIdType INSTANCE = new GPClaimIdType();

    @Override
    public String getName() {
        return "GPClaimID";
    }

}
