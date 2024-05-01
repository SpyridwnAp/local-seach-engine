package sample;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher {
    IndexSearcher indexSearcher;
    Directory indexDirectory;
    IndexReader indexReader;
    QueryParser queryParser;
    String Comboboxselect;

    Query query;

    public Searcher(String indexDirectoryPath, String cbselect) throws IOException {
        Comboboxselect = cbselect;
        Path indexPath = Paths.get(indexDirectoryPath);
        indexDirectory = FSDirectory.open(indexPath);
        indexReader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(indexReader);
        if (Comboboxselect.equals("ALL CONTENT")) {
            queryParser = new QueryParser(LuceneConstants.CONTENTS, new EnglishAnalyzer());
        } else if (Comboboxselect.equals("PLACES")) {
            queryParser = new QueryParser(LuceneConstants.PLACES, new EnglishAnalyzer());
        } else if (Comboboxselect.equals("PEOPLE")) {
            queryParser = new QueryParser(LuceneConstants.PEOPLE, new EnglishAnalyzer());
        } else if (Comboboxselect.equals("TITLE")) {
            queryParser = new QueryParser(LuceneConstants.TITLE, new EnglishAnalyzer());
        } else if (Comboboxselect.equals("BODY")) {
            queryParser = new QueryParser(LuceneConstants.BODY, new EnglishAnalyzer());
        }
    }

    public TopDocs search(String searchQuery) throws IOException,
            ParseException {
        query = queryParser.parse(searchQuery);
        query.createWeight(indexSearcher, ScoreMode.COMPLETE, 0.1f);

        System.out.println("query: " + query.toString());
        return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws
            CorruptIndexException, IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        indexReader.close();
        indexDirectory.close();
    }
}
