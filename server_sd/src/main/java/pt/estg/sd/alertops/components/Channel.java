package pt.estg.sd.alertops.components;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    private int id;
    private String name;
    private String description;
    private List<User> users;

    public Channel( String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Channel( String name, String description, List<User> users) {
        this.name = name;
        this.description = description;
        this.users = users;
    }
}


