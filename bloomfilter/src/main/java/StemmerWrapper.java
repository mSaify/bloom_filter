

import org.tartarus.snowball.ext.englishStemmer;

public class StemmerWrapper {

    public static String getStemmedWords(String s) {
        englishStemmer stemmer = new englishStemmer();
        stemmer.setCurrent(s);
        return stemmer.getCurrent();
    }
}
