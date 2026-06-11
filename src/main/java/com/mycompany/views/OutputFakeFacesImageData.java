package com.mycompany.views;

import com.mycompany.utils.FileUtils;

public class OutputFakeFacesImageData extends OutputImageData {

	private final String fakeGroup;
	private final String fakeType;
	private final String fakeDetailedType;
	private final String artworkGenerationMethod;

	public OutputFakeFacesImageData(String date, String query, String url, int imageId, String operatorId,
			String hashId,
			int bulkId, String source, Boolean identifiedFace, Integer personId, String personName,
			String personIdentification, String standardizeName, String imageExtension,
			String lastSixHashId, String originality) {
		super(date, query, url, imageId, operatorId, hashId, bulkId, source, identifiedFace, personId, personName,
				personIdentification, standardizeName,
				imageExtension, lastSixHashId, originality);
		this.fakeGroup = FileUtils.getProperty("fake_group");
		this.fakeType = FileUtils.getProperty("fake_type");
		this.fakeDetailedType = FileUtils.getProperty("fake_detailed_type");
		this.artworkGenerationMethod = FileUtils.getProperty("artwork_generation_method");
	}

	public String getFakeGroup() {
		return fakeGroup;
	}

	public String getFakeType() {
		return fakeType;
	}

	public String getFakeDetailedType() {
		return fakeDetailedType;
	}

	public String getArtworkGenerationMethod() {
		return artworkGenerationMethod;
	}

}
