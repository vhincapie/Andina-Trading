package co.edu.unbosque.foresta.exceptions.exceptions;

public class MarketDataException extends RuntimeException {
    public MarketDataException(String message) { super(message); }
    public MarketDataException(String message, Throwable cause) { super(message, cause); }
}