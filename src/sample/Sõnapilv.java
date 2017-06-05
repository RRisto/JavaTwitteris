package sample;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class Sõnapilv {


    public static void teeSõnapilv(String tekst) throws IOException {
        //http://kennycason.com/posts/2014-07-03-kumo-wordcloud.html
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        InputStream inputStream = new ByteArrayInputStream(tekst.getBytes(StandardCharsets.UTF_8));
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(inputStream);
        final Dimension dimension = new Dimension(300, 300);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        wordCloud.setPadding(0);
        wordCloud.setBackground(new RectangleBackground(dimension));
        wordCloud.setColorPalette(new ColorPalette(new Color(0xD5CFFA), new Color(0xBBB1FA), new
                Color(0x9A8CF5), new Color(0x806EF5)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile("sõnapilv.png");
    }
}
