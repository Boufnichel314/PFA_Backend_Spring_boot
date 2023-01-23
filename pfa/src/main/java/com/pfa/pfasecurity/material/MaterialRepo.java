package com.pfa.pfasecurity.material;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialRepo extends JpaRepository<Material, Integer> {
		Optional<Material> findByName(String name);
		
		@Query("SELECT m FROM Material m WHERE CONCAT(',', m.mots_cle, ',') LIKE CONCAT('%,', :keyword, ',%')")
	    List<Material> findByMots_cle(@Param("keyword") String keyword);
		
}
