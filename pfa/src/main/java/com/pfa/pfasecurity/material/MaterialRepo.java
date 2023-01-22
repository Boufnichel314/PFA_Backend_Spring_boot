package com.pfa.pfasecurity.material;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepo extends JpaRepository<Material, Integer> {
		Optional<Material> findByName(String name);
}
