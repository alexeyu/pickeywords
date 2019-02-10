package nl.alexeyu.photomate.util;

import java.nio.file.Path;
import java.util.List;

public interface CmdExecutor {

    String exec(Path path, List<String> args);

}