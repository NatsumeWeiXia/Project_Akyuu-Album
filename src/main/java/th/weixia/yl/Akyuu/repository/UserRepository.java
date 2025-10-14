package th.weixia.yl.Akyuu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.weixia.yl.Akyuu.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}