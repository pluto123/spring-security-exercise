package tw.com.pluto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.pluto.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
