package com.grzm.pqconninfo.alpha;

/**
 * An exception thrown when an error is encountered when parsing a service file.
 */
public class MalformedServiceFileException extends RuntimeException {
    /**
     * The line number where the error was encountered.
     */
    private final int lineNumber;

    /**
     * The line where the error was encountered.
     */
    private final String line;

    /**
     * @param message The error message
     * @param lineNumber The line number of the malformed line.
     * @param line The malformed line.
     */
    public MalformedServiceFileException(
            final String message, final int lineNumber, final String line) {
        super(message);
        this.lineNumber = lineNumber;
        this.line = line;
    }

    /**
     * @return The line number of the malformed line.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @return The malformed line.
     */
    String getLine() {
        return line;
    }

}
