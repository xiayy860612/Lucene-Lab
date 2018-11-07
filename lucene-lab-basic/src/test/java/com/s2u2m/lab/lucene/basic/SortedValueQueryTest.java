package com.s2u2m.lab.lucene.basic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * SortedValueQueryTest
 * Create by Yangyang.xia on 10/30/18
 */
public class SortedValueQueryTest {

    private static final String INDEX_DIR;
    static  {
        URL indexDirUrl = LuceneLabBasicMain.class.getClassLoader().getResource("index");
        INDEX_DIR = String.join(File.separator, indexDirUrl.getPath(), UUID.randomUUID().toString());
    }

    private static final String ID_FIELD = "id";
    private static final String CONTENT＿FIELD = "content";
    private static final String CREATE_TIME_FIELD = "create_time";

    @Before
    public void addDocument() throws IOException, InterruptedException {
        Path indexDirPath = Paths.get(INDEX_DIR);
        Files.createDirectory(indexDirPath);

        FSDirectory directory = FSDirectory.open(indexDirPath);
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(directory, config);

        Document d1 = createDoc(1L, true);
        Document d2 = createDoc(2L, false);
        Document d3 = createDoc(3L, true);
        Document d4 = createDoc(4L, false);

        // add document to index
        writer.addDocuments(Arrays.asList(d1, d2, d3, d4));
        writer.close();
    }

    private Document createDoc(long id, boolean addSorted) throws InterruptedException {
        Thread.sleep(1000L);
        Document document = new Document();
        // add field to document
        Field field = new StringField(ID_FIELD, Long.toString(id), Field.Store.YES);
        document.add(field);

        Field sortedIdField = new SortedDocValuesField(ID_FIELD, new BytesRef(Long.toBinaryString(id)));
        document.add(sortedIdField);



        if (addSorted) {
            Field cf = new StringField(CREATE_TIME_FIELD, Long.toString(Instant.now().getEpochSecond()), Field.Store.YES);
            document.add(cf);

            Field sortedCTField = new NumericDocValuesField(CREATE_TIME_FIELD, Instant.now().getEpochSecond());
            document.add(sortedCTField);
        }
        return document;
    }

    @Test
    public void getSortedQuery__success() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new MatchAllDocsQuery();
        Sort sort = new Sort(new SortField(CREATE_TIME_FIELD, SortField.Type.LONG, true));
        TopDocs topDocs = searcher.search(query, 10, sort);

        for (ScoreDoc sd: topDocs.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            String id = doc.get(ID_FIELD);
            System.out.println(id);

            String createTime = doc.get(CREATE_TIME_FIELD);
            System.out.println(createTime);
        }
    }
}
