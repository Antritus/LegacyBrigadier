package bet.astral.brigadier.forge.mixin.common.network;

import bet.astral.brigadier.forge.LegacyBrigadierV1_7_10;
import com.google.gson.*;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(S3APacketTabComplete.class)
public abstract class ClientBoundTabCompletePacketMixin {
    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping()
            .registerTypeAdapter(Suggestion.class,
                    (JsonSerializer<Suggestion>) (src, typeOfSrc, context) -> {
                JsonObject tooltipObj = null;
                if (src.getTooltip()!=null){
                    IChatComponent tooltip
                            = LegacyBrigadierV1_7_10.convertMessageToComponent(src.getTooltip());
                    tooltipObj = (JsonObject) parser.parse(IChatComponent.Serializer.func_150696_a(tooltip));
                }

                JsonObject suggestionObj = new JsonObject();
                suggestionObj.addProperty("val", src.getText());
                suggestionObj.add("ttp", tooltipObj);
                suggestionObj.addProperty("range_s", src.getRange().getStart());
                suggestionObj.addProperty("range_e", src.getRange().getEnd());
                return suggestionObj;
            }
            ).registerTypeAdapter(Suggestion.class,
                    (JsonDeserializer<Suggestion>) (json, typeOfT, context) -> {
                        JsonObject suggestionObj = (JsonObject) json;
                        // The actual suggestion
                        String suggestion = suggestionObj.get("val").getAsString();
                        // The range for this suggestion
                        StringRange range = new StringRange(
                                suggestionObj.get("range_s").getAsInt(),
                                suggestionObj.get("range_e").getAsInt()
                        );

                        // Tooltip
                        JsonElement ttp = suggestionObj.get("ttp");
                        if (ttp.isJsonNull()) {
                            return new Suggestion(range, suggestion);
                        } else {
                            Message tooltip = LegacyBrigadierV1_7_10.jsonMessage(ttp.toString());

                            return new Suggestion(range, suggestion, tooltip);
                        }
                    }
            )
            .create();
    @Shadow private String[] field_149632_a;

    @Shadow public abstract void processPacket(INetHandlerPlayClient p_148833_1_);

    @Nullable
    public Suggestion[] suggestions = null;

    @Nullable
    public Suggestion[] getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(@Nullable Suggestion[] suggestions) {
        this.suggestions = suggestions;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        int buffer = packetBuffer.readVarIntFromBuffer();
        if (buffer == -1){
            return;
        }
        this.suggestions = new Suggestion[buffer];
        for (int i = 0; i < buffer; i++){
            this.suggestions[i] = gson.fromJson(packetBuffer.readStringFromBuffer(32767), Suggestion.class);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        if (suggestions==null){
            packetBuffer.writeVarIntToBuffer(-1);
            return;
        }
        packetBuffer.writeVarIntToBuffer(suggestions.length);
        for (Suggestion suggestion : suggestions) {
            String serialized = gson.toJson(suggestion);
            packetBuffer.writeStringToBuffer(serialized);
        }
    }

    @Overwrite
    public String serialize() {
        return String.format("candidates=\'%s\'", new Object[]{ArrayUtils.toString(this.field_149632_a)});
    }

    @Overwrite
    @SideOnly(Side.CLIENT)
    public String[] func_149630_c() {
        if (this.suggestions != null){
            String[] strings = new String[suggestions.length];
            for (int i = 0; i < strings.length; i++){
                strings[i] = suggestions[i].getText();
            }

            return strings;
        }
        return new String[0];
    }

    /**
     * Handles the tab completion
     */
    @Overwrite
    public void processPacket(INetHandler p_148833_1_)
    {
//        S3APacketTabComplete s3APacketTabComplete = new S3APacketTabComplete(new String[]{"Hello", "Hello2", "Hello3"});
  //      ((INetHandlerPlayClient)p_148833_1_).handleTabComplete(s3APacketTabComplete);
              ((INetHandlerPlayClient)p_148833_1_).handleTabComplete((S3APacketTabComplete) (Object) this);
    }
}
