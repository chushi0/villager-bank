package online.cszt0.mcmod.bank.net;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@AllArgsConstructor
public class OpenClarkScenePackage extends NetworkPackage {
    @Getter
    @Setter
    @Net(order = 1)
    private UUID entity;

    @Override
    protected Identifier identifier() {
        return PackageIdentifier.OPEN_CLARK_SCENE;
    }
}
