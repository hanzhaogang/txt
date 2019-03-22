package com.example.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ServletContextAware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.geiri.predictClient.PredictClient;

import predictMessage.Message.detection;
import predictMessage.Message.response;

class RequestBean{
	@NotNull
	public String company;
	@NotNull
	public String ip;
	/*@Digits(fraction = 9999999, integer = 0)
	public int port;
	public String id;
	public String fileName;
	@Digits(fraction = 0, integer = 0)
	public long timeStamp;
	public int fileSize;
	public int type;*/
	public String toString() {
		return "company"+company+"ip"+ip;
	}
}


@RestController
public class SampleController implements ServletContextAware{
	
	private ServletContext context;
	private int maxSize = 102400 * 1024;// 102400KB以内(100MB)

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
	    this.context = servletContext;
	}
	
    @RequestMapping("/")
    String home() {
        return "Hello This is the AI object detection service_simpler_http_common !";
    }

    @PostMapping(value="/")
    String homePost(@RequestBody @Valid List< RequestBean> beans) {
    	for(RequestBean bean:beans) {
    		System.out.println(bean);
    	}
    	System.out.println("homePost");
        return "Hello This is the AI object detection service_simpler_http_common !";
    }
    
    
    
    /*
     * 1 receive the file and save it into the file system.
     * 2 response to the request.
     */
    @PostMapping(value="/drfcn22")
    String fileUploadId(HttpServletRequest request) {
    	Logger.w("entering the fileUpload method.");

    	/*String[] objectNames=new String[] {
				"tower_crane_arm",
				"truck_crane",
				"truck_crane_working_arm",
				"truck_crane",
				"truck_crane_working_arm",
				"truck_crane_working_arm",
				"cement_pump_truck",
				"cement_pump_truck",
				"excavator",
				"none",
				"bulldozer",
				"excavator"};
    	*/
    	//MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //MultipartFile file = multipartRequest.getFile("file");
    	
        try  {
            if (!Files.exists(Paths.get(PathResolver.getImgPath()))) {
            	Files.createDirectories(Paths.get(PathResolver.getImgPath()));
            }
            
        	Part jsonPart=request.getPart("FileUpload");
        	InputStream input2 = jsonPart.getInputStream();
        	String jsonString = IOUtils.toString(input2, "utf-8"); 
        	//{"filename":ing = IOUtils.toString(input2, "utf-8");  "869721020145075_20170907071152.jpg",
        	//"filesize":1,
        	//"id":"869721020145075_20170907071152",
        	//"ip":"127.0.0.1",
        	//"port":0,
        	//"timestamp":1541651386}
			input2.close();
			JsonNode rootNode = new ObjectMapper().
					readTree(new StringReader(jsonString));
			String company=rootNode.get("company").asText();
			String ip=rootNode.get("ip").asText();
			String id=rootNode.get("id").asText();
			
			
			Result idRes=ResultFactory.getInstance().buildResult(
					id, company, ip, 65535, "", 0);
	    	
	    	
			//save file
			Part filePart=request.getPart("file");
        	InputStream input = filePart.getInputStream();
			File testFile=new File(PathResolver.getBasePath()+
        			PathResolver.drfcn_conf_file_name);
			String imgPath;
			if(!(testFile.getUsableSpace()<1000000000)) {
	        	imgPath=PathResolver.getImgPath()+genId()+".jpg";
			}else {
	        	imgPath=PathResolver.getImgPath()+"input"+".jpg";
				Logger.w("disk not enough!");
			}
	        Files.copy(input, 
	        		Paths.get(imgPath),
	        		StandardCopyOption.REPLACE_EXISTING);
			input.close();
        	Logger.w("upload file "+imgPath+" saved in the file system");
			
        	List<String> lines=hzgIO.getLineFrom(PathResolver.getBasePath()+
        			PathResolver.drfcn_conf_file_name);
        	String drfcnIp=lines.get(0);
        	Integer drfcnPort=Integer.valueOf(lines.get(1));
			List<Target> targets=Target.getTargetAbsTLIds(
					drfcnIp,drfcnPort,10000,
					Arrays.asList("drfcn26","tongdao2"),imgPath,0.5);
			idRes.targets=targets;
			Logger.w("returning result: "+idRes);
			return JsonUtil.toJson(idRes);
        } catch (Exception e) {
			e.printStackTrace();
			return "internal error";
        }

    }
   
	private String genId() {
    	/*Instant now = Instant.now();
		ZoneId zoneId = ZoneId.of("Asia/Shanghai");
		ZonedDateTime dateAndTimeInSH = ZonedDateTime.ofInstant(now, zoneId);
		return dateAndTimeInSH.toString();*/
		long unixTime = System.currentTimeMillis() / 1000L;
		return Long.toString(unixTime);
    }
	
	
    /*@PostMapping(value="/drfcn22/id")
    String fileUpload(HttpServletRequest request) {
    	System.out.println("entering the fileUpload method.");

    	String[] objectNames=new String[] {
				"tower_crane_arm",
				"truck_crane",
				"truck_crane_working_arm",
				"truck_crane",
				"truck_crane_working_arm",
				"truck_crane_working_arm",
				"cement_pump_truck",
				"cement_pump_truck",
				"excavator",
				"none",
				"bulldozer",
				"excavator"};
    	
    	//MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //MultipartFile file = multipartRequest.getFile("file");
    	//ResultWithId idRes=new ResultWithId();
    	
        Result res=new Result();
        try  {
        	Path uploadPath = Paths.get(
            		"/home/share/matlab/MPSInstances/noHelmetPersonDetector/Step0_MyTestImages/");
            if (!Files.exists(uploadPath)) {
            	Files.createDirectories(uploadPath);
            }
            
        	
        	Part jsonPart=request.getPart("FileUpload");
        	InputStream input2 = jsonPart.getInputStream();
        	String jsonString = IOUtils.toString(input2, "utf-8"); 
        	//{"filename":ing = IOUtils.toString(input2, "utf-8");  "869721020145075_20170907071152.jpg",
        	//"filesize":1,
        	//"id":"869721020145075_20170907071152",
        	//"ip":"127.0.0.1",
        	//"port":0,
        	//"timestamp":1541651386}
			input2.close();
			JsonNode rootNode = new ObjectMapper().readTree(new StringReader(jsonString));
			JsonNode companyFeild=rootNode.get("company");
			JsonNode ipFeild=rootNode.get("ip");
			JsonNode idFeild=rootNode.get("id");
			
			res.id=idFeild.asText();
			res.code=0;
	    	res.message="";
	    	res.company=companyFeild.asText();
	    	res.ip=ipFeild.asText();
	    	res.port=65535;
	    	
            //jsonPart.getH
	    	
        	Part filePart=request.getPart("file");
        	InputStream input = filePart.getInputStream();
        	//System
        	Files.copy(input, 
        			Paths.get(uploadPath+"/input.jpg"),
        			StandardCopyOption.REPLACE_EXISTING);
        	System.out.println("targetPath2:"+uploadPath+"/input.jpg");
        	
        	System.out.println("upload file saved in the file system");
			input.close();
			
			
			PredictClient predictClient=new PredictClient("127.0.0.1", 6379, 10000);
			
			//System.out.println("Sending request to:" + );
			response resp=predictClient.sendRequest("drfcn26",
					new File("/home/share/matlab/MPSInstances/noHelmetPersonDetector/Step0_MyTestImages/input.jpg"),
					0.5);
			while(resp.getStatus()!=0) {
				System.out.println("server return error!");
				resp=predictClient.sendRequest("drfcn26",
						new File("/home/share/matlab/MPSInstances/noHelmetPersonDetector/Step0_MyTestImages/input.jpg"),
						0.5);
			}
			List<detection> detections=resp.getDetsList();
			List<Target> targets=new ArrayList<>();
			for(detection det:detections) {
				int[] coord=getCoord(resp,det);
				
				Target tar=new Target();
				tar.objectType=det.getCls();
				tar.x=coord[0];
				tar.y=coord[1];
				tar.h=coord[2];
				tar.w=coord[3];
				targets.add(tar);
			}
			res.targets=targets;
			
			//NameIdMapper.setName2Id(NameIdMapper.MAPPING_CONF_PATH);
			//idRes=NameIdMapper.map(res);
        } catch (Exception e) {
			e.printStackTrace();
        }

		return JsonUtil.toJson(res);
    }
    */
    
	/*
    @PostMapping(value="/testInterface")
    String test(HttpServletRequest request) {
    	System.out.println("entering the test method.");

    	ResultWithId idRes=new ResultWithId();
    	

        try  {
        	Path uploadPath = Paths.get(
            		"/home/share/matlab/MPSInstances/noHelmetPersonDetector/Step0_MyTestImages/");
            if (!Files.exists(uploadPath)) {
            	Files.createDirectories(uploadPath);
            }
            
            Part jsonPart=request.getPart("FileUpload");
        	InputStream input2 = jsonPart.getInputStream();
        	String jsonString = IOUtils.toString(input2, "utf-8"); 
        	//{"filename":"869721020145075_20170907071152.jpg",
        	//"filesize":1,
        	//"id":"869721020145075_20170907071152",
        	//"ip":"127.0.0.1",
        	//"port":0,
        	//"timestamp":1541651386}
			input2.close();
			JsonNode rootNode = new ObjectMapper().readTree(new StringReader(jsonString));
			JsonNode companyFeild=rootNode.get("company");
			JsonNode ipFeild=rootNode.get("ip");
			JsonNode idFeild=rootNode.get("id");
    	idRes.id=idFeild.asText();
		idRes.code=0;
    	idRes.message="";
    	idRes.company=companyFeild.asText();
    	idRes.ip=ipFeild.asText();
    	idRes.port=65535;
    	
    	Target tar=new Target();
		tar.objectType="细小金具缺陷：缺销子";
		tar.x=1300;
		tar.y=1300;
		tar.h=400;
		tar.w=400;

    	TargetAdapter ta=new TargetAdapter(tar);
    	ta.id=65602;
    	List<TargetAdapter> tas=new ArrayList<TargetAdapter>();
    	tas.add(ta);
    	idRes.targets=tas;
    	}catch(Exception e) {
    		System.out.println("exception");
    	}

		return JsonUtil.toJson(idRes);
    }
     
    */

}

/*class PhotoIds{
	public List<String> photoIds;
}

class Photo{
	public String id;
	public String ip;
	public String company;
	public int port;
	public List<Target>results;
	//public int stamp;
	//public int timestamp;
	//public String url;
}*/

class JsonUtil{
	public static String toJson(Object obj) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	String json="";
    	try {
			json = ow.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return json;    
	}
}

