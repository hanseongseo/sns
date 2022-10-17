package personal.sns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal.sns.model.entity.LikeEntity;
import personal.sns.model.entity.PostEntity;
import personal.sns.model.entity.UserEntity;

import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}
