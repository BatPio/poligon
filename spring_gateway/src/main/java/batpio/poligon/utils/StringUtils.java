package batpio.poligon.utils;

public class StringUtils {

    public static String join(char delimiter, Object[] objects) {
        StringBuilder sb = new StringBuilder();
        for(Object o : objects) {
            sb.append(o);
            sb.append(delimiter);
        }
        return sb.toString();
    }

}
