package com.pfa.pfasecurity.material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class MaterialService {
	
	@Autowired
	private MaterialRepo materialrepo;
	
	public String uploadImage(MultipartFile file, String name, String description) throws IOException {
	    Material material = materialrepo.save(Material.builder()
	            .name(name)
	            .description(description)
	            .image(ImageUtils.compressImage(file.getBytes()))
	            .build());
	    if (material != null) return "File Uploaded";
	    else return "Error";
	}
	
	public byte[] downloadImage(String fileName){
        Optional<Material> dbImageData = materialrepo.findByName(fileName);
        byte[] images=ImageUtils.decompressImage(dbImageData.get().getImage());
        return images;
    }
	
	public List<Material> getAllMaterialsWithImages(){
	    List<Material> materials = materialrepo.findAll();
	    for(Material material : materials){
	        byte[] image = downloadImage(material.getName());
	        material.setImage(image);
	    }
	    return materials;
	}
	
	@Transactional
	public void reserveMaterial(int id) {
	    Material material = materialrepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Material not found with id: " + id));
	    if (material.isDispo()) {
	        material.setIsDispo(false);
	        materialrepo.save(material);
	    } else {
	        throw new IllegalStateException("Material is not available for reservation");
	    }
	}
	
	public void addKeyword(int id, String keyword) {
        Material material = materialrepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Material not found"));
        List<String> keywords = material.getMots_cle();
        if(keywords == null){
            keywords = new ArrayList<>();
        }
        keywords.add(keyword);
        material.setMots_cle(keywords);
        materialrepo.save(material);
    }


}
