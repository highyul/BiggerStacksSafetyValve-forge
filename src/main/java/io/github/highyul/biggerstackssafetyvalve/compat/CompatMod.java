package io.github.highyul.biggerstackssafetyvalve.compat;

public enum CompatMod {
    //modID
    FAST_BENCH("fastbench"),
    AE2("ae2"),
    JEI("jei"),
    REFINED_STORAGE("refinedstorage"),
    TOMS_STORAGE("toms_storage");

    public final String id;

    CompatMod(String id) {
        this.id = id;
    }
}
