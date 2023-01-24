package frc.robot.path;

import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.ParseException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Paths {

  private JSONArray pathArray = null;

  private static Paths paths = null;

  public static Paths getInstance(pathName pName){
    if(paths == null){
      paths = new Paths(pName);
    }
    return paths;
  }

  private Paths(pathName pName){
    switch(pName){
      case BLUE:
        try {
          Object obj = new JSONParser().parse(new FileReader("src/main/java/frc/robot/path/Blue.wpilib.json"));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;
    }
  }

  public JSONArray getPathArray(){
    if(pathArray == null){
      return new JSONArray();
    }
    return pathArray;
  }

  public static enum pathName{
    BLUE
  }

}
