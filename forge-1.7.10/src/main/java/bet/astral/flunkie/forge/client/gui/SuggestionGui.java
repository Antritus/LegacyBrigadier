package bet.astral.flunkie.forge.client.gui;

import bet.astral.flunkie.command.SuggestionResult;
import bet.astral.flunkie.forge.LegacyBrigadierV1_7_10;
import bet.astral.flunkie.network.PacketHandler;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionGui extends Gui {
    public static final int BACKGROUND_COLOR = new Color(0, 0, 0, 0.9F).getRGB();
    private static final int MAX_PAGE_DISPLAY_AMOUNT = 10;
    private static final int MAX_PAGE_DISPLAY = 9;
    private GuiTextField field;
    private String oldString = "";
    private SuggestionList suggestionList;
    public int height;
    private boolean lastTabComplete = false;

    public SuggestionGui(GuiTextField field) {
        this.field = field;
        lastTabComplete = true;
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiChat){
            GuiChat chat = (GuiChat) screen;
            this.height = chat.height- field.yPosition;
        }
    }

    public void onDeFocus(){
        suggestionList = null;
    }

    public void requestSuggestions(String command, int range){
        if (command == null || command.isEmpty()){
            suggestionList = null;
            return;
        }
        PacketHandler.Client client = LegacyBrigadierV1_7_10.getInstance().getPacketHandlerClient();
        client.sendTabCompleteRequest(command, range);
    }

    public void onKeyTyped() {
        if (getCommand().equals(oldString)){
            return;
        }
        oldString = getCommand();
        requestSuggestions(getCommand(), getCursor());
    }


    public void tabCompleteUp() {
        if (suggestionList != null){
            suggestionList.moveDown(getCommand());
        }
    }

    public void tabCompleteDown() {
        if (suggestionList != null){
            suggestionList.moveUp(getCommand());
        }
    }

    public void updateSelectedString() {
        if (suggestionList == null) {
            return;
        }
        String input = getCommand();
        String prefix = input.contains(" ") ? input.substring(0, input.lastIndexOf(' ')) : "";
        prefix = prefix.endsWith(" ") ? prefix : prefix + " ";
        prefix = prefix.equals(" ") ? "" : prefix;

        final SuggestionList.Item selected = suggestionList.getCurrentItem();
        if (selected == null){
            return;
        }
        String selectedText = selected.text.getUnformattedText();

        field.setText(prefix + selectedText);
    }

    public void onTabComplete() {
        if (lastTabComplete) {
            tabCompleteDown();
        }
        updateSelectedString();
        lastTabComplete = true;
    }

    public void onArrowDown(){
        tabCompleteDown();
        lastTabComplete = false;
    }
    public void onArrowUp(){
        tabCompleteUp();
        lastTabComplete = false;
    }

    public String getCommand(){
        return field.getText();
    }

    public int getCursor(){
        return field.getCursorPosition();
    }

    public boolean hasSuggestions(){
        return suggestionList != null && !suggestionList.get(getCommand()).isEmpty();
    }

    public boolean matchesCurrentSuggestions(SuggestionResult result){
        if (this.suggestionList == null){
            return false;
        }
        return suggestionList.matches(result);
    }


    public void renderSuggestions() {
        if (!hasSuggestions()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        int baseY = mc.currentScreen.height - height - 12; // Adjust based on GUI layout.
        int lineHeight = mc.fontRenderer.FONT_HEIGHT + 2;  // Line height for rendering.
        List<SuggestionList.Item> toRender = suggestionList.get(getCommand());
        if (toRender == null || toRender.isEmpty()){
            return;
        }

        int maxLength = 0;
        for (SuggestionList.Item item : toRender) {
            int length = mc.fontRenderer.getStringWidth(item.text.getUnformattedText());
            if (length > maxLength) {
                maxLength = length;
            }
        }

        int dynamicBoxWidth = Math.min(200, maxLength + 4);  // Extra padding for better appearance

        String prefix = getCommand().contains(" ") ? getCommand().substring(0, getCommand().lastIndexOf(' ')+1) : "";
        int x = mc.fontRenderer.getStringWidth(prefix);

        // Start rendering each suggestion
        int line = 0;
        for (SuggestionList.Item item : toRender) {
            GL11.glPushMatrix();
            GL11.glTranslatef(2.0F, (baseY - (line * lineHeight))-0.5f, 0.0F); // Correct Y position

            // Highlight the selected suggestion
            boolean isSelected = (item.currentLine); // Use the selected index for highlighting
            String text = item.text.getUnformattedText();

            int color = isSelected ? 0x00FF00 : 0xFFFFFF; // Green for selected, white for others

            // Render background for each suggestion (optional visual enhancement)
            drawRect(x, -2, x+dynamicBoxWidth, lineHeight, BACKGROUND_COLOR); // Green for selected, white for others

            // Render the suggestion text
            mc.fontRenderer.drawStringWithShadow(text, x+2, 0, color);  // 2px padding from the left

            GL11.glPopMatrix();
            line++;  // Move to the next line for the next suggestion
        }
    }

    @ApiStatus.Internal
    public void onSuggestionReceive(String[] suggestionsString){
        String value = StringUtils.getCommonPrefix(suggestionsString);
        int cursor = value.length();
        StringRange range = StringRange.at(cursor);

        Arrays.sort(suggestionsString);
        Arrays.sort(suggestionsString);
        ArrayList<String> suggestionsArray = Arrays.stream(suggestionsString).collect(Collectors.toCollection(ArrayList::new));

        Suggestions suggestions = new Suggestions(range, suggestionsArray.stream().map(s->
                new Suggestion(range, s)).collect(Collectors.toCollection(ArrayList::new)));
        SuggestionResult result = new SuggestionResult(suggestions);

        onSuggestionReceive(result);
    }
    @ApiStatus.Internal
    public void onSuggestionReceive(SuggestionResult result) {
        if (result == null || (result.getSuggestions() == null && result.getUsages() == null)) {
            resetSuggestions();
            return;
        }

        boolean isMatch = matchesCurrentSuggestions(result);
        if (!isMatch || getCommand().endsWith(" ")) {
            resetSuggestions();

            suggestionList = new SuggestionList(result, getCommand());
        }
    }

    private void resetSuggestions() {
        this.suggestionList = null;
    }
}
