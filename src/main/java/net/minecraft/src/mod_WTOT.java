package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class mod_WTOT extends BaseMod {
    private boolean installedForWorld = false;

    public mod_WTOT() {
        ModLoader.SetInGameHook(this, true, false);
    }

    @Override
    public String Version() {
        return "1.0.3-blocktooltip";
    }

    @Override
    public boolean OnTickInGame(Minecraft minecraft) {
        if (minecraft == null || minecraft.theWorld == null || minecraft.thePlayer == null) {
            installedForWorld = false;
            return true;
        }

        if (!installedForWorld) {
            minecraft.ingameGUI = new GuiWTOT(minecraft, minecraft.ingameGUI);
            installedForWorld = true;
        }

        return true;
    }
}