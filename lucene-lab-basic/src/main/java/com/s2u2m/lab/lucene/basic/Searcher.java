package com.s2u2m.lab.lucene.basic;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Searcher
 * Create by Yangyang.xia on 8/30/18
 */
public class Searcher implements AutoCloseable {

    private IndexReader indexReader;

    public Searcher(String indexDir) throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(indexDir));
        indexReader = DirectoryReader.open(directory);
    }

    public List<String> getFilePaths(String key) throws IOException {
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new TermQuery(new Term("file_name", key));
        TopDocs docs = indexSearcher.search(query, 10);

        List<String> result = new LinkedList<>();
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            result.add(document.get("path"));
        }

        return result;
    }

    @Override
    public void close() throws IOException {
        indexReader.close();
    }
}
