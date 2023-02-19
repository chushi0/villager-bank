package online.cszt0.mcmod.bank.screen;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import online.cszt0.mcmod.bank.VillageBank;

@Slf4j(topic = VillageBank.MODID)
public class BankScreen extends Screen {

    public BankScreen() {
        super(Text.of("绿宝石商人"));
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.of("Click"), btn -> {
            log.info("Click!");
        }).dimensions(10, 64, 128, 64).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }
}
