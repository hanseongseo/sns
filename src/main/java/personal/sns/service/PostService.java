package personal.sns.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.sns.exception.ErrorCode;
import personal.sns.exception.SnsApplicationException;
import personal.sns.model.Post;
import personal.sns.model.entity.LikeEntity;
import personal.sns.model.entity.PostEntity;
import personal.sns.model.entity.UserEntity;
import personal.sns.repository.LikeEntityRepository;
import personal.sns.repository.PostEntityRepository;
import personal.sns.repository.UserEntityRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;

    private final UserEntityRepository userEntityRepository;

    private final LikeEntityRepository likeEntityRepository;

    @Transactional
    public void create(String title, String body, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));
        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(String userName, Integer postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));
        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntityRepository.delete(postEntity);
    }

    @Transactional
    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    @Transactional
    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));
        // check already liked
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already liked post %d", userName, postId));
        });
        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
    }

    @Transactional
    public int likeCount(Integer postId) {
        return likeEntityRepository.countLikeEntitiesByPostId(postId);
    }
}
