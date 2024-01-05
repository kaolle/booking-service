package pb.se.bookingservice.port.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pb.se.bookingservice.application.UserNotFoundException;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.domain.User;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.persistence.QueryRepository;
import pb.se.bookingservice.port.persistence.UserRepository;
import pb.se.bookingservice.port.rest.dto.JwtResponse;
import pb.se.bookingservice.port.rest.dto.SigninRequest;
import pb.se.bookingservice.port.rest.dto.SignupRequest;
import pb.se.bookingservice.port.security.CustomUserDetails;
import pb.se.bookingservice.port.security.JwtUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    FamilyMemberRepository familyMemberRepository;

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest loginRequest) {

        return authenticate(HttpStatus.OK, loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {

        FamilyMember member = queryRepository.findFamilyMemberByPhrase(signupRequest.getFamilyPhrase());

        if (userRepository.findById(signupRequest.getUsername()).isPresent()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (queryRepository.findUserByMember(member) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        userRepository.save(new User(member, signupRequest.getUsername(), encoder.encode(signupRequest.getPassword())));

        return authenticate(HttpStatus.CREATED, signupRequest.getUsername(), signupRequest.getPassword());
    }

    @DeleteMapping("/signdown")
    public ResponseEntity selfSignDown(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findById(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<JwtResponse> authenticate(HttpStatus httpStatus, String requestUsername, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestUsername, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return new ResponseEntity<>(new JwtResponse(jwt,
                userDetails.getMemberId(),
                userDetails.getUsername(),
                "no-email@b.b",
                roles), httpStatus);
    }

}
