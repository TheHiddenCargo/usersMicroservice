package arsw.tamaltolimense.playermanager.model;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Getter
@Document
public class Bid {
    @Id
    private final ObjectId bidId;
    private final String containerId;
    private final int amount;
    private Map<String, Object> extraData;

    public Bid(String containerId, int amount) {
        this.bidId = new ObjectId();
        this.containerId = containerId;
        this.amount = amount;
    }

    public Bid(String containerId, int amount, Map<String, Object> extraData) {
        this.bidId = new ObjectId();
        this.containerId = containerId;
        this.amount = amount;
        this.extraData = extraData;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + bidId.hashCode();
        result = prime * result + ((containerId == null) ? 0 : containerId.hashCode());
        result = prime * result + amount;
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || obj.getClass() != this.getClass()) return false;
        Bid bid = (Bid) obj;
        return this.bidId.equals(bid.getBidId())
                && (this.containerId == null ? bid.getContainerId() == null :this.containerId.equals(bid.getContainerId()))
                && (this.amount == 0 ? bid.getAmount() == 0 :this.amount == bid.getAmount());
    }
}


