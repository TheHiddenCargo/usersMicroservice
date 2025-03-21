package arsw.tamaltolimense.playermanager.model;

import arsw.tamaltolimense.playermanager.exception.UserException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


import lombok.Getter;
import lombok.Setter;


@Getter
@Document(collection = "Users")
public class User {



    @Id
    @NotNull
    private final String email;

    @Indexed(unique = true)
    @Setter private String nickName;

    private int balance;

    @Setter private String imagePath;

    private final List<Bid> bids;

    public User(String email, String nickName){
        this.email = email;
        this.nickName = nickName;
        balance = 0;
        imagePath = "";
        bids = new ArrayList<>();
    }

    public void transaction(int amount) throws UserException{
        if(this.balance + amount < 0) throw new UserException(UserException.NEGATIVE_BALANCE);
        this.balance += amount;
    }

    public void registerBet(Bid bid) {
        bids.add(bid);
    }

    public void cleanBids(){
        bids.clear();
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((nickName == null) ? 0 : nickName.hashCode());
        result = prime * result + balance;
        result = prime * result + bids.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || obj.getClass() != this.getClass()) return false;
        User user = (User) obj;
        boolean equalBids = true;
        if(this.bids.size() != user.bids.size()) return false;
        for(int i = 0; i < this.bids.size(); i++){
            if(!this.bids.get(i).equals(user.bids.get(i))){
                equalBids = false;
                break;
            }
        }
        return (this.nickName == null ? user.getNickName() == null : this.nickName.equals(user.getNickName()))
                && (this.email == null ? user.getEmail() == null :this.email.equals(user.getEmail()))
                && (this.balance == 0 ? user.getBalance() == 0 :this.balance == user.getBalance())
                && equalBids;
    }

}
