package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

final class ExtendedBufferedReader extends BufferedReader {

    /** The count of EOLs (CR/LF/CRLF) seen so far */
    private long eolCounter = 0;
    private long CRLFCounter = 0;
	private int lastChar;
	private long bytesRead = 0;
	static final char  CR = '\r';
	static final char LF = '\n';
	private boolean end_of_stream = false;
	private int lineCount = 0;
    /**
     * Created extended buffered reader using default buffer-size
     */
    ExtendedBufferedReader(final Reader reader) {
        super(reader);
    }

    @Override
    public int read() throws IOException {
		final int current = super.read();
		if (current == LF && lastChar == CR) {
//			System.out.println("CRLF detected at: " + lineCount);
			CRLFCounter++;
		} else if (current == LF && lastChar != CR) {
			System.out.println("LF detected at: " + lineCount);
			eolCounter++;
		} else if (current == CR && lookAhead() != LF) {
			System.out.println("CR detected at: " + lineCount);
			eolCounter++;
		} else if (current != -1 && current !=  CR && current != LF) {
			this.bytesRead++;
		}
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
    @Override
    public String readLine() throws IOException {
    	this.lineCount  ++;
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
    
    // for CRLF
    private boolean isEndOfLine(int ch) throws IOException {
		if (ch == CR || ch == -1){
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
    
    public long getCRLFCounter() {
    	return this.CRLFCounter;
    }
    
    public void resetCRLFCounter() {
    	this.CRLFCounter = 0;
    }
    
    public long getEOLCounter() {
    	return this.eolCounter;
    }
    
    public void resetEOLCounter() {
    	this.eolCounter = 0;
    }
    
    public long getTotalBytes() {
    	return this.bytesRead + 2*this.CRLFCounter + this.eolCounter;
    }
    
    public void resetTotalBytes() {
    	resetBytesRead();
    	resetCRLFCounter();
    	resetEOLCounter();
    }
}
