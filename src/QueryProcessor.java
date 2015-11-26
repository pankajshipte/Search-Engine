import java.io.*;
import java.security.Key;
import java.util.*;

/**
 * Created by SAMARTH on 2/13/2015.
 */
public class QueryProcessor {

    static ArrayList<String> keyList = new ArrayList<String>();
    static ArrayList<java.lang.Long> valueList = new ArrayList<java.lang.Long>();
    static ArrayList<Long> pageid = new ArrayList<Long>();
    static ArrayList<String> offset = new ArrayList<String>();

    static TreeSet<String> getPageIds(String pl){
        TreeSet tp = new TreeSet<String>();
        String[] temp = pl.split(",");
        int len = temp.length;
        for (int j = 0; j < len; j++) {
            StringBuilder docId = new StringBuilder();
            for (int k = 0; k < temp[j].length(); k++) {
                if (temp[j].charAt(k) >= '0' && temp[j].charAt(k) <= '9') {
                    docId.append(temp[j].charAt(k));
                } else {
                    break;
                }
            }
            tp.add(docId.toString());
        }
        return tp;
    }


    static LinkedHashSet<Long> sort(String pl, boolean fieldQuery, char field){
        TreeSet<PLObject> tp = new TreeSet<PLObject>();
        LinkedHashSet<Long> treeSet = new LinkedHashSet<Long>();
        pl = pl.substring(0, pl.lastIndexOf(","));
        String[] temp = pl.split(",");
        int len = temp.length;
        if(fieldQuery) {
            for (int j = 0; j < len; j++) {
                tp.add(new PLObject(temp[j], field));
            }
        }else{
            for (int j = 0; j < len; j++) {
                tp.add(new PLObject(temp[j]));
            }
        }

        Iterator<PLObject> itr = tp.iterator();
        while(itr.hasNext()){
            treeSet.add(Long.parseLong(itr.next().getPageId()));
        }

        return treeSet;
    }

    static void printResult(HashSet<Long> res, RandomAccessFile titleDense) throws IOException {

        int cnt = 0;
        for (Long s : res) {
            int indx = Collections.binarySearch(pageid, s);

            if (indx < 0) {
                indx = -indx - 2;
                if (indx == -1) {
                    continue;
                }

                int count = 0;
                String line, title = "";
                titleDense.seek(Long.parseLong(offset.get(indx)));
                while ((line = titleDense.readLine()) != null && count++ <= 11) {
                    String[] str1 = line.split("-");
                    if (s == Long.parseLong(str1[0])) {
                        title = str1[1];
                        break;
                    }
                }
                if (!title.isEmpty()) {
                    System.out.println(s + "-" + title);
                }
            } else {
                titleDense.seek(Long.parseLong(offset.get(indx)));
                String[] title = titleDense.readLine().split("-");
                System.out.println(s + "-" + title[1]);
            }

            if (++cnt > 10) {
                break;
            }
        }
    }

    static String searchTerm(String str, RandomAccessFile denseReader, RandomAccessFile reader){
        try {
            String line = "";
            byte[] pl = new byte[100000];
            int indx = Collections.binarySearch(keyList, str);
            if (indx < 0) {
                indx = -indx - 2;
                int count = 0;
                denseReader.seek(valueList.get(indx));
                line = denseReader.readLine();
                while ((line = denseReader.readLine()) != null && count++ < 10) {
                    String[] str1 = line.split("-");
                    if(str.equalsIgnoreCase(str1[0])){
                        reader.seek(Long.parseLong(str1[1]));
                        reader.read(pl, 0, 100000);
                        String[] res = new String(pl).split("\n");
                        return res[0];
                    }
                }
                return null;
            } else {
                denseReader.seek(valueList.get(indx));
                reader.seek(Long.parseLong(denseReader.readLine()));
                reader.read(pl, 0, 100000);
                String[] res = new String(pl).split("\n");
                return res[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            String line;
            if(args.length != 2){
                System.out.println("Invalid Number of arguments");
                System.exit(-1);
            }
            String inpFile = args[0];
            String path = args[1];
            int tc;

            BufferedReader sparseReader = new BufferedReader (new FileReader(path + "/indexSparse.txt"));
            RandomAccessFile denseReader = new RandomAccessFile (path + "/indexDense.txt", "r");
            RandomAccessFile reader = new RandomAccessFile (path + "/index.txt", "r");

            BufferedReader titleSparse = new BufferedReader(new FileReader(path + "/indexSparseTitle.txt"));
            RandomAccessFile titleDense = new RandomAccessFile(path + "/indexTitle.txt", "r");

            BufferedReader inp = new BufferedReader (new FileReader(inpFile));
            tc = Integer.parseInt(inp.readLine());


            while ((line = sparseReader.readLine()) != null) {
                String[] str = line.split("-");
                keyList.add(str[0]);
                valueList.add(Long.parseLong(str[1]));
            }
            sparseReader.close();

            while ((line = titleSparse.readLine()) != null) {
                String[] str = line.split("-");
                pageid.add(Long.parseLong(str[0]));
                if(str.length > 1)
                    offset.add(str[1]);
                else
                    offset.add("");
            }
            titleSparse.close();
            String query;
            boolean fieldQuery;
            while(tc-- > 0) {
                fieldQuery = false;
                query = inp.readLine();
                System.out.println("***" + query + "***");
                long start = System.currentTimeMillis();

                StopStem stopStem = new StopStem();
                for (int i = 0; i < query.length(); i++) {
                    if ((query.charAt(i) >= 65 && query.charAt(i) <= 90) || (query.charAt(i) >= 97 && query.charAt(i) <= 122) || query.charAt(i) == ':') {
                        continue;
                    } else {
                        query = query.replace(query.charAt(i), ' ');
                    }
                }

                String str;
                LinkedHashSet<Long> res = new LinkedHashSet<Long>();
                LinkedHashSet<Long> tp;
                String[] term;
                term = query.split(" ");

                int qlen = term.length;
                for (int i = 0; i < qlen; i++) {
                    term[i] = term[i].trim().toLowerCase();
                    int tlen = term[i].length();
                    char field = 't';
                    for (int j = 0; j < tlen; j++) {
                        if(term[i].charAt(j) == ':'){
                            fieldQuery = true;
                            term[i] = term[i].substring(j+1);
                            break;
                        }
                        field = term[i].charAt(j);
                    }

                    if (!term[i].isEmpty() && !stopStem.stopWords.contains(term[i])) {
                        str = stopStem.stemming(term[i]);
                        String pl = searchTerm(str, denseReader, reader);
                        if (pl != null) {
//                            if(fieldQuery) {
                                tp = sort(pl, fieldQuery, field );
                            /*}else{
                                tp = getPageIds(pl);
                            }*/

                            if (res.size() != 0) {
                                res.retainAll(tp);
                            } else {
                                res.addAll(tp);
                            }
                            tp.clear();
                        }
                    }
                }

                long end = System.currentTimeMillis();

                if (res.size() > 0) {
                    printResult(res, titleDense);
                } else {
                    System.out.println("No Result found..");
                }

                System.out.println("=>" + (end - start) / 1000f + "secs !\n");

            }
            System.out.println("Thank you :)");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

   }

}
