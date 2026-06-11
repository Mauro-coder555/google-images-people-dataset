package com.mycompany.imagesdataset;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Person {

	@SerializedName("name")
	private final String personName;
	private final int id;
	private List<String> synonyms;
	@SerializedName("person_info")
	private PersonInfo personInfo;
	@SerializedName("standardized_name")
	private final String standardizedName;

	public Person(String personName, int id, List<String> synonyms, String standardizedName, PersonInfo personInfo) {
		this.personName = personName;
		this.id = id;
		this.synonyms = synonyms;
		this.personInfo = personInfo;
		this.standardizedName = standardizedName;
	}

	public String getPersonName() {
		return personName;
	}

	public String getStandardizedName() {
		return standardizedName;
	}

	public int getId() {
		return id;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

	public String getFormatedId() {
		return String.format("%08d", id);
	}

	public String getPersonIdentification() {
		return String.format("p%s+%s", getFormatedId(), getStandardizedName());
	}
}
