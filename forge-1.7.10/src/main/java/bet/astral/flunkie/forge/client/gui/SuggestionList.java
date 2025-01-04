package bet.astral.flunkie.forge.client.gui;

import bet.astral.flunkie.command.SuggestionResult;
import bet.astral.flunkie.forge.LegacyBrigadierV1_7_10;
import bet.astral.flunkie.tuple.Pair;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.stream.Collectors;

public class SuggestionList {
    public static final int MAX_ITEMS_PER_PAGE = 10;
    private final List<Item> items = new ArrayList<>();
    private IChatComponent error;
    private int page = 0;
    private int selection = 0;

    public SuggestionList(SuggestionResult result, String cmd) {
        if (result == null) {
            return;
        }

        if (result.getUsages() != null) {
            for (Message usage : result.getUsages()) {
                if (usage != null) {
                    items.add(new Item(LegacyBrigadierV1_7_10.convertMessageToComponent(usage), null));
                }
            }
        }

        if (result.getSuggestions() != null) {
            for (Suggestion suggestion : result.getSuggestions().getList()) {
                if (suggestion != null) {
                    IChatComponent text = (IChatComponent) LegacyBrigadierV1_7_10.legacyMessage(suggestion.getText());
                    IChatComponent tooltip = suggestion.getTooltip() != null
                            ? LegacyBrigadierV1_7_10.convertMessageToComponent(suggestion.getTooltip())
                            : null;
                    items.add(new Item(text, tooltip));
                }
            }
        }

        if (result.getError() != null) {
            error = LegacyBrigadierV1_7_10.convertMessageToComponent(result.getError());
        }

        /*
        List<Item> newItems = filter(items, cmd);
        items.clear();
        items.addAll(newItems);
         */

        // Reverse the list directly
        Collections.reverse(items);

        // Initialize selection to the first valid item
        resetSelection(cmd);
    }

    public void moveTo(int newSelection, String cmd) {
        List<Item> items  = filter(this.items, cmd);
        if (items.isEmpty()) {
            selection = 0;
            page = 0;
            return;
        }

        // Wrap around the selection index
        selection = (newSelection % items.size() + items.size()) % items.size();

        // Adjust page calculation to reflect bottom-to-top order
        page = selection / MAX_ITEMS_PER_PAGE;
    }

    public int getCurrentSelection() {
        return selection;
    }

    public Item getCurrentItem() {
        if (items.isEmpty() || selection < 0 || selection >= items.size()) {
            return null; // No valid item if list is empty or selection is invalid
        }
        return items.get(selection);
    }

    public void moveUp(String cmd) {
        if (items.isEmpty()) return; // Prevent action on empty list
        moveTo(selection - 1, cmd); // Move toward higher indices in bottom-to-top view
    }

    public void moveDown(String cmd) {
        if (items.isEmpty()) return; // Prevent action on empty list
        moveTo(selection + 1, cmd); // Move toward lower indices in bottom-to-top view
    }
    public int getCurrentPage() {
        return page;
    }

    public Pair<Boolean, List<Item>> get(String cmd) {
        List<Item> returnItems = new ArrayList<>();
        if (items.isEmpty()) return new Pair<>(false, returnItems); // Return an empty list if there are no items

        // Filter items based on the command
        List<Item> newItems = filter(items, cmd);

        // Adjust start and end indices
        int totalItems = newItems.size();
        if (totalItems == 0) {
            selection = 0;
            page = 0;
            return new Pair<>(false, returnItems); // Return an empty list if filtered list is empty
        }

        int start = page * MAX_ITEMS_PER_PAGE;
        int end = Math.min(start + MAX_ITEMS_PER_PAGE, totalItems);

        // Handle cases where the page index exceeds the available items
        if (start >= totalItems) {
            start = Math.max(0, totalItems - MAX_ITEMS_PER_PAGE);
            end = totalItems;
            page = start / MAX_ITEMS_PER_PAGE;
        }

        // Extract the items for this page
        List<Item> displayItems = newItems.subList(start, end);
        int size = displayItems.size();

        // Ensure the selection is within bounds
        if (selection >= totalItems) {
            selection = totalItems - 1;
        }

        boolean anyMatch = false;;
        for (int i = 0; i < size; i++) {
            Item item = displayItems.get(i);
            // Highlight the selected line
            item.currentLine = (selection == start + i);
            returnItems.add(item);
            if (cmd.equalsIgnoreCase(item.text.toString())){
                anyMatch = true;
            }
        }
        return new Pair<>(anyMatch, returnItems);
    }

    private List<Item> filter(List<Item> items, String command){
        if (command.endsWith(" ")){
            return items;
        } else if (command.contains(" ")){
            String[] cmd = command.split(" ");
            return Lists.newLinkedList(items).stream()
                    .filter(item -> item.text.getUnformattedText().startsWith(cmd[cmd.length-1]))
                    .collect(Collectors.toList());
        }
//        return items;
        return Lists.newLinkedList(items).stream()
                .filter(item -> item.text.getUnformattedText().startsWith(command))
                .collect(Collectors.toList());
    }

    public boolean matches(SuggestionResult result){
        if (result.getUsages() != null && !result.getUsages().isEmpty()) {
            if (this.items.stream().noneMatch(item->item.isUsage)){
                return false;
            }
            return new HashSet<>(result.getUsages().stream().map(Message::getString).collect(Collectors.toList())).containsAll(this.items.stream().map(item->item.text.getUnformattedText()).collect(Collectors.toList()));
        }
        if (result.getSuggestions() != null && !result.getSuggestions().getList().isEmpty()){
            if (this.items.stream().noneMatch(item->item.isSuggestion)) {
                return false;
            }
            return new HashSet<>(result.getSuggestions().getList().stream().map(Suggestion::getText).collect(Collectors.toList())).containsAll(this.items.stream().map(item->item.text.getUnformattedText()).collect(Collectors.toList()));
        }
        return false;
    }

    private void resetSelection(String cmd) {
        if (items.isEmpty()) {
            selection = 0;
            page = 0;
        } else {
            // Start with the last item (index `items.size() - 1` after reversal)
            moveTo(items.size() - 1, cmd);
        }
    }
    public static class Item {
        boolean isUsage;
        boolean isSuggestion;
        boolean currentLine;
        IChatComponent text;
        IChatComponent tooltip;

        public Item(IChatComponent text, IChatComponent tooltip) {
            this.text = text;
            this.tooltip = tooltip;
        }
    }
}
