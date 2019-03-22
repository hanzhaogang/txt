package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geiri.predictClient.PredictClient;

import predictMessage.Message.detection;
import predictMessage.Message.response;

public abstract class Target {
	public double h;
	public double w;
	public double x;
	public double y;
	
	
	/*
	 * multiple models
	 */
    public static List<Target> getTargetAbsTLIds(
    		  				String ip,
			  				int port,
			  				int timeout,
			  				List<String> models,
			  				String imgPath,
			  				double threash) {
    	List<Target> targets=new ArrayList<>();
    	for(String model:models) {
    		targets.addAll(
    				Target.getTargetAbsTLIds(
    				ip,port,timeout,model,imgPath,threash));
    	}
    	
    	//Logger.w(targets.toString());
    	return targets;
    }
    
    
    /*
     * single model
     */
    public static List<Target> getTargetAbsTLIds(
    		  			String ip,
			  			int port,
			  			int timeout,
			  			String model,
			  			String imgPath,
			  			double threash) {
    	List<Target> targets=new ArrayList<>();
    	
    	response res=getPBRes(ip,port,timeout,model,imgPath,threash);
    	/*List<Target> sources=getTargetRelCTypes(
    			ip,port,timeout,model,imgPath,threash);*/
		List<detection> dets=res.getDetsList();
    	for(detection det:dets) {
			try {
				NameIdMapper.setName2Id(PathResolver.getBasePath()+
						PathResolver.default_map_file_name);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String,Integer> map=NameIdMapper.getName2Id();
			Logger.w("we are using current map: "+ map.toString());

			String type=det.getCls();
			Integer id=NameIdMapper.map(type);
			TargetAbsTLId t= new TargetAbsTLId();
			if(id!=-1) {
				t.id=id;
				Logger.w("type: "+type);
				Logger.w("id: "+id);
			}else {
				Logger.w("detected an object not exists in the name id mapping.");
				continue;
			}
			
			double imgWidth=res.getWidth();
			double imgHeight=res.getHeight();
		    
			double[] relC=new double[4];
			relC[0]=det.getXc();
			relC[1]=det.getYc();
			relC[2]=det.getW();
			relC[3]=det.getH();
		
			double[] absTL=relC2absTL(relC,imgWidth,imgHeight);
			t.x=absTL[0];
			t.y=absTL[1];
			t.w=absTL[2];
			t.h=absTL[3];
			targets.add(t);
		}
			
		Logger.w("targets from model: "+model+ 
				"for img: "+imgPath+
				": "+targets.toString());
   		return targets;
	}
		
    
    private static response getPBRes(
    		    			String ip,
							int port,
							int timeout,
							String model,
							String imgPath,
							double threash) {
    	
		try {
			PredictClient predictClient=new PredictClient(
					ip, port, timeout);
			//System.out.println("Sending request to:" + );
			response res = predictClient.sendRequest(model,
					new File(imgPath), threash);
			while(res.getStatus()!=0) {
				Logger.w("server return error, try again!");
				res=predictClient.sendRequest(model,
						new File(imgPath),
						threash);
				}
			return res;
		} catch (IllegalArgumentException | IllegalAccessException | 
				IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    
	public static List<Target> getTargetRelCTypes(
							String ip,
							int port,
							int timeout,
							String model,
							String imgPath,
							double threash) {
		response res=getPBRes(ip,port,timeout,model,imgPath,threash);
		List<detection> detections=res.getDetsList();
		List<Target> targets=new ArrayList<>();
		for(detection det:detections) {
		
			Target tar=new TargetRelCType();
			String type=det.getCls();
			((TargetRelCType)tar).objectType=type;
			//System.out.println(type);
			tar.x=det.getXc();
			tar.y=det.getYc();
			tar.h=det.getH();
			tar.w=det.getW();
			targets.add(tar);
		}
		return targets;
	}
	
	
	/*
	 * get absolute top left x,y, and absolute width, height of the 
	 * rectangle, from relative cneter x,y and relative width, height
	 */
	private static double[] relC2absTL(double[] relC,double imgW,double imgH){
		/*
		 * 	relC[0]=det.getXc();
			relC[1]=det.getYc();
			relC[2]=det.getW();
			relC[3]=det.getH();
		 */
		double xcPrecent=relC[0];
		double ycPrecent=relC[1]; 
		double rectWPrecent=relC[2];
		double rectHPrecent=relC[3];
		
		double xc=imgW*xcPrecent;
		double yc=imgH*ycPrecent;

		double rectWidth=imgW*rectWPrecent;
		double rectHeight=imgH*rectHPrecent;
		double rectLeftTopX=xc-rectWidth/2;
		double rectLeftTopY=yc-rectHeight/2;

		return new double[] {rectLeftTopX,
				             rectLeftTopY,
				             rectWidth,
				             rectHeight};
	}
}



/*
 * 
 */
class TargetAbsTLType extends Target{
	public String objectType;
	@Override
	public String toString() {
		return "Target [h=" + h +
				", w=" + w + 
				", x=" + x + 
				", y=" + y + 
				", objectType=" + objectType + "]";
	}
	
	
}



class TargetRelCType extends Target{
	public String objectType;
	@Override
	public String toString() {
		return "Target [h=" + h +
				", w=" + w + 
				", x=" + x + 
				", y=" + y + 
				", objectType=" + objectType + "]";
	}
	
	
}


class TargetAbsTLId extends Target{
	public Integer id;

	/*public TargetAdapter(Target target) {
		h=target.h;
		w=target.w;
		x=target.x;
		y=target.y;
	}*/

	@Override
	public String toString() {
		return "TargetAdapter [h=" + h + 
				", w=" + w + 
				", x=" + x + 
				", y=" + y + 
				", id=" + id + "]";
	}
	
}