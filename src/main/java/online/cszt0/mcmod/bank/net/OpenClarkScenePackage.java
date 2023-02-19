package online.cszt0.mcmod.bank.net;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class OpenClarkScenePackage extends NetworkPackage {
    @Getter
    @Setter
    @Net(order = 1, name = "uuid")
    private UUID entity;
}
