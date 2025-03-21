package arsw.tamaltolimense.playermanager.service.impl;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
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
            User user = new User("casbsuw@mail.com","milo45");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User registeredUser = userService.registerUser("casbsuw@mail.com","milo45");

            assertNotNull(registeredUser);
            assertEquals(new User("casbsuw@mail.com","milo45"), registeredUser);

            //Verificamos que se llamo a save una vez
            verify(userRepository, times(1)).save(any(User.class));

            //Capturamos el objeto que se pasó a save y verificamos sus valores
            // Creamos un ArgumentCaptor para capturar objetos de tipo User
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            // Capturamos el argumento pasado a save()
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertEquals(registeredUser, savedUser);
            assertEquals(new User("casbsuw@mail.com","milo45") , savedUser);
        }catch (UserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void shouldNotRegisterDuplicateNickName(){
        try{
            User user1 = new User("casbsuw@mail.com","milo45");
            User user2 = new User("casbsuw@mail.com","milo46");

            when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

            userService.registerUser("casbsuw@mail.com","milo45");

            fail("No exception thrown");
        }catch (UserException e) {
            assertEquals(UserException.NICK_NAME_FOUND,e.getMessage());

            verify(userRepository, times(0)).save(any(User.class));
        }
    }

    @Test
    void shouldNotRegisterNullNickName(){
        try{
           userService.registerUser("casbsuw@mail.com",null);
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
        }
    }

    @Test
    void shouldNotRegisterNullEmail(){
        try{
            userService.registerUser(null,"null");
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
        }
    }


    @Test
    void shouldInfoUser(){
        try{
            User user = new User("casbsuw@mail.com", "milo45");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            String[] oldInfo = userService.getUserInfo("milo45");
            assertNotNull(oldInfo);
            assertEquals("milo45", oldInfo[0]);
            assertEquals("", oldInfo[1]);
            userService.updatePhoto("milo45","hola.png");

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertEquals("hola.png",savedUser.getImagePath());


            String[] updatedPhoto = userService.getUserInfo("milo45");
            assertNotNull(updatedPhoto);
            assertEquals("hola.png", updatedPhoto[1]);
            assertEquals(oldInfo[0], updatedPhoto[0]);
            assertNotEquals(oldInfo[1], updatedPhoto[1]);

            verify(userRepository, times(3)).findByNickName("milo45");

            userService.updateNickName("milo45","milo46");

            assertEquals("milo46",savedUser.getNickName());

            when(userRepository.findByNickName("milo46")).thenReturn(user);
            String[] updatedNickName = userService.getUserInfo("milo46");
            assertNotNull(updatedNickName);
            assertEquals("milo46", updatedNickName[0]);
            assertEquals(updatedPhoto[1], updatedNickName[1]);
            assertNotEquals(oldInfo[0], updatedNickName[1]);

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
            User user = new User("casbsuw@mail.com", "milo45");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            userService.updateNickName("milo45",null);

            fail("Should have thrown exception");

        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
            verify(userRepository, times(0)).save(any(User.class));

        }
    }

    @Test
    void shouldNotUpdateEmptyNickName(){
        try{
            User user = new User("casbsuw@mail.com", "milo45");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            userService.updateNickName("milo45","");

            fail("Should have thrown exception");

        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
            verify(userRepository, times(0)).save(any(User.class));

        }
    }

    @Test
    void shouldNotUpdateDuplicatelNickName(){
        try{
            User user1 = new User("casbsuw@mail.com","milo45");
            User user2 = new User("casbsuw@mail.com","milo46");

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
            User user = new User("casbsuw@mail.com", "milo45");
            when(userRepository.findByNickName(user.getNickName())).thenReturn(user);
            userService.updatePhoto("milo45",null);

            fail("No exception thrown");


        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
            verify(userRepository, times(0)).save(any(User.class));
        }
    }

    @Test
    void shouldDoDeposit(){
        try{
            User savedUser;
            User user = new User("casbsuw@mail.com", "milo45");
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            userService.deposit("milo45", 45000);
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            savedUser = userCaptor.getValue();

            assertEquals(user, savedUser);
            assertEquals(45000, userService.getUserBalance("milo45"));

            verify(userRepository, times(1)).save(any(User.class));
            verify(userRepository, times(2)).findByNickName("milo45");

        }catch (UserException e){
            fail(e.getMessage());
        }
    }

    @Test
    void shouldNotDeposit0(){
        try{
            when(userRepository.findByNickName("milo45")).thenReturn(new User("casbsuw@mail.com", "milo45"));
            userService.deposit("milo45", 0);
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_VALUE,e.getMessage());
        }

    }

    @Test
    void shouldNotDepositNegative(){
        try{
            when(userRepository.findByNickName("milo45")).thenReturn(new User("casbsuw@mail.com", "milo45"));
            userService.deposit("milo45", -45);
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_VALUE,e.getMessage());
        }

    }

    @Test
    void shouldWithdraw(){
        try{

            User user = new User("casbsuw@mail.com", "milo45");
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            userService.deposit("milo45", 45000);

            userService.withdraw("milo45", 40000);
            assertEquals(5000, userService.getUserBalance("milo45"));
            verify(userRepository, times(2)).save(any(User.class));
            verify(userRepository, times(3)).findByNickName("milo45");

        }catch (UserException e){
            fail(e.getMessage());
        }
    }

    @Test
    void shouldNotWithdraw0(){
        try{

            when(userRepository.findByNickName("milo45")).thenReturn(new User("casbsuw@mail.com", "milo45"));
            userService.withdraw("milo45", 0);
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_VALUE,e.getMessage());
        }

    }

    @Test
    void shouldNotWithdrawNegative(){
        try{
            when(userRepository.findByNickName("milo45")).thenReturn(new User("casbsuw@mail.com", "milo45"));
            userService.withdraw("milo45", -45);
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_VALUE,e.getMessage());
        }

    }

    @Test
    void shouldNotWithdrawMore(){
        User user = new User("casbsuw@mail.com", "milo45");
        when(userRepository.findByNickName("milo45")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        try{
            userService.deposit("milo45", 50000);
        }catch (UserException e){
            fail(e.getMessage());
        }

        try{
            userService.withdraw("milo45", 550000);
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_BALANCE,e.getMessage());
            assertEquals(50000, user.getBalance());
            verify(userRepository, times(1)).save(any(User.class));
            verify(userRepository, times(2)).findByNickName("milo45");
        }
    }

    @Test
    void shouldBet(){
        try{
            User user = new User("casbsuw@mail.com", "milo45");
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            userService.deposit("milo45", 50000);
            userService.bet("milo45",new Bid("1",5000));
            userService.bet("milo45",new Bid("1",5000));
            userService.bet("milo45",new Bid("1",5000));

            assertEquals(3, userService.getBids("milo45").size());
            assertEquals("1", userService.getBids("milo45").getFirst().getContainerId());
            assertEquals(5000,userService.getBids("milo45").getFirst().getAmount());
            assertEquals(35000,userService.getUserBalance("milo45"));

            verify(userRepository, times(4)).save(any(User.class));
            verify(userRepository, times(8)).findByNickName("milo45");

        }catch (UserException e){
            fail(e.getMessage());
        }

    }

    @Test
    void shouldNotBet0(){
        User user = new User("casbsuw@mail.com", "milo45");
        try{
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            userService.bet("milo45", new Bid("1",0));
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_VALUE,e.getMessage());
            assertEquals(0, user.getBalance());
            assertEquals(0,user.getBids().size());
            verify(userRepository, times(0)).save(any(User.class));
            verify(userRepository, times(1)).findByNickName("milo45");
        }

    }

    @Test
    void shouldNotBetNegative(){
        User user = new User("casbsuw@mail.com", "milo45");
        try{
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            userService.bet("milo45", new Bid("1",-45));
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_VALUE,e.getMessage());
            assertEquals(0, user.getBalance());
            assertEquals(0,user.getBids().size());
            verify(userRepository, times(0)).save(any(User.class));
            verify(userRepository, times(1)).findByNickName("milo45");
        }
    }

    @Test
    void shouldNotBetMore(){
        User user = new User("casbsuw@mail.com", "milo45");
        when(userRepository.findByNickName("milo45")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        try{
            userService.deposit("milo45", 50000);
        }catch (UserException e){
            fail(e.getMessage());
        }

        try{
            userService.bet("milo45", new Bid("1",55000));
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NEGATIVE_BALANCE,e.getMessage());
            assertEquals(50000, user.getBalance());
            assertEquals(0,user.getBids().size());
            verify(userRepository, times(1)).save(any(User.class));
            verify(userRepository, times(2)).findByNickName("milo45");
        }
    }

    @Test
    void shouldNotRegisterNullBid(){
        User user = new User("casbsuw@mail.com", "milo45");
        try{
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            userService.bet("milo45", null);
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_VALUE,e.getMessage());
            assertEquals(0, user.getBalance());
            assertEquals(0,user.getBids().size());
            verify(userRepository, times(0)).save(any(User.class));
            verify(userRepository, times(0)).findByNickName("milo45");
        }
    }

    @Test
    void shouldNotRegisterNullContainer(){
        User user = new User("casbsuw@mail.com", "milo45");
        try{
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            userService.bet("milo45", new Bid(null,-45));
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_CONTAINER,e.getMessage());
            assertEquals(0, user.getBalance());
            assertEquals(0,user.getBids().size());
            verify(userRepository, times(0)).save(any(User.class));
            verify(userRepository, times(0)).findByNickName("milo45");
        }
    }

    @Test
    void shouldNotRegisterEmptyContainer(){
        User user = new User("casbsuw@mail.com", "milo45");
        try{
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            userService.bet("milo45", new Bid("",-45));
            fail("Should have thrown exception");
        }catch (UserException e){
            assertEquals(UserException.NULL_CONTAINER,e.getMessage());
            assertEquals(0, user.getBalance());
            assertEquals(0,user.getBids().size());
            verify(userRepository, times(0)).save(any(User.class));
            verify(userRepository, times(0)).findByNickName("milo45");
        }
    }

    @Test
    void shouldCleanBids(){
        try{
            User user = new User("casbsuw@mail.com", "milo45");
            when(userRepository.findByNickName("milo45")).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            userService.deposit("milo45", 50000);
            userService.bet("milo45",new Bid("1",5000));
            userService.bet("milo45",new Bid("1",5000));
            userService.bet("milo45",new Bid("1",5000));

            userService.cleanBids("milo45");

            assertEquals(0, userService.getBids("milo45").size());

            assertEquals(35000,userService.getUserBalance("milo45"));

            verify(userRepository, times(5)).save(any(User.class));
            verify(userRepository, times(7)).findByNickName("milo45");

        }catch (UserException e){
            fail(e.getMessage());
        }
    }



}