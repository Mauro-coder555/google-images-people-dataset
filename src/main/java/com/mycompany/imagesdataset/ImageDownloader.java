package com.mycompany.imagesdataset;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.tika.Tika;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mycompany.exceptions.ImageLimitReachedException;
import com.mycompany.utils.WebDriverUtils;
import com.mycompany.views.OutputFakeFacesImageData;
import com.mycompany.views.OutputImageData;

public class ImageDownloader {

	private AtomicLong totalSaveTime;

	private static final Logger LOG = LoggerFactory.getLogger(ImageDownloader.class);

	private static final boolean REAL_FACE_TYPE = ImageProperties.REAL_FACE_TYPE;
	private static final boolean IDENTIFIED_FACE = ImageProperties.IDENTIFIED_FACE;
	private static final String FACE_TYPE = REAL_FACE_TYPE ? "real" : "fake";
	private static final String ORIGINALITY = "origin";

	private static final String SOURCE_WEBSITE = "google_images";
	private static final String BASE_64 = "base64";
	private static final String ENCRYPTED = "encrypted";

	private static final By GOOGLE_IMAGE_THUMBNAILS = By.cssSelector(
			"img.YQ4gaf, img.Q4LuWd, img.rg_i, div[data-ri] img, a[href*='imgurl='] img"
	);

	private static final By GOOGLE_FULL_IMAGES = By.cssSelector(
		"img.sFlh5c, img.iPVvYb, img.n3VNCb, a[href*='imgurl=']"
	);

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private static final Gson GSON = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.disableHtmlEscaping()
			.setPrettyPrinting()
			.create();

	private final String destinationFolderPath;
	private final ChromeDriver driver;
	private final int imagesAmount;
	private final List<String> filterList;
	private String bulkId;
	private String operatorId;

	private static final Map<String, String> EXTENSIONS = Map.of(
			"image/jpeg", "jpg",
			"image/png", "png",
			"image/x-ms-bmp", "bmp",
			"image/webp", "webp",
			"application/octet-stream", "webp"
	);

	public ImageDownloader(String destinationFolderPath, int imagesAmount, List<String> filtersList,
			String bulkId, String operatorId) {
		this.destinationFolderPath = destinationFolderPath;
		this.imagesAmount = imagesAmount;
		this.filterList = filtersList;
		this.bulkId = bulkId;
		this.operatorId = operatorId;
		this.totalSaveTime = new AtomicLong(0);

		ChromeOptions options = new ChromeOptions();
		//options.addArguments("--headless=new");
		options.addArguments("--lang=en");
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--disable-gpu");
		options.addArguments("--window-size=1920,1080");

		driver = new ChromeDriver(options);
	}

	public void downloadImages(Person p) {
		AtomicInteger imageIndex = new AtomicInteger(1);
		StopWatch timer = StopWatch.createStarted();
		AtomicLong totalTime = new AtomicLong(0);
		long duration = 0;

		driver.manage().window().maximize();

		try {
			for (UrlQuery urlQuery : getUrlQuerys(p)) {
				driver.get(urlQuery.getUrl());

				long startTimeScroll = System.nanoTime();

				try {
					WebDriverUtils.showAllResults(driver);
				} catch (Exception e) {
					LOG.info("No se pudo ejecutar showAllResults, se continua con los resultados visibles");
				}

				long endTimeScroll = System.nanoTime();
				duration = (endTimeScroll - startTimeScroll) / 1000000;

				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

				checkExistenceOfResults(wait, urlQuery);

				long startTimeUrls = System.currentTimeMillis();
				List<String> urls = collectImageUrls(wait, imagesAmount);
				long elapsedTimeUrls = System.currentTimeMillis() - startTimeUrls;

				LOG.info("Tiempo demorado en obtener URLs: {} ms", elapsedTimeUrls);
				totalTime.addAndGet(elapsedTimeUrls);

				for (String imgUrl : urls) {
					boolean saved = this.saveImageWithTimeout(p, imgUrl, imageIndex.get(), urlQuery);

					if (saved) {
						imageIndex.incrementAndGet();
					}

					if (imageIndex.get() > imagesAmount) {
						throw new ImageLimitReachedException("Limite de imagenes alcanzado");
					}
				}
			}
		} catch (ImageLimitReachedException e) {
			LOG.info(e.getMessage());
		}

		timer.stop();

		registerProfile(
				p.getPersonName(),
				p.getId(),
				duration,
				totalTime.get(),
				totalSaveTime.get(),
				timer.getTime()
		);

		imageIndex.set(1);
		this.totalSaveTime.set(0);
	}

	private List<String> collectImageUrls(WebDriverWait wait, int limit) {
		Set<String> urls = new LinkedHashSet<>();
		JavascriptExecutor executor = driver;

		List<WebElement> thumbnails = new ArrayList<>(driver.findElements(GOOGLE_IMAGE_THUMBNAILS));

		LOG.info("Thumbnails encontrados: {}", thumbnails.size());

		for (WebElement thumbnail : thumbnails) {
			if (urls.size() >= limit) {
				break;
			}

			try {
				executor.executeScript("arguments[0].scrollIntoView({block: 'center'});", thumbnail);
				executor.executeScript("arguments[0].click();", thumbnail);

				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(GOOGLE_FULL_IMAGES));

				for (WebElement element : driver.findElements(GOOGLE_FULL_IMAGES)) {
					String url = extractImageUrl(element);

					if (isValidImageUrl(url)) {
						urls.add(url);
						LOG.info("URL encontrada: {}", url);

						if (urls.size() >= limit) {
							break;
						}
					}
				}
			} catch (Exception e) {
				LOG.info("No se pudo obtener imagen desde thumbnail");
			}
		}

		LOG.info("URLs finales encontradas: {}", urls.size());

		return new ArrayList<>(urls);
	}

	private void checkExistenceOfResults(WebDriverWait wait, UrlQuery urlQuery) {
		try {
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(GOOGLE_IMAGE_THUMBNAILS));
		} catch (TimeoutException e) {
			LOG.info("There are no results for query {}:", urlQuery.getQuery());
		}
	}

	private static String extractImageUrl(WebElement element) {
	String href = element.getAttribute("href");

	if (isEncodedUrlWithExtraInfo(href)) {
		String finalUrl = getFinalUrl(href);

		if (isValidImageUrl(finalUrl)) {
			return finalUrl;
		}
	}

	String src = element.getAttribute("src");

	if (isValidImageUrl(src)) {
		return getFinalUrl(src);
	}

	String dataSrc = element.getAttribute("data-src");

	if (isValidImageUrl(dataSrc)) {
		return getFinalUrl(dataSrc);
	}

	return null;
}

	private static boolean isValidImageUrl(String url) {
	if (url == null || url.isBlank()) {
		return false;
	}

	if (!url.startsWith("http")) {
		return false;
	}

	String lowerUrl = url.toLowerCase();

	if (lowerUrl.contains(BASE_64) || lowerUrl.startsWith("data:")) {
		return false;
	}

	if (lowerUrl.contains("encrypted-tbn0.gstatic.com")) {
		return false;
	}

	if (lowerUrl.contains("gstatic.com")) {
		return false;
	}

	if (lowerUrl.contains("google.com/logos")) {
		return false;
	}

	if (lowerUrl.contains("fonts.gstatic.com")) {
		return false;
	}

	if (lowerUrl.endsWith(".svg")) {
		return false;
	}

	if (lowerUrl.endsWith(".ico")) {
		return false;
	}

	return true;
}

	public void closeDriver() {
		driver.quit();
	}

	public boolean saveImage(Person p, String imageUrl, String personFolderPath, int index,
			UrlQuery urlQuery) {
		AtomicLong startTime = new AtomicLong();
		startTime.set(System.currentTimeMillis());

		boolean saveSuccessful = true;
		String imageId = String.format("%08d", index);
		String personIdentification = IDENTIFIED_FACE ? p.getPersonIdentification() : "unidentified";

		String destPath = String.format(
				"%s/%s-%s-b%s-%s-%s-i%s",
				personFolderPath,
				SOURCE_WEBSITE,
				operatorId,
				bulkId,
				FACE_TYPE,
				personIdentification,
				imageId
		);

		if (Strings.isNullOrEmpty(imageUrl)) {
			saveSuccessful = false;
		} else {
			try {
				saveHttps(imageUrl, destPath, imageId, urlQuery, p);
				saveSuccessful = true;
			} catch (IOException | IllegalStateException | NullPointerException | IllegalArgumentException
					| NoSuchAlgorithmException e) {
				LOG.error("An error downloading or converting an image from url", e);
				saveSuccessful = false;
			}
		}

		long elapsedTime = System.currentTimeMillis() - startTime.get();

		LOG.info("Tiempo demorado en guardar imagen {}: {} ms", imageId, elapsedTime);

		totalSaveTime.addAndGet(elapsedTime);

		return saveSuccessful;
	}

	private void saveHttps(String imageUrl, String destPath, String imageId, UrlQuery urlQuery,
			Person p) throws IOException, IllegalArgumentException, NoSuchAlgorithmException {

		LOG.info("QUERY:{} - LINK N:{} {}", urlQuery.getQuery(), imageId, imageUrl);

		String personIdentification = IDENTIFIED_FACE ? p.getPersonIdentification() : "unidentified";
		BufferedImage image = ImageIO.read(new URL(imageUrl).openStream());

		if (image == null) {
			throw new IllegalStateException("ImageIO could not read image");
		}

		String extension = getExtension(imageUrl);
		String hashId = getHashId(image, extension);
		String lastSixHash = getLastSixHash(hashId);

		String fullDestPath = String.format(
				"%s-%s-%s.%s",
				destPath,
				lastSixHash,
				ORIGINALITY,
				extension
		);

		boolean canWrite = ImageIO.write(image, extension, new FileOutputStream(fullDestPath));

		if (!canWrite) {
			throw new IllegalStateException();
		} else {
			saveImageJson(
					p,
					String.format("%s-%s-%s.%s", destPath, lastSixHash, ORIGINALITY, "json"),
					urlQuery.getQuery(),
					imageUrl,
					imageId,
					operatorId,
					bulkId,
					hashId,
					extension,
					personIdentification,
					lastSixHash
			);
		}
	}

	private static void saveImageJson(Person p, String destPath, String query, String url, String imageId,
			String operatorId, String bulkId, String hashId, String extension, String personIdentification,
			String lastSixHash) {

		String personName;
		String standardizedName;
		Integer personId;

		if (!IDENTIFIED_FACE) {
			personName = null;
			standardizedName = null;
			personId = null;
		} else {
			personName = p.getPersonName();
			standardizedName = p.getStandardizedName();
			personId = p.getId();
		}

		OutputImageData output = REAL_FACE_TYPE
				? new OutputImageData(
						DATE_FORMATTER.format(LocalDateTime.now()),
						query,
						url,
						Integer.parseInt(imageId),
						operatorId,
						hashId,
						Integer.parseInt(bulkId),
						SOURCE_WEBSITE,
						IDENTIFIED_FACE,
						personId,
						personName,
						personIdentification,
						standardizedName,
						extension,
						lastSixHash,
						ORIGINALITY
				)
				: new OutputFakeFacesImageData(
						DATE_FORMATTER.format(LocalDateTime.now()),
						query,
						url,
						Integer.parseInt(imageId),
						operatorId,
						hashId,
						Integer.parseInt(bulkId),
						SOURCE_WEBSITE,
						IDENTIFIED_FACE,
						personId,
						personName,
						personIdentification,
						standardizedName,
						extension,
						lastSixHash,
						ORIGINALITY
				);

		try (FileWriter writer = new FileWriter(destPath)) {
			writer.write(GSON.toJson(output));
		} catch (IOException e) {
			LOG.error("Error writing image json", e);
		}
	}

	private static String getHashId(BufferedImage image, String extension)
			throws IOException, NoSuchAlgorithmException {
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

		ImageIO.write(image, extension, byteArrayOut);

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		return new BigInteger(1, md.digest(byteArrayOut.toByteArray())).toString(16);
	}

	private static String getLastSixHash(String hashId) {
		return hashId.substring(hashId.length() - 6);
	}

	private static boolean isEncodedUrlWithExtraInfo(String url) {
		return url != null && url.contains("%3A%2F%2F") && url.contains("imgurl=");
	}

	private static String getFinalUrl(String imageUrl) {
		if (imageUrl == null) {
			return null;
		}

		if (isEncodedUrlWithExtraInfo(imageUrl)) {
			int startIndex = imageUrl.indexOf("imgurl=") + 7;
			int endIndex = imageUrl.indexOf('&', startIndex);

			if (endIndex == -1) {
				endIndex = imageUrl.length();
			}

			return URLDecoder.decode(imageUrl.substring(startIndex, endIndex), StandardCharsets.UTF_8);
		}

		return URLDecoder.decode(imageUrl, StandardCharsets.UTF_8);
	}

	private static String getExtension(String imageUrl) throws IOException {
		String mimeType = new Tika().detect(new URL(imageUrl).openStream());

		return EXTENSIONS.getOrDefault(mimeType, "png");
	}

	private List<UrlQuery> getUrlQuerys(Person p) {
		List<String> synonyms = p.getSynonyms();

		Stream<String> names = Stream.concat(
				Stream.of(p.getPersonName()),
				synonyms != null ? synonyms.stream() : Stream.empty()
		);

		return names.map(name -> new UrlQuery(name, filterList))
				.collect(Collectors.toList());
	}

	public String getSourceWebsite() {
		return SOURCE_WEBSITE;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public String getBulkId() {
		return bulkId;
	}

	public boolean saveImageWithTimeout(Person p, String imageUrl, int index,
			UrlQuery urlQuery) {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Callable<Boolean> callable = () -> saveImage(p, imageUrl, destinationFolderPath, index, urlQuery);

		try {
			Future<Boolean> future = executor.submit(callable);
			boolean result = future.get(1, TimeUnit.MINUTES);

			executor.shutdown();

			return result;
		} catch (java.util.concurrent.TimeoutException | ExecutionException e) {
			LOG.error("Descarga cancelada por exceder limite de tiempo", e);
			registerProfileFailSaves(p, imageUrl, index, urlQuery);
			executor.shutdown();

			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			executor.shutdown();

			return false;
		}
	}

	private static void registerProfileFailSaves(Person p, String imageUrl, int index,
			UrlQuery urlQuery) {

		String json = readJsonArrayFile("failsProfile.json");
		JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

		JsonObject newJsonObject = new JsonObject();

		newJsonObject.addProperty("nombre", p.getPersonName());
		newJsonObject.addProperty("id", p.getId());
		newJsonObject.addProperty("url", imageUrl);
		newJsonObject.addProperty("query", urlQuery.getQuery());
		newJsonObject.addProperty("image_id", index);

		JsonElement cantFallosElement = newJsonObject.get("cant_fallos");

		if (cantFallosElement == null) {
			newJsonObject.addProperty("cant_fallos", 1);
		} else {
			int cantFallosValue = cantFallosElement.getAsInt();
			newJsonObject.addProperty("cant_fallos", cantFallosValue + 1);
		}

		jsonArray.add(newJsonObject);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String updatedJson = gson.toJson(jsonArray);

		try (FileWriter writer = new FileWriter("failsProfile.json")) {
			writer.write(updatedJson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void registerProfile(String name, int i, long duration, long l, long m, long n) {
		String json = readJsonArrayFile("profile.json");
		JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

		JsonObject newJsonObject = new JsonObject();

		newJsonObject.addProperty("nombre", name);
		newJsonObject.addProperty("id", i);
		newJsonObject.addProperty("total_time_scroll_min", (duration / 1000) / 60);
		newJsonObject.addProperty("total_time_urls_min", (l / 1000) / 60);
		newJsonObject.addProperty("total_time_imgs_min", (m / 1000) / 60);
		newJsonObject.addProperty("total_time_person_min", (n / 1000) / 60);
		newJsonObject.addProperty("total_time_scroll_ms", duration);
		newJsonObject.addProperty("total_time_urls_ms", l);
		newJsonObject.addProperty("total_time_imgs_ms", m);
		newJsonObject.addProperty("total_time_person_ms", n);

		jsonArray.add(newJsonObject);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String updatedJson = gson.toJson(jsonArray);

		try (FileWriter writer = new FileWriter("profile.json")) {
			writer.write(updatedJson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String readJsonArrayFile(String fileName) {
		try {
			if (!Files.exists(Paths.get(fileName))) {
				Files.writeString(Paths.get(fileName), "[]", StandardCharsets.UTF_8);
			}

			String json = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);

			if (json == null || json.isBlank()) {
				return "[]";
			}

			return json;
		} catch (IOException e) {
			e.printStackTrace();
			return "[]";
		}
	}
}