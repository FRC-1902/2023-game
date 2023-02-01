package frc.robot.path;

import java.io.File;
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
      case TEST:
        try {
          Object obj = new JSONParser().parse(new FileReader("/home/lvuser/deploy/test.json"));
          pathArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        break;
      case STRAIGHT:
        try {
          Object obj = new JSONParser().parse(new FileReader(new File("/home/lvuser/deploy/straight.json")));
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

  public static enum pathName{
    BLUE,
    STRAIGHT,
    CIRCLE,
    TEST
  }

}
