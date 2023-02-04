package com.pfa.pfasecurity.material;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.pfasecurity.auth.AuthenticationService;
import com.pfa.pfasecurity.pannier.pannierRepository;
import com.pfa.pfasecurity.reservation.reservationRepository;
import com.pfa.pfasecurity.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MaterialController {
	private final MaterialRepo materialRepository;
	
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
    @GetMapping("/GetMaterials")
    public List<Material> getMaterials() {
        return materialRepository.findAll();
    }
}
