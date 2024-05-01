package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.EnglishStemmer;

public class Indexer {
	private IndexWriter writer;
	public Indexer(String indexDirectoryPath) throws IOException {
		 Path indexPath = Paths.get(indexDirectoryPath);
		 if(!Files.exists(indexPath)) {
		 Files.createDirectory(indexPath);
		 }
		 //Path indexPath = Files.createTempDirectory(indexDirectoryPath);
		 Directory indexDirectory = FSDirectory.open(indexPath);
		 //create the indexer
		 IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
		 config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		 writer = new IndexWriter(indexDirectory, config);
		 }
		 public void close() throws CorruptIndexException, IOException {
		 writer.close();
		 }
		 private Document getDocument(File file) throws IOException {
		 String currentLine = "";
		 String allcont = "";
		 String body = "";
		 String people = "";
		 String title = "";
		 String places = "";
		 String stemmedtitle="";
		 String stemmedbody="";
		 String stemmedtitleandbody="";
		 StopAnalyzer sa = new StopAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
		 EnglishStemmer stemmer = new EnglishStemmer(); 

		 Document document = new Document();
		 //index file contents
		 BufferedReader br = new BufferedReader(new FileReader(file));
		 //String currentLine = br.readLine().toString();
		 Field contentField= null;
		 Field contentFieldPlaces= null;
		 Field contentFieldPeople= null;
		 Field contentFieldTitle= null;
		 Field contentFieldBody= null;
		 while ((currentLine = br.readLine()) != null)   {
			 //System.out.println (currentLine);
			 allcont = allcont + " " + currentLine;
		 }
		 if (allcont.contains("<BODY>"))
		 {
			 int pFrom = allcont.indexOf("<BODY>") + "<BODY>".length();
			 int pTo = allcont.lastIndexOf("</BODY>");
			 body = allcont.substring(pFrom, pTo);
			 //System.out.println (body);
		 }
		 if (allcont.contains("<PEOPLE>"))
		 {
			 int pFrom = allcont.indexOf("<PEOPLE>") + "<PEOPLE>".length();
			 int pTo = allcont.lastIndexOf("</PEOPLE>");
			 people = allcont.substring(pFrom, pTo);
			// System.out.println (people);
		 }
		 if (allcont.contains("<TITLE>"))
		 {
			 int pFrom = allcont.indexOf("<TITLE>") + "<TITLE>".length();
			 int pTo = allcont.lastIndexOf("</TITLE>");
			 title = allcont.substring(pFrom, pTo);
			// System.out.println (title);
		 }
		 if (allcont.contains("<PLACES>"))
		 {
			 int pFrom = allcont.indexOf("<PLACES>") + "<PLACES>".length();
			 int pTo = allcont.lastIndexOf("</PLACES>");
			 places = allcont.substring(pFrom, pTo);
			// System.out.println (places);
		 }
		 //String titleandbody = title + " " + body;
		 
		 String[] splittitle = title.split(" |,|;|:(?![0-9]*)|//(?![0-9]*)|!|-|\\\\|\\?|_|%|@|\\.(?![0-9]*)");
		 
		 for(int i=0 ;i<splittitle.length;i++) {
			 stemmer.setCurrent(splittitle[i]);
			 stemmer.stem();
			 stemmedtitle = stemmedtitle + " " + stemmer.getCurrent();
		 }
		 String[] splitbody = body.split(" |,|;|:(?![0-9]*)|//(?![0-9]*)|!|-|\\\\|\\?|_|%|@|\\.(?![0-9]*)");
		 for(int i=0 ;i<splitbody.length;i++) {
		 	stemmer.setCurrent(splitbody[i]);
		 	stemmer.stem();
		 	stemmedbody = stemmedbody + " " +stemmer.getCurrent();
		 }
		 stemmedtitleandbody = stemmedtitle + " " + stemmedbody;
		// System.out.println(stemmedtitleandbody);
		 
		 String endstring = places + " " + people + " " + stemmedtitleandbody;
		 System.out.println (endstring+"............");


			contentField = new Field(LuceneConstants.CONTENTS, endstring, TextField.TYPE_STORED);

			contentFieldPlaces = new Field(LuceneConstants.PLACES, places, TextField.TYPE_STORED);

			contentFieldPeople = new Field(LuceneConstants.PEOPLE, people, TextField.TYPE_STORED);

			contentFieldTitle = new Field(LuceneConstants.TITLE, stemmedtitle, TextField.TYPE_STORED);

			contentFieldBody = new Field(LuceneConstants.BODY, stemmedbody, TextField.TYPE_STORED);

		
		 
		 //index file name
		 Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(),
		StringField.TYPE_STORED);
		 //index file path
		 Field filePathField = new Field(LuceneConstants.FILE_PATH,
		file.getCanonicalPath(), StringField.TYPE_STORED);
		 document.add(contentField);
		 document.add(contentFieldPlaces);
		 document.add(contentFieldPeople);
		 document.add(contentFieldTitle);
		 document.add(contentFieldBody);
		 document.add(fileNameField);
		 document.add(filePathField);
		
		 br.close();
		 return document;
		 }
		 private void indexFile(File file) throws IOException {
		 System.out.println("Indexing "+file.getCanonicalPath());
		 Document document = getDocument(file);
		 writer.addDocument(document);
		 }
		 public int createIndex(String dataDirPath, FileFilter filter) throws
		IOException {
		 //get all files in the data directory
		 File[] files = new File(dataDirPath).listFiles();
		 for (File file : files) {
		 if(!file.isDirectory()
		 && !file.isHidden()
		 && file.exists()
		 && file.canRead()
		 && filter.accept(file)
		 ){
		 indexFile(file);
		 }
		 }
		 return writer.numRamDocs();
		 }


}
