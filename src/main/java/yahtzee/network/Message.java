package yahtzee.network;

import com.google.gson.JsonElement;

/**
 * Immutable data holder for network messages: type identifier and JSON payload.
 */
public record Message(
        MessageType type,       // Type of the message (e.g., ROLL, SELECT, END)
        JsonElement payload     // JSON-formatted data associated with this message
) {}
