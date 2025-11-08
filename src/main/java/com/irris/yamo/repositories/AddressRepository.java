package com.irris.yamo.repositories;


import com.irris.yamo.entities.Adresse;
import com.irris.yamo.entities.UserYamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Adresse, Long> {
    List<Adresse> findByUser(UserYamo user);
}
