package com.pfa.pfasecurity.auth;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.pfasecurity.material.Image;
import com.pfa.pfasecurity.material.Material;
import com.pfa.pfasecurity.material.MaterialRepo;
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
    //http://localhost:8080/api/v1/auth/GetMaterials
    @GetMapping("/GetMaterials")
    public List<Material> getMaterials() {
        return materialRepository.findAll();
    }
    //switching the boolean !
    @PostMapping("testing/hh")
    public void checkDueDate(){
        List<Material> materials = materialRepository.findAll();
        for (Material material : materials) {
            if(material.getDueDate().before(new Date()) && !material.isDisponible()){
                material.setDisponible(true);
                System.out.println("testiing" + material.getDueDate());
                materialRepository.save(material);
                // additional action like charging user for late return
            }
        }
    }
    @PutMapping("/{id}/reserved")
    public ResponseEntity<String> Reserver(@PathVariable Integer id, @RequestBody Map<String, String> requestBody, @RequestBody int qte) {
        Material material = materialRepository.findById(id).orElse(null);
        if (material == null) {
            return ResponseEntity.notFound().build();
        }
        try {
        	if(material.getQuantite() >= qte) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dueDate = LocalDateTime.parse(requestBody.get("due_date"), formatter);
            material.setDueDate(Date.from(dueDate.atZone(ZoneId.systemDefault()).toInstant()));
            material.setQuantite(material.getQuantite() - qte);
        	}
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid due_date format. Please use ISO-8601 format.");
        }
        if(material.getQuantite() == 0)
        material.setDisponible(false);
        materialRepository.save(material);
        return ResponseEntity.ok("Material Reserved");
    }


    


}