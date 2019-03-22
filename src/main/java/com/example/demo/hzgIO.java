package com.example.demo;


import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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


public class hzgIO {
	public static List<Path> getPathFrom(String directory) throws IOException{
		List<Path> paths=new ArrayList<>();
		Path root=Paths.get(directory);
        //Files.find(start, maxDepth, matcher, options)
		Files.list(root);
		
		return paths;
	}
	
	public long getFileSize(String filePath) {
		return new File(filePath).length();
	}

    public static List<String> getLineFrom(String path){
    	/*try {
			Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	    List<String> line=new ArrayList<String>();
	    try (BufferedReader r = 
	    		Files.newBufferedReader(Paths.get(path),StandardCharsets.UTF_8)){
	    	r.lines().forEach(ln->line.add(ln));
	    }catch(Exception e) {
	    	System.out.println(e);
	    }
	    return line;
	}
    
    public static boolean writeLineTo(List<String> lines,String path) {
    	try(BufferedWriter w=
    			Files.newBufferedWriter(Paths.get(path),StandardCharsets.UTF_8,
    					StandardOpenOption.CREATE,StandardOpenOption.APPEND)){
    		for(String line:lines) {
    			w.append(line);
    			w.newLine();
    		}
    	}catch(Exception e) {
	    	System.out.println(e);
    	}
    	return true;
    }
}
