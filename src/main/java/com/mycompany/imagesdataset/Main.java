package com.mycompany.imagesdataset;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			try (InputStream input = new FileInputStream(args[0])) {
				Properties prop = new Properties();
				prop.load(input);

				String folderPath = prop.getProperty("destinationFolderPath", "");
				String peopleFilePath = prop.getProperty("peopleFilePath", "");
				int imagesAmount = Integer.parseInt(prop.getProperty("imagesAmount", "1000"));
				String operatorId = prop.getProperty("operatorId", "");

				ImageManager.downloadImages(folderPath, peopleFilePath, imagesAmount, operatorId, SearchFilters
						.getFilters(prop));
			}
		} else {
			EventQueue.invokeLater(() -> {
				Window frame = new Window();
				frame.setVisible(true);
			});
		}
	}

}
