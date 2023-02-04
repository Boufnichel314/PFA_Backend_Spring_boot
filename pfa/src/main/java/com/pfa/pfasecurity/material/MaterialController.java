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
    @PostMapping ("/materials/reserve")
    public ResponseEntity<String> reserveMaterial(@RequestBody ReserveDto reserveDto) {
        try {
            Material material = materialRepository.findById(reserveDto.getMaterialId()).orElse(null);
            User user = repository.findById(reserveDto.getUserId()).orElse(null);
            if (user == null || material==null) {
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
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if(reserveDto.getDateRetour().before(calendar.getTime())){
                return ResponseEntity.badRequest().body("Invalid Date ");
            }

            Date dueDate = calendar.getTime();
            Reservation reservation = new Reservation();
            reservation.setDueDate(reserveDto.getDateRetour());
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
    @GetMapping("/GetMaterial/{id}")
    public Optional<Material> GetAllMaterials(@PathVariable Integer id){return materialRepository.findById(id);}
    @GetMapping("/materials/indisponible")
    public ResponseEntity<List<Material>> getIndisponibleMaterialNames() {
        List<Material> materials = materialRepository.findByDisponible(false);
        List<Material> materialIndisponible = new ArrayList<>();
        for (Material material : materials) {
            materialIndisponible.add(material);
        }
        return ResponseEntity.ok(materialIndisponible);
    }
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
    @DeleteMapping("/materials/delete/{id}")
    public String deleteMaterial(@PathVariable int id) {
        //check if material is disponible
        Material material = materialRepository.findById(id).orElse(null);
        if(material == null) {
            return "Material not found";
        }
        if(!material.isDisponible()) {
            return "Material is already reserved, wait for it !";
        }
        materialRepository.deleteById(id);
        return "Material deleted";
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
        material.setQuantite(materials.getQuantite());
        material.setTags(materials.getTags());
        materialRepository.save(material);
        return ResponseEntity.ok("materielles est ajoutées");
    }
}
