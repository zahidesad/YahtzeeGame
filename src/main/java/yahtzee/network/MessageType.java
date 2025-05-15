package yahtzee.network;

/**
 * Defines types of network messages exchanged between client and server.
 */
public enum MessageType {
    HELLO,      // Initial handshake message
    MATCHED,    // Indicates players have been paired
    ROLL,       // Contains dice roll information
    SELECT,     // Contains category selection and scoring data
    UPDATE,     // General state update message
    END,        // Signals end of game or concession
    PING        // Keep-alive or latency check
}
