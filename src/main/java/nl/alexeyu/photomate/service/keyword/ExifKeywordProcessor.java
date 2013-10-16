package nl.alexeyu.photomate.service.keyword;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExifKeywordProcessor implements KeywordProcessor {

    private final Logger logger = LoggerFactory.getLogger("ExifKeywordReader");

    @Override
    public List<String> readKeywords(String photoPath) {
        List<String> keywords = new ArrayList<String>();
        String result = execExif("-keywords", photoPath);
        int columnPos = result.indexOf(":");
        if (columnPos > 0) {
            String keywordsLine = result.substring(columnPos + 1);
            StringTokenizer tokenizer = new StringTokenizer(keywordsLine, ",");
            while (tokenizer.hasMoreTokens()) {
                keywords.add(tokenizer.nextToken());
            }
        }
        return keywords;
    }

    public void addKeyword(String photoPath, String keyword) {
        execExif("-keywords+=" + keyword, photoPath);
    }

    public void removeKeyword(String photoPath, String keyword) {
        execExif("-keywords-=" + keyword, photoPath);
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
