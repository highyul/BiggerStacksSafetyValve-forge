package io.github.highyul.biggerstackssafetyvalve;

public enum CompatMod {
    //modID
    FAST_BENCH("fastbench"),
    AE2("ae2"),
    JEI("jei"),
    REFINED_STORAGE("refinedstorage"),
    TOMS_STORAGE("toms_storage"),
    SOPHISTICATED_CORE("sophisticatedcore"),
    AVARITIA("avaritia");

    public final String id;

    CompatMod(String id) {
        this.id = id;
    }
}
