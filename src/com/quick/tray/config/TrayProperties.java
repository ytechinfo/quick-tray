/**
(#)TrayProperties.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayProperties
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
 */

package com.quick.tray.config;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import com.quick.tray.entity.DataEntity;

/**
 * Properties is a Hashtable where the keys and values must be Strings. Each
 * Properties can have a default Properties which specifies the default values
 * which are used if the key is not in this Properties.
 * 
 * @see Hashtable
 * @see java.lang.System#getProperties
 */
public class TrayProperties extends DataEntity {

	private static final long serialVersionUID = 4112578634029874840L;

	protected TrayProperties defaults;

	private static final int NONE = 0, SLASH = 1, UNICODE = 2, CONTINUE = 3,
			KEY_DONE = 4, IGNORE = 5;

	/**
	 * Constructs a new Properties object.
	 */
	public TrayProperties() {
		super();
	}

	public synchronized void load(InputStream in) throws IOException {
		if (in == null) {
			throw new NullPointerException();
		}
		BufferedInputStream bis = new BufferedInputStream(in);
		bis.mark(Integer.MAX_VALUE);
		boolean isAscii = isAscii(bis);
		bis.reset();

		if (isAscii) {
			load(new InputStreamReader(bis, "UTF-8")); //$NON-NLS-1$
		} else {
			load(new InputStreamReader(bis)); //$NON-NLS-1$
		}
	}

	public synchronized void load(Reader reader) throws IOException {
		int mode = NONE, unicode = 0, count = 0;
		char nextChar, buf[] = new char[40];
		int offset = 0, keyLength = -1;
		boolean firstChar = true;
		char[] inbuf = new char[256];
		int inbufCount = 0, inbufPos = 0;

		while (true) {
			if (inbufPos == inbufCount) {
				if ((inbufCount = reader.read(inbuf)) == -1) {
					break;
				}
				inbufPos = 0;
			}
			nextChar = inbuf[inbufPos++];

			if (offset == buf.length) {
				char[] newBuf = new char[buf.length * 2];
				System.arraycopy(buf, 0, newBuf, 0, offset);
				buf = newBuf;
			}
			if (mode == UNICODE) {
				int digit = Character.digit(nextChar, 16);
				if (digit >= 0) {
					unicode = (unicode << 4) + digit;
					if (++count < 4) {
						continue;
					}
				} else {
					throw new IllegalArgumentException();
				}
				mode = NONE;
				buf[offset++] = (char) unicode;
				if (nextChar != '\n') {
					continue;
				}
			}
			if (mode == SLASH) {
				mode = NONE;
				switch (nextChar) {
				case '\r':
					mode = CONTINUE; // Look for a following \n
					continue;
				case '\n':
					mode = IGNORE; // Ignore whitespace on the next line
					continue;
				case 'b':
					nextChar = '\b';
					break;
				case 'f':
					nextChar = '\f';
					break;
				case 'n':
					nextChar = '\n';
					break;
				case 'r':
					nextChar = '\r';
					break;
				case 't':
					nextChar = '\t';
					break;
				case 'u':
					mode = UNICODE;
					unicode = count = 0;
					continue;
				}
			} else {
				switch (nextChar) {
				case '#':
				case '!':
					if (firstChar) {
						while (true) {
							if (inbufPos == inbufCount) {
								if ((inbufCount = reader.read(inbuf)) == -1) {
									inbufPos = -1;
									break;
								}
								inbufPos = 0;
							}
							nextChar = (char) inbuf[inbufPos++]; // & 0xff
																	// not
																	// required
							if (nextChar == '\r' || nextChar == '\n') {
								break;
							}
						}
						continue;
					}
					break;
				case '\n':
					if (mode == CONTINUE) { // Part of a \r\n sequence
						mode = IGNORE; // Ignore whitespace on the next line
						continue;
					}
					// fall into the next case
				case '\r':
					mode = NONE;
					firstChar = true;
					if (offset > 0) {
						if (keyLength == -1) {
							keyLength = offset;
						}
						String temp = new String(buf, 0, offset);
						put(temp.substring(0, keyLength),
								temp.substring(keyLength));
					}
					keyLength = -1;
					offset = 0;
					continue;
				case '\\':
					if (mode == KEY_DONE) {
						keyLength = offset;
					}
					mode = SLASH;
					continue;
				case ':':
				case '=':
					if (keyLength == -1) { // if parsing the key
						mode = NONE;
						keyLength = offset;
						continue;
					}
					break;
				}
				if (Character.isWhitespace(nextChar)) {
					if (mode == CONTINUE) {
						mode = IGNORE;
					}
					// if key length == 0 or value length == 0
					if (offset == 0 || offset == keyLength || mode == IGNORE) {
						continue;
					}
					if (keyLength == -1) { // if parsing the key
						mode = KEY_DONE;
						continue;
					}
				}
				if (mode == IGNORE || mode == CONTINUE) {
					mode = NONE;
				}
			}
			firstChar = false;
			if (mode == KEY_DONE) {
				keyLength = offset;
				mode = NONE;
			}
			buf[offset++] = nextChar;
		}
		if (keyLength == -1 && offset > 0) {
			keyLength = offset;
		}
		if (keyLength >= 0) {
			String temp = new String(buf, 0, offset);
			put(temp.substring(0, keyLength), temp.substring(keyLength));
		}
	}

	private boolean isAscii(BufferedInputStream in) throws IOException {
		byte b;
		while ((b = (byte) in.read()) != -1) {
			if (b == 0x23 || b == 0x0a || b == 0x3d) {// ascii: newline/#/=
				return true;
			}
			if (b == 0x15) {// EBCDIC newline
				return false;
			}
		}
		// we found no ascii newline, '#', neither '=', relative safe to
		// consider it
		// as non-ascii, the only exception will be a single line with only
		// key(no value and '=')
		// in this case, it should be no harm to read it in default charset
		return false;
	}

	public void store(Writer writer, String comments) throws IOException {
		store0((writer instanceof BufferedWriter) ? (BufferedWriter) writer
				: new BufferedWriter(writer), comments, false);
	}

	public void store(OutputStream out, String comments) throws IOException {
		store0(new BufferedWriter(new OutputStreamWriter(out, "8859_1")),
				comments, true);
	}

	private void store0(BufferedWriter bw, String comments, boolean escUnicode)
			throws IOException {
		if (comments != null) {
			writeComments(bw, comments);
		}
		bw.write("#" + new Date().toString());
		bw.newLine();
		synchronized (this) {
			for (Enumeration e = keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String val = (String) get(key);
				key = saveConvert(key, true, escUnicode);
				/*
				 * No need to escape embedded and trailing spaces for value,
				 * hence pass false to flag.
				 */
				val = saveConvert(val, false, escUnicode);
				bw.write(key + "=" + val);
				bw.newLine();
			}
		}
		bw.flush();
	}
	
	/*
     * Converts unicodes to encoded &#92;uxxxx and escapes
     * special characters with a preceding slash
     */
    private String saveConvert(String theString,
                               boolean escapeSpace,
                               boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);

        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\'); outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                          break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                          break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                          break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                          break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\'); outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    private static void writeComments(BufferedWriter bw, String comments)
        throws IOException {
        bw.write("#");
        int len = comments.length();
        int current = 0;
        int last = 0;
        char[] uu = new char[6];
        uu[0] = '\\';
        uu[1] = 'u';
        while (current < len) {
            char c = comments.charAt(current);
            if (c > '\u00ff' || c == '\n' || c == '\r') {
                if (last != current)
                    bw.write(comments.substring(last, current));
                if (c > '\u00ff') {
                    uu[2] = toHex((c >> 12) & 0xf);
                    uu[3] = toHex((c >>  8) & 0xf);
                    uu[4] = toHex((c >>  4) & 0xf);
                    uu[5] = toHex( c        & 0xf);
                    bw.write(new String(uu));
                } else {
                    bw.newLine();
                    if (c == '\r' &&
                        current != len - 1 &&
                        comments.charAt(current + 1) == '\n') {
                        current++;
                    }
                    if (current == len - 1 ||
                        (comments.charAt(current + 1) != '#' &&
                        comments.charAt(current + 1) != '!'))
                        bw.write("#");
                }
                last = current + 1;
            }
            current++;
        }
        if (last != current)
            bw.write(comments.substring(last, current));
        bw.newLine();
    }
    
    /**
     * Convert a nibble to a hex character
     * @param   nibble  the nibble to convert.
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }
    
    /** A table of hex digits */
    private static final char[] hexDigit = {
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };
}
