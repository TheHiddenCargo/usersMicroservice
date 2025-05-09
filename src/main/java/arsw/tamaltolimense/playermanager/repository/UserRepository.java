package arsw.tamaltolimense.playermanager.repository;

import arsw.tamaltolimense.playermanager.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    User findByNickName(String nickName); // Añadir este método para búsqueda por nickname
    void deleteByEmail(String email);
}