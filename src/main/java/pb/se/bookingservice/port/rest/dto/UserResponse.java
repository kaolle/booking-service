package pb.se.bookingservice.port.rest.dto;

import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.domain.Role;
import pb.se.bookingservice.domain.User;

public class UserResponse {
    private final String username;
    private final FamilyMember familyMember;
    private final Role role;

    public UserResponse(String username, FamilyMember familyMember, Role role) {
        this.username = username;
        this.familyMember = familyMember;
        this.role = role;
    }

    public static UserResponse fromDomain(User user) {
        return new UserResponse(user.getUsername(), user.getFamilyMember(), user.getRole());
    }

    public String getUsername() {
        return username;
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public Role getRole() {
        return role;
    }
}
