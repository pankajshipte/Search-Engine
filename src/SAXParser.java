import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
//import java.util.concurrency;
import java.io.*;
import java.util.*;

/**
 * Created by pankaj on 18/1/15.
 */
@SuppressWarnings("ALL")
class SAXParser extends DefaultHandler {
    int pageCount = 0;
    private static final int TITLE_COUNT = 1, INFO_COUNT=2, CATEGORY_COUNT=3, LINK_COUNT=4, REFERENCE_COUNT=5, TEXT_COUNT=6;
    public final Set<String> stopWords = new HashSet<String>(Arrays.asList("ca","ht","pt","nl","vo","nbsp","a", "about", "above", "after","need","thats","using","he'd","said","noted","particular","until","becoming","thanks","over","ff","thereby","she","something","right","these","else","once","possibly","shown","respectively","he","theirs","apart","shows","few","further","he's","somebody","downwards","herself","each","wherever","go","t's","she's","before","accordingly","made","indicate","namely","six","ed","hereafter","she'd","sufficiently","slightly","could","consider","eighty","usually","ninety","tell","do","affecting","whither","thorough","f","look","g","ex","d","may","e","b","noone","c","needs","a","n","o","l","m","won't","j","ones","k","h","i","w","yes","v","somethan","ref","eg","u","new","t","s","what","r","q","p","nothing","having","et","z","y","strongly","x","here's","yet","shes","ca","thru","anywhere","least","you'd","took","by","same","enough","has","who","couldn't","would","approximately","any","everybody","overall","had","primarily","be","biol","think","get","seeing","begins","likely","far","a's","much","and","co","particularly","gotten","near","i'd","better","often","against","doing","containing","seeming","example","i'm","make","does","invention","shan't","ignored","saying","obtained","aren","tried","former","through","possible","following","especially","name","tries","edu","all","keeps","five","obviously","makes","she'll","at","as","still","hello","therefore","neither","shed","never","which","see","ran","take","sec","anyone","am","i'll","there","an","off","thoroughly","ah","nay","why","they","nobody","somehow","you've","no","nine","otherwise","anyways","hed","of","help","given","among","recently","says","anybody","hes","on","only","her","ok","everyone","that's","itself","oh","maybe","or","done","regarding","third","sensible","them","then","will","ought","furthermore","novel","auth","upon","different","indeed","getting","home","announce","most","thanx","followed","across","aside","looking","thank","normally","unless","where's","mg","rather","me","ml","aren't","similar","kept","mr","pages","don't","it's","et-al","my","whereupon","na","okay","it'd","specified","per","how's","nd","sometime","within","thereupon","described","truly","follows","you're","cause","second","tends","hid","last","sometimes","being","contains","since","actually","him","where","every","eight","related","potentially","almost","unto","looks","kg","more","results","his","inc","we'd","when","someone","wonder","value","useful","none","certainly","seriously","everywhere","asking","onto","appropriate","isn't","c's","such","hers","liked","means","whereafter","here","anymore","heres","predominantly","km","whole","this","causes","appreciate","becomes","way","adj","from","hi","believe","while","was","id","allows","ain't","able","if","corresponding","seemed","ie","below","various","wherein","lest","between","less","those","is","it","added","besides","ourselves","gives","similarly","important","your","gets","into","howbeit","im","past","in","know","section","two","away","necessary","proud","themselves","act","also","lets","changes","found","couldnt","appear","etc","they'll","arent","hopefully","ours","its","omitted","yourselves","showed","exactly","c'mon","although","formerly","greetings","it'll","entirely","along","secondly","serious","alone","awfully","going","nowhere","relatively","how","under","suggest","available","became","indicated","always","theres","inward","refs","itd","own","indicates","specify","try","we","reasonably","specifying","give","i've","accordance","next","use","hardly","vs","consequently","run","date","mrs","resulting","when's","significant","substantially","best","whenever","mostly","definitely","unfortunately","whatever","we'll","later","come","back","us","seen","un","seem","cannot","up","gave","either","insofar","sorry","doesn't","they'd","down","part","quickly","happens","keep","to","arise","affects","com","both","inner","uucp","become","you'll","somewhere","poorly","meantime","must","th","didn't","affected","after","mug","necessarily","nevertheless","who's","whereby","considering","taken","welcome","what's","index","however","whose","so","behind","gone","willing","whereas","that","associated","than","whom","unlikely","thence","several","previously","due","got","ltd","immediately","hereby","sub","can","www","about","well","sup","re","rd","above","que","qv","four","placed","too","yours","thus","resulted","moreover","provides","you","soon","owing","immediate","seven","anything","effect","whoever","abst","certain","somewhat","pp","our","brief","very","specifically","out","forth","via","for","hereupon","everything","hundred","towards","zero","whether","went","elsewhere","beyond","course","whence","are","shouldn't","can't","page","briefly","yourself","therein","thereafter","plus","information","million","others","we're","mainly","viz","did","again","wasn't","like","without","shall","'ll","non","many","not","present","nos","he'll","nor","obtain","haven't","anyhow","cant","now","promptly","say","myself","saw","ask","some","why's","outside","might","put","line","ord","self","trying","they've","according","seems","twice","latter","presumably","inasmuch","probably","giving","want","end","regardless","hence","just","readily","fifth","let","already","should","research","wouldn't","really","successfully","beforehand","mustn't","clearly","despite","hither","but","old","afterwards","meanwhile","wish","herein","hadn't","amongst","little","show","used","together","though","been","hasn't","anyway","were","sent","please","toward","there's","three","concerning","sure","throughout","goes","except","regards","we've","comes","wants","himself","knows","importance","contain","even","latterly","known","perhaps","ever","stop","other","allow","have","one","selves","currently","recent","merely","let's","showns","because","another","fix","during","lately","mean","they're","apparently","beginnings","weren't","with","beginning","nearly","the","came","ending","around","begin","beside","nonetheless","quite","largely","instead","uses","significantly","their","first","miss"));
    List<CustomFile> fileList = new ArrayList<CustomFile>();
    private final TreeMap<String, String> treeMap = new TreeMap<String, String>();
    private final HashMap<String, int[]> hashMap = new HashMap<String, int[]>();
    StringBuilder titleIndex = new StringBuilder();
    StopStem stopStem =new StopStem();
    private Boolean firstWrite = true;
    private int count = TEXT_COUNT;
    StringBuilder sb = new StringBuilder();
    private Boolean isPage = false;
    private String tempVal = "";
    PrintWriter sparseIndexWriter;
    PrintWriter indexWriter;
    private long offset = 0;
    IO io = new IO();
    long indexOffset = 0;
    int cnt = 10;

    public SAXParser(List<CustomFile> fileList, PrintWriter writer, PrintWriter sparseWriter) {
        this.fileList = fileList;
        this.indexWriter = writer;
        this.sparseIndexWriter = sparseWriter;
    }

    void parseToken(String tempVal, int count){
//        tempVal = tempVal.replaceAll("\\P{L}", " ").trim();
        if(!tempVal.isEmpty()) {
            for (int i = 0; i < tempVal.length(); i++) {
                if((tempVal.charAt(i) >= 65 && tempVal.charAt(i) <= 90) || (tempVal.charAt(i) >= 97 && tempVal.charAt(i) <= 122)){
                    continue;
                }else{
                    tempVal = tempVal.replace(tempVal.charAt(i), ' ');
                }
            }
            String str;
            String[] token = tempVal.split(" ");
            for (int i = 0; i < token.length; i++) {
                token[i] = token[i].trim();
                token[i] = token[i].toLowerCase();
                if(count == TITLE_COUNT || !stopStem.stopWords.contains(token[i])){
                    str = stopStem.stemming(token[i]);
                    if(!str.isEmpty() && (str.length() > 2 || count == TITLE_COUNT)) {  //
                        int [] wordFlags = new int[7];
                        wordFlags[count] = 1;
                        wordFlags[0]++;
                        if(count == TITLE_COUNT){
                            titleIndex.append(str).append(" ");
                        }
                        if(hashMap.containsKey(str)) {
                            int [] temp = (int []) hashMap.get(str);
                            wordFlags = add(wordFlags, temp);
                        }
                        hashMap.put(str, wordFlags);
//                        System.out.print(str+"("+count+") ");
                    }
                }
            }
        }
    }

    private int[] add(int[] wordFlags, int[] temp) {
        if(temp != null) {
            for (int i = 0; i < wordFlags.length; i++) {
                wordFlags[i] = wordFlags[i] + temp[i];
            }
        }
        return wordFlags;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //super.startElement(uri, localName, qName, attributes);
        if (qName.equalsIgnoreCase("page")) {
            isPage = true;
        }else if(qName.equalsIgnoreCase("revision")){
            isPage = false;
        }else if(qName.equalsIgnoreCase("contributor")){
            count = INFO_COUNT;
        }else if(qName.equalsIgnoreCase("title")){
            count = TITLE_COUNT;
        }else if(qName.equalsIgnoreCase("text")){
            count = TEXT_COUNT;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        tempVal = String.copyValueOf(ch, start, length).trim();
        String[] token = tempVal.trim().split("\n");

        for (int i = 0; i < token.length; i++) {
            if(!token[i].isEmpty()){
                if (count == TEXT_COUNT || count == REFERENCE_COUNT || count == LINK_COUNT || count == CATEGORY_COUNT ||  count == INFO_COUNT) {  // || isOthrCat
                    if (token[i].trim().startsWith("{{Infobox settlement")) {
                        count = INFO_COUNT;
                        token[i] = token[i].replace("{{Infobox settlement","") ;
                    }else if (token[i].trim().equals("}}")) {
                        count = TEXT_COUNT;
                    }else if (token[i].trim().startsWith("==References==") || token[i].trim().startsWith("== References ==") || token[i].trim().startsWith("===References===")) {
                        count = REFERENCE_COUNT;
                        token[i] = token[i].replace("References","") ;
                    } else if (token[i].trim().startsWith("==External links==") || token[i].trim().startsWith("== External links ==") || token[i].trim().startsWith("===External links===")) {
                        count = LINK_COUNT;
                        token[i] = token[i].replace("External links","") ;
                    } else if (token[i].trim().startsWith("[[Category:")) {
                        count = CATEGORY_COUNT;
                        token[i] = token[i].replace("[[Category:", "");
                    } else if (token[i].trim().startsWith("[[ Category:")) {
                        count = CATEGORY_COUNT;
                        token[i] = token[i].replace("[[ Category:", "");
                    } else if (token[i].trim().startsWith("==")){
                        count = CATEGORY_COUNT;
                        token[i] = "";
                    }else if(token[i].trim().startsWith("#") || token[i].trim().startsWith(":")){
                        count = INFO_COUNT;
                    } else if (token[i].trim().startsWith("[[") || token[i].trim().startsWith("!")) {
                        token[i] = "";
                    }else if(token[i].trim().startsWith("|")){
                        String[] temp = token[i].split("=");
                        if(temp.length > 1) {
                            token[i] = temp[1];
                        }else {
                            token[i] = "";
                        }
                    }
                }
                parseToken(token[i], count);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equals("file")) {
                if (treeMap.size() != 0) {
                    File tempFile = File.createTempFile("tempRecList", ".txt", new File("./Index"));
                    PrintWriter bufferedWriter = new PrintWriter(new FileWriter(tempFile));
                    io.writeTemp(bufferedWriter, treeMap);
                    tempFile.deleteOnExit();
                    fileList.add(new CustomFile(tempFile));
                }
//                Runtime.getRuntime().gc();
            } else if (qName.equalsIgnoreCase("page")) {
                isPage = false;
                count = TEXT_COUNT;
                StringBuilder stringBuilder = new StringBuilder();
                for (Map.Entry<String, int[]> ee : hashMap.entrySet()) {
                    if (treeMap.containsKey(ee.getKey())) {
                        stringBuilder.append(treeMap.get(ee.getKey()));
                        stringBuilder.append(",").append(offset);
                    } else {
                        stringBuilder.append(offset);
                    }

                    int[] temp = (int[]) ee.getValue();
                    //                str = str.append(temp[0]).append(",");
                    if (temp[1] != 0){
                        stringBuilder = stringBuilder.append("t").append(temp[1]);}
                    if (temp[5] != 0){
                        stringBuilder = stringBuilder.append("r").append(temp[5]);}
                    if (temp[3] != 0){
                        stringBuilder = stringBuilder.append("c").append(temp[3]);}
                    if (temp[2] != 0){
                        stringBuilder = stringBuilder.append("i").append(temp[2]);}
                    if (temp[6] != 0){
                        stringBuilder = stringBuilder.append("b").append(temp[6]);}
                    if (temp[4] != 0){
                        stringBuilder = stringBuilder.append("e").append(temp[4]);}

                    treeMap.put(ee.getKey(), stringBuilder.toString());
                    stringBuilder.delete(0, stringBuilder.length());
                }
                hashMap.clear();
                if (pageCount > 4000) { //MAX_HEAP - Runtime.getRuntime().freeMemory()) >= blocksize
                    File tempFile = File.createTempFile("tempRecList", ".txt", new File("./Index"));
                    PrintWriter tempFileWriter = new PrintWriter(new FileWriter(tempFile));
                    io.writeTemp(tempFileWriter, treeMap);
                    tempFile.deleteOnExit();
                    fileList.add(new CustomFile(tempFile));
                    pageCount = 0;
                } else {
                    pageCount++;
                }
//                Runtime.getRuntime().gc();
            } else if (qName.equalsIgnoreCase("revision")) {
                isPage = true;
                count = TEXT_COUNT;
            } else if (qName.equalsIgnoreCase("contributor")) {
                count = TEXT_COUNT;
            } else if (qName.equalsIgnoreCase("title")) {
                count = TEXT_COUNT;
            } else if (qName.equalsIgnoreCase("id")) {
                if (isPage) {
//                offset = Integer.parseInt(tempVal) - docId;
                    offset = Integer.parseInt(tempVal);
                    sb.append(offset).append("-").append(titleIndex);
                    indexWriter.println(sb);
                    if(++cnt > 10){
                        cnt = 0;
                        sparseIndexWriter.println(offset+"-"+indexOffset);
                    }
                    indexOffset+=(sb.length()+2);
                    sb.delete(0, sb.length());
                    titleIndex.delete(0, titleIndex.length());
                }
            } else if (qName.equalsIgnoreCase("text")) {
                count = TEXT_COUNT;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
