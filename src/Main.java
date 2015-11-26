import javax.xml.parsers.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length == 1){
            try {
                List<CustomFile> fileList = new ArrayList<CustomFile>();
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                javax.xml.parsers.SAXParser parser = parserFactory.newSAXParser();
                PrintWriter indexWriter = new PrintWriter(new FileWriter("./Index/indexTitle.txt"));
                PrintWriter sparseIndexWriter = new PrintWriter(new FileWriter("./Index/indexSparseTitle.txt"));
                SAXParser handler = new SAXParser(fileList, indexWriter, sparseIndexWriter);
                long start = System.currentTimeMillis();
                System.out.println("Working ...");
                parser.parse(args[0], handler); //untitled
                Merge merger = new Merge(fileList);
                merger.mergeIndex();
                System.out.println("Done ...");
                long end = System.currentTimeMillis();
                System.out.println("=>"+(end - start) / 1000f +"secs done !\n");
                Sorter sorter = new Sorter();
                sorter.sortIndex();
                start = System.currentTimeMillis();
                System.out.println("=>"+(start - end) / 1000f +"secs done !\n");
                indexWriter.close();
                sparseIndexWriter.close();
                /*IO io = new IO();
                io.findPostingList();
                io.findTitle();*/
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error\n");
                System.exit(-1);
            }
        }else{
            System.out.println("Enter proper arguments\n");
            System.exit(-1);
        }
    }
}
