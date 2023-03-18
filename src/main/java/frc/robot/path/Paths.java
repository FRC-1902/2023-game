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
      case TEST:
        try {
          //Object obj = new JSONParser().parse(new FileReader("/home/lvuser/deploy/test.json"));
          Object obj = new JSONParser().parse(new FileReader(new File("/home/lvuser/deploy/pathplanner/generatedJSON/straight1m.parsed.json")));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;
      case STRAIGHT:
        try {
          Object obj = new JSONParser().parse(new FileReader(new File("/home/lvuser/deploy/pathplanner/generatedJSON/slow_straight.wpilib.json")));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;      
      case REVERSE:
        try {
          Object obj = new JSONParser().parse(new FileReader(new File("/home/lvuser/deploy/pathplanner/generatedJSON/reverse.wpilib.json")));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;
      case SQUARE:
        try {
          Object obj = new JSONParser().parse(new FileReader(new File("/home/lvuser/deploy/pathplanner/generatedJSON/square2m.parsed.json")));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;
      case CIRCLE:
        try {
          Object obj = new JSONParser().parse(new FileReader(new File("/home/lvuser/deploy/circle.json")));
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
      // System.out.println("path array is null");
      return new JSONArray().toArray();

    }
    // System.out.println("path array is not null");

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
    BLUE,
    STRAIGHT,
    REVERSE,
    CIRCLE,
    TEST,
    SQUARE
  }

}
