package com.s2u2m.lab.lucene.basic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * LucenceBasicOpsTest
 * Create by Yangyang.xia on 9/11/18
 */
public class LucenceBasicOpsTest {

    private static final URL indexDirUrl;
    private String INDEX_DIR;
    static  {
        indexDirUrl = LuceneLabBasicMain.class.getClassLoader().getResource("index");
    }

    private static final String idField = "id";
    private static final String idValue = "1";

    @Before
    public void addDocument() throws IOException {
        INDEX_DIR = String.join(File.separator, indexDirUrl.getPath(), UUID.randomUUID().toString());

        FSDirectory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(directory, config);

        Document document = new Document();
        // add field to document
        Field field = new StringField(idField, idValue, Field.Store.YES);
        document.add(field);

        // add document to index
        writer.addDocument(document);

        writer.close();
    }

    @After
    public void deleteDocument() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(directory, config);

        Query query = new TermQuery(new Term(idField, idValue));
        writer.deleteDocuments(query);

        writer.close();
    }

    @Test
    public void query() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new TermQuery(new Term(idField, idValue));
        TopDocs topDocs = searcher.search(query, 10);

        assertEquals(1, topDocs.totalHits);

        Document doc = searcher.doc(topDocs.scoreDocs[0].doc);
        String exactIdValue = doc.get(idField);
        assertEquals(idValue, exactIdValue);
    }



}
