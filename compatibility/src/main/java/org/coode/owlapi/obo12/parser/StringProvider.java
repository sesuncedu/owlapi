/* Generated By:JavaCC: Do not edit this line. StringProvider.java Version 7.0 */
/* JavaCCOptions:KEEP_LINE_COLUMN=true */
package org.coode.owlapi.obo12.parser;


import java.io.IOException;

@SuppressWarnings("all")
public class StringProvider implements Provider {

  String _string;
  int _position = 0;
  int _size;

  public StringProvider(String string) {
    _string = string;
    _size = string.length();
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    int numCharsOutstandingInString = _size - _position;

    if (numCharsOutstandingInString == 0) {
      return -1;
    }

    int numBytesInBuffer = cbuf.length;
    int numBytesToRead = numBytesInBuffer - off;
    numBytesToRead = numBytesToRead > len ? len : numBytesToRead;

    if (numBytesToRead > numCharsOutstandingInString) {
      numBytesToRead = numCharsOutstandingInString;
    }

    _string.getChars(_position, _position + numBytesToRead, cbuf, off);

    _position += numBytesToRead;

    return numBytesToRead;
  }

  @Override
  public void close() throws IOException {
    _string = null;
  }

}
/* JavaCC - OriginalChecksum=838da35578a91bf479fb442399d871a1 (do not edit this line) */
