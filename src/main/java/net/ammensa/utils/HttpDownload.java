package net.ammensa.utils;

import net.ammensa.exception.HttpDownloadException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class HttpDownload {

	private static final Logger log = Logger.getLogger("debug");

	public static byte[] download(String resourceUrl) throws HttpDownloadException {
		byte[] resource;
		try {
			URL url = new URL(resourceUrl);
			log.info("starting download from " + resourceUrl);
			resource = retrieveResourceFromUrl(url);
			log.info("... done");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new HttpDownloadException("cannot complete the download: " + ex.getMessage());
		}
		return resource;
	}

	private static byte[] retrieveResourceFromUrl(URL url) throws IOException {

		InputStream input = null;
		ByteArrayOutputStream output = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(0);
			conn.setUseCaches(false);
			int httpResponseCode = conn.getResponseCode();
			if (httpResponseCode == HttpURLConnection.HTTP_OK) {
				input = conn.getInputStream();
				output = writeByteArrayOutputStreamFromInputStream(input);
			} else {
				throw new IOException("response code " + httpResponseCode);
			}
		} finally {
			if (input != null) {
				input.close();
				conn.disconnect();
			}
		}
		return output.toByteArray();
	}

	private static ByteArrayOutputStream writeByteArrayOutputStreamFromInputStream(
			InputStream inputStream) throws IOException {

		InputStream input = inputStream;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int length;
		while ((length = input.read(data)) != -1) {
			buffer.write(data, 0, length);
		}
		return buffer;
	}
}