package com.s2u2m.lab.lucene.basic;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * LuceneLabBasicMain
 * Create by Yangyang.xia on 8/29/18
 */
public class LuceneLabBasicMain {

    public static void main(String[] args) throws IOException {
        URL dataSrcDir = LuceneLabBasicMain.class.getClassLoader().getResource("data_src");
        URL indexDir = LuceneLabBasicMain.class.getClassLoader().getResource("index");

        try (Indexer indexer = new Indexer(indexDir.getPath())) {
            indexer.index(dataSrcDir.getPath());
        }

        try (Searcher searcher = new Searcher(indexDir.getPath())) {
            List<String> files = searcher.getFilePaths("lucene_history.txt");
            System.out.println(String.join("\n", files));
        }

        System.out.println("Done");
    }
}
