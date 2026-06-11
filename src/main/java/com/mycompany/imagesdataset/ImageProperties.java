package com.mycompany.imagesdataset;

import com.mycompany.utils.FileUtils;

public final class ImageProperties {

	public static final boolean REAL_FACE_TYPE = FileUtils.getProperty("face_type").equals("real");
	public static final boolean IDENTIFIED_FACE = Boolean.parseBoolean(FileUtils.getProperty("identified_face"));
	public static final String IMAGE_RECOPILATION_METHOD = FileUtils.getProperty("image_recopilation_tool");
	public static final String IMAGE_RECOPILATION_TOOL = FileUtils.getProperty("image_recopilation_method");

	private ImageProperties() {
	}
}
