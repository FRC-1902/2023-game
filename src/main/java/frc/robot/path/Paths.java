package frc.robot.path;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Paths {

  private JSONArray pathArray = null;

  private static Paths paths = null;
  private static final String JSONFILEPATH = "/home/lvuser/deploy/pathplanner/generatedJSON/";

  public static Paths getInstance(){
    if(paths == null){
      paths = new Paths();
    }
    return paths;
  }

  private Paths(){

  }

  public Paths readPathArray(pathName pName){
    switch(pName){ 
      case REVERSE:
        try {
          Object obj = new JSONParser().parse(new FileReader(new File(JSONFILEPATH + "reverse.wpilib.json")));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;
      case BALANCE:
        try {
          Object obj = new JSONParser().parse(new FileReader(new File(JSONFILEPATH + "balance.wpilib.json")));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;
    }
    return this;
  }

  public Object[] getPathArray(){
    if(pathArray == null){
      return new JSONArray().toArray();
    }

    return pathArray.toArray();
  }

  public JSONObject[] getJSONObjectArray() {
    Object[] objectArray = getPathArray();
    JSONObject[] jsonObjectArray = new JSONObject[objectArray.length];
    for(int i = 0; i < jsonObjectArray.length; i++){
      jsonObjectArray[i] = (JSONObject) objectArray[i];
    }
    return jsonObjectArray;
  }

  public static enum pathName{
    REVERSE,
    BALANCE
  }

}
