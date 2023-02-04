package com.pfa.pfasecurity.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.pfasecurity.auth.AuthenticationService;
import com.pfa.pfasecurity.material.MaterialRepo;
import com.pfa.pfasecurity.pannier.pannierRepository;
import com.pfa.pfasecurity.reservation.reservationRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserRepository repository;
	
	//make call to get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return repository.findAll();
    }
    //delete by id
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        repository.deleteById(id);
        return "User deleted";
    }
    //////////////////////////Approve user////////////////////////

    @PostMapping("/approve/{id}")
    //Update user to approved with repository String
    public String approveUser(@PathVariable int id) {
        User user = repository.findById(id).orElseThrow();
        user.setApproved(true);
        repository.save(user);
        return "User approved";
    }
    //get users who are not approved
    @GetMapping("/users/notapproved")
    public List<User> getNotApprovedUsers() {
        return repository.findByApproved(false);
    }

    //////////////////////Role////////////////////
    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(@PathVariable Role role) {
        return repository.findByRole(role);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = repository.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> map = new HashMap<>();
        map.put("role", user.getRole().name());
        return ResponseEntity.ok(map);
    }

    
    @PutMapping("/role/{id}/{newRole}")
    public String changeRole(@PathVariable int id, @PathVariable Role newRole) {
        try {
            User user = repository.findById(id).orElseThrow();
            user.setRole(newRole);
            if(user.getRole() == null) return "invalide role !";
            repository.save(user);
            return "Role changed";}
        catch (Exception e) {
            return "hh";
        }
    }
}
