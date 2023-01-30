package com.pfa.pfasecurity.auth;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.pfasecurity.material.Image;
import com.pfa.pfasecurity.material.Material;
import com.pfa.pfasecurity.material.MaterialRepo;
import com.pfa.pfasecurity.pannier.Pannier;
import com.pfa.pfasecurity.pannier.PannierDto;
import com.pfa.pfasecurity.pannier.pannierRepository;
import com.pfa.pfasecurity.reservation.Reservation;
import com.pfa.pfasecurity.reservation.ReservationDto;
import com.pfa.pfasecurity.reservation.ReserveDto;
import com.pfa.pfasecurity.reservation.reservationRepository;
import com.pfa.pfasecurity.user.Role;
import com.pfa.pfasecurity.user.User;
import com.pfa.pfasecurity.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final UserRepository repository;
    private final reservationRepository reservationRepository;
    private final MaterialRepo materialRepository;
    private final pannierRepository pannierRepository;
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
    //method to change role
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
    
    @PostMapping("/AddMaterials")
    public ResponseEntity<String> AddMaterials(@RequestBody List<Material> materials){
        materialRepository.saveAll(materials);
        return ResponseEntity.ok("materielles est ajoutées");
    }
    @PutMapping("/updateMaterial/{id}")
    public ResponseEntity<String> AddMaterials(@PathVariable Integer id ,@RequestBody Material materials){
        //find by id
        Material material = materialRepository.findById(id).orElse(null);
        if(material == null) {
            return ResponseEntity.notFound().build();
        }
            //titre and images and sku and description and departement of materia
            material.setTitre(materials.getTitre());
            material.setImages(materials.getImages());
            material.setSku(materials.getSku());
            material.setDescription(materials.getDescription());
            material.setDepartement(materials.getDepartement());
            materialRepository.save(material);        
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
    //delete material by id (only disponible materials)
    @DeleteMapping("/materials/delete/{id}")
    public String deleteMaterial(@PathVariable int id) {
        //check if material is disponible
        Material material = materialRepository.findById(id).orElse(null);
        if(material == null) {
            return "Material not found";
        }
        if(!material.isDisponible()) {
            return "Material is not disponible";
        }
        materialRepository.deleteById(id);
        return "Material deleted";
    }
    //http://localhost:8080/api/v1/auth/GetMaterials
    @GetMapping("/GetMaterials")
    public List<Material> getMaterials() {
        return materialRepository.findAll();
    }
    //switching the boolean !
    @PutMapping("/materials/reserve")
    public ResponseEntity<String> reserveMaterial(@RequestBody ReserveDto reserveDto) {
        try {
            Material material = materialRepository.findById(reserveDto.getMaterialId()).orElse(null);
            User user = repository.findById(reserveDto.getUserId()).orElse(null);
            if (material == null || user == null) {
                return ResponseEntity.notFound().build();
            }
            if (!material.isDisponible()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Material is already reserved");
            }
            if (reserveDto.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Quantity must be greater than 0");
            }
            if (reserveDto.getQuantity() > material.getQuantite()) {
                return ResponseEntity.badRequest().body("Material quantity is not sufficient");
            }
            //reserveDto has return_date
            Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(reserveDto.getReturn_date().toString());
            Reservation reservation = new Reservation();
            reservation.setDueDate(dueDate);
            reservation.setReservationDate(new Date());
            reservation.setMaterial(material);
            reservation.setUser(user);
            reservation.setQuantity(reserveDto.getQuantity());
            reservationRepository.save(reservation);
            material.setQuantite(material.getQuantite() - reserveDto.getQuantity());
            if(material.getQuantite() == 0)
            material.setDisponible(false);
            materialRepository.save(material);
            return ResponseEntity.ok("Material Reserved");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
    //All Reservation
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> getReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        List<ReservationDto> reservationDtos = new ArrayList<>();
        for (Reservation reservation : reservations) {
            User user = repository.findById(reservation.getUser().getId()).orElse(null);
            Material material = materialRepository.findById(reservation.getMaterial().getId()).orElse(null);
            ReservationDto reservationDto = new ReservationDto();
            reservationDto.setReservation(reservation);
            reservationDto.setUser(user);
            reservationDto.setMaterial(material);
            reservationDtos.add(reservationDto);
        }
        return ResponseEntity.ok(reservationDtos);
    }


    //delete reservation by id
    @DeleteMapping("reservations/{id}")
    public String deleteReservation(@PathVariable int id) {
    	reservationRepository.deleteById(id);
    	return "deleted !";
    }
    
    //reserved materials 
    @GetMapping("/materials/indisponible")
    public ResponseEntity<List<String>> getIndisponibleMaterialNames() {
        List<Material> materials = materialRepository.findByDisponible(false);
        List<String> materialNames = new ArrayList<>();
        for (Material material : materials) {
            materialNames.add(material.getTitre());
        }
        return ResponseEntity.ok(materialNames);
    }
     
    //switch available
    @PutMapping("/materials/available/{id}")
    public ResponseEntity<String> makeMaterialAvailable(@PathVariable Integer id) {
        Material material = materialRepository.findById(id).orElse(null);
        if (material == null) {
            return ResponseEntity.notFound().build();
        }
        material.setDisponible(true);
        materialRepository.save(material);
        return ResponseEntity.ok("Material is available again");
    }
    
    /////////////////////////Pannier////////////////////////////
    @PostMapping("/panniers")
    public ResponseEntity<Pannier> createPannier(@RequestBody PannierDto pannierDto) {
        try {
            User user = repository.findById(pannierDto.getUserId()).orElse(null);
            if (user == null) {
                System.out.println("no users");
                return null;
            }
            Optional<Material> material = materialRepository.findById(pannierDto.getMaterialId());
            if(material.isEmpty()){
                System.out.println("no materials");
                return null;
            }
            List<Pannier> pannierList = pannierRepository.findAllByUser(user);
            for (Pannier p : pannierList) {
                if (p.getMaterials().contains(material.get())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
            Pannier pannier = new Pannier();
            pannier.setUser(user);
            List<Material> materialList = new ArrayList<>();

                materialList.add(material.get());

            pannier.setMaterials(materialList);
            Pannier savedPannier = pannierRepository.save(pannier);
            return ResponseEntity.ok(savedPannier);
        } catch (Exception ex) {
            System.out.println("Error saving Pannier"+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //gett all panniers by user id
    @GetMapping("/panniers/{userId}")
    public ResponseEntity<List<Material>> getPannierByUserId(@PathVariable Integer userId) {
        try {
            User user = repository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            List<Pannier> pannier = pannierRepository.findAll();
            List<Material> materials = new ArrayList<>();
            for (Pannier p : pannier) {
                if (p.getUser().getId().equals(userId)) {
                    materials.addAll(p.getMaterials());
                }
            }
            return ResponseEntity.ok(materials);
        } catch (Exception ex) {
            System.out.println("Error retrieving materials for user " + userId + ": " + ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //delete panneir by user id
    @Transactional
    @DeleteMapping("panneir/delete/{userId}")
    public String deletePannier(@PathVariable int userId){
        try {
            User user = repository.findById(userId).orElse(null);
            if(user != null)
            pannierRepository.deleteByUser(user);
            return "Pannier Deleted !";
        }
        catch(Exception e) {
        	return e.getMessage();
        }
    }
    //delete pannier by material id and user id
    @Transactional
    @DeleteMapping("panneir/delete/{userId}/{materialId}")
    public String deletePannier(@PathVariable int userId, @PathVariable int materialId){
        try {
            User user = repository.findById(userId).orElse(null);
            Material material = materialRepository.findById(materialId).orElse(null);
            if(user == null || material == null)
                return "User or Material not found !";
                List<Pannier> panniers = pannierRepository.findAll();
            for (Pannier p : panniers) {
                if (p.getMaterials().contains(material) && p.getUser().getId().equals(userId) ) {
                    p.getMaterials().remove(material);
                    pannierRepository.save(p);
                }
            }
            return "Pannier Deleted !";
        }
        catch(Exception e) {
        	return e.getMessage();
        }
    }

}