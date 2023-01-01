package net.enecske.customblock_core.core;

import net.minecraft.block.enums.Instrument;
import net.minecraft.util.Identifier;

public class BlockIdentifier {
    private final int instrument;
    private final int note;
    private final Identifier id;

    public BlockIdentifier(int instrument, int note, String namespace, String path) {
        this.instrument = Math.max(0, Math.min(15, instrument));
        this.note = Math.max(0, Math.min(24, note));
        this.id = new Identifier(namespace, path);
    }

    public int getInstrument() {
        return instrument;
    }
    public int getNote() {
        return note;
    }

    public Identifier getId() {
        return id;
    }
    public String getNamespace() {
        return id.getNamespace();
    }
    public String getPath() {
        return id.getPath();
    }

    @Override
    public String toString() {
        return Instrument.values()[instrument].toString().toLowerCase() + ", " + note;
    }
}
