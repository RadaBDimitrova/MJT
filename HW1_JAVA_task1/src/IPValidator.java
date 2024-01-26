public class IPValidator {

    public static boolean validateIPv4Address(String str) {
        if (str.isEmpty() || str.charAt(0) == '.'){
            return false;
        }
        String[] res = str.split("\\.");
        if (res.length != 4) {
            return false;
        }
        int dotCtr = 0;
        for (char ch : str.toCharArray()) {
            if (ch != '.' && (ch < '0' || ch > '9')) {
                return false;
            }
            if (ch == '.'){
                dotCtr++;
            }
        }
        if(dotCtr!=3){
            return false;
        }
        for (String st : res) {
            int num = Integer.parseInt(st);
            if (num < 0 || num > 255) {
                return false;
            }
            if(st.length()>1 && num<10){
                return false;
            }
        }
        return true;
    }

}
