package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiWTOT extends GuiIngame {
    private final Minecraft client;
    private final GuiIngame parent;
    private static final RenderItem itemRenderer = new RenderItem();

    public GuiWTOT(Minecraft minecraft, GuiIngame parent) {
        super(minecraft);
        this.client = minecraft;
        this.parent = parent;
    }

    public GuiIngame getParentHud() {
        return parent;
    }

    @Override
    public void addChatMessage(String message) {
        if (parent != null) {
            parent.addChatMessage(message);
        } else {
            super.addChatMessage(message);
        }
    }

    @Override
    public void updateTick() {
        if (parent != null) {
            parent.updateTick();
        } else {
            super.updateTick();
        }
    }

    @Override
    public void renderGameOverlay(float partialTicks, boolean flag, int mouseX, int mouseY) {
        if (parent != null) {
            parent.renderGameOverlay(partialTicks, flag, mouseX, mouseY);
        } else {
            super.renderGameOverlay(partialTicks, flag, mouseX, mouseY);
        }

        if (client == null || client.theWorld == null || client.thePlayer == null) {
            return;
        }

        MovingObjectPosition hit = client.objectMouseOver;
        if (hit == null) {
            try {
                hit = client.thePlayer.rayTrace(4.0D, partialTicks);
            } catch (Throwable t) {
                return;
            }
        }

        if (hit == null) {
            return;
        }

        int x = hit.blockX;
        int y = hit.blockY;
        int z = hit.blockZ;

        int blockId = client.theWorld.getBlockId(x, y, z);
        if (blockId <= 0 || blockId >= Block.blocksList.length) {
            return;
        }

        Block block = Block.blocksList[blockId];
        if (block == null) {
            return;
        }

        int meta = client.theWorld.getBlockMetadata(x, y, z);

        String nameLine = getBlockDisplayName(block);
        String idLine = "ID: " + blockId + "  Meta: " + meta;

        ItemStack iconStack = new ItemStack(blockId, 1, meta);
        drawTooltip(nameLine, idLine, iconStack);
    }

    private void drawTooltip(String line1, String line2, ItemStack iconStack) {
        ScaledResolution sr =
                new ScaledResolution(client.gameSettings, client.displayWidth, client.displayHeight);

        int pad = 4;
        int lineHeight = 10;
        int iconSize = 16;
        int iconGap = 4;
        int borderColor = 0x60FFFFFF;
        int backgroundColor = 0x90000000;

        int textWidth = Math.max(
                client.fontRenderer.getStringWidth(line1),
                client.fontRenderer.getStringWidth(line2)
        );

        int textHeight = lineHeight * 2;
        int contentHeight = Math.max(iconSize, textHeight);

        int boxWidth = pad + iconSize + iconGap + textWidth + pad;
        int boxHeight = pad + contentHeight + pad;

        int left = sr.getScaledWidth() / 2 - boxWidth / 2;
        int top = 6;
        int right = left + boxWidth;
        int bottom = top + boxHeight;

        drawRect(left, top, right, bottom, backgroundColor);

        // full border
        drawRect(left, top, right, top + 1, borderColor);         // top
        drawRect(left, bottom - 1, right, bottom, borderColor);   // bottom
        drawRect(left, top, left + 1, bottom, borderColor);       // left
        drawRect(right - 1, top, right, bottom, borderColor);     // right

        int iconX = left + pad;
        int iconY = top + (boxHeight - iconSize) / 2;

        int textX = iconX + iconSize + iconGap;
        int textStartY = top + (boxHeight - textHeight) / 2;

        client.fontRenderer.drawStringWithShadow(line1, textX, textStartY, 0xFFFFFF);
        client.fontRenderer.drawStringWithShadow(line2, textX, textStartY + lineHeight, 0xAFAFAF);

        renderIcon(iconStack, iconX, iconY);
    }

    private void renderIcon(ItemStack stack, int x, int y) {
        if (stack == null) {
            return;
        }

        GL11.glPushMatrix();
        RenderHelper.enableStandardItemLighting();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.zLevel = 100.0F;

        itemRenderer.renderItemIntoGUI(
                client.fontRenderer,
                client.renderEngine,
                stack,
                x,
                y
        );

        this.zLevel = 0.0F;

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private String getBlockDisplayName(Block block) {
        String raw = block.getBlockName();
        if (raw == null) {
            return "Unknown Block";
        }

        String translated = StringTranslate.getInstance().translateKey(raw);
        if (translated != null && translated.length() > 0 && !translated.equals(raw)) {
            return translated;
        }

        return prettify(raw);
    }

    private String prettify(String raw) {
        if (raw.startsWith("tile.")) {
            raw = raw.substring(5);
        } else if (raw.startsWith("item.")) {
            raw = raw.substring(5);
        }

        StringBuffer out = new StringBuffer();

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);

            if (i == 0) {
                out.append(Character.toUpperCase(c));
            } else if (Character.isUpperCase(c)) {
                out.append(' ');
                out.append(c);
            } else {
                out.append(c);
            }
        }

        return out.toString();
    }
}