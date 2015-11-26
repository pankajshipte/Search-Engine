import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Created by SAMARTH on 2/11/2015.
 */
public class Merge {

    List<CustomFile> fileList = new ArrayList<CustomFile>();
    private final TreeMap treeMap = new TreeMap();

    public Merge(List<CustomFile> fileList) {
        this.fileList = fileList;
    }

    void mergeIndex() throws IOException {
        PriorityQueue<PQObject> priorityQueue = new PriorityQueue<PQObject>();
        PrintWriter writer = new PrintWriter(new FileWriter(new File("./Index/index.txt")));
        PrintWriter denseIndexWriter = new PrintWriter("./Index/indexDense.txt");
        PrintWriter sparseIndexWriter = new PrintWriter("./Index/indexSparse.txt");
        BufferedReader[]fp = new BufferedReader[fileList.size()];
        boolean[] finished = new boolean[fileList.size()];
        StringBuilder stringBuilder = new StringBuilder();
        PQObject top, next;
        treeMap.clear();
        int i=0;

        for (CustomFile tempFile : fileList)
            fp[i++] = new BufferedReader(new FileReader(tempFile.getFile()));

        String line;

        for (i = 0; i < fp.length; ++i) {
            if ((line = fp[i].readLine()) != null) {
                String str[] = line.split("-");
                priorityQueue.add(new PQObject(str[0], i, str[1]));
            }
        }
        if(fp.length == 1) {
            if ((line = fp[0].readLine()) != null) {
                String str[] = line.split("-");
                priorityQueue.add(new PQObject(str[0], 0, str[1]));
            }
        }

        long offset = 0, count = 10, denseOffset = 0;
        while(priorityQueue.size()>1) {
            top = priorityQueue.poll();
            if (top.getWord().toString().equalsIgnoreCase(priorityQueue.peek().getWord().toString())) {
                next = priorityQueue.poll();
                next.append(top);
                priorityQueue.add(next);
            } else {
                writer.println(top.getWord() + "-" + top.getpostingList());
                stringBuilder.delete(0, stringBuilder.length());
                stringBuilder.append(top.getWord()).append("-").append(offset + top.getWord().length() + 1);   //dash
                denseIndexWriter.println(stringBuilder.toString());
                if(++count >= 10){
                    count = 0;
                    sparseIndexWriter.println(top.getWord() + "-" + (denseOffset + top.getWord().length() + 1));
                }
                denseOffset = denseOffset + stringBuilder.length() + 2;
                offset = offset + top.getWord().length() + top.getpostingList().length() + 3;
            }

            if (!finished[top.gettempFileID()] && (line = fp[top.gettempFileID()].readLine()) != null) {
                String str[] = line.split("-");
                priorityQueue.add(new PQObject(str[0], top.gettempFileID(), str[1]));
            }else {
                fp[top.gettempFileID()].close();
                finished[top.gettempFileID()] = true;
            }
        }

        top = priorityQueue.poll();
        writer.println(top.getWord() + "-" + top.getpostingList());
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(top.getWord()).append("-").append(offset + top.getWord().length() + 1);   //dash
        denseIndexWriter.println(stringBuilder.toString());
        if(++count >= 10){
            sparseIndexWriter.println(top.getWord() + "-" + (denseOffset + top.getWord().length() + 1));
        }

        writer.close();
        denseIndexWriter.close();
        sparseIndexWriter.close();
        for ( i = 0; i < fp.length; ++i) {
            fp[i].close();
        }
    }
}
