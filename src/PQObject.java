import java.util.Comparator;

public class PQObject implements Comparable<PQObject>{
    StringBuilder word;
    int tempFileID;
    StringBuilder postingList;

    public PQObject(String word, int tempFileID, String postingList) {
        this.word = new StringBuilder(word);
        this.tempFileID = tempFileID;
        this.postingList = new StringBuilder(postingList);
    }

    public void append(PQObject obj){
        this.postingList.append(",").append(obj.getpostingList());
    }

    public StringBuilder getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = new StringBuilder(word);
    }

    public int gettempFileID() {
        return tempFileID;
    }

    public void settempFileID(int tempFileID) {
        this.tempFileID = tempFileID;
    }

    public StringBuilder getpostingList() {
        return postingList;
    }

    public void setpostingList(String postingList) {
        this.postingList = new StringBuilder(postingList);
    }

    @Override
    public int compareTo(PQObject obj) {
        return this.getWord().toString().compareToIgnoreCase(obj.getWord().toString());
    }

}
