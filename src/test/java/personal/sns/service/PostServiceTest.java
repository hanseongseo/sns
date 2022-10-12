package personal.sns.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import personal.sns.exception.ErrorCode;
import personal.sns.exception.SnsApplicationException;
import personal.sns.fixture.PostEntityFixture;
import personal.sns.fixture.UserEntityFixture;
import personal.sns.model.entity.PostEntity;
import personal.sns.model.entity.UserEntity;
import personal.sns.repository.PostEntityRepository;
import personal.sns.repository.UserEntityRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    //    Create Post --start
    @Test
    void 포스트작성이_성공한경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));

    }

    @Test
    void 포스트작성시_요청한유저가_존재하지않는경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }
    //    Create Post --end

    //    Modify Post --start
    @Test
    void 포스트수정이_성공한경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    void 포스트수정시_포스트가_존재하지_않는경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정시_권한이_없는경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();
        UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }
    //    Modify Post --end

    //    Delete Post --start
    @Test
    void 포스트삭제_성공한경우() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.delete(userName, postId));
    }

    @Test
    void 포스트삭제시_포스트가_존재하지_않는경우() {

        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.delete(userName, postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트삭제시_권한이_없는경우() {

        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.delete(userName, postId));
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }
    //    Delete Post --end

    //    list Post --start
    @Test
    void 피드목록_성공한경우() {
        Pageable pageable = mock(Pageable.class);

        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 내_피드목록_성공한경우() {
        Pageable pageable = mock(Pageable.class);
        UserEntity user = mock(UserEntity.class);
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findAllByUser(user, pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.my("", pageable));
    }
    //    list Post --end
}
