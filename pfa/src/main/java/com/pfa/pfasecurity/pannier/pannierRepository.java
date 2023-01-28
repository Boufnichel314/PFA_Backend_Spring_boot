package com.pfa.pfasecurity.pannier;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pfa.pfasecurity.user.User;

public interface pannierRepository extends JpaRepository<Pannier, Integer> {

	public void deleteByUser(User user);    
	
}
