package hong.gom.springbootsecuritypractice4.repository;

import hong.gom.springbootsecuritypractice4.dto.SpUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpUserRepository extends JpaRepository<SpUser, Long> {

    Optional<SpUser> findSpUserByEmail(String email);

}
