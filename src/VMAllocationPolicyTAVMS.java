import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;

import java.util.*;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/26.
 */
public class VMAllocationPolicyTAVMS extends VmAllocationPolicySimple {


    /**
     * Creates a new VmAllocationPolicySimple object.
     *
     * @param list the list of hosts
     * @pre $none
     * @post $none
     */
    public VMAllocationPolicyTAVMS(List<? extends Host> list) {
        super(list);
    }


    /**
     * Optimize allocation of the VMs according to current utilization.
     *
     * @param vmList the vm list
     * @return the array list< hash map< string, object>>
     * <p>
     * It returns a list of maps, where each map key is a string
     * and stores an object. Each map will contain two entry:
     * The first key should be "vm"
     * The second key should be "host"
     * Generally, the Object identified by key "vm" is a Vm
     * and will be migrated to the Host Object identified by key "host"
     * Migration is not the responsibility of this function!
     * <p>
     * !!!@Note: We don't use the {@param vmList}
     */
    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
        DatacenterTAVMS datacenter = (DatacenterTAVMS) getHostList().get(0).getDatacenter();
        if (datacenter.getSchedulingCount() == 1) { //第一次要初始化争用矩阵
            TAVMSUtils.initFightMatrix();
        }

        List<Vm> vmsToMigrate = selectVmToMigrate(getHostList());
        List<Map<String, Object>> migrationList = new ArrayList<>();
        for (Vm vm : vmsToMigrate) {
            List<Host> availableHosts = TAVMSUtils.findAvailableHosts(getHostList(), vm, migrationList);
            Host targetHost = selectTargetHostForVm(availableHosts, vm, migrationList);
            if (vm != null && targetHost != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("hometown", vm.getHost());
                map.put("vm", vm);
                map.put("host", targetHost);
                if (vm.getHost().getId() != targetHost.getId()) { //不自己瞎折腾
                    migrationList.add(map);
                }
            }

        }
        if (migrationList.size() != 0) return migrationList;
        return null;
    }

    /**
     * TAVMS-虚拟机选择算法
     *
     * @param hosts
     * @return
     * @author Zhang Chen
     */
    public List<Vm> selectVmToMigrate(List<Host> hosts) {
        Map<Host, Double> diversities = getDiversities(hosts);
        List<Double> diversitiesNubmers = new ArrayList<>();
        for (Map.Entry<Host, Double> diversity : diversities.entrySet()) {
            diversitiesNubmers.add(diversity.getValue());
        }
        List<Vm> vmsToMigrate = new ArrayList<>();
        for (Host host : hosts) {
            if (TAVMSUtils.isOverload(host)) {//发生了资源争用
                if (TAVMSUtils.hasResContention(host)) {
                    //检测到资源争用时，更新争用矩阵。
                    TAVMSUtils.updateFightMatrix(host);
                }

                Vm vmSelect = null;
                double maxDiversity = 0;
                double maxSimilarity = -Double.MAX_VALUE;
                for (Vm vm : host.getVmList()) {
                    ArrayList<Vm> vmsWithoutCurrent = new ArrayList<>();
                    vmsWithoutCurrent.addAll(host.getVmList());
                    vmsWithoutCurrent.remove(host.getVmList().indexOf(vm));
                    double tryRmvDiversity = TAVMSUtils.diversity(vmsWithoutCurrent);
                    double tryRvmSimilarity = TAVMSUtils.similarity(vm,vmsWithoutCurrent);
                    /*if (tryRmvDiversity > diversities.get(host)) { //移除该vm可以增加多样性
                        //找出vm，使得移除该vm后，host上剩下的vm具有最大的多样性
                        if (tryRmvDiversity > maxDiversity) {
                            maxDiversity = tryRmvDiversity;
                            vmSelect = vm;
                        }
                    }else{
                        if(null==vmSelect){ //保证至少选择一个vm，因为此host资源已经超限
                            vmSelect = vm;
                        }
                    }*/
                    if(tryRvmSimilarity >maxSimilarity){
                        maxSimilarity = tryRvmSimilarity;
                        vmSelect = vm;
                    }

                }
                if (vmSelect != null) {
                    vmsToMigrate.add(vmSelect);
                }
            }
        }

        //优先从较小diversity的host中取出vm，因为较小的diversity意味着比较激烈的资源争用
        /*vmsToMigrate.sort(new Comparator<Vm>() {
            @Override
            public int compare(Vm vm1, Vm vm2) {
                Host host1 = vm1.getHost();
                Host host2 = vm2.getHost();
                return diversities.get(host1).compareTo(diversities.get(host2));
            }
        });*/
        vmsToMigrate.sort(new Comparator<Vm>() {
            @Override
            public int compare(Vm vm1, Vm vm2) {
                Host host1=vm1.getHost();
                Host host2=vm2.getHost();
                double u1=TAVMSUtils.usageOfHotestResource(host1);
                double u2=TAVMSUtils.usageOfHotestResource(host2);
                return Double.compare(u2,u1);
            }
        });
        return vmsToMigrate;
    }

    /**
     * TAVMS-虚拟机放置算法
     *
     * @param availableHosts 满足资源需求的host列表，这里不再检查
     * @param vm             待迁移的虚拟机
     * @param migrationPlans 当前已经确定的迁移计划
     * @return
     * @author Zhang Chen
     */
    public Host selectTargetHostForVm(List<Host> availableHosts, Vm vm, List<Map<String, Object>> migrationPlans) {
        double minSim = Double.MAX_VALUE;
        Host targetHost = null;
        DatacenterTAVMS datacenterTAVMS = (DatacenterTAVMS) getHostList().get(0).getDatacenter();
        List<Host> hosts = datacenterTAVMS.getHostList();
        Map<Integer, Map<String, Double>> utilizationMap = datacenterTAVMS.getUtilizationOfHosts(hosts);

        if (availableHosts.size() == 0) return null;

        Map<Host, Double> diversities = getDiversities(getHostList());
        List<Double> diversitiesNubmers = new ArrayList<>();
        for (Map.Entry<Host, Double> diversity : diversities.entrySet()) {
            diversitiesNubmers.add(diversity.getValue());
        }
        double avgDiversity = MathUtils.average(diversitiesNubmers);
        double diversityVariance = MathUtils.variance(diversitiesNubmers);
        double maxImprovement = 0;
        double maxTryPutDiversity = 0;
        double minFactor = Double.MAX_VALUE;
        for (Host host : availableHosts) {
            if (host.getId() == vm.getHost().getId()) //不能选择原宿主机作为目标宿主机
                continue;
            double[] usageVector = new double[3];
            usageVector[0] = utilizationMap.get(host.getId()).get("cpu");
            usageVector[1] = utilizationMap.get(host.getId()).get("ram");
            usageVector[2] = utilizationMap.get(host.getId()).get("bw");
            double[] vmtype = TAVMSUtils.vmtype(vm);
            List<Vm> vmList = new ArrayList<>();
            vmList.addAll(host.getVmList());
            for (Map<String, Object> plan : migrationPlans) {
                Vm planedVm = (Vm) plan.get("vm");
                Host hometown = (Host) plan.get("hometown");
                Host newHome = (Host) plan.get("host");
                if (hometown.getId() == host.getId()) { //迁出此物理机
                    vmList.remove(planedVm);
                }
                if (newHome.getId() == host.getId()) { //迁入此物理机
                    vmList.add(planedVm);
                }
            }
            double factor = TAVMSUtils.similarity(vm, vmList);//越小类型越互补
            if (factor < minFactor) {
                minFactor = factor;
                targetHost = host;
            }
        }
        return targetHost;
    }


    /**
     * 虚拟机放置在VMID%hostNumber的物理机上
     *
     * @param vm {@inheritDoc}
     * @return {@inheritDoc}
     * @pre $none
     * @post $none
     *//*
    @Override
    public boolean allocateHostForVm(Vm vm) {
        return allocateHostForVm(vm, getHostList().get(vm.getId() % getHostList().size()));
    }*/
    public Map<Host, Double> getDiversities(List<Host> hosts) {
        Map<Host, Double> diversities = new HashMap<>();
        for (Host host : hosts) {
            diversities.put(host, TAVMSUtils.diversity(host.getVmList()));
        }
        return diversities;
    }

    /**
     * 按照RAM从大到小排列
     *
     * @param hosts
     * @return
     */
    public List<Host> sortHostByRam(List<Host> hosts) {
        hosts.sort(new Comparator<Host>() {
            @Override
            public int compare(Host host1, Host host2) {
                return Integer.compare(host2.getRam(), host1.getRam()); //大的排在前面
            }
        });
        return hosts;
    }


    /**
     * 竞争相似度
     *
     * @param
     * @return
     */
    public double sumUpSimilarity(double[] vector1, double[] vector2) {
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


}
