package arsw.tamaltolimense.playermanager.model;

import arsw.tamaltolimense.playermanager.UserException;
import com.mongodb.DuplicateKeyException;
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

    public static final char DEPOSIT = 'd';
    public static final char WITHDRAW = 'w';

    @Id
    @NotNull
    private final String email;

    @Indexed(unique = true)
    @Setter private String nickName;

    private int balance;

    @Setter private String imagePath;

    private final List<Bid> bids;

    public User(String email, String nickName) throws UserException, DuplicateKeyException {
        if(nickName == null || nickName.isEmpty()) throw new UserException(UserException.NULL_VALUE);
        this.email = email;
        this.nickName = nickName;
        balance = 0;
        imagePath = "";
        bids = new ArrayList<>();
    }

    public void transaction(String type,int amount) throws UserException{
        if(amount <= 0) throw new UserException(UserException.NEGATIVE_VALUE);
        switch (type.toLowerCase()) {
            case "deposit":
                this.balance += amount;
                break;
            case "withdraw":
                if(this.balance - amount < 0) throw new UserException(UserException.NEGATIVE_BALANCE);
                this.balance -= amount;
                break;
            default:
                throw new UserException(UserException.INVALID_TRANSACTION);
        }
    }

    public void registerBid(String container, int amount) {
        bids.add(new Bid(container, amount));
    }

    public void registerBid(Bid bid) {
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
        return (this.nickName == null ? user.getNickName() == null : this.nickName.equals(user.getNickName()))
                && (this.email == null ? user.getEmail() == null :this.email.equals(user.getEmail()))
                && (this.balance == 0 ? user.getBalance() == 0 :this.balance == user.getBalance())
                && bids.equals(user.getBids());
    }

}
