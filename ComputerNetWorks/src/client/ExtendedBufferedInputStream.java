package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

final class ExtendedBufferedInputStream extends BufferedInputStream {

	private int lastChar;
	private long bytesRead = 0;
	static final char  CR = '\r';
	static final char LF = '\n';
	private boolean end_of_stream = false;
    /**
     * Created extended buffered reader using default buffer-size
     */
    ExtendedBufferedInputStream(final InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public int read() throws IOException {
		final int current = super.read();
		if (current == LF && lastChar == CR) {
//			System.out.println("CRLF detected);
		} else if (current == LF && lastChar != CR) {
//			System.out.println("LF detected);
		} else if (current == CR && lookAhead() != LF) {
//			System.out.println("CR detected);
		}
		this.bytesRead++;
        lastChar = current;
        return lastChar;
    }
    
    /**
     * Returns the last character that was read as an integer (0 to 65535). This will be the last character returned by
     * any of the read methods. This will not include a character read using the {@link #lookAhead()} method. If no
     * character has been read then this will return {@link Constants#UNDEFINED}. If the end of the stream was reached
     * on the last read then this will return {@link Constants#END_OF_STREAM}.
     *
     * @return the last character that was read
     */
    int getLastChar() {
        return lastChar;
    }


    /**
     * Calls {@link BufferedReader#readLine()} which drops the line terminator(s). This method should only be called
     * when processing a comment, otherwise information can be lost.
     * <p>
     * Increments {@link #eolCounter}
     * <p>
     * Sets {@link #lastChar} to {@link Constants#END_OF_STREAM} at EOF, otherwise to LF
     *
     * @return the line that was read, or null if reached EOF.
     */
    public String readLine() throws IOException {
    	int current;
    	String line = "";
    	while (!isEndOfLine(current = this.read())) {
    		if (!(current == CR || current == LF)) {
    			line += Character.toString((char) current);
    		}
    	}
    	if (this.end_of_stream)	
    		return null;
    	if (current == -1) {
    		this.end_of_stream = true;
    	}
        return line;
    }
    
    public String readLine(long limit) throws IOException {
    	int current;
    	String line = "";
    	while (!isEndOfLine(current = this.read(), limit)) {
    		if (!(current == CR || current == LF)) {
    			line += Character.toString((char) current);
    		}
    	}
    	if (this.end_of_stream)	
    		return null;
    	if (current == -1) {
    		this.end_of_stream = true;
    	}
        return line;
    }
    
    // for CRLF
    private boolean isEndOfLine(int ch) throws IOException {
		if (ch == CR || ch == -1){
			return true;
		}
		return false;
	}
    
    // for CRLF
    private boolean isEndOfLine(int ch, long limit) throws IOException {
    	if (getBytesRead() > limit) {
    		return true;
    	}
    	else if (ch == CR || ch == -1){
			return true;
		}
		return false;
	}
    
    int lookAhead() throws IOException {
        super.mark(1);
        final int c = super.read();
        super.reset();

        return c;
    }


	public long getBytesRead() {
    	return this.bytesRead;
    }
    
    public void resetBytesRead() {
    	this.bytesRead = 0;
    }
}
