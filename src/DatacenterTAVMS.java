import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/28.
 */
public class DatacenterTAVMS extends Datacenter {

    /**
     * Indicates if migrations are disabled or not.
     */
    private boolean disableMigrations;

    /**
     * The VM migration count.
     */
    private int migrationCount;

    /**
     * The last time submitted cloudlets were processed.
     */
    private double cloudletSubmitted;

    /**
     * 是否可以开始调度
     */
    private boolean canSchedule;

    //已经上传了的cloudlet数目
    private int cloudletCount;

    //上一次尝试执行optimizeAllocation的时间
    private double lastOptimizeAllocationTryTime;

    //尝试执行optimizeAllocation的周期
    private double optimizeAllocationPeriod;


    //第几次调度
    private int schedulingCount;

    //使用哪种调度算法，0表示TAVMS，1表示随机
    private int algorithmType;

    //最大调度次数
    private int maxSchedulingCount;

    public int getMaxSchedulingCount() {
        return maxSchedulingCount;
    }

    public void setMaxSchedulingCount(int maxSchedulingCount) {
        this.maxSchedulingCount = maxSchedulingCount;
    }

    public int getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(int algorithmType) {
        this.algorithmType = algorithmType;
    }

    public int getSchedulingCount() {
        return schedulingCount;
    }

    public void setSchedulingCount(int schedulingCount) {
        this.schedulingCount = schedulingCount;
    }

    public int increaseSchedulingCount() {
        return ++this.schedulingCount;
    }

    public boolean isDisableMigrations() {
        return disableMigrations;
    }

    public void setDisableMigrations(boolean disableMigrations) {
        this.disableMigrations = disableMigrations;
    }

    public int getMigrationCount() {
        return migrationCount;
    }

    public void setMigrationCount(int migrationCount) {
        this.migrationCount = migrationCount;
    }

    public double getCloudletSubmitted() {
        return cloudletSubmitted;
    }

    public void setCloudletSubmitted(double cloudletSubmitted) {
        this.cloudletSubmitted = cloudletSubmitted;
    }

    public boolean isCanSchedule() {
        return canSchedule;
    }

    public void setCanSchedule(boolean canBeginScheduling) {
        this.canSchedule = canBeginScheduling;
    }

    public int getCloudletCount() {
        return cloudletCount;
    }

    public void setCloudletCount(int cloudletCount) {
        this.cloudletCount = cloudletCount;
    }

    public double getLastOptimizeAllocationTryTime() {
        return lastOptimizeAllocationTryTime;
    }

    public void setLastOptimizeAllocationTryTime(double lastOptimizeAllocationTryTime) {
        this.lastOptimizeAllocationTryTime = lastOptimizeAllocationTryTime;
    }

    public double getOptimizeAllocationPeriod() {
        return optimizeAllocationPeriod;
    }

    public void setOptimizeAllocationPeriod(double optimizeAllocationPeriod) {
        this.optimizeAllocationPeriod = optimizeAllocationPeriod;
    }


    public int increaseCloudletCount() {
        int old = getCloudletCount();
        int newValue = old + 1;
        setCloudletCount(newValue);
        return newValue;
    }

    /**
     * Allocates a new Datacenter object.
     *
     * @param name               the name to be associated with this entity (as required by the super class)
     * @param characteristics    the characteristics of the datacenter to be created
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @param storageList        a List of storage elements, for data simulation
     * @param schedulingInterval the scheduling delay to process each datacenter received event
     * @throws Exception when one of the following scenarios occur:
     *                   <ul>
     *                   <li>creating this entity before initializing CloudSim package
     *                   <li>this entity name is <tt>null</tt> or empty
     *                   <li>this entity has <tt>zero</tt> number of PEs (Processing Elements). <br/>
     *                   No PEs mean the Cloudlets can't be processed. A CloudResource must contain
     *                   one or more Machines. A Machine must contain one or more PEs.
     *                   </ul>
     * @pre name != null
     * @pre resource != null
     * @post $none
     */
    public DatacenterTAVMS(String name, DatacenterCharacteristics characteristics,
                           VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
                           double schedulingInterval) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);

        setAlgorithmType(algorithmType);
        setDisableMigrations(false);
        setCloudletSubmitted(-1);
        setMigrationCount(0);
        setCloudletCount(0);
        setCanSchedule(false);
        setOptimizeAllocationPeriod(100);
        setMaxSchedulingCount(100);
        setLastOptimizeAllocationTryTime(-1);
        setSchedulingCount(0);
    }

    /**
     * Updates processing of each cloudlet running in this Datacenter. It is necessary because
     * Hosts and VirtualMachines are simple objects, not entities. So, they don't receive events and
     * updating cloudlets inside them must be called from the outside.
     *
     * @pre $none
     * @post $none
     */
    @Override
    protected void updateCloudletProcessing() {
        // if some time passed since last processing
        // R: for term is to allow loop at simulation start. Otherwise, one initial
        // simulation step is skipped and schedulers are not properly initialized
        if (CloudSim.clock() < 0.111 || CloudSim.clock() > getLastProcessTime() + CloudSim.getMinTimeBetweenEvents()) {
            List<? extends Host> list = getVmAllocationPolicy().getHostList();
            double smallerTime = Double.MAX_VALUE;
            // for each host...
            for (int i = 0; i < list.size(); i++) {
                Host host = list.get(i);
                // inform VMs to update processing
                double time = host.updateVmsProcessing(CloudSim.clock());
                // what time do we expect that the next cloudlet will finish?
                if (time < smallerTime) {
                    smallerTime = time;
                }
            }

            /** !!! mention this value is calculated by 256*8/2,if vmNumber and taskTypeNumber in {@link Test1} changed,
             * this value will differ*/
            if (getCloudletCount() == TAVMSUtils.getCloudletNumber()) setCanSchedule(true);
            if (getSchedulingCount() == getMaxSchedulingCount()) setCanSchedule(false);

            if (getSchedulingCount() == 5) {
                //System.out.println();
            }
            if (CloudSim.clock() < 0.111 || canSchedule) {
                double nextSchedulingTime = CloudSim.clock() + getOptimizeAllocationPeriod();
                if (nextSchedulingTime < smallerTime) {
                    smallerTime = nextSchedulingTime;
                }
            }


            // gurantees a minimal interval before scheduling the event
            if (smallerTime < CloudSim.clock() + CloudSim.getMinTimeBetweenEvents() + 0.01) {
                smallerTime = CloudSim.clock() + CloudSim.getMinTimeBetweenEvents() + 0.01;
            }


            if (!isDisableMigrations()
                    && canSchedule
                    && (-1 == lastOptimizeAllocationTryTime || CloudSim.clock() - lastOptimizeAllocationTryTime - getOptimizeAllocationPeriod() < 0.001)
                    ) {
                increaseSchedulingCount();
                setLastOptimizeAllocationTryTime(CloudSim.clock());
                Map<Vm, Boolean> isResizeSuccessful = new HashMap<>();
                if (getSchedulingCount() == 65) {
                    System.out.print("");
                }

                //在打印信息和调度之前先更新vm的资源使用情况
                for (Host host : getHostList()) {
                    if (host.getId() == 6) {
                        System.out.print("");
                    }
                    for (Vm vm : host.getVmList()) {
                        VmTAVMS vmTAVMS = (VmTAVMS) vm;
                        if (vmTAVMS.getTotalCurrentRequestedMIPS() < vmTAVMS.getTotalAllocatedMIPS()) {
                            System.out.print("");
                        }
                        boolean result1 = vmTAVMS.resizeMIPS(vmTAVMS.getTotalCurrentRequestedMIPS());
                        boolean result2 = vmTAVMS.resizeRam(vmTAVMS.getCurrentRequestedRam());
                        boolean result3 = vmTAVMS.resizeBw(vmTAVMS.getCurrentRequestedBw());
                        /**
                         * There is a bug, that a vm resize fail and get all the left resource
                         * but till we print usage, some vm released some res
                         * so it seems unresonable that exists vm resize fail but the host don't overload!!!
                         * So we need to do this circulation twice,
                         * in the first walk, all the release will succeded,
                         * in the second walk, vm can get all the spare res,then succeed or fail still
                         * but this time, fail will lead to a overload, this give a chance to vm scheduling.
                         */
                        if (result1 && result2 && result3) {
                            isResizeSuccessful.put(vm, true);
                        } else {
                            isResizeSuccessful.put(vm, false);
                        }
                    }
                    for (Vm vm : host.getVmList()) {
                        VmTAVMS vmTAVMS = (VmTAVMS) vm;
                        if (vmTAVMS.getTotalCurrentRequestedMIPS() < vmTAVMS.getTotalAllocatedMIPS()) {
                            System.out.print("");
                        }
                        if (isResizeSuccessful.get(vm) == false) { //第二遍只重做在第一遍失败的那些
                            boolean result1 = vmTAVMS.resizeMIPS(vmTAVMS.getTotalCurrentRequestedMIPS());
                            boolean result2 = vmTAVMS.resizeRam(vmTAVMS.getCurrentRequestedRam());
                            boolean result3 = vmTAVMS.resizeBw(vmTAVMS.getCurrentRequestedBw());

                            if (result1 && result2 && result3) {
                                isResizeSuccessful.put(vm, true);
                            }
                        }
                    }
                }

                //for debug
//                System.out.println("Time: "+CloudSim.clock()+" MIPS of VM8: "+((VmTAVMS)getVmList().get(8)).getTotalAllocatedMIPS());
//                System.out.println("Time: "+CloudSim.clock()+" request MIPS of VM8: "+((VmTAVMS)getVmList().get(8)).getTotalCurrentRequestedMIPS());

                //开始制定调度计划
                List<Map<String, Object>> migrationMap = getVmAllocationPolicy().optimizeAllocation(
                        getVmList());

                if (migrationMap != null) {
                    for (Map<String, Object> migrate : migrationMap) {
                        Vm vm = (Vm) migrate.get("vm");
                        //虽然resize失败，但是成功迁移，
                        // 而迁移的时候使用的最新需求的size，也等同于resize成功
                        isResizeSuccessful.put(vm, true);
                    }
                }

                int violationVmNumber = 0;
                for (Map.Entry<Vm, Boolean> entry : isResizeSuccessful.entrySet()) {
                    if (entry.getValue() == false) {
                        violationVmNumber++;
                    }
                }
                printUsage(((migrationMap == null) ? 0 : migrationMap.size()), violationVmNumber);
                System.out.println(CloudSim.clock() + ": 第" + getSchedulingCount() + "次调度:");
                System.out.println("迁移的虚拟机数量：" + ((migrationMap == null) ? 0 : migrationMap.size()));

                //开始按照调度计划迁移虚拟机
                if (migrationMap != null) {
                    System.out.println("vm\t-->host");
                    for (Map<String, Object> migrate : migrationMap) {
                        Vm vm = (Vm) migrate.get("vm");
                        Host targetHost = (Host) migrate.get("host");
                        Host oldHost = vm.getHost();

                        System.out.println(vm.getId() + "\t-->" + targetHost.getId());
                        if (oldHost == null) {
                            Log.formatLine(
                                    "%.2f: Migration of VM #%d to Host #%d is started",
                                    CloudSim.clock(),
                                    vm.getId(),
                                    targetHost.getId());
                        } else {
                            Log.formatLine(
                                    "%.2f: Migration of VM #%d from Host #%d to Host #%d is started",
                                    CloudSim.clock(),
                                    vm.getId(),
                                    oldHost.getId(),
                                    targetHost.getId());
                        }

                        //addMigratingInVm中不能检查，因为plan中该迁移的虚拟机还未迁移，该释放的资源还未释放
                        //因此，可能会报错
                        targetHost.addMigratingInVm(vm);
                        incrementMigrationCount();

                        /** VM migration delay = RAM / bandwidth + C (C = 10 sec) **/
                        send(
                                getId(),
                                0,
                                CloudSimTags.VM_MIGRATE,
                                migrate);
                    }
                }
            }

            if (smallerTime != Double.MAX_VALUE) {
                schedule(getId(), (smallerTime - CloudSim.clock()), CloudSimTags.VM_DATACENTER_EVENT);
            }
            setLastProcessTime(CloudSim.clock());
        }
    }

    /**
     * Increment migration count.
     */
    protected void incrementMigrationCount() {
        setMigrationCount(getMigrationCount() + 1);
    }

    /**
     * Processes a Cloudlet submission.
     *
     * @param ev  information about the event just happened
     * @param ack indicates if the event's sender expects to receive
     *            an acknowledge message when the event finishes to be processed
     * @pre ev != null
     * @post $none
     */
    @Override
    protected void processCloudletSubmit(SimEvent ev, boolean ack) {
        super.processCloudletSubmit(ev, ack);
        setCloudletSubmitted(CloudSim.clock());
        increaseCloudletCount();

        Cloudlet cl = (Cloudlet) ev.getData();
        Vm vm = getVmList().get(cl.getVmId());
        if (TAVMSUtils.getCloudletsOfVM().get(vm) == null) {
            TAVMSUtils.getCloudletsOfVM().put(vm, new ArrayList<Cloudlet>());
        }
        TAVMSUtils.getCloudletsOfVM().get(vm).add(cl);

        //所有cloudlet上传完成了
        if (TAVMSUtils.getCloudletNumber() == getCloudletCount()) {
            printUsage(0, 0);
        }
    }

    /**
     * 输出资源利用率
     *
     * @param planCount 本次要调度的虚拟机数量
     */
    public void printUsage(int planCount, int slaViolationVmNumber) {
        if (getSchedulingCount() == 100) {
            System.out.print("");
        }
        String separator = "\t";
        System.out.println(CloudSim.clock() + ": Usage:");

        List<Host> hostList = getHostList();

        DecimalFormat df = new DecimalFormat("0.000000");
        df.setRoundingMode(RoundingMode.HALF_UP);//四舍五入

        Map<Integer, Map<String, Double>> utilizationMap = getUtilizationOfHosts(hostList);
        List<Double> cpuUtilizationList = new ArrayList<>();
        List<Double> ramUtilizationList = new ArrayList<>();
        List<Double> bwUtilizationList = new ArrayList<>();
        ResourceType[] resourceTypes = new ResourceRequirementTypeManager().getApplicationTypes();
        double totalNumber = 0.0;
        for (ResourceType type : resourceTypes) {
            List<Host> hosts = findHostsWithThisTypeApp(getHostList(), type);
            totalNumber += hosts.size();
        }
        double avgAppDistributionRate = totalNumber / resourceTypes.length;

        double totalVarianceOfUsagesOfEachResInHost = 0.0;
        double totalResUsageRangeInOneHost = 0.0;
        int overloadHostNumber = 0;
        int affectedVmNumber = 0;
        for (Host host : getHostList()) {
            double cpuUsage = utilizationMap.get(host.getId()).get("cpu");
            double ramUsage = utilizationMap.get(host.getId()).get("ram");
            double bwUsage = utilizationMap.get(host.getId()).get("bw");

            cpuUtilizationList.add(cpuUsage);
            ramUtilizationList.add(ramUsage);
            bwUtilizationList.add(bwUsage);

            List<Double> usagesOfEachResInHost = new ArrayList<>();
            usagesOfEachResInHost.add(cpuUsage);
            usagesOfEachResInHost.add(ramUsage);
            usagesOfEachResInHost.add(bwUsage);
            totalVarianceOfUsagesOfEachResInHost += MathUtils.variance(usagesOfEachResInHost);
            totalResUsageRangeInOneHost += MathUtils.range(usagesOfEachResInHost);

            if (TAVMSUtils.isOverload(host)) {
                overloadHostNumber++;
                affectedVmNumber += host.getVmList().size();
            }
        }
        double averageVarianceOfUsagesOfEachResInVm = totalVarianceOfUsagesOfEachResInHost / utilizationMap.size();
        double averageResUsageRangeInOneHost = totalResUsageRangeInOneHost / utilizationMap.size();

        String diversityContent = getSchedulingCount() + "";
        List<Double> diversities = new ArrayList<>();
        List<Integer> typeNumber = new ArrayList<>();
        double totalTypeNumber = 0.0;
        for (Host host : getHostList()) {
            double diversity = TAVMSUtils.diversity(host.getVmList());
            diversityContent += separator;
            diversityContent += diversity;
            diversities.add(diversity);
            totalTypeNumber += TAVMSUtils.howManyType(host);
        }
        double averageTypeNumber = totalTypeNumber / getHostList().size();

        //计算skewness
        Map<Integer, Double> sknesmap = TAVMSUtils.getSkewnessOfHosts(hostList);
        List<Double> sknes = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sknesmap.entrySet()) {
            sknes.add(entry.getValue());
        }
        double avgSkewness = MathUtils.sum(sknes);

        if (getVmAllocationPolicy() instanceof VMAllocationPolicyTAVMS) {

            FileUtils.writeUsageTAVMS("hostId" + separator + "CPU" + separator + "RAM" + separator + "BW");

            for (Host host : getHostList()) {
                double cpuUsage = utilizationMap.get(host.getId()).get("cpu");
                double ramUsage = utilizationMap.get(host.getId()).get("ram");
                double bwUsage = utilizationMap.get(host.getId()).get("bw");
                FileUtils.writeUsageTAVMS(host.getId() + separator + cpuUsage + separator + ramUsage + separator + bwUsage);
                FileUtils.writeVmNumberTAVMS(host.getId() + separator + host.getVmList().size());
            }


            System.out.println("方差" + separator +
                    df.format(MathUtils.variance(cpuUtilizationList)) + separator +
                    df.format(MathUtils.variance(ramUtilizationList)) + separator +
                    df.format(MathUtils.variance(bwUtilizationList)));
            FileUtils.writeVarianceTAVMS(getSchedulingCount() + separator +
                    df.format(MathUtils.variance(cpuUtilizationList)) + separator +
                    df.format(MathUtils.variance(ramUtilizationList)) + separator +
                    df.format(MathUtils.variance(bwUtilizationList)));
            FileUtils.writeMigratedVMCountTAVMS(getSchedulingCount() + separator +
                    planCount);
            FileUtils.writeAvgAppDistributionRateTAVMS(getSchedulingCount() + separator +
                    avgAppDistributionRate);
            FileUtils.writeAvgVarianceOfResUsageInHostTAVMS(getSchedulingCount() + separator +
                    averageVarianceOfUsagesOfEachResInVm);
            FileUtils.writeAvgResUsageRangeInOneHostTAVMS(getSchedulingCount() + separator +
                    averageResUsageRangeInOneHost);
            FileUtils.writeUsageRangeTAVMS(getSchedulingCount() + separator +
                    df.format(MathUtils.range(cpuUtilizationList)) + separator +
                    df.format(MathUtils.range(ramUtilizationList)) + separator +
                    df.format(MathUtils.range(bwUtilizationList)));
            FileUtils.writeOverloadHostNumberTAVMS(getSchedulingCount() + separator + overloadHostNumber);
            FileUtils.writeAffectedVmNumberTAVMS(getSchedulingCount() + separator + affectedVmNumber);
            FileUtils.writeSLAViolationVmNumberTAVMS(getSchedulingCount() + separator + slaViolationVmNumber);
            FileUtils.writeSumSkewnessTAVMS(getSchedulingCount() + separator + avgSkewness);

            FileUtils.writeDiversityTAVMS(diversityContent);
            FileUtils.writeDiversityTAVMS("均值：" + MathUtils.average(diversities));
            FileUtils.writeDiversityTAVMS("方差：" + MathUtils.variance(diversities));
            FileUtils.writeAvgTypeNumberTAVMS(getSchedulingCount() + separator + averageTypeNumber);
            FileUtils.writeUsageTAVMS("第" + getSchedulingCount() + "次调度");
            FileUtils.writeUsageTAVMS("迁移的虚拟机数量：" + planCount);
        }
        if (getVmAllocationPolicy() instanceof VMAllocationPolicyRandom) {

            FileUtils.writeUsageRandom("hostId" + separator + "CPU" + separator + "RAM" + separator + "BW");

            for (Host host : getHostList()) {
                double cpuUsage = utilizationMap.get(host.getId()).get("cpu");
                double ramUsage = utilizationMap.get(host.getId()).get("ram");
                double bwUsage = utilizationMap.get(host.getId()).get("bw");
                FileUtils.writeUsageRandom(host.getId() + separator + cpuUsage + separator + ramUsage + separator + bwUsage);
                FileUtils.writeVmNumberRandom(host.getId() + separator + host.getVmList().size());
            }

            System.out.println("方差" + separator +
                    df.format(MathUtils.variance(cpuUtilizationList)) + separator +
                    df.format(MathUtils.variance(ramUtilizationList)) + separator +
                    df.format(MathUtils.variance(bwUtilizationList)));
            FileUtils.writeVarianceRandom(getSchedulingCount() + separator +
                    df.format(MathUtils.variance(cpuUtilizationList)) + separator +
                    df.format(MathUtils.variance(ramUtilizationList)) + separator +
                    df.format(MathUtils.variance(bwUtilizationList)));
            FileUtils.writeMigratedVMCountRandom(getSchedulingCount() + separator +
                    planCount);
            FileUtils.writeAvgAppDistributionRateRandom(getSchedulingCount() + separator +
                    avgAppDistributionRate);
            FileUtils.writeAvgVarianceOfResUsageInHostRandom(getSchedulingCount() + separator +
                    averageVarianceOfUsagesOfEachResInVm);
            FileUtils.writeAvgResUsageRangeInOneHostRandom(getSchedulingCount() + separator +
                    averageResUsageRangeInOneHost);
            FileUtils.writeUsageRangeRandom(getSchedulingCount() + separator +
                    df.format(MathUtils.range(cpuUtilizationList)) + separator +
                    df.format(MathUtils.range(ramUtilizationList)) + separator +
                    df.format(MathUtils.range(bwUtilizationList)));
            FileUtils.writeOverloadHostNumberRandom(getSchedulingCount() + separator + overloadHostNumber);
            FileUtils.writeAffectedVmNumberRandom(getSchedulingCount() + separator + affectedVmNumber);
            FileUtils.writeSLAViolationVmNumberRandom(getSchedulingCount() + separator + slaViolationVmNumber);
            FileUtils.writeSumSkewnessRandom(getSchedulingCount() + separator + avgSkewness);

            FileUtils.writeAvgTypeNumberRandom(getSchedulingCount() + separator + averageTypeNumber);
            FileUtils.writeUsageRandom("第" + getSchedulingCount() + "次调度");
            FileUtils.writeUsageRandom("迁移的虚拟机数量：" + planCount);
        }
    }


    /**
     * Process the event for an User/Broker who wants to migrate a VM. This Datacenter will
     * then send the status back to the User/Broker.
     *
     * @param ev  information about the event just happened
     * @param ack indicates if the event's sender expects to receive
     *            an acknowledge message when the event finishes to be processed
     * @pre ev != null
     * @post $none
     */
    @Override
    protected void processVmMigrate(SimEvent ev, boolean ack) {
        super.processVmMigrate(ev, ack);
        setMigrationCount(getMigrationCount() - 1);
        if (getMigrationCount() == 0) {
            //printUsage();
        }
    }

    /**
     * 计算所有虚拟机的分配总量与宿主机总量比例
     *
     * @param hostList
     * @return Map<Integer, Map<String, Double>>
     * the outer key is host.getid
     * the inner key is String "cpu","ram"or "bw"
     */
    public Map<Integer, Map<String, Double>> getUtilizationOfHosts(List<Host> hostList) {
        List<Double> cpuUtilizationList = new ArrayList<>();
        List<Double> ramUtilizationList = new ArrayList<>();
        List<Double> bwUtilizationList = new ArrayList<>();
        Map<Integer, Map<String, Double>> utilizationMap1 = new HashMap<>();
        for (Host host : hostList) {
            Map<String, Double> usageOfThisHost = new HashMap<>();
            List<Vm> vmList = host.getVmList();
            if (host.getId() == 6) {
                System.out.print("");
            }

            double cpuUsed = 0.0; //MIPS
            double ramUsed = 0.0; //MB
            double bwUsed = 0.0; //Kbps

            int totalCPU = host.getTotalMips();
            int totalRam = host.getRam();
            long totalBw = host.getBw();

            for (Vm vm : vmList) {
                if (vm.getId() == 454) {
                    System.out.print("");
                }
                VmTAVMS vmTAVMS = (VmTAVMS) vm;
                cpuUsed += vmTAVMS.getTotalAllocatedMIPS();
                ramUsed += vmTAVMS.getCurrentAllocatedRam();
                bwUsed += vmTAVMS.getCurrentAllocatedBw();
            }

            double cpuUtilization = cpuUsed / totalCPU;
            double ramUtilization = ramUsed / totalRam;
            double bwUtilization = bwUsed / totalBw;

            cpuUtilizationList.add(cpuUtilization);
            ramUtilizationList.add(ramUtilization);
            bwUtilizationList.add(bwUtilization);

            usageOfThisHost.put("cpu", cpuUtilization);
            usageOfThisHost.put("ram", ramUtilization);
            usageOfThisHost.put("bw", bwUtilization);

            utilizationMap1.put(host.getId(), usageOfThisHost);
        }
        return utilizationMap1;
    }

    /**
     * 获取host上热点资源的利用率
     *
     * @param host
     * @return
     */
    public double getHottestResourceUsage(Host host) {
        List<Host> hosts = new ArrayList<>();
        hosts.add(host);
        Map<Integer, Map<String, Double>> utilizationOfHosts = getUtilizationOfHosts(hosts);
        double cpu = utilizationOfHosts.get(host.getId()).get("cpu");
        double ram = utilizationOfHosts.get(host.getId()).get("ram");
        double bw = utilizationOfHosts.get(host.getId()).get("bw");
        return Math.max(Math.max(cpu, ram), bw);
    }

    public List<Host> findHostsWithThisTypeApp(List<Host> hosts, ResourceType resourceType) {
        Set<Host> hostsWithThisTypeApp = new HashSet<>();
        for (Host host : hosts) {
            for (Vm vm : host.getVmList()) {
                List<Cloudlet> cloudletsOfVm = TAVMSUtils.getCloudletsOfVM().get(vm);
                if (cloudletsOfVm != null) {
                    for (Cloudlet cloudlet : cloudletsOfVm) {
                        CloudletWithResType cloudletWithResType = (CloudletWithResType) cloudlet;
                        if (cloudletWithResType.getResourceType().getId() == resourceType.getId()) {
                            hostsWithThisTypeApp.add(host);
                            break;
                        }
                    }
                }

            }
        }

        List<Host> result = new ArrayList<>();
        result.addAll(hostsWithThisTypeApp);
        return result;
    }
}
