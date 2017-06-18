package net.muse.misc;

import java.nio.charset.UnsupportedCharsetException;

public class UnsupportedParameterTypeException extends
		UnsupportedCharsetException {

	private static final long serialVersionUID = 1L;

	public UnsupportedParameterTypeException(String charsetName) {
		super(charsetName);
	}

}
