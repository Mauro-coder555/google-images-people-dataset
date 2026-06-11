package com.mycompany.imagesdataset;

import com.mycompany.utils.FileUtils;
import com.mycompany.views.OutputPersonData;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ImageManager {

	private ImageManager() {
	}

	private static final Logger LOG = LoggerFactory.getLogger(ImageManager.class);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	private static final Gson GSON = new GsonBuilder()
			.disableHtmlEscaping()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.setPrettyPrinting()
			.create();

	private static final boolean REAL_FACE_TYPE = ImageProperties.REAL_FACE_TYPE;
	private static final boolean IDENTIFIED_FACE = ImageProperties.IDENTIFIED_FACE;

	public static void downloadImages(String destinationFolderPath, String peopleFilePath, int imagesAmount,
			String operatorId,
			List<String> valueFilterList) {
		String folderToCompressPath = getWorkFolders(destinationFolderPath);

		ImageDownloader myImageDownloader = new ImageDownloader(folderToCompressPath,
				imagesAmount,
				valueFilterList,
				FileUtils.getBulkId(), operatorId);
		StopWatch timer = StopWatch.createStarted();
		try (Reader reader = Files.newBufferedReader(Paths.get(peopleFilePath))) {
			Gson gson = new Gson();
			Type listType = new TypeToken<List<Person>>() {}.getType();
			List<Person> people = gson.fromJson(reader, listType);

			people.stream()
					.filter(Objects::nonNull)
					.forEach(person -> {
						if (IDENTIFIED_FACE) {
							generatePersonJson(folderToCompressPath,
									myImageDownloader.getSourceWebsite(),
									myImageDownloader.getOperatorId(),
									myImageDownloader.getBulkId(),
									person);
						}
						myImageDownloader.downloadImages(person);
					});
		} catch (IOException e) {
			LOG.error("Error parsing people file", e);
		}

		myImageDownloader.closeDriver();
		timer.stop();
		LOG.info("Tiempo de ejecucion de downloadImages(): {} ms", timer.getTime());

		getTarFile(destinationFolderPath, folderToCompressPath, myImageDownloader);
	}

	public static void getTarFile(String destinationFolderPath, String finalFolderPath,
			ImageDownloader myImageDownloader) {
		String nameTarGzFile = String.format("%s-%s-b%s.tar.gz", myImageDownloader.getSourceWebsite(), myImageDownloader
				.getOperatorId(), myImageDownloader
						.getBulkId());

		FileUtils.generateTarGz(destinationFolderPath + "/" + nameTarGzFile, new String[] {finalFolderPath});

		FileUtils.removeAuxFolders(destinationFolderPath);

	}

	private static String getWorkFolders(String destinationFolderPath) {
		String faceTypeFolder;

		if (REAL_FACE_TYPE)
			faceTypeFolder = FileUtils.createFolder(destinationFolderPath, "1_real");
		else faceTypeFolder = FileUtils.createFolder(destinationFolderPath, "0_fake");

		return faceTypeFolder;
	}

	private static void generatePersonJson(String destPath, String sourceWebsite,
			String operatorId, String bulkId, Person p) {

		String fullPath = String.format("%s/person-%s-%s-%s-b%s.json", destPath, p.getPersonIdentification(),
				sourceWebsite,
				operatorId, bulkId);

		savePersonJson(fullPath, p, sourceWebsite, operatorId, bulkId);
	}

	private static void savePersonJson(String fullPath, Person p, String sourceWebsite, String operatorId,
			String bulkId) {
		OutputPersonData output = new OutputPersonData(p.getPersonName(), p.getStandardizedName(), p.getId(), p
				.getPersonIdentification(), p.getSynonyms(), Integer.parseInt(
						bulkId), operatorId, sourceWebsite, DATE_FORMATTER.format(
								LocalDateTime.now()), p.getPersonInfo());

		try (FileWriter writer = new FileWriter(fullPath)) {
			writer.write(GSON.toJson(output));
		} catch (IOException e) {
			LOG.error("Error writing person json", e);
		}
	}
}
