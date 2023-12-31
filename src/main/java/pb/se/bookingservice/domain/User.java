package pb.se.bookingservice.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users", collation = "sv")
public class User {

    @Id
    private final String username;
    private final String password;
    @DBRef
    private final FamilyMember familyMember;

    public User(FamilyMember familyMember, String username, String password) {
        this.username = username;
        this.password = password;
        this.familyMember = familyMember;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }
}
