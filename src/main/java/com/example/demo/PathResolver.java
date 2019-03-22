package com.example.demo;

public class PathResolver {
	static final String windows_image_path="d:/tytImage/";
	static final String ubuntu_base_path="/home/geiri/httpInterface/";
	static final String ubuntu_image_path="img/";
	public static String default_log_file_name="txt.log";
	public static String default_map_file_name="nameIdMapping.conf";
	public static String drfcn_conf_file_name="drfcn.conf";

	public static String getImgPath() {
		if(PathResolver.getOS().contains("Window")) {
			return windows_image_path;
		}else {
			return ubuntu_base_path+ubuntu_image_path;
		}
	}
	
	
	
	public static String getBasePath() {
		if(PathResolver.getOS().contains("Window")) {
			return windows_image_path;
		}else {
			return ubuntu_base_path;
		}
	}
	
	
	public static String getOS() {
		String os=System.getProperty("os.name");
		//Logger.w("the server type: "+os);
		return os;
	}
}
