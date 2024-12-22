package bet.astral.flunkie.network.client;

import bet.astral.flunkie.network.LegacyPacket;

public interface ClientBoundLegacyTabCompletePacket extends LegacyPacket {
    String[] getSuggestions();
}
