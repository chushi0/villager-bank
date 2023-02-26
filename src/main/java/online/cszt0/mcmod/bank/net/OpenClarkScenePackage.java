package online.cszt0.mcmod.bank.net;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Deprecated(forRemoval = true, since = "0.0.0")
@NoArgsConstructor
@AllArgsConstructor
public class OpenClarkScenePackage extends NetworkPackage {
    @Getter
    @Setter
    @Net(order = 1)
    private UUID entity;

    @Getter
    @Setter
    @Net(order = 2)
    private Text name;

    @Override
    protected Identifier identifier() {
        return PackageIdentifier.OPEN_CLARK_SCENE;
    }
}
