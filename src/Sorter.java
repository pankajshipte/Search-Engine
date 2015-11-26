import java.io.*;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by SAMARTH on 2/12/2015.
 */
public class Sorter {
    void sortIndex() throws IOException {
        PrintWriter writer = new PrintWriter("./Index/indexSorted.txt");
        TreeSet<PLObject> hashSet = new TreeSet<PLObject>();
        BufferedReader reader = new BufferedReader(new FileReader("./Index/index.txt"));
        String line = "";
        while((line = reader.readLine()) != null){
            String str[] = line.split("-");
            writer.print(str[0] + "-");
            String pl[] = str[1].split(",");
            int len = pl.length;
            for (int i = 0; i < len; i++) {
                hashSet.add( new PLObject(pl[i]));
            }
            Iterator<PLObject> itr = hashSet.iterator();
            while(itr.hasNext() &&  --len != 0){
                writer.print(itr.next().list+",");
            }
            writer.print(itr.next().list);
            writer.println();
            hashSet.clear();
        }
        writer.close();
    }
}
