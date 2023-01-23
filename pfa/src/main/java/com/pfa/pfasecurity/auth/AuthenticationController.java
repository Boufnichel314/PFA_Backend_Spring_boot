package com.pfa.pfasecurity.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.pfasecurity.material.Material;
import com.pfa.pfasecurity.material.MaterialService;
import com.pfa.pfasecurity.user.Role;
import com.pfa.pfasecurity.user.User;
import com.pfa.pfasecurity.user.UserRepository;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService service;
  private final UserRepository repository;
  @Autowired
	private MaterialService materialService;
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
    
    //////////////////////Materials///////////////////
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                         @RequestParam("name") String name,
                                         @RequestParam("description") String description) throws IOException, java.io.IOException {
        String uploadImage = materialService.uploadImage(file, name, description);
        return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
    }

	@GetMapping("/{fileName}")
	public ResponseEntity<?> downloadImage(@PathVariable String fileName){
		byte[] imageData=materialService.downloadImage(fileName);
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.valueOf("image/png"))
				.body(imageData);
	}
	
  @GetMapping("/materials")
  public List<Material> getAllMaterials() {
      return materialService.getAllMaterialsWithImages();
  }
  
  
  @PutMapping("materials/{id}/reserve")
  public String reserveMaterial(@PathVariable int id) {
      try {
          materialService.reserveMaterial(id);
          return "Material has been reserved successefally";
      } catch (EntityNotFoundException ex) {
          return "There is no material";
      } catch (IllegalStateException ex) {
          return "there is an conflit";
      }
  }
  
  @PutMapping("/{id}/keywords")
  public ResponseEntity<String> addKeyword(@PathVariable int id, @RequestBody String keyword) {
      materialService.addKeyword(id, keyword);
      return new ResponseEntity<>("Keyword added", HttpStatus.OK);
  }
  
  @GetMapping("/search/{keyword}")
  public List<Material> getMaterialsByKeyword(@PathVariable String keyword) {
      return materialService.getMaterialsByKeyword(keyword);
  }
  
}