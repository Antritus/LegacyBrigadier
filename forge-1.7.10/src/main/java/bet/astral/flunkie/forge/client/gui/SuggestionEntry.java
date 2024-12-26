package bet.astral.flunkie.forge.client.gui;

import net.minecraft.util.IChatComponent;

public class SuggestionEntry {
    String value;
    IChatComponent tooltip;
    boolean isError;
    boolean isSelected;

    public SuggestionEntry(String value, IChatComponent tooltip, boolean isError, boolean isSelected) {
        this.value = value;
        this.tooltip = tooltip;
        this.isError = isError;
        this.isSelected = isSelected;
    }
}
