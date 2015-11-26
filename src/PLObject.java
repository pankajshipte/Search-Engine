/**
 * Created by SAMARTH on 2/12/2015.
 */
public class PLObject implements Comparable<PLObject>{
    String list;
    int value;

    public PLObject(String str, char field) {
        list = str;
        StringBuilder val = new StringBuilder();
        for (int i = 0; i <list.length(); i++) {
            if (list.charAt(i) == field) {
                do {
                    val.append(list.charAt(++i));
                } while (((i+1) < (list.length() - 1)) && (list.charAt(i+1) >= '0') && (list.charAt(i+1) <= '9'));
                value = value + Integer.valueOf(val.toString());
                break;
            }
        }
    }


    public PLObject(String str) {
        list = str;
        int mul = 1;
        StringBuilder val = new StringBuilder();
        for (int i = 0; i <list.length(); i++) {
            if (list.charAt(i) >= 'a' && list.charAt(i) <= 'z') {
                if(list.charAt(i) == 't')
                    mul = 100;
                else if(list.charAt(i) == 'r')
                    mul = 20;
                else if(list.charAt(i) == 'c')
                    mul = 35;
                else if(list.charAt(i) == 'i')
                    mul = 50;
                else if(list.charAt(i) == 'b')
                    mul = 30;
                else if(list.charAt(i) == 'e')
                    mul = 10;

                do {
                    val.append(list.charAt(++i));
                } while (((i+1) < list.length()) && (list.charAt(i+1) >= '0') && (list.charAt(i+1) <= '9'));
                value = value + Integer.valueOf(val.toString()) * mul;
                val.delete(0, val.length());
            }
        }
    }

    @Override
    public int compareTo(PLObject o) {
        if(value < o.value){
            return 1;
        }else {
            return -1;
        }
    }

    public String getPageId(){
        int len = list.length();
        StringBuilder pageId = new StringBuilder();
        for (int k = 0; k < len; k++) {
            if (list.charAt(k) >= '0' && list.charAt(k) <= '9') {
                pageId.append(list.charAt(k));
            } else {
                break;
            }
        }
        return pageId.toString();
    }

}
