package pb.se.bookingservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
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

    @JsonProperty
    @SuppressWarnings("unused")
    private Instant created;
    public FamilyMember() {
    }

    public FamilyMember(String name) {
        this.created = Instant.now();
        this.uuid = UUID.randomUUID();
        this.name = name;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("aBitMore", aBitMore)
                .toString();
    }
}
