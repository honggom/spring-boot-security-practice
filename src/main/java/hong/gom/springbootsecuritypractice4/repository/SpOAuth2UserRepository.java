package hong.gom.springbootsecuritypractice4.repository;

import hong.gom.springbootsecuritypractice4.dto.SpOAuth2User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpOAuth2UserRepository extends JpaRepository<SpOAuth2User, String> {

}
