package org.tutorial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tutorial.pojo.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

}
