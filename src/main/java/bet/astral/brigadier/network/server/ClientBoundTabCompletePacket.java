package bet.astral.brigadier.network.server;

import bet.astral.brigadier.LegacyBrigadier;
import bet.astral.brigadier.command.SuggestionResult;
import bet.astral.brigadier.network.Packet;
import com.google.gson.*;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import java.util.ArrayList;
import java.util.List;

public interface ClientBoundTabCompletePacket extends Packet {
    Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(Suggestions.class, (JsonSerializer<Suggestions>) (suggestions, type, jsonSerializationContext) -> {
                if (suggestions == null) {
                    return null;
                }
                JsonArray array = new JsonArray();
                for (Suggestion suggestion : suggestions.getList()) {
                    JsonObject object = new JsonObject();
                    object.addProperty("value", suggestion.getText());
                    object.addProperty("range-s", suggestion.getRange().getStart());
                    object.addProperty("range-e", suggestion.getRange().getEnd());
                    if (suggestion.getTooltip() != null) {
                        object.add("ttp", LegacyBrigadier.messageToJson(suggestion.getTooltip()));
                    } else {
                        object.add("ttp", JsonNull.INSTANCE);
                    }
                    array.add(object);
                }
                return array;
            })
            .registerTypeAdapter(Suggestions.class, (JsonDeserializer<Suggestions>) (jsonElement, type, jsonDeserializationContext) -> {
                int range = 0;
                int to = 0;
                List<Suggestion> suggestions = new ArrayList<>();
                for (JsonElement suggestionElement : jsonElement.getAsJsonArray()) {
                    JsonObject obj = suggestionElement.getAsJsonObject();
                    String value = obj.get("value").getAsString();
                    int rangeStart = obj.get("range-s").getAsInt();
                    int rangeEnd = obj.get("range-e").getAsInt();
                    range = rangeStart;
                    to = rangeEnd;
                    JsonElement element = obj.get("ttp");
                    if (element == null || element.isJsonNull()) {
                        suggestions.add(new Suggestion(
                                new StringRange(rangeStart, rangeEnd),
                                value
                        ));
                    } else {
                        Message ttp = LegacyBrigadier.jsonMessage(
                                element.getAsString()
                        );

                        suggestions.add(new Suggestion(
                                new StringRange(rangeStart, rangeEnd),
                                value,
                                ttp
                        ));
                    }
                }

                return new Suggestions(new StringRange(range, to), suggestions);
            })
            .create();

    SuggestionResult getSuggestionResult();

    void setSuggestionResult(SuggestionResult suggestionResult);


    @Override
    default void serialize(JsonObject object) {
        if (getSuggestionResult().getSuggestions() != null) {
            object.addProperty("suggestions", gson.toJson(getSuggestionResult().getSuggestions()));
        } else if (getSuggestionResult().getUsages() != null) {
            JsonArray array = new JsonArray();
            for (Message message : getSuggestionResult().getUsages()) {
                array.add(LegacyBrigadier.messageToJson(message));
            }
            object.add("usages", array);
        } else if (getSuggestionResult().getError() != null) {
            object.add("error", LegacyBrigadier.messageToJson(getSuggestionResult().getError()));
        }
    }

    @Override
    default void deserialize(JsonObject object) {
        JsonElement suggestionsElement = object.get("suggestions");
        JsonElement usageElement = object.get("usages");
        JsonElement errorElement = object.get("error");
        if ((suggestionsElement == null || suggestionsElement.isJsonNull())
                && (usageElement == null || usageElement.isJsonNull())
        ) {
            setSuggestionResult(
                    new SuggestionResult(
                            LegacyBrigadier.jsonMessage(
                                    gson.toJson(errorElement)
                            )
                    ));
        } else if (suggestionsElement == null || suggestionsElement.isJsonNull()) {
            setSuggestionResult(
                    new SuggestionResult(
                            gson.fromJson(suggestionsElement, Suggestions.class)
                    )
            );
        } else {
            List<Message> usages = new ArrayList<>();
            for (JsonElement element : usageElement.getAsJsonArray()) {
                usages.add(
                        LegacyBrigadier.jsonMessage(
                                gson.toJson(element)
                        )
                );
            }
            setSuggestionResult(
                    new SuggestionResult(
                            usages
                    ));
        }
    }

    default Gson getGson(){
        return gson;
    }

}