package com.s2u2m.lab.lucene.basic;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * AddDocumentTest
 * Create by Yangyang.xia on 9/11/18
 */
public class AddDocumentTest {

    private static final String INDEX_DIR;
    static  {
        URL indexDirUrl = LuceneLabBasicMain.class.getClassLoader().getResource("index");
        INDEX_DIR = indexDirUrl.getPath();
    }

    @Test
    public void addDocumentTest() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(directory, config);

        final String idField = "id";
        final String idValue = "1";

        Document document = new Document();
        // add field to document
        Field field = new StringField(idField, idValue, Field.Store.YES);
        document.add(field);

        // add document to index
        writer.addDocument(document);

        writer.close();

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
