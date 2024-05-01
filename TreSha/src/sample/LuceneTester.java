package sample;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {
    private String indexDir = "D:\\IntllijProjectFolder\\FirstBrowser\\index";
    private String dataDir = "D:\\IntllijProjectFolder\\FirstBrowser\\Reuters_articles";
    private Indexer indexer;
    private Searcher searcher;
    private String results;
    private String hitsresult;
    private String cbselect;
    private int numIndexed;

    public void start() {
        LuceneTester tester;
        try {
            tester = new LuceneTester();
            tester.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createIndex() throws IOException {

        indexer = new Indexer(indexDir);
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " File(s) indexed, time taken: " +
                (endTime - startTime) + " ms");
    }

    public  void search(String searchQuery) throws IOException, ParseException {
        searcher = new Searcher(indexDir,cbselect);
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(searchQuery);
        long endTime = System.currentTimeMillis();


        System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
        hitsresult= hits.totalHits + " documents found. Time :" + (endTime - startTime);
        int j = 0;
        results = "";
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
            if (j == 0) {
                results =doc.get(LuceneConstants.FILE_PATH);
                j++;
            } else{
                results = results + "," + doc.get(LuceneConstants.FILE_PATH);
            }
        }
        searcher.close();
    }
    public String getHits(){
        return hitsresult;
    }
    public String getResults(){
        return results;
    }

    public void setCbselect(String helper){
        cbselect = helper;
    }


}
