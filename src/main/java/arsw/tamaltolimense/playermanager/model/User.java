package arsw.tamaltolimense.playermanager.model;

import arsw.tamaltolimense.playermanager.exception.UserException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;


import lombok.Getter;
import lombok.Setter;


@Getter
@Document(collection = "Users")
public class User {



    @Id
    @NotNull

    private final String email;

    @Setter
    @Indexed(unique = true)
    private String nickName;

    private int balance;

    @Setter
    private String imagePath;



    public User(String email, String nickName, int balance, String imagePath){
        this.email = email;
        this.nickName = nickName;
        this.balance = balance;
        this.imagePath = imagePath;
    }

    public void transaction(int amount){
        this.balance += amount;


    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((nickName == null) ? 0 : nickName.hashCode());
        result = prime * result + balance;
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || obj.getClass() != this.getClass()) return false;
        User user = (User) obj;
        return (this.nickName == null ? user.getNickName() == null : this.nickName.equals(user.getNickName()))
                && (this.email == null ? user.getEmail() == null :this.email.equals(user.getEmail()))
                && (this.balance == 0 ? user.getBalance() == 0 :this.balance == user.getBalance());
    }

    public String getNickName() {
        return nickName;
    }

    public int getBalance() {
        return balance;
    }


    public String getEmail() {
        return email;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
