package com.example.demo;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Logger {
	
	public static boolean w(String logLine) {
    	System.out.println(logLine);
    	return writeLogTo(logLine,
    			PathResolver.getBasePath()+
    			PathResolver.default_log_file_name);
    }
   
	public static boolean w(String logLine,String filePath) {
    	System.out.println(logLine);
    	return writeLogTo(logLine,filePath);
    }
	
    private static boolean writeLogTo(String line,String path) {
    	List<String> lines=new ArrayList<>();

    	Instant now = Instant.now();
    	ZoneId zoneId = ZoneId.of("Asia/Shanghai");
    	ZonedDateTime dateAndTimeInSH = ZonedDateTime.ofInstant(now, zoneId);
    	
    	lines.add(dateAndTimeInSH.toString());
    	lines.add("   ");
    	lines.add(line);

    	hzgIO.writeLineTo(lines, path);
    	return true;
    }
}
