package com.pfa.pfasecurity.pannier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.pfasecurity.material.Material;
import com.pfa.pfasecurity.material.MaterialRepo;
import com.pfa.pfasecurity.user.User;
import com.pfa.pfasecurity.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PannierController {
	private final UserRepository repository;
	private final MaterialRepo materialRepository;
    private final pannierRepository pannierRepository;
	
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
}
