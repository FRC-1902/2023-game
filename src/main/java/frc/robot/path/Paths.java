package frc.robot.path;

import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.ParseException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

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
      case BLUE:
        try {
          Object obj = new JSONParser().parse(new FileReader("src/main/java/frc/robot/path/Blue.wpilib.json"));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;
      case STRAIGHT:
        try {
          Object obj = new JSONParser().parse(new FileReader("src/main/java/frc/robot/path/straight.json"));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
    }
    return this;
  }

  public Object[] getPathArray(){
    if(pathArray == null){
      return new JSONArray().toArray();
    }
    return pathArray.toArray();
  }

  public static enum pathName{
    BLUE,
    STRAIGHT
  }

}
