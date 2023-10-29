package pb.se.bookingservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.UUID;

@Document(collection = "familyMembers", collation = "sv")
public class FamilyMember implements Serializable {
    @Id
    @JsonProperty
    private UUID uuid;
    @JsonProperty
    private String name;

    @JsonProperty
    private String aBitMore;

    public FamilyMember() {
    }

    public FamilyMember(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.name = "anonymous";
    }

    public FamilyMember(String name, String aBitMore) {
        this(name);
        this.aBitMore = aBitMore;
    }

    public FamilyMember(UUID uuid, String name, String aBitMore) {
        this(name);
        this.uuid = uuid;
        this.aBitMore = aBitMore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FamilyMember that = (FamilyMember) o;

        return new EqualsBuilder().append(uuid, that.uuid).append(name, that.name).append(aBitMore, that.aBitMore).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(uuid).append(name).append(aBitMore).toHashCode();
    }
}
