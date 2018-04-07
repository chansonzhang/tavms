import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/2.
 */
public class MathUtils {
    public static double variance(List<Double> numbers) {
        double squareSum = 0;
        double avg = average(numbers);
        for (double number : numbers) {
            squareSum += Math.pow((number - avg), 2);
        }
        return squareSum / numbers.size();
    }

    /**
     * 标准差
     *
     * @param numbers
     * @return
     */
    public static double stddev(List<Double> numbers) {
        double variance = variance(numbers);
        return Math.sqrt(variance);
    }

    public static double stddev(double[] numbers) {
        List<Double> arr = new ArrayList<>();
        for (double number : numbers) {
            arr.add(number);
        }
        return stddev(arr);
    }

    public static double average(List<Double> numbers) {
        double sum = 0;
        for (double number : numbers) {
            sum += number;
        }
        return sum / numbers.size();
    }

    public static double average(double[] numbers) {
        List<Double> arr = new ArrayList<>();
        for (double number : numbers) {
            arr.add(number);
        }
        return average(arr);
    }

    public static double range(List<Double> numbers) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (double number : numbers) {
            if (number > max) {
                max = number;
            }
            if (number < min) {
                min = number;
            }
        }
        return max - min;
    }

    /**
     * 向量乘法
     *
     * @param vector1
     * @param vector2
     * @return
     */
    public static double vectorMultiply(double[] vector1, double[] vector2) {
        double multiply = 0;
        for (int k = 0; k < vector1.length; k++) {
            multiply += (vector1[k] * vector2[k]);
        }
        return multiply;
    }

    /**
     * 余弦相似度
     *
     * @param vector1
     * @param vector2
     * @return
     */
    public static double cosSimilarity(double[] vector1, double[] vector2) {
        double multiply = 0;
        double modsquare1 = 0;
        double modsquare2 = 0;
        for (int k = 0; k < vector1.length; k++) {
            multiply += (vector1[k] * vector2[k]);
            modsquare1 += (vector1[k] * vector1[k]);
            modsquare2 += (vector2[k] * vector2[k]);
        }
        if (modsquare1 - 0 < Math.pow(10, -6) || modsquare2 - 0 < Math.pow(10, -6)) {
            return 0;//空向量可以和任何向量匹配，需要最低的相似度鼓励迁入
        }
        return multiply / (Math.sqrt(modsquare1) * Math.sqrt(modsquare2));
    }

    public static double sum(List<Double> numbers) {
        double sum = 0;
        for (double number : numbers) {
            sum += number;
        }
        return sum;
    }

    public static double sum(double[] numbers) {
        List<Double> array=new ArrayList<>();
        for (double number : numbers) {
            array.add(number);
        }
        return sum(array);
    }

    public static boolean equalsZero(double number) {
        return Math.abs(number - 0) < Double.MIN_NORMAL;
    }

    /**
     * 向量求模
     *
     * @param vector
     * @return
     */
    public static double mod(double[] vector) {
        double modsquare1 = 0;
        for (int k = 0; k < vector.length; k++) {
            modsquare1 += (vector[k] * vector[k]);
        }

        return Math.sqrt(modsquare1);
    }

    /**
     * 归一化
     *
     * @param vector
     * @return
     */
    public static double[] normalization(double[] vector) {
        int d = vector.length;
        double mod = mod(vector);
        double[] newVector = new double[d];

        for (int k = 0; k < d; k++) {
            if (mod - 0 < Double.MIN_NORMAL) {
                newVector[k] = 0;
            } else {
                newVector[k] = vector[k] / mod;
            }
        }
        return newVector;
    }

    /**
     * 协方差
     *
     * @param vector1
     * @param vector2
     * @return
     */
    public static double covariance(double[] vector1, double[] vector2) {
        int dimension = vector1.length;
        double avg1 = average(vector1);
        double avg2 = average(vector2);
        List<Double> arr = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            arr.add((vector1[i] - avg1) * (vector2[i] - avg2));
        }
        return average(arr);
    }





    /**
     * 最小概率系数S-Si*Sj
     *
     * @param vector1
     * @param vector2
     * @return
     */
    public static double pCofficient(double[] vector1, double[] vector2) {
        int dimension = vector1.length;
        double s=MathUtils.vectorMultiply(vector1,vector2);
        double si=MathUtils.sum(vector1);
        double sj=MathUtils.sum(vector2);

        return s/(si*sj);
    }

    /**
     * 相关系数
     *
     * @param vector1
     * @param vector2
     * @return
     */
    public static double correlation(double[] vector1, double[] vector2) {
        double stdev1 = stddev(vector1);
        double stdev2 = stddev(vector2);
        return covariance(vector1,vector2)/(stdev1*stdev2);
    }


}
