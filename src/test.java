import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/28.
 */
public class test {
    public static void main(String[] args){
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);//��������
        int a=2000;
        int b=100000;
        System.out.println(df.format(a/b));
        double totalSim=0.00;
        totalSim+=0.77;
        System.out.println(1.0/2);
    }
}
