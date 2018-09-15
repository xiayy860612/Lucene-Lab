package com.s2u2m.lab.lucene.basic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * MultipleValueQueryTest
 * Create by Yangyang.xia on 9/15/18
 */
public class MultipleValueQueryTest {

    private static final String INDEX_DIR;
    static  {
        URL indexDirUrl = LuceneLabBasicMain.class.getClassLoader().getResource("index");
        INDEX_DIR = String.join(File.separator, indexDirUrl.getPath(), UUID.randomUUID().toString());
    }

    private static final String KEY_FILE_LIST = "fileList";
    private static final String KEY_ID = "id";

    // test split in [":", "-", "_", " "]
    private static final String split = " ";

    @Test
    public void multipleValueQuery() throws IOException {
        Path indexDirPath = Paths.get(INDEX_DIR);
        Files.createDirectory(indexDirPath);

        FSDirectory directory = FSDirectory.open(indexDirPath);
        IndexWriterConfig iwConfig = new IndexWriterConfig(new WhitespaceAnalyzer());
        iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        try (IndexWriter writer = new IndexWriter(directory, iwConfig)) {
            Document document = docOneFieldMutipleValues("1");
            writer.addDocument(document);

            document = docOneFieldMutipleValues("2");
            writer.addDocument(document);
        }

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new TermQuery(new Term(KEY_FILE_LIST, (String.join(split, "file", "3") + ".TXT")));
        TopDocs topDocs = searcher.search(query, 10);

        System.out.println("TotalHits: " + topDocs.totalHits);

        for (ScoreDoc scoreDoc: topDocs.scoreDocs) {
            Document resultDoc = searcher.doc(scoreDoc.doc);
            System.out.println("Find Doc Id:" + resultDoc.get(KEY_ID));
            System.out.println("Doc File List: " + Arrays.asList(resultDoc.getValues(KEY_FILE_LIST)));
        }
    }

    private Document docOneFieldMutipleValues(String id) {
        Document document = new Document();
        document.add(new StringField(KEY_ID, id, Field.Store.YES));

        for (int value : IntStream.rangeClosed(1, 3).boxed().collect(Collectors.toList())) {
            String fileName = String.join(split, "file", Integer.toString(value)) + ".TXT";
            System.out.println("Doc["+ id +"」 Added File: " + fileName);
            Field field = new StringField(KEY_FILE_LIST, fileName, Field.Store.YES);
            // cannot use TextField, it will divide fileName into pieces by space
            // Field field = new TextField(KEY_FILE_LIST, fileName, Field.Store.YES);
            document.add(field);
        }

        return document;
    }
}
