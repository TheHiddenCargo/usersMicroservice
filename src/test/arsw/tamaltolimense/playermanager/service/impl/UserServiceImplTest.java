package arsw.tamaltolimense.playermanager.service.impl;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.User;
import arsw.tamaltolimense.playermanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUser(){
        try{
            User user = new User("casbsuw@mail.com","milo45",5000,"imagen");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User registeredUser = userService.registerUser("casbsuw@mail.com","milo45",500,"imaen");

            assertNotNull(registeredUser);
            assertEquals(new User("casbsuw@mail.com","milo45",5000,"imagen"), registeredUser);

            //Verificamos que se llamo a save una vez
            verify(userRepository, times(1)).save(any(User.class));

            //Capturamos el objeto que se pasó a save y verificamos sus valores
            // Creamos un ArgumentCaptor para capturar objetos de tipo User
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            // Capturamos el argumento pasado a save()
            verify(userRepository).save(userCaptor.capture());



        }catch (UserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void shouldNotRegisterDuplicateNickName(){
        try{
            User user1 = new User("casbsuw@mail.com","milo45",5000,"imagen");
            User user2 = new User("casbsuw@mail.com","milo46",5000,"imagen");

            when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

            userService.registerUser("casbsuw@mail.com","milo45",5000,"imagen");

            fail("No exception thrown");
        }catch (UserException e) {
            assertEquals(UserException.NICK_NAME_FOUND,e.getMessage());

            verify(userRepository, times(0)).save(any(User.class));
        }
    }

    @Test
    void shouldNotRegisterDuplicateEmail(){
        try{
            User user1 = new User("casbsuw@mail.com","milo45",5000,"imagen");
            User user2 = new User("casbsuw@mail.com","milo46",5000,"imagen");

            when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

            userService.registerUser("casbsuw@mail.com","milo48",5000,"imagen");

            fail("No exception thrown");
        }catch (UserException e) {
            assertEquals(UserException.EMAIL_FOUND,e.getMessage());

            verify(userRepository, times(0)).save(any(User.class));
        }
    }

    @Test
    void shouldNotRegisterNullNickName(){
        try{
           userService.registerUser("casbsuw@mail.com",null,5000,"imagen");
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
        }
    }

    @Test
    void shouldNotRegisterEmptyNickName(){
        try{
            userService.registerUser("casbsuw@mail.com","",5000,"imagen");
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
        }
    }


    @Test
    void shouldNotRegisterNullEmail(){
        try{
            userService.registerUser(null,"milo45",5000,"imagen");
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
        }
    }

    @Test
    void shouldNotRegisterEmptyEmail(){
        try{
            userService.registerUser("   ","null",500,"isismdnj");
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
        }
    }


    @Test
    void shouldInfoUser(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",7000,"imagen");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            Map<String,String> oldInfo = userService.getUserInfo("milo45");
            assertNotNull(oldInfo);
            assertEquals("milo45", oldInfo.get("nickName"));
            userService.updatePhoto("milo45","hola.png");

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertEquals("hola.png",savedUser.getImagePath());


            Map<String,String> updatedPhoto = userService.getUserInfo("milo45");
            assertNotNull(updatedPhoto);
            assertEquals("hola.png", updatedPhoto.get("photo"));
            assertEquals(oldInfo.get("nickName"), updatedPhoto.get("nickName"));
            assertNotEquals(oldInfo.get("photo"), updatedPhoto.get("photo"));

            verify(userRepository, times(3)).findByNickName("milo45");

            userService.updateNickName("milo45","milo46");

            assertEquals("milo46",savedUser.getNickName());

            when(userRepository.findByNickName("milo46")).thenReturn(user);
            Map<String,String> updatedNickName = userService.getUserInfo("milo46");
            assertNotNull(updatedNickName);
            assertEquals("milo46", updatedNickName.get("nickName"));
            assertEquals(updatedPhoto.get("photo"), updatedNickName.get("photo"));
            assertNotEquals(oldInfo.get("nickName"), updatedNickName.get("photo"));

        }catch (UserException e){
            fail(e.getMessage());
        }
    }

    @Test
    void shouldNotFindUser(){
        try{
            userService.getUserInfo("milo45");
            verify(userRepository, times(1)).findByNickName("milo45");
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.USER_NOT_FOUND,e.getMessage());
        }
    }

    @Test
    void shouldNotUpdateNullNickName(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",500,"image");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            userService.updateNickName("milo45",null);

            fail("Should have thrown exception");

        }catch (UserException e){
            assertEquals(UserException.NULL_NICK_NAME,e.getMessage());
            verify(userRepository, times(0)).save(any(User.class));

        }
    }

    @Test
    void shouldNotUpdateEmptyNickName(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",500,"image");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            userService.updateNickName("milo45","");

            fail("Should have thrown exception");

        }catch (UserException e){
            assertEquals(UserException.NULL_NICK_NAME,e.getMessage());
            verify(userRepository, times(0)).save(any(User.class));

        }
    }

    @Test
    void shouldNotUpdateDuplicatelNickName(){
        try{
            User user1 = new User("casbsuw@mail.com","milo45",5000,"image");
            User user2 = new User("casbsuw@mail.com","milo46",5000,"imagen");

            when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

            userService.updateNickName("milo45","milo46");

            fail("No exception thrown");

        }catch (UserException e){
            assertEquals(UserException.NICK_NAME_FOUND,e.getMessage());
            verify(userRepository, times(0)).save(any(User.class));

        }
    }

    @Test
    void shouldNotUpdatePhoto(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",500,"image");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            userService.updatePhoto("milo45",null);

            fail("No exception thrown");


        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
            verify(userRepository, times(0)).save(any(User.class));
        }
    }

   @Test
   void shouldDoTransaction(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",500,"image");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            userService.transaction("milo45",50000);
            userService.transaction("milo45",-10000);

            assertEquals(40500,userService.getUserBalance("milo45"));

            verify(userRepository, times(2)).save(any(User.class));
            verify(userRepository, times(3)).findByNickName("milo45");

        }catch (UserException e){
            fail(e.getMessage());
        }
   }

}