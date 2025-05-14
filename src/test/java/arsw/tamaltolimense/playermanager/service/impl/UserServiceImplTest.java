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

            User registeredUser = userService.registerUser("casbsuw@mail.com","milo45",500,"imagen");

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
    void shouldNotRegisterNegativeBalance(){
        try{
            userService.registerUser(" ssdbdbd","hola",-5000,"isismdnj");
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_BALANCE,e.getMessage());
        }
    }


    @Test
    void shouldInfoUser(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",7000,"imagen");
            when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

            Map<String,String> oldInfo= userService.getUserInfo(user.getEmail());

            assertEquals("milo45",oldInfo.get("nickname"));
            assertEquals("imagen",oldInfo.get("photo"));

            userService.updateNickName("casbsuw@mail.com","ccastano47");
            userService.updatePhoto("casbsuw@mail.com","chao");

            oldInfo = userService.getUserInfo(user.getEmail());

            assertEquals("ccastano47",oldInfo.get("nickname"));
            assertEquals("chao",oldInfo.get("photo"));

            verify(userRepository, times(6)).findByEmail("casbsuw@mail.com");
            verify(userRepository, times(2)).save(any(User.class));



        }catch (UserException e){
            fail(e.getMessage());
        }
    }

    @Test
    void shouldNotFindUser(){
        try{
            userService.getUserInfo("casbsuw@mail.com");
            verify(userRepository, times(1)).findByEmail("casbsuw@mail.com");
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.USER_NOT_FOUND,e.getMessage());
        }
    }

    @Test
    void shouldNotUpdateNullNickName(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",500,"image");
            when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
            userService.updateNickName("casbsuw@mail.com",null);

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
            when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
            userService.updateNickName("casbsuw@mail.com","");

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
            when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

            when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

            userService.updateNickName("casbsuw@mail.com","milo46");

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
            when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
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
            when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            userService.transaction("casbsuw@mail.com",50000);
            userService.transaction("casbsuw@mail.com",-10000);

            assertEquals(40500,userService.getUserBalance("casbsuw@mail.com"));

            verify(userRepository, times(2)).save(any(User.class));
            verify(userRepository, times(3)).findByEmail("casbsuw@mail.com");

        }catch (UserException e){
            fail(e.getMessage());
        }
    }

    @Test
    void shouldUpdateConcurrent() {
        User user = new User("casbsuw@mail.com", "milo45",500,"image");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        Thread thread1 = new Thread(() -> {
            try {
                userService.updateNickName("casbsuw@mail.com", "ccastano42");
                userService.transaction("casbsuw@mail.com", -100);
            } catch (UserException e){
                fail(e.getMessage());
            }
        });

        Thread thread2 = new Thread(() -> {
            try {

                userService.transaction("casbsuw@mail.com", 1000);
            }catch (UserException e){
                fail(e.getMessage());
            }
        });

        Thread thread3 = new Thread(() -> {
            try {
                userService.transaction("casbil.com", 1000);
            }catch (UserException e){
                System.out.println("casbil.com "+e.getMessage());
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();


        try{
            thread1.join();
            thread2.join();
            thread3.join();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            verify(userRepository, times(3)).save(userCaptor.capture());

            User capturedUser = userCaptor.getAllValues().get(1);
            Map<String,String> userUpdatedInfo = userService.getUserInfo(user.getEmail());
            int balance = userService.getUserBalance(user.getEmail());
            assertEquals("ccastano42",userUpdatedInfo.get("nickname"));
            assertEquals(1400,balance);
            assertEquals("ccastano42",capturedUser.getNickName());
            assertEquals(1400,capturedUser.getBalance());
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }catch (UserException e){
            fail(e.getMessage());
        }
    }

    @Test
    void shouldConcurrentTransaction(){
        User user = new User("casbsuw@mail.com", "milo45",2000,"image");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);


        Thread thread1 = new Thread(() -> {
            try {
                userService.transaction("casbsuw@mail.com", 1000);
            } catch (UserException e){
                assertEquals(UserException.NEGATIVE_BALANCE,e.getMessage());
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                userService.transaction("casbsuw@mail.com", -2500);
            } catch (UserException e){
                assertEquals(UserException.NEGATIVE_BALANCE,e.getMessage());
            }
        });

        thread1.start();
        thread2.start();

        try{
            thread1.join();
            thread2.join();
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, atLeastOnce()).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            int balance = userService.getUserBalance(user.getEmail());
            assertTrue(balance == 500 || balance == 3000);
            assertTrue(capturedUser.getBalance() == 500 || capturedUser.getBalance() == 3000);

        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }catch (UserException e){
            fail(e.getMessage());
        }
    }

    @Test
    void shouldNickNameTransaction(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",500,"image");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            userService.transactionByNickname("milo45",50000);
            userService.transactionByNickname("milo45",-10000);

            assertEquals(40500,userService.getUsernickNameBalance("milo45"));

        }catch (UserException e){
            fail(e.getMessage());
        }
    }

    @Test
    void shouldNotNullNickTransaction(){
        try{
            new User("casbsuw@mail.com", "milo45",500,"image");
            userService.transactionByNickname("milo45",100);

            fail("Not exception");
        }catch (UserException e){
            assertEquals(UserException.USER_NOT_FOUND,e.getMessage());
        }
    }

    @Test
    void shouldNotNegativeTransaction(){
        try{
            User user = new User("casbsuw@mail.com", "milo45",500,"image");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            userService.transactionByNickname("milo45",-501);
            fail("Not exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_BALANCE,e.getMessage());
        }
    }

    @Test
    void shouldNotNullBalance(){
        try{
            new User("casbsuw@mail.com", "milo45",500,"image");
            userService.getUsernickNameBalance("milo45");

            fail("Not exception");
        }catch (UserException e){
            assertEquals(UserException.USER_NOT_FOUND,e.getMessage());
        }
    }

}