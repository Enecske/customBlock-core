package net.enecske.customblock_core.core;

public class BlockIdentifier {
    private int instrument, note;

    public BlockIdentifier(int instrument, int note) {
        this.instrument = Math.max(0, Math.min(15, instrument));
        this.note = Math.max(0, Math.min(24, note));
    }

    public int getInstrument() {
        return instrument;
    }

    public int getNote() {
        return note;
    }
}
