package com.ezbumhikvisuals;

import java.util.List;

public final class Modules {
    public static final class M {
        public final String name;
        public boolean on;
        M(String name, boolean on) { this.name = name; this.on = on; }
    }

    public static final M WATERMARK = new M("Watermark", true);
    public static final M HUD_INFO  = new M("HUD Info", true);
    public static final M ARMOR     = new M("Armor %", true);
    public static final M BUFFS     = new M("Buff Timers", true);
    public static final M ITEM_GLOW = new M("Sword/Pickaxe Glow", true);
    public static final M FT_HELPER = new M("FT Helper", true);
    public static final M NO_FIRE   = new M("No Fire Overlay", true);
    public static final M NO_SHAKE  = new M("No Screen Shake", true);
    public static final M RAIN      = new M("Rain FX", false);
    public static final M SNOW      = new M("Snow FX", false);
    public static final M SPACE     = new M("Space FX", false);
    public static final M LOW_END   = new M("Low-End Mode", false);

    public static final List<M> ALL = List.of(WATERMARK, HUD_INFO, ARMOR, BUFFS, ITEM_GLOW,
            FT_HELPER, NO_FIRE, NO_SHAKE, RAIN, SNOW, SPACE, LOW_END);

    private Modules() {}
}
