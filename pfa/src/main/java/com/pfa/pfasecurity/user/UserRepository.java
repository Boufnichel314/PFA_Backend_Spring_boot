package com.pfa.pfasecurity.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  List<User> findByApproved(boolean approved);
  boolean existsByEmail(String email);
  List<User> findByRole(Role role);
  //findByRole

}