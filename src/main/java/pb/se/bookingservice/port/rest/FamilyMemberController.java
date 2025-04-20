package pb.se.bookingservice.port.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pb.se.bookingservice.application.MemberNotFoundException;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.persistence.UserRepository;
import pb.se.bookingservice.port.rest.dto.FamilyMemberRequest;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing family members.
 */
@RestController
@RequestMapping("/family")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Adds a new family member. Only users with FAMILY_UBERHEAD role can access this endpoint.
     *
     * @param request The family member creation request
     * @return ResponseEntity with the created family member
     */
    @PostMapping("/member")
    @PreAuthorize("hasRole('FAMILY_UBERHEAD')")
    public ResponseEntity<FamilyMember> addFamilyMember(@Valid @RequestBody FamilyMemberRequest request) {
        FamilyMember familyMember = new FamilyMember(request.getName(), request.getPhrase());
        FamilyMember savedMember = familyMemberRepository.save(familyMember);
        return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
    }

    /**
     * Gets all family members. This endpoint is publicly accessible.
     *
     * @return List of all family members
     */
    @GetMapping("/members")
    @PreAuthorize("hasRole('FAMILY_UBERHEAD')")
    public ResponseEntity<List<FamilyMember>> getAllFamilyMembers() {
        List<FamilyMember> members = familyMemberRepository.findAll();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    /**
     * Gets a family member by ID. This endpoint is publicly accessible.
     *
     * @param id The UUID of the family member
     * @return ResponseEntity with the family member
     * @throws MemberNotFoundException if the family member is not found
     */
    @GetMapping("/member/{id}")
    @PreAuthorize("hasRole('FAMILY_UBERHEAD')")
    public ResponseEntity<FamilyMember> getFamilyMemberById(@PathVariable UUID id) {
        FamilyMember member = familyMemberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Family member not found with id: " + id));
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    /**
     * Updates a family member. Only users with FAMILY_UBERHEAD role can access this endpoint.
     *
     * @param id The UUID of the family member to update
     * @param request The updated family member data
     * @return ResponseEntity with the updated family member
     * @throws MemberNotFoundException if the family member is not found
     */
    @PutMapping("/member/{id}")
    @PreAuthorize("hasRole('FAMILY_UBERHEAD')")
    public ResponseEntity<FamilyMember> updateFamilyMember(@PathVariable UUID id,
                                                          @Valid @RequestBody FamilyMemberRequest request) {
        FamilyMember existingMember = familyMemberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Family member not found with id: " + id));

        // Update the member with new values
        FamilyMember updatedMember = new FamilyMember(id, request.getName(), request.getPhrase());
        FamilyMember savedMember = familyMemberRepository.save(updatedMember);

        return new ResponseEntity<>(savedMember, HttpStatus.OK);
    }

    /**
     * Deletes a family member. Only users with FAMILY_UBERHEAD role can access this endpoint.
     *
     * @param id The UUID of the family member to delete
     * @return ResponseEntity with no content
     * @throws MemberNotFoundException if the family member is not found
     */
    @DeleteMapping("/member/{id}")
    @PreAuthorize("hasRole('FAMILY_UBERHEAD')")
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable UUID id) {
        // Check if family member exists
        FamilyMember familyMember = familyMemberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Family member not found with id: " + id));

        // Find and delete any user associated with this family member
        userRepository.findAll().stream()
                .filter(user -> user.getFamilyMember() != null &&
                        user.getFamilyMember().getUuid().equals(id))
                .forEach(user -> userRepository.deleteById(user.getUsername()));

        // Delete the family member
        familyMemberRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
