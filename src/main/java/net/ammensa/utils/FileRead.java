package net.ammensa.utils;

import net.ammensa.exception.FileReadException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileRead {

	public static byte[] readFile(String uri) throws FileReadException {
		byte[] resource;
			try {
				System.out.print("reading file from: " + uri);
				resource = retrieveResourceFromUri(uri);
				System.out.println("... done");
			} catch (IOException ioe) {
				FileReadException fre = new FileReadException();
				fre.initCause(ioe);
				throw fre;
			}
			return resource;
	}

	private static byte[] retrieveResourceFromUri(String uri)
			throws IOException {

		InputStream input = null;
		try {
			input = new FileInputStream(uri);
			if (input != null && input.available() != 0) {
				ByteArrayOutputStream outputStream = writeByteArrayOutputStreamFromInputStream(input);
				return outputStream.toByteArray();
			} else {
				throw new IOException("the local resource is not available");
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	private static ByteArrayOutputStream writeByteArrayOutputStreamFromInputStream(
			InputStream inputStream) throws IOException {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] chunk = new byte[4096];
		int length;
		while ((length = inputStream.read(chunk)) != -1) {
			buffer.write(chunk, 0, length);
		}
		return buffer;
	}
}