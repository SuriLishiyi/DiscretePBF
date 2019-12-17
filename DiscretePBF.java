import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class PBF
{
    private static HashMap<Integer, int[]> map;
    private static HashMap<Integer, int[]> intervals = new HashMap<>();
    private static HashMap<Integer, BasicBloomFilter> bfArray = new HashMap<>();
    private static ArrayList<String[]> allData = new ArrayList<>();
    private static int num;
    private static int bitNum = 10000;
    private static int hashNum = 4;

    public PBF() { }

    public static boolean insertString(){
        for(String[] data: allData) {
            insertString(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]));
        }
        return true;
    }

    public static boolean insertString(String element, int startTime, int endTime)
    {
        ArrayList<Integer> allInterval = getAllInterval(startTime, endTime);
        for (int i: allInterval) {
            bfArray.get(i).insertString(element);
        }
        return true;
    }

    public static boolean queryString(String element, int startTime, int endTime)
    {
        ArrayList<Integer> indexArray = getFinalInterval(startTime, endTime);
        for (int i: indexArray) {
            if(bfArray.get(i).queryString(element)){
                return true;
            }
        }
        return false;
    }

    public static int getLevelNum(int num)
    {
        int result = 0;
        for (int i = 0; i < CommonConstants.g.length; i++)
        {
            if ((num + 1) <= CommonConstants.g[i])
            {
                result = i + 1;
                break;
            }
        }
        return result;
    }

    public static ArrayList<Integer> getFinalInterval(int start, int end){
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);
        ArrayList<Integer> list = new ArrayList<>();
        while (!queue.isEmpty()) {
            int n = queue.remove();
            if(intervals.get(n) != null) {
                int first = intervals.get(n)[0];
                int second = intervals.get(n)[1];
                if(start<=first && second <= end) {
                    list.add(n);
                }else if(start > second || end < first){
                    continue;
                }else {
                    if(intervals.get(2*n) == null && intervals.get(2*n+1) == null){
                        list.add(n);
                    }else {
                        queue.add(2*n);
                        queue.add(2*n+1);
                    }
                }
            }
        }
        return list;
    }

    public static ArrayList<Integer> getAllInterval(int start, int end){
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);
        ArrayList<Integer> list = new ArrayList<>();
        while (!queue.isEmpty()) {
            int n = queue.remove();
            if(intervals.get(n) != null) {
                int first = intervals.get(n)[0];
                int second = intervals.get(n)[1];
                if(start > second || end < first){
                    continue;
                }else {
                    list.add(n);
                    queue.add(2*n);
                    queue.add(2*n+1);
                }
            }
        }
        return list;
    }

    public static TreeSet<Integer> filterPoints(String fileName){
        TreeSet<Integer> set = new TreeSet<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String tempStr = null;
            while ((tempStr = reader.readLine()) != null){
                String[] str = tempStr.split(" ");
                allData.add(str);
                set.add(Integer.parseInt(str[1]));
                set.add(Integer.parseInt(str[2]) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        set.comparator();
        return set;
    }

    public static HashMap<Integer, int[]> constructMap(ArrayList<Integer> list, int head, int tail){
        HashMap<Integer, int[]> map = new HashMap<>();
        int current = 0;
        if(list.get(0) > head) {
            map.put(0, new int[]{head, list.get(0) -1});
            current ++;
        }
        for(int i=0; i<list.size(); i++) {
            int first = list.get(i);
            int second = tail + 1;
            if(i+1<list.size()){
                second = list.get(i+1);
            }
            map.put(current, new int[]{first, second-1});
            current++;
        }
        return map;
    }

    public static void constructIntervals(){
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, num-1, 1});
        while (!queue.isEmpty()) {
            int[] interval = queue.remove();
            int first = map.get(interval[0])[0];
            int second = map.get(interval[1])[1];
            int pos = interval[2];
            intervals.put(pos, new int[]{first, second});
            bfArray.put(pos, new BasicBloomFilter(bitNum, hashNum));
            if(interval[0] == interval[1]) {
                continue;
            }
            int mid = (interval[0] + interval[1])/2;
            if(mid >= interval[0]){
                queue.add(new int[]{interval[0], mid, pos*2});
            }
            if(mid < interval[1]) {
                queue.add(new int[]{mid+1, interval[1], pos*2+1});
            }
        }

    }

    public static void main(String[] args)
    {
        TreeSet<Integer> set = filterPoints("/Users/surisyli/data.txt");
        ArrayList<Integer> list = new ArrayList<>(set);
        for(int i: list){
            System.out.print(i + " ");
        }
        System.out.println();
        map = constructMap(list, 0, 40);
        for(Map.Entry<Integer, int[]> entry: map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue()[0] + " " + entry.getValue()[1]);
        }
        num = map.size();
        System.out.println("==============");
        constructIntervals();
        for(Map.Entry<Integer, int[]> entry: intervals.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue()[0] + " " + entry.getValue()[1]);
        }
        System.out.println("==============");
        ArrayList<Integer> finalInterval = getFinalInterval(0, 9);
        for(int i: finalInterval) {
            System.out.print(i + " ");
        }
        System.out.println("\n==============");
        ArrayList<Integer> allInterval = getAllInterval(0, 9);
        for(int i: allInterval) {
            System.out.print(i + " ");
        }
        System.out.println("\n==============");
        insertString();
        System.out.println(queryString("30", 3, 25));
        System.out.println(queryString("100", 19, 25));
    }

}
