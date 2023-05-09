package com.tpe.repository;

import com.tpe.domain.User;
import com.tpe.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {//entity classının ismini vermem lazım oda user
    // ve unıq olan fieldinın yanı id nin de data typeini yazıyorum oda long

    Optional<User> findByUserName(String userName) throws ResourceNotFoundException;
    //security katmanında butun ogın işlemlerini unıq olan datam username uzerinden yapacgım için
    // bu repository de ki su useri bul diye bir request gelecekse findById olarak gelmez findByUserName olarak gelir.

    
    // UserService gibi bir clasim olmayacagi icin ( UserDetailService ) exception kismini
    // burada handle ettik.
}
