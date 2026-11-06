package com.ezbumhikvisuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MenuScreen extends Screen {
    private static final Identifier GIRL = Identifier.of("ezbumhikvisuals", "textures/gui/menu_girl.png");
    private static final int IMG_W = 246, IMG_H = 360; // размер файла menu_girl.png
    private static final int PW = 420, PH = 230;

    public MenuScreen() { super(Text.literal("EzbumhikVisuals")); }

    @Override public void renderBackground(DrawContext c, int mx, int my, float d) { c.fill(0, 0, width, height, 0x90000000); }
    @Override public boolean shouldPause() { return false; }

    private int px() { return (width - PW) / 2; }
    private int py() { return (height - PH) / 2; }

    @Override
    public void render(DrawContext c, int mx, int my, float d) {
        super.render(c, mx, my, d);
        int x = px(), y = py();
        c.fill(x, y, x + PW, y + PH, 0xF0121218);
        c.fill(x, y, x + PW, y + 2, 0xFF7C5CFF);

        // аниме-арт справа (вписываем в панель)
        int ih = PH - 20, iw = ih * IMG_W / IMG_H;
        c.drawTexture(RenderLayer::getGuiTextured, GIRL, x + PW - iw - 10, y + 10, 0, 0, iw, ih, IMG_W, IMG_H, IMG_W, IMG_H);

        c.drawText(textRenderer, "Visuals  /  HUD  /  Utilities", x + 14, y + 12, 0xFFEDEDF7, false);
        c.drawText(textRenderer, "Founder: ezbumhikvisuals", x + 14, y + PH - 14, 0xFF7C5CFF, false);

        int i = 0;
        for (Modules.M m : Modules.ALL) {
            int bx = x + 14, by = y + 34 + i * 15;
            boolean hover = mx >= bx && mx <= bx + 220 && my >= by && my <= by + 13;
            c.fill(bx, by, bx + 220, by + 13, hover ? 0xFF22222E : 0xFF1A1A24);
            c.drawText(textRenderer, m.name, bx + 6, by + 3, m.on ? 0xFFEDEDF7 : 0xFF70708A, false);
            int sx = bx + 196;
            c.fill(sx, by + 3, sx + 18, by + 10, m.on ? 0xFF7C5CFF : 0xFF2C2C3A);
            int kx = m.on ? sx + 11 : sx;
            c.fill(kx, by + 2, kx + 7, by + 11, 0xFFFFFFFF);
            i++;
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int x = px(), y = py(), i = 0;
        for (Modules.M m : Modules.ALL) {
            int bx = x + 14, by = y + 34 + i * 15;
            if (mx >= bx && mx <= bx + 220 && my >= by && my <= by + 13) { m.on = !m.on; return true; }
            i++;
        }
        return super.mouseClicked(mx, my, button);
    }
}
