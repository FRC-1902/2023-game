package frc.robot;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import edu.wpi.first.wpilibj.RobotController;

public class LoggerFormatter extends Formatter {
    public String format(LogRecord record) {
        return String.format(
            "[%s:%s() @ %d : tid %d] %s: %s\n", 
            record.getSourceClassName(), 
            record.getSourceMethodName(), 
            RobotController.getFPGATime(),
            record.getLongThreadID(),
            record.getLevel(), 
            record.getMessage()
        );
    }
}
