package com.mycompany.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

	private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);
	private static final String ERROR_CONFIG_PROPERTIES = "Error with config properties";

	private FileUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getProperty(String propertyName) {
		String configFile = "config.properties";
		Properties properties = new Properties();

		try (FileInputStream inputStream = new FileInputStream(configFile)) {
			properties.load(inputStream);
		} catch (IOException e) {
			LOG.error("Error getting property in config properties", e);
		}

		return properties.getProperty(propertyName);
	}

	public static String getBulkId() {
		String configFile = "config.properties";
		Properties properties = new Properties();

		try (FileInputStream inputStream = new FileInputStream(configFile)) {
			properties.load(inputStream);
		} catch (IOException e) {
			LOG.error(ERROR_CONFIG_PROPERTIES, e);
		}

		int counter = Integer.parseInt(properties.getProperty("bulk"));
		counter++;
		properties.setProperty("bulk", Integer.toString(counter));
		try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
			properties.store(outputStream, "");
		} catch (IOException e) {
			LOG.error(ERROR_CONFIG_PROPERTIES, e);
		}
		return String.format("%04d", counter - 1);
	}

	public static void generateTarGz(String outputFilePath, String[] folders) {
		try (FileOutputStream outFile = new FileOutputStream(outputFilePath);
				BufferedOutputStream outBuffer = new BufferedOutputStream(outFile);
				GzipCompressorOutputStream outGzip = new GzipCompressorOutputStream(outBuffer);
				TarArchiveOutputStream outTar = new TarArchiveOutputStream(outGzip)) {
			outTar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

			for (String folder : folders) {
				File file = new File(folder);
				addFolder(file, outTar, "");
			}
		} catch (IOException e) {
			LOG.error("An error creating tar.gz", e);
		}
	}

	public static String createFolder(String destinationFolderPath, String folderName) {
		Path path = Paths.get(destinationFolderPath, folderName);
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			LOG.error("An error creating {} folder", folderName, e);
		}
		return path.toString();
	}

	public static void removeAuxFolders(String destinationFolderPath) {
		File[] files = new File(destinationFolderPath).listFiles();
		for (File f : files) {
			if (f.isFile() && f.getName().endsWith(".tar.gz")) {
				continue;
			}
			try {
				removeFolder(f);
			} catch (IOException e) {
				LOG.error("An error removing aux folders", e);
			}
		}
	}

	public static void removeFolder(File folder) throws IOException {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					removeFolder(file);
				} else {
					Files.delete(file.toPath());
				}
			}
		}
		Files.delete(folder.toPath());
	}

	private static void addFolder(File folder, TarArchiveOutputStream outTar, String relativePath)
			throws IOException {

		TarArchiveEntry tarEntry = new TarArchiveEntry(folder, relativePath + folder.getName() + "/");
		outTar.putArchiveEntry(tarEntry);
		outTar.closeArchiveEntry();

		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				addFolder(file, outTar, relativePath + folder.getName() + "/");
			} else {
				tarEntry = new TarArchiveEntry(file, relativePath + folder.getName() + "/" + file.getName());
				outTar.putArchiveEntry(tarEntry);
				try (BufferedInputStream entryFile = new BufferedInputStream(new FileInputStream(file))) {
					byte[] buffer = new byte[1024];
					int read;
					while ((read = entryFile.read(buffer)) != -1) {
						outTar.write(buffer, 0, read);
					}
				} catch (IOException e) {
					LOG.error("An error adding folder", e);
				}
				outTar.closeArchiveEntry();
			}
		}
	}

}
