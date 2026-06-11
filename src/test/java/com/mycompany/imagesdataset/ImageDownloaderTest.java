package com.mycompany.imagesdataset;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

//import javax.activation.MimetypesFileTypeMap;

import org.junit.jupiter.api.Test;

public class ImageDownloaderTest {

	@Test
	public void TestDownloadImages() {

		File tempDestFolder = null;
		File tempNamesFile = null;
		try {
			tempDestFolder = Files.createTempDirectory("java-").toFile();
			tempNamesFile = File.createTempFile("tempNames", null);
			tempNamesFile.deleteOnExit();
			tempDestFolder.deleteOnExit();
			Files.write(tempNamesFile.toPath(), ("Lionel Messi" + System.lineSeparator() + "Tom Cruise").getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
		}

		try {
			ImageManager.downloadImages(tempDestFolder.getAbsolutePath(), tempNamesFile.getAbsolutePath(), 10, "MG",
					new ArrayList<String>());
		} catch (Exception e) {
		}

		Arrays.stream(tempDestFolder.listFiles()).flatMap(personFolder -> Arrays.stream(personFolder.listFiles()))
				.forEach(image -> {
					{
						//String mimetype = new MimetypesFileTypeMap().getContentType(image);
					//	String type = mimetype.split("/")[0];
					//	System.out.println(type.equals("image"));
					//	assertEquals(true, type.equals("image"));
					} ;
				});
	}

}
