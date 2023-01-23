package com.pfa.pfasecurity.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pfa.pfasecurity.material.Image;
import com.pfa.pfasecurity.material.Material;
import com.pfa.pfasecurity.material.MaterialRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.pfasecurity.user.Role;
import com.pfa.pfasecurity.user.User;
import com.pfa.pfasecurity.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final UserRepository repository;

    private final MaterialRepo materialRepository;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

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

    @PostMapping("/AddMaterials")
    public ResponseEntity<String> AddMaterials(@RequestBody List<Material> materials){
        materialRepository.saveAll(materials);
        return ResponseEntity.ok("materielles est ajoutées");
    }
    
    @GetMapping("/materials/{id}/images")
    public ResponseEntity<List<Image>> getMaterialImages(@PathVariable Integer id) {
        Material material = materialRepository.findById(id).orElse(null);
        if(material == null) {
            return ResponseEntity.notFound().build();
        }
        List<Image> images = material.getImages();
        return ResponseEntity.ok(images);
    }


}