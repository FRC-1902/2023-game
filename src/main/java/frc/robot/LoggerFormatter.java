package frc.robot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import edu.wpi.first.wpilibj.RobotController;

public class LoggerFormatter extends Formatter {
    public String format(LogRecord record) {
        String ret = String.format(
            "[%s:%s() @ %d : tid %d] %s: %s\n", 
            record.getSourceClassName(), 
            record.getSourceMethodName(), 
            RobotController.getFPGATime(),
            record.getLongThreadID(),
            record.getLevel(), 
            record.getMessage()
        );

        // If there's an exception attached to the log message, we should probably print that out.
        if (record.getThrown() != null) {
            // Isn't java amazing?
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);

            ret += String.format("\n==== BEGIN ERROR MESSAGE ====\n%s==== END ERROR MESSAGE ====\n\n", sw);
        }

        return ret;
    }
}
