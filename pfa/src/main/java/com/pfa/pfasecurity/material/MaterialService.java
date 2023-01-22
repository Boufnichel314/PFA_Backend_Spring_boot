package com.pfa.pfasecurity.material;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

}
