package com.mycompany.views;

import com.mycompany.imagesdataset.ImageProperties;
import com.mycompany.utils.FileUtils;

public class OutputImageData {

	private String dateAndTime;
	private String query;
	private String url;
	private int imageId;
	private String operatorId;
	private String hash;
	private int bulkId;
	private String source;
	private boolean identifiedFace;
	private final String faceType;
	private String imageExtension;
	private String personIdentification;
	private String hashId;
	private String originality;
	private String imageRecopilationTool;
	private String imageRecopilationMethod;
	private String standardizedName;
	private Integer personId;
	private String personName;

	public OutputImageData(String date, String query, String url, int imageId, String operatorId, String hash,
			int bulkId, String source, boolean identifiedFace, Integer personId, String personName,
			String personIdentification, String standardizeName, String imageExtension,
			String hashId, String originality) {
		this.dateAndTime = date;
		this.query = query;
		this.url = url;
		this.imageId = imageId;
		this.operatorId = operatorId;
		this.hash = hash;
		this.bulkId = bulkId;
		this.source = source;
		this.identifiedFace = identifiedFace;
		this.faceType = FileUtils.getProperty("face_type");
		this.imageExtension = imageExtension;
		this.personId = personId;
		this.personName = personName;
		this.standardizedName = standardizeName;
		this.personIdentification = personIdentification;
		this.hashId = hashId;
		this.originality = originality;
		this.imageRecopilationTool = ImageProperties.IMAGE_RECOPILATION_TOOL;
		this.imageRecopilationMethod = ImageProperties.IMAGE_RECOPILATION_METHOD;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}

	public String getQuery() {
		return query;
	}

	public String getUrl() {
		return url;
	}

	public int getImageId() {
		return imageId;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public String getHash() {
		return hash;
	}

	public int getBulkId() {
		return bulkId;
	}

	public String getSource() {
		return source;
	}

	public boolean getIdentifiedFace() {
		return identifiedFace;
	}

	public String getFaceType() {
		return faceType;
	}

	public String getImageExtension() {
		return imageExtension;
	}

	public String getPersonIdentification() {
		return personIdentification;
	}

	public String getHashId() {
		return hashId;
	}

	public String getOriginality() {
		return originality;
	}

	public String getImageRecopilationTool() {
		return imageRecopilationTool;
	}

	public String getImageRecopilationMethod() {
		return imageRecopilationMethod;
	}

	public String getStandardizedName() {
		return standardizedName;
	}

	public Integer getPersonId() {
		return personId;
	}

	public String getPersonName() {
		return personName;
	}
}
