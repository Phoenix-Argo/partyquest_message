package partyquest.chat.tutorial.v1;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class HelloMessage {
    private String name;

    public HelloMessage(String name) {
        this.name = name;
    }
}
