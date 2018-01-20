package nl.alexeyu.photomate.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

public class DefaultCmdExecutor implements CmdExecutor {

    private final Logger logger = LogManager.getLogger();

    private final String cmd;

    public DefaultCmdExecutor(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String exec(Path path, List<String> args) {
        try {
            List<String> execArgs = Lists.newArrayList(args);
            execArgs.add(0, cmd);
            execArgs.add(path.toString());
            logger.debug("{}", execArgs);
            return doExec(execArgs);
        } catch (IOException ex) {
            logger.error("Could not run " + cmd, ex);
            return "";
        }
    }

    private String doExec(List<String> execArgs) throws IOException {
        Process p = Runtime.getRuntime().exec(execArgs.toArray(new String[0]));
        try (InputStream is = p.getInputStream()) {
            String result = CharStreams.toString(new InputStreamReader(is));
            logger.debug(result);
            return result;
        }
    }
}
