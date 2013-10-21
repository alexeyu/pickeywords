package nl.alexeyu.photomate.service.keyword;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExifKeywordProcessor implements KeywordProcessor {

    private final Logger logger = LoggerFactory.getLogger("ExifKeywordReader");

    @Override
    public List<String> readKeywords(String photoPath) {
        String result = execExif("-keywords", photoPath);
        int columnPos = result.indexOf(":");
        if (columnPos > 0) {
            String keywordsLine = result.substring(columnPos + 1);
            return Arrays.asList(StringUtils.split(keywordsLine, ","));
        }
        return Collections.emptyList();
    }

    @Override
    public void addKeywords(String photoPath, List<String> keywords) {
        execExif("-keywords+=" + StringUtils.join(keywords, ","), photoPath);
    }

    @Override
    public void removeKeywords(String photoPath, List<String> keywords) {
        execExif("-keywords-=" + StringUtils.join(keywords, ","), photoPath);
    }

    private String execExif(String args, String path) {
        try {
            String[] execArgs = new String[] { "exiftool", args, path };
            Process p = Runtime.getRuntime().exec(execArgs);
            try (InputStream is = p.getInputStream()) {
                return IOUtils.toString(is);
            }
        } catch (IOException ex) {
            logger.error("Could not run exif", ex);
            return "";
        }
    }

}
