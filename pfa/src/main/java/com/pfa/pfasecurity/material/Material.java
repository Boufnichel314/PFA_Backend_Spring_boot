package com.pfa.pfasecurity.material;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private byte[] image;
	private boolean isDispo = false;
	private List<String> mots_cle;
	private String description;
	public Material(String name, byte[] image, String description, boolean isDispo, List<String> mots_cle) {
		super();
		this.name = name;
		this.image = image;
		this.description = description;
		this.isDispo = isDispo;
		this.mots_cle = mots_cle;
	}
	public void setIsDispo(boolean b) {
		this.isDispo = b;
	}
	
	
	
	
	
}