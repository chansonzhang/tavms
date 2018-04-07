import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/3.
 */
public class TAVMSUtils {
    private static double[][] fightMatrix = new double[8][8];

    public static double[][] getFightMatrix() {
        return fightMatrix;
    }

    private static int cloudletNumber;

    private static final double cpuUpperBound = 0.8; //经验值，效果最好
    private static final double cpuLowerBound = 0.2;
    private static final double ramUpperBound = 0.8;
    private static final double ramLowerBound = 0.2;
    private static final double bwUpperBound = 0.8;
    private static final double bwLowerBound = 0.2;

    //維護一個map用來記錄哪些cloudlet被分配到了指定的虛擬機上
    private static Map<Vm, List<Cloudlet>> cloudletsOfVM = new HashMap<>();

    public static double getCpuUpperBound() {
        return cpuUpperBound;
    }

    public static double getCpuLowerBound() {
        return cpuLowerBound;
    }

    public static double getRamUpperBound() {
        return ramUpperBound;
    }

    public static double getRamLowerBound() {
        return ramLowerBound;
    }

    public static double getBwUpperBound() {
        return bwUpperBound;
    }

    public static double getBwLowerBound() {
        return bwLowerBound;
    }


    public static int getCloudletNumber() {
        return cloudletNumber;
    }

    public static void setCloudletNumber(int cloudletNumber) {
        TAVMSUtils.cloudletNumber = cloudletNumber;
    }

    public static void initFightMatrix() {
        int dimension = new ResourceRequirementTypeManager().getCloudletTypeNumber();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i == j) {
                    fightMatrix[i][j] = 1;
                } else {
                    fightMatrix[i][j] = 0;
                }
            }
        }
    }

    public static Map<Vm, List<Cloudlet>> getCloudletsOfVM() {
        return cloudletsOfVM;
    }

    public static void updateFightMatrix(Host host) {
        DatacenterTAVMS datacenterTAVMS = (DatacenterTAVMS) host.getDatacenter();
        List<Vm> vmList = host.getVmList();
        Map<ResourceType, Integer> appsOfThisHost = new HashMap<>();
        //获取host中的所有应用类型，以及每种应用出现的次数
        for (Vm vm : vmList) {
            List<Cloudlet> cloudlets = TAVMSUtils.getCloudletsOfVM().get(vm);
            if (cloudlets != null) {
                for (Cloudlet cloudlet : cloudlets) {
                    CloudletWithResType cloudletWithResType = (CloudletWithResType) cloudlet;
                    ResourceType resourceType = cloudletWithResType.getResourceType();
                    //去重
                    if (appsOfThisHost.get(resourceType) == null) {
                        appsOfThisHost.put(resourceType, 1);
                    } else {
                        appsOfThisHost.put(resourceType, appsOfThisHost.get(resourceType) + 1);
                    }
                }
            }

        }

        List<Map.Entry<ResourceType, Integer>> appsWithNumber = new ArrayList<>();
        for (Map.Entry<ResourceType, Integer> entry : appsOfThisHost.entrySet()) {
            appsWithNumber.add(entry);
        }

        int totalPairOccourNumber = 0;
        double maxUsageInThisHost = 0.0;
        double usage = datacenterTAVMS.getHottestResourceUsage(host);

        //calculate totalPairOccourNubmer
        for (int i = 0; i < appsWithNumber.size(); i++) {
            for (int j = i + 1; j < appsWithNumber.size(); j++) {
                int pairOccourNumber = appsWithNumber.get(i).getValue() * appsWithNumber.get(j).getValue();
                totalPairOccourNumber += pairOccourNumber;
            }
        }

        //update
        double newFight = usage / totalPairOccourNumber;
        for (int i = 0; i < appsWithNumber.size(); i++) {
            for (int j = i + 1; j < appsWithNumber.size(); j++) {
                ResourceType app1 = appsWithNumber.get(i).getKey();
                ResourceType app2 = appsWithNumber.get(j).getKey();
                double oldFight = Math.max(getFightMatrix()[app1.getId()][app2.getId()],
                        getFightMatrix()[app2.getId()][app1.getId()]); //理论上应该相等，防止出错，取最大值
                double average = Math.min((newFight + oldFight) / 2, 1); //不会超过1
                getFightMatrix()[app1.getId()][app2.getId()] = newFight + oldFight;
                getFightMatrix()[app2.getId()][app1.getId()] = newFight + oldFight;
            }
        }
    }

    public static double diversity(List<Vm> vmList) {
        double totalSim = 0.0;
        int n = vmList.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                totalSim += similarity(vmList.get(i), vmList.get(j));
            }
        }
        if (n == 0 || n == 1) {
            return 1000000;//不要将虚拟机从此迁出
        }
        return -totalSim / ((n * n - n) / 2);
    }

    /**
     * host上利用率最大的资源的利用率
     *
     * @param host
     * @return
     */
    public static double usageOfHotestResource(Host host) {
        DatacenterTAVMS datacenterTAVMS = (DatacenterTAVMS) host.getDatacenter();
        Map<Integer, Map<String, Double>> usage = datacenterTAVMS.getUtilizationOfHosts(datacenterTAVMS.getHostList());
        Map<String, Double> usageOfThisHost = usage.get(host.getId());
        double cpuUsage = usageOfThisHost.get("cpu");
        double ramUsage = usageOfThisHost.get("ram");
        double bwUsage = usageOfThisHost.get("bw");
        return Math.max(Math.max(cpuUsage, ramUsage), bwUsage);
    }

    public static double similarity(Vm vm1, Vm vm2) {
        return similarityWithoutAPPFight(vm1, vm2);
    }

    public static double similarity(Vm vm, List<Vm> vmList) {
        return similarityWithoutAPPFight(vm, vmList);
    }


    /**
     * 不考虑不同APP之间冲突的计算方法
     *
     * @param vm1
     * @param vm2
     * @return
     */
    private static double similarityWithoutAPPFight(Vm vm1, Vm vm2) {
        double[] vmtype1 = vmtype(vm1);
        double[] vmtype2 = vmtype(vm2);
        //return MathUtils.cosSimilarity(vmtype1, vmtype2);
        //return MathUtils.vectorMultiply(vmtype1,vmtype2);
        return MathUtils.covariance(vmtype1, vmtype2);
        //return MathUtils.correlation(vmtype1,vmtype2);
        //return MathUtils.pCofficient(vmtype1,vmtype2);
    }

    /**
     * 不考虑不同APP之间冲突的计算方法
     *
     * @param vm
     * @param vmList
     * @return
     */
    private static double similarityWithoutAPPFight(Vm vm, List<Vm> vmList) {
        double[] vmtype = vmtype(vm);
        double[] hostType = appType(vmList);
        //return MathUtils.cosSimilarity(vmtype, hostType);
        //return MathUtils.vectorMultiply(vmtype,hostType);
        return MathUtils.covariance(vmtype, hostType);
        //return MathUtils.correlation(vmtype,hostType);
        //return MathUtils.pCofficient(vmtype,hostType);
    }

    /**
     * 考虑了争用矩阵的相似度计算方法
     *
     * @param vm
     * @param vmList
     * @return
     */
    private static double similarityWithAPPFight(Vm vm, List<Vm> vmList) {
        double[] vmFightType = vmFightType(vm);
        double[] hostFightType = appFightType(vmList);
        return MathUtils.cosSimilarity(vmFightType, hostFightType);
    }

    /**
     * 考虑了争用矩阵的相似度计算方法
     *
     * @param vm1
     * @param vm2
     * @return
     */
    private static double similarityWithAPPFight(Vm vm1, Vm vm2) {
        double[] vmFightType1 = vmFightType(vm1);
        double[] vmFightType2 = vmFightType(vm2);
        return MathUtils.cosSimilarity(vmFightType1, vmFightType2);
    }

    public static double[] appType(List<Vm> vmList) {
        ResourceRequirementTypeManager resourceRequirementTypeManager = new ResourceRequirementTypeManager();
        int dimension = resourceRequirementTypeManager.getCloudletTypeNumber();
        double[] hostType = new double[dimension];
        for (int k = 0; k < dimension; k++) {
            double sum = 0;
            for (Vm vm : vmList) {
                sum += vmtype(vm)[k];
            }
            hostType[k] = sum;
        }
        // return MathUtils.normalization(hostType);
        return hostType;
    }

    /**
     * 考虑了不同应用之间的争用
     *
     * @param vmList
     * @return
     */
    public static double[] appFightType(List<Vm> vmList) {
        ResourceRequirementTypeManager resourceRequirementTypeManager = new ResourceRequirementTypeManager();
        int dimension = resourceRequirementTypeManager.getCloudletTypeNumber();
        double[] hostType = new double[dimension];
        for (int k = 0; k < dimension; k++) {
            double sum = 0;
            for (Vm vm : vmList) {
                sum += vmFightType(vm)[k];
            }
            hostType[k] = sum;
        }
        return MathUtils.normalization(hostType);
    }

    public static boolean containResType(List<Cloudlet> cloudlets, ResourceType resType) {
        if (null == cloudlets) {
            return false;
        }
        for (Cloudlet cloudlet : cloudlets) {
            CloudletWithResType cloudletWithResType = (CloudletWithResType) cloudlet;
            if (resType.equals(cloudletWithResType.getResourceType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回标准化后的向量
     *
     * @param vm
     * @return
     */
    public static double[] vmtype(Vm vm) {
        List<Cloudlet> cloudlets = TAVMSUtils.getCloudletsOfVM().get(vm);
        ResourceRequirementTypeManager resourceRequirementTypeManager = new ResourceRequirementTypeManager();
        ResourceType[] resourceTypes = resourceRequirementTypeManager.getApplicationTypes();
        int dimension = resourceRequirementTypeManager.getCloudletTypeNumber();
        double[] vmtype = new double[dimension];
        for (int k = 0; k < dimension; k++) {
            if (containResType(cloudlets, resourceTypes[k])) {
                vmtype[k] = 1;
            } else {
                vmtype[k] = 0;
            }
        }
        //return MathUtils.normalization(vmtype);
        return vmtype;
    }

    /**
     * 虚拟机的应用类型矩阵与虚拟机的争用矩阵相乘
     *
     * @param vm
     * @return
     */
    public static double[] vmFightType(Vm vm) {
        int dimension = new ResourceRequirementTypeManager().getCloudletTypeNumber();
        double[][] fightMatrix = getFightMatrix().clone(); //防止修改原有的数据
        double[] vmtype = vmtype(vm);
        double[] vmFightType = new double[dimension];
        for (int k = 0; k < dimension; k++) {
            double[] fightVector = MathUtils.normalization(fightMatrix[k]); //第K应用与其他任务的争用向量
            vmFightType[k] = MathUtils.vectorMultiply(fightVector, vmtype); //第k个应用与vm1的争用总量
        }
        return MathUtils.normalization(vmFightType);
    }

    public static int howManyType(Host host) {
        double[] hosttype = TAVMSUtils.appType(host.getVmList());
        int count = 0;
        for (double value : hosttype) {
            if (value - 0 > Double.MIN_NORMAL) {
                count++;
            }
        }
        return count;
    }

    /**
     * 是否有资源超限
     *
     * @param host
     * @return
     */
    public static boolean isOverload(Host host) {
        DatacenterTAVMS datacenterTAVMS = (DatacenterTAVMS) host.getDatacenter();
        Map<Integer, Map<String, Double>> utilizationMap = datacenterTAVMS.getUtilizationOfHosts(datacenterTAVMS.getHostList());
        List<Double> cpuUtilization = new ArrayList<>();
        List<Double> ramUtilization = new ArrayList<>();
        List<Double> bwUtilization = new ArrayList<>();

        for (Host h : datacenterTAVMS.getHostList()) {
            cpuUtilization.add(utilizationMap.get(h.getId()).get("cpu"));
            ramUtilization.add(utilizationMap.get(h.getId()).get("ram"));
            bwUtilization.add(utilizationMap.get(h.getId()).get("bw"));
        }

        double avgCpuUsage = MathUtils.average(cpuUtilization);
        double stdCpuDev = MathUtils.stddev(cpuUtilization);
        double varCpuUsage = MathUtils.variance(cpuUtilization);
        double avgRamUsage = MathUtils.average(ramUtilization);
        double stdRamDev = MathUtils.stddev(ramUtilization);
        double varRamUsage = MathUtils.variance(ramUtilization);
        double avgBwUsage = MathUtils.average(bwUtilization);
        double stdBwDev = MathUtils.stddev(bwUtilization);
        double varBwUsage = MathUtils.variance(bwUtilization);

        if (cpuUtilization.get(host.getId()) > cpuUpperBound
                || ramUtilization.get(host.getId()) > ramUpperBound
                || bwUtilization.get(host.getId()) > bwUpperBound) {
            return true;
        }

        return false;
    }

    public static boolean hasResContention(Host host) {
        return isOverload(host);
    }

    public static boolean isUnderload(Host host) {
        DatacenterTAVMS datacenterTAVMS = (DatacenterTAVMS) host.getDatacenter();
        Map<Integer, Map<String, Double>> utilizationMap = datacenterTAVMS.getUtilizationOfHosts(datacenterTAVMS.getHostList());
        List<Double> cpuUtilization = new ArrayList<>();
        List<Double> ramUtilization = new ArrayList<>();
        List<Double> bwUtilization = new ArrayList<>();

        for (Host h : datacenterTAVMS.getHostList()) {
            cpuUtilization.add(utilizationMap.get(h.getId()).get("cpu"));
            ramUtilization.add(utilizationMap.get(h.getId()).get("ram"));
            bwUtilization.add(utilizationMap.get(h.getId()).get("bw"));
        }

        double avgCpuUsage = MathUtils.average(cpuUtilization);
        double stdCpuDev = MathUtils.stddev(cpuUtilization);
        double varCpuUsage = MathUtils.variance(cpuUtilization);
        double avgRamUsage = MathUtils.average(ramUtilization);
        double stdRamDev = MathUtils.stddev(ramUtilization);
        double varRamUsage = MathUtils.variance(ramUtilization);
        double avgBwUsage = MathUtils.average(bwUtilization);
        double stdBwDev = MathUtils.stddev(bwUtilization);
        double varBwUsage = MathUtils.variance(bwUtilization);

        if (cpuUtilization.get(host.getId()) < cpuLowerBound
                && ramUtilization.get(host.getId()) < ramLowerBound
                && bwUtilization.get(host.getId()) < bwLowerBound) {
            return true;
        }


        return false;
    }

    /**
     * 找出资源满足需求的虚拟机
     *
     * @param hosts
     * @param vm
     * @return
     */
    public static List<Host> findAvailableHosts(List<Host> hosts, Vm vm, List<Map<String, Object>> migrationList) {

        List<Host> availableHosts = new ArrayList<>();
        for (Host host : hosts) {
            if (vm.getId() == 55
                    && host.getId() == 47) {
                System.out.print("");
            }
            double usedMIPS = 0;
            int releasedMIPS = 0;
            int usedRam = 0;
            int releasedRam = 0;
            long usedBw = 0;
            long releasedBw = 0;
            for (Map<String, Object> map : migrationList) {
                Host hostReleased = (Host) map.get("hometown");
                Host hostUsed = (Host) map.get("host");
                VmTAVMS vmPlaned = (VmTAVMS) map.get("vm");
                try {
                    //迁入此host
                    if (hostUsed.getId() == host.getId()) {
                        usedMIPS += vmPlaned.getTotalCurrentRequestedMIPS();
                        usedRam += vmPlaned.getCurrentRequestedRam();
                        usedBw += vmPlaned.getCurrentRequestedBw();
                    }

                    //已经计划把vmPlaned迁出此host
                    if (hostReleased.getId() == host.getId()) {
                        releasedMIPS += vmPlaned.getTotalAllocatedMIPS();
                        releasedRam += vmPlaned.getCurrentAllocatedRam();
                        releasedBw += vmPlaned.getCurrentAllocatedBw();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            double estimateAllocatedCpu = host.getTotalMips() - (host.getAvailableMips() - usedMIPS + releasedMIPS - ((VmTAVMS) vm).getTotalCurrentRequestedMIPS());
            double estimateAllocatedRam = host.getRamProvisioner().getRam() - (host.getRamProvisioner().getAvailableRam() - usedRam + releasedRam - vm.getCurrentRequestedRam());
            double estimateAllocatedBw = host.getBwProvisioner().getBw() - (host.getBwProvisioner().getAvailableBw() - usedBw + releasedBw - vm.getCurrentRequestedBw());

            if (estimateAllocatedCpu <= host.getTotalMips() * TAVMSUtils.getCpuUpperBound()
                    && estimateAllocatedRam <= host.getRamProvisioner().getRam() * TAVMSUtils.getRamUpperBound()
                    && estimateAllocatedBw <= host.getBwProvisioner().getBw() * TAVMSUtils.getBwUpperBound()) {
                availableHosts.add(host);
            }
        }
        return availableHosts;
    }

    /**
     * 获取资源使用率向量
     * 因为资源使用量的绝对值容易受到单位的影响，因此必须使用百分比
     * 本来因为是要考察前者是否要迁移到后者，应该以后者的宿主机作为参考系
     * 但是后面的vmlist里面包含了一些还未迁移，但计划迁移至此的虚拟机，因此不知道哪个虚拟机原来就在目标宿主机上
     * 所以还是以前面的虚拟机所在的宿主机为参考系，这样不会有问题，原因如下：
     * 1.宿主机是同构的
     * 2.参考系只是为了消除单位的影响而已，具体值不重要，只要大家的标准一致即可
     *
     * @param vm
     * @return
     */
    public static double getResUsageVectorMultiply(Vm vm, List<Vm> vmList) {
        Host host = vm.getHost();
        double totalCPU = host.getTotalMips();
        double totalRam = host.getRamProvisioner().getRam();
        double totalBw = host.getBwProvisioner().getBw();
        VmTAVMS vmTAVMS = (VmTAVMS) (vm);

        double[] vector1 = new double[3];
        vector1[0] = vmTAVMS.getTotalCurrentRequestedMIPS() / totalCPU;
        vector1[1] = vmTAVMS.getCurrentRequestedRam() / totalRam;
        vector1[2] = vmTAVMS.getCurrentRequestedBw() / totalBw;

        double[] vector2 = new double[3];
        double usedCpu = 0.0;
        double usedRam = 0.0;
        double usedBw = 0.0;
        for(Vm v:vmList){
            VmTAVMS vt= (VmTAVMS) v;
            usedCpu+=vt.getTotalCurrentRequestedMIPS();
            usedRam+=vt.getCurrentRequestedRam();
            usedBw+=vt.getCurrentRequestedBw();
        }
        vector2[0] = usedCpu / totalCPU;
        vector2[1] = usedRam / totalRam;
        vector2[2] = usedBw / totalBw;

        return MathUtils.vectorMultiply(vector1,vector2);
    }

    public static double getResUsageCovariance(Vm vm, List<Vm> vmList) {
        Host host = vm.getHost();
        double totalCPU = host.getTotalMips();
        double totalRam = host.getRamProvisioner().getRam();
        double totalBw = host.getBwProvisioner().getBw();
        VmTAVMS vmTAVMS = (VmTAVMS) (vm);

        double[] vector1 = new double[3];
        vector1[0] = vmTAVMS.getTotalCurrentRequestedMIPS() / totalCPU;
        vector1[1] = vmTAVMS.getCurrentRequestedRam() / totalRam;
        vector1[2] = vmTAVMS.getCurrentRequestedBw() / totalBw;

        double[] vector2 = new double[3];
        double usedCpu = 0.0;
        double usedRam = 0.0;
        double usedBw = 0.0;
        for(Vm v:vmList){
            VmTAVMS vt= (VmTAVMS) v;
            usedCpu+=vt.getTotalCurrentRequestedMIPS();
            usedRam+=vt.getCurrentRequestedRam();
            usedBw+=vt.getCurrentRequestedBw();
        }
        vector2[0] = usedCpu / totalCPU;
        vector2[1] = usedRam / totalRam;
        vector2[2] = usedBw / totalBw;

        return MathUtils.covariance(vector1,vector2);
    }

    /**
     * 计算扭曲度
     * @param hostList
     * @return Map<Integer,Double>
     *     key 是host.getid value是偏倚度
     */
    public static Map<Integer,Double> getSkewnessOfHosts(List<Host> hostList){
        DatacenterTAVMS datacenter= (DatacenterTAVMS) hostList.get(0).getDatacenter();
        Map<Integer,Map<String,Double>> usageMap=datacenter.getUtilizationOfHosts(hostList);
        Map<Integer,Double> skewnessOfHosts=new HashMap<>();
        for(Host host:hostList){
            List<Double> usages=new ArrayList<>();
            double cpuUsage=usageMap.get(host.getId()).get("cpu");
            double ramUsage=usageMap.get(host.getId()).get("ram");
            double bwUsage=usageMap.get(host.getId()).get("bw");
            usages.add(cpuUsage);
            usages.add(ramUsage);
            usages.add(bwUsage);
            double skness=skewness(usages);
            skewnessOfHosts.put(host.getId(),skness);
        }
        return skewnessOfHosts;
    }

    public static double skewness(List<Double> numbers){
        double avg=MathUtils.average(numbers);
        double squareSum=0.0;
        for(double number:numbers){
            squareSum+=Math.pow(number/avg-1.0,2);
        }
        return Math.sqrt(squareSum);
    }
}
