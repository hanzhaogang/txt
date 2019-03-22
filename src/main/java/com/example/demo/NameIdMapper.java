package com.example.demo;

import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NameIdMapper{
	//public static String MAPPING_CONF_PATH="/nameIdMapping.conf";
	private static Map<String,Integer> name2Id=new HashMap<>();
	private static Map<Integer,String> id2Name=new HashMap<>();
	
	public static Map<String, Integer> getName2Id() {
		return name2Id;
	}
	
	public static void setName2Id(String mappingConfPath) throws Exception {
		List<String> line=hzgIO.getLineFrom(mappingConfPath);
		for(String nameIdPair:line) {
			String[] element=nameIdPair.split(",");
			if(element.length!=2) {
				System.out.println("config file format error!");
				throw new NameIDMappingConfFileFormatError();
			}
			name2Id.put(element[0], Integer.parseInt(element[1]));
		}
	}

	public static Integer map(String objectType) {
		return name2Id.getOrDefault(objectType,-1);
	}
	
	
	
	
	/*public List<ResultsWithId> map(List<Results> results){
		List<ResultsWithId> res=new ArrayList<>();
		for(Results result:results) {
			ResultsWithId idRes=new ResultsWithId();
			idRes.code=name2Id.get(result.targets)
			
		}
		return res;
	}*/
	
}




class NameIDMappingConfFileFormatError extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}