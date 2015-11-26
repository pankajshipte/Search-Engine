import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by SAMARTH on 2/11/2015.
 */
public class IO {

    void writeTemp(PrintWriter writer, TreeMap<String, String> treeMap) throws IOException {  //PrintWriter indexWriter, TreeMap<java.lang.Long, String> treeIndex
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, String> me : treeMap.entrySet()) {
            str.append(me.getKey()).append("-").append(me.getValue()).append("\n");
            writer.print(str.toString());
            str.delete(0, str.length());
        }
        writer.close();
        treeMap.clear();

    /*    for (Map.Entry<java.lang.Long, String> me : treeIndex.entrySet()) {
            str.append(me.getKey()).append("-").append(me.getValue());
            indexWriter.println(str.toString());
            str.delete(0, str.length());
        }
        treeIndex.clear();*/

    }

void findPostingList() throws FileNotFoundException {
        RandomAccessFile denseIndexReader = new RandomAccessFile("./Index/indexDense.txt", "r");
        RandomAccessFile sparseIndexReader = new RandomAccessFile("./Index/indexSparse.txt", "r");
        RandomAccessFile indexReader = new RandomAccessFile("./Index/indexSorted.txt", "r");
        long offset = 0;
        String line, line1;
        try {
            for (int i = 0; i < 50; i++) {
                line = sparseIndexReader.readLine();
                String str[] = line.split("-");
                System.out.print(str[0]+"-");
                denseIndexReader.seek(Long.parseLong(str[1]));
                line1 = denseIndexReader.readLine();
                indexReader.seek(Long.parseLong(line1));
                line1 = indexReader.readLine();
                System.out.println(line1);
/*                    offset = offset + line.length() + 2;
                sparseIndexReader.seek(offset);*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void findTitle() throws FileNotFoundException {
        RandomAccessFile indexReader = new RandomAccessFile("./Index/indexTitle.txt", "r");
        RandomAccessFile sparseIndexReader = new RandomAccessFile("./Index/indexSparseTitle.txt", "r");
        long offset = 0;
        String line, line1;
        try {
            for (int i = 0; i < 50; i++) {
                line = sparseIndexReader.readLine();
                String str[] = line.split("-");
                System.out.print(str[0]+"-");
                indexReader.seek(Long.parseLong(str[1]));
                line1 = indexReader.readLine();
                System.out.println(line1);
/*                    offset = offset + line.length() + 2;
                sparseIndexReader.seek(offset);*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
