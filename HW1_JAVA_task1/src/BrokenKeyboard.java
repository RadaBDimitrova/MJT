public class BrokenKeyboard {
    public static int calculateFullyTypedWords(String message, String brokenKeys){
        String mess = message.strip();
        if (mess.isEmpty()){
            return 0;
        }
        String[] res = mess.split("\\s+");
        char[] broken = brokenKeys.toCharArray();
        int ctr = res.length;
        for (int i = 0; i< res.length; i++){
            String r = res[i].strip();
            for(int j = 0; j< broken.length; j++){
            if (r.contains(Character.toString(broken[j]))){
                ctr--;
                break;
            }
        }
    }
        return ctr;
    }
}
