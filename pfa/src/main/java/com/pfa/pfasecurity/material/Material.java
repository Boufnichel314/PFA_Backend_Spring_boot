package com.pfa.pfasecurity.material;
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
	private String description;
	public Material(String name, byte[] image, String description) {
		super();
		this.name = name;
		this.image = image;
		this.description = description;
	}
	
	
	
	
	
}