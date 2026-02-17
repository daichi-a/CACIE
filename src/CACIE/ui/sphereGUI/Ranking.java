package CACIE.ui.sphereGUI;
public class Ranking{
    public static void main(String[] args){
        double[] mark = {50.0, 20.0, 100.0, 70.0, 30.0, 80.0};
        int[] rank = new int[mark.length];
        rank = Rank.getRank(mark);

        //•\Ž¦
        for(int i = 0; i < rank.length; i++){
            System.out.println(mark[i] + " score is "
                               + rank[i] + " rank");
        }
    }
}

class Rank{
    public static int[] getRank(double[] mark){
        int[] rank = new int[mark.length];

        for(int k = 0; k < rank.length; k++){
            rank[k] = 1;
        }

        for(int i = 0; i < mark.length; i++){
            for(int j = 0; j < mark.length; j++){
                if(mark[i] > mark[j]){
                    rank[j] += 1;
                }
            }
        }
        return rank;
    }
}