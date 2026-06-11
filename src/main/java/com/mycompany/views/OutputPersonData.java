package com.mycompany.views;

import java.util.List;

import com.mycompany.imagesdataset.ImageProperties;
import com.mycompany.imagesdataset.PersonInfo;

public class OutputPersonData {

	private String personName;
	private String standardizedName;
	private String personIdentification;
	private int personId;
	private List<String> synonyms;
	private String folderName;
	private String source;
	private int bulkId;
	private String operatorId;
	private String dateAndTime;
	private PersonInfo personInfo;
	private String imageRecopilationTool;
	private String imageRecopilationMethod;

	public OutputPersonData(String personName, String standardizeName, int personId, String personIdentification,
			List<String> synonyms, int bulkId,
			String operatorId, String source, String dateAndTime, PersonInfo personInfo) {
		this.personName = personName;
		this.standardizedName = standardizeName;
		this.personId = personId;
		this.personIdentification = personIdentification;
		this.synonyms = synonyms;
		this.bulkId = bulkId;
		this.operatorId = operatorId;
		this.source = source;
		this.dateAndTime = dateAndTime;
		this.personInfo = personInfo;
		this.imageRecopilationTool = ImageProperties.IMAGE_RECOPILATION_TOOL;
		this.imageRecopilationMethod = ImageProperties.IMAGE_RECOPILATION_METHOD;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public String getSourceWebsite() {
		return source;
	}

	public String getFolderName() {
		return folderName;
	}

	public int getBulkId() {
		return bulkId;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}

	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public String getPersonName() {
		return personName;
	}

	public int getPersonId() {
		return personId;
	}

	public String getStandardizedName() {
		return standardizedName;
	}

	public String getPersonIdentification() {
		return personIdentification;
	}

	public String getImageRecopilationTool() {
		return imageRecopilationTool;
	}

	public String getImageRecopilationMethod() {
		return imageRecopilationMethod;
	}
}
