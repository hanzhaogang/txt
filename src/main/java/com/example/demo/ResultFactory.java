package com.example.demo;

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

public class ResultFactory {
	private static ResultFactory instance;
	private ResultFactory() {}
	
	public static ResultFactory getInstance() {
		if(instance==null) {
			instance=new ResultFactory();
		}
		return instance;
	}
	
	public Result buildResult(String id,String company,String ip,Integer port,
			                   String message,Integer code) {
		Result res=new Result();
		res.id=id;
		res.code=code;
    	res.message=message;
    	res.company=company;
    	res.ip=ip;
    	res.port=port;
		return res;
	}
	
	/*private ResultWithId buildResultWithId() {
		ResultWithId res=new ResultWithId();
		return res;
	}*/
}

class Result{
	public String id;
	public int code;
	public String message;
	public String ip;
	public String company;
	public int port;
	public List<Target> targets;
	@Override
	public String toString() {
		StringBuilder list=new StringBuilder();
		for(Target target:targets) {
			list.append(target);
		}
		
		return "Result [id=" + id + 
				", code=" + code + 
				", message=" + message + 
				", ip=" + ip + 
				", company=" + company + 
				", port=" + port + 
				", targets=" + list.toString() + "]";
	}
	
	/*public String toString() {
		
		return ""+id+","+code+message+" "+ip+" "+company+" "+port + " "+
	}*/
} 	


/*class ResultWithId{
	public String id;
	public int code;
	public String message;
	public String ip;
	public String company;
	public int port;
	public List<TargetAdapter> targets;
	@Override
	public String toString() {
		
		StringBuilder list=new StringBuilder();
		for(TargetAdapter target:targets) {
			list.append(target);
		}
		
		return "ResultWithId [id=" + id + ", code=" + code + ", message=" + message + ", ip=" + ip + ", company="
				+ company + ", port=" + port + ", targets=" + list.toString()  + "]";
	}
	
	
}  	
*/




