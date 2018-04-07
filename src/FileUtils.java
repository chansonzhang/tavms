import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/2.
 */
public class FileUtils {
    public static final String outputDir="E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\";
    public static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss-");
    public static Date date=new Date();
    public static final String prefix=format.format(date);
    public static boolean writeVarianceTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\variance-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static boolean writeVarianceRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\variance-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content+"\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeUsageTAVMS(String content){
        FileWriter fileWriter = null;
        try {
           fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\usage-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content+"\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeUsageRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\usage-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content+"\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeDiversityTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\diversity-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content+"\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAvgTypeNumberTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\avgTypeNumber-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content+"\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAvgTypeNumberRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\avgTypeNumber-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content+"\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeMigratedVMCountTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\migratedVMCount-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeMigratedVMCountRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\migratedVMCount-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeUsageRangeTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\usageRange-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeUsageRangeRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\usageRange-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAvgAppDistributionRateTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\appDistributionRate-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean writeAvgAppDistributionRateRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\appDistributionRate-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAvgVarianceOfResUsageInHostTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\avgVarianceOfUsagesOfEachResInVm-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAvgVarianceOfResUsageInHostRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\avgVarianceOfUsagesOfEachResInVm-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAvgResUsageRangeInOneHostTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\avgResUsageRangeInOneHost-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAvgResUsageRangeInOneHostRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\avgResUsageRangeInOneHost-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeOverloadHostNumberTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\overloadHostNumber-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeOverloadHostNumberRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\overloadHostNumber-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAffectedVmNumberTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\affectedVmNumber-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAffectedVmNumberRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\affectedVmNumber-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeVmNumberTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\vmNumber-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeVmNumberRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\vmNumber-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeSLAViolationVmNumberTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\slaViolationVmNumber-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeSLAViolationVmNumberRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter("E:\\Document\\我的\\研究生毕业论文\\实验\\CloudSim实验\\调度与不调度比较\\slaViolationVmNumber-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeSumSkewnessTAVMS(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter(outputDir+prefix+"sumSkewness-tavms.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeSumSkewnessRandom(String content){
        FileWriter fileWriter = null;
        try {
            fileWriter =new FileWriter(outputDir+prefix+"sumSkewness-random.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(content + "\n");
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
