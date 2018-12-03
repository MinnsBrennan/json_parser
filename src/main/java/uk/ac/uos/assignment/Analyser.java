package uk.ac.uos.assignment;

import java.io.StringReader;

import java.io.PushbackReader;
import java.io.IOException;

//Class reads through JSON and deciphers the symbol types
//of the JSON syntax
public class Analyser {

	// PushbackReader used to move my position forward and
	// back whenever I decided to read ahead more than one
	// character in the JSON
	private PushbackReader pushbackReader;

	// Constructor that takes StringReader as its argument
	public Analyser(StringReader stringReader) {
		pushbackReader = new PushbackReader(stringReader);

	}

	public Symbol next() throws IOException, CustomException {
		Symbol result = null;
		int position = pushbackReader.read();

		// I used isWhitespace and pushbackReader to check 
		// for consecutive whitespace and return to my initial
		// position once ended.
		if ( -1 != position ) {
			if (Character.isWhitespace(position)) {
				result = buildWhitespace(position, result);
			}
			
			// BuildString will check for characters between quotation
			// marks, and return that string as its symbol.
			else if (position == '"' ) {
				result = buildString(position, result);
			}
			
			// These builders will read through the JSON and check if the
			// characters spell the specific boolean; returning its symbol.
			else if (position == 'n') {
				result = buildNullBoolean(position, result);
			}
			else if (position == 't') {
				result = buildTrueBoolean(position, result);
			}
			else if (position == 'f') {
				result = buildFalseBoolean(position, result);
			}
			
			// This builder will read forward until an invalid character
			// is found; returning the valid number symbol. A number could
			// start with either a digit or a - for a negative number.
			else if (Character.isDigit(position) || position == '-') {
				result = buildNumber(position, result);
			}
			
			// Checking single characters to assign them to relative
			// symbols types.
			else if (position == '{') {
				result = new Symbol(Symbol.Type.LEFT_CURLY_BRACKET);
			}
			else if (position == '}') {
				result = new Symbol(Symbol.Type.RIGHT_CURLY_BRACKET);
			}
			else if (position == '[') {
				result = new Symbol(Symbol.Type.LEFT_SQUARE_BRACKET);
			}
			else if (position == ']') {
				result = new Symbol(Symbol.Type.RIGHT_SQUARE_BRACKET);
			}
			else if (position == ',') {
				result = new Symbol(Symbol.Type.COMMA);
			}
			else if (position == ':') {
				result = new Symbol(Symbol.Type.COLON);
			}
		}
		return result;
	}
	
	//Builder for whitespace
	public Symbol buildWhitespace(int position, Symbol result) throws IOException, CustomException {
		while (Character.isWhitespace(position)) {
			position = pushbackReader.read();
			result = new Symbol(Symbol.Type.SPACE);
			pushbackReader.unread(position);
		}
		return result;
	}

	//Builder for a string
	public Symbol buildString(int position, Symbol result) throws IOException, CustomException {
		String finalString = "";
		position = pushbackReader.read();
		while (position != '"') {
			finalString += (char) position;
			position = pushbackReader.read();
		}
		result = new Symbol(Symbol.Type.STRING, finalString);
		return result;

	}
	
	//Builder for a null boolean
	public Symbol buildNullBoolean(int position, Symbol result) throws IOException, CustomException {
		position = pushbackReader.read();
		if (position == 'u') {
			position = pushbackReader.read();
			if (position == 'l') {
				position = pushbackReader.read();
				if (position == 'l') {
					result = new Symbol(Symbol.Type.NULL_BOOLEAN);
				} else {
					throw new CustomException("BOOM! Error. Invalid true value.");
				}
			} else {
				throw new CustomException("BOOM! Error. Invalid true value.");
			}
		} else {
			throw new CustomException("BOOM! Error. Invalid true value.");
		}
		return result;
	}

	//Builder for a true boolean
	public Symbol buildTrueBoolean(int position, Symbol result) throws IOException, CustomException {
		position = pushbackReader.read();
		if (position == 'r') {
			position = pushbackReader.read();
			if (position == 'u') {
				position = pushbackReader.read();
				if (position == 'e') {
					result = new Symbol(Symbol.Type.TRUE_BOOLEAN);
				} else {
					throw new CustomException("BOOM! Error. Invalid true value.");
				}
			} else {
				throw new CustomException("BOOM! Error. Invalid true value.");
			}
		} else {
			throw new CustomException("BOOM! Error. Invalid true value.");
		}
		return result;
	}
	
	//Builder for a false boolean
	public Symbol buildFalseBoolean(int position, Symbol result) throws IOException, CustomException {
		position = pushbackReader.read();
		if (position == 'a') {
			position = pushbackReader.read();
			if (position == 'l') {
				position = pushbackReader.read();
				if (position == 's') {
					position = pushbackReader.read();
					if (position == 'e') {
						result = new Symbol(Symbol.Type.FALSE_BOOLEAN);
					} else {
						throw new CustomException("BOOM! Error. Invalid true value.");
					}
				} else {
					throw new CustomException("BOOM! Error. Invalid true value.");
				}
			} else {
				throw new CustomException("BOOM! Error. Invalid true value.");
			}
		} else {
			throw new CustomException("BOOM! Error. Invalid true value.");
		}
		return result;
	}
	
	// Builder for a number
	public Symbol buildNumber(int position, Symbol result) throws IOException, CustomException {
		String finalNumber = "";
		while (Character.isDigit(position) || position == '.' || position == '-' || position == '+' || position == 'e' || position == 'E') {
			finalNumber += (char) position;

			position = pushbackReader.read();
		}
		result = new Symbol(Symbol.Type.NUMBER, finalNumber);
		pushbackReader.unread(position);
		return result;
	}
}