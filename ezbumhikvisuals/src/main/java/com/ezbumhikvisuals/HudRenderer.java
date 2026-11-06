package com.ezbumhikvisuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

import java.awt.Color;

public final class HudRenderer {
    private static final int BG = 0xB00E0E16, ACCENT = 0xFF7C5CFF, TEXT = 0xFFEDEDF7, DIM = 0xFF9A9AB0;

    public static void render(DrawContext ctx, RenderTickCounter tc) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;
        if (p == null || mc.options.hudHidden) return;
        int w = mc.getWindow().getScaledWidth(), h = mc.getWindow().getScaledHeight();

        Fx.render(ctx, w, h);
        if (Modules.WATERMARK.on) watermark(ctx, mc, p);
        if (Modules.HUD_INFO.on)  ctx.drawText(mc.textRenderer, "Founder: ezbumhikvisuals", w - mc.textRenderer.getWidth("Founder: ezbumhikvisuals") - 6, 6, ACCENT, true);
        if (Modules.ARMOR.on)     armor(ctx, mc, p, h);
        if (Modules.BUFFS.on)     buffs(ctx, mc, p, w);
        if (Modules.ITEM_GLOW.on) glow(ctx, p, w, h);
        if (Modules.FT_HELPER.on) cooldowns(ctx, mc, p, tc, w, h);
    }

    private static void watermark(DrawContext c, MinecraftClient mc, PlayerEntity p) {
        int ping = 0;
        if (mc.getNetworkHandler() != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(p.getUuid());
            if (e != null) ping = e.getLatency();
        }
        String s = "ezbumhikvisuals  |  " + ping + " ms  |  " + mc.getCurrentFps() + " FPS";
        int tw = mc.textRenderer.getWidth(s);
        c.fill(6, 6, 6 + tw + 12, 24, BG);
        c.fill(6, 6, 8, 24, ACCENT);
        c.drawText(mc.textRenderer, s, 14, 12, TEXT, false);
    }

    private static void armor(DrawContext c, MinecraftClient mc, PlayerEntity p, int h) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        String[] names = {"Helmet", "Chest", "Legs", "Boots"};
        int y = h / 2 - 30;
        c.fill(6, y - 4, 96, y + 4 * 11 + 14, BG);
        c.fill(6, y - 4, 96, y - 3, ACCENT);
        int total = 0, cnt = 0;
        for (int i = 0; i < 4; i++) {
            ItemStack s = p.getEquippedStack(slots[i]);
            String t = "-";
            if (!s.isEmpty() && s.getMaxDamage() > 0) {
                int pct = (s.getMaxDamage() - s.getDamage()) * 100 / s.getMaxDamage();
                t = pct + "%"; total += pct; cnt++;
            }
            c.drawText(mc.textRenderer, names[i] + ": " + t, 10, y + 2 + i * 11, TEXT, false);
        }
        c.drawText(mc.textRenderer, "Armor: " + (cnt == 0 ? 0 : total / cnt) + "%", 10, y + 4 * 11 + 4, ACCENT, false);
    }

    private static void buffs(DrawContext c, MinecraftClient mc, PlayerEntity p, int w) {
        int y = 30;
        for (StatusEffectInstance e : p.getStatusEffects()) {
            String name = e.getEffectType().value().getName().getString() + " " + (e.getAmplifier() + 1);
            int sec = e.getDuration() / 20;
            String time = e.isInfinite() ? "∞" : String.format("%d:%02d", sec / 60, sec % 60);
            int tw = mc.textRenderer.getWidth(name + "  " + time);
            c.fill(6, y, 6 + tw + 12, y + 14, BG);
            c.fill(6, y, 8, y + 14, sec < 10 && !e.isInfinite() ? 0xFFFF5555 : ACCENT);
            c.drawText(mc.textRenderer, name, 12, y + 3, TEXT, false);
            c.drawText(mc.textRenderer, time, 12 + mc.textRenderer.getWidth(name + "  "), y + 3, DIM, false);
            y += 16;
        }
    }

    private static void glow(DrawContext c, PlayerEntity p, int w, int h) {
        float hue = Modules.LOW_END.on ? 0.75f : (System.currentTimeMillis() % 3000) / 3000f;
        for (int i = 0; i < 9; i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (s.isEmpty() || !(s.isIn(ItemTags.SWORDS) || s.isIn(ItemTags.PICKAXES))) continue;
            int x = w / 2 - 90 + i * 20 + 2, y = h - 19;
            int rgb = Color.HSBtoRGB(hue, 0.6f, 1f) & 0xFFFFFF;
            int a = p.getInventory().selectedSlot == i ? 0xFF : 0x90;
            int col = (a << 24) | rgb;
            c.fill(x - 2, y - 2, x + 18, y - 1, col);
            c.fill(x - 2, y + 17, x + 18, y + 18, col);
            c.fill(x - 2, y - 2, x - 1, y + 18, col);
            c.fill(x + 17, y - 2, x + 18, y + 18, col);
            c.fill(x - 1, y - 1, x + 17, y + 17, (0x30 << 24) | rgb);
        }
    }

    private static void cooldowns(DrawContext c, MinecraftClient mc, PlayerEntity p, RenderTickCounter tc, int w, int h) {
        float td = tc.getTickDelta(false);
        int y = h / 2 + 40;
        for (int i = 0; i < 9; i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (!(s.isOf(Items.SNOWBALL) || s.isOf(Items.CROSSBOW) || s.isOf(Items.ENDER_PEARL) || s.isOf(Items.SPLASH_POTION))) continue;
            float cd = p.getItemCooldownManager().getCooldownProgress(s, td);
            if (cd <= 0) continue;
            String t = s.getName().getString() + " " + (int) (cd * 100) + "%";
            int tw = mc.textRenderer.getWidth(t);
            c.fill(6, y, 6 + tw + 12, y + 13, BG);
            c.fill(6, y, 8, y + 13, ACCENT);
            c.drawText(mc.textRenderer, t, 12, y + 3, TEXT, false);
            y += 15;
        }
    }
}
