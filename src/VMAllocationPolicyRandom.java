import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;

import java.util.*;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/2.
 */
public class VMAllocationPolicyRandom extends VmAllocationPolicySimple {
    /**
     * Creates a new VmAllocationPolicySimple object.
     *
     * @param list the list of hosts
     * @pre $none
     * @post $none
     */
    public VMAllocationPolicyRandom(List<? extends Host> list) {
        super(list);
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
        List<Map<String, Object>> migrationList = new ArrayList<>();
        List<Vm> vmsToMigrate = selectVmToMigrate(getHostList());
        for (Vm vm : vmsToMigrate) {
            List<Host> availableHosts = TAVMSUtils.findAvailableHosts(getHostList(), vm, migrationList);
            Host targetHost = selectTargetHostForVm(availableHosts, vm);
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
     * 虚拟机选择算法 随机算法
     *
     * @param hosts
     * @return
     * @author Zhang Chen
     */
    public List<Vm> selectVmToMigrate(List<Host> hosts) {
        DatacenterTAVMS datacenterTAVMS = (DatacenterTAVMS) getHostList().get(0).getDatacenter();
        Map<Integer, Map<String, Double>> utilizationMap = datacenterTAVMS.getUtilizationOfHosts(hosts);
        List<Double> cpuUtilization = new ArrayList<>();
        List<Double> ramUtilization = new ArrayList<>();
        List<Double> bwUtilization = new ArrayList<>();

        for (Host h : datacenterTAVMS.getHostList()) {
            cpuUtilization.add(utilizationMap.get(h.getId()).get("cpu"));
            ramUtilization.add(utilizationMap.get(h.getId()).get("ram"));
            bwUtilization.add(utilizationMap.get(h.getId()).get("bw"));
        }
        List<Vm> vmsToMigrate = new ArrayList<>();
        for (Host host : hosts) {
            if (TAVMSUtils.isOverload(host)) {
                //随机选择一个虚拟机
                Vm vmSelect = host.getVmList().get(new Random().nextInt(host.getVmList().size()));
                vmsToMigrate.add(vmSelect);
            }
        }
        return vmsToMigrate;
    }



    /**
     * 虚拟机放置算法-随机算法
     *
     * @param availableHosts 满足资源需求的host列表，这里不再检查
     * @param vm
     * @return
     * @author Zhang Chen
     */
    public Host selectTargetHostForVm(List<Host> availableHosts, Vm vm) {
        DatacenterTAVMS datacenterTAVMS = (DatacenterTAVMS) getHostList().get(0).getDatacenter();
        List<Host> hosts = datacenterTAVMS.getHostList();
        Map<Integer, Map<String, Double>> utilizationMap = datacenterTAVMS.getUtilizationOfHosts(hosts);
        List<Double> cpuUtilization = new ArrayList<>();
        List<Double> ramUtilization = new ArrayList<>();
        List<Double> bwUtilization = new ArrayList<>();

        for (Host h : datacenterTAVMS.getHostList()) {
            cpuUtilization.add(utilizationMap.get(h.getId()).get("cpu"));
            ramUtilization.add(utilizationMap.get(h.getId()).get("ram"));
            bwUtilization.add(utilizationMap.get(h.getId()).get("bw"));
        }

        Host targetHost = null;
        if (availableHosts.size() == 0) return null;
        targetHost = availableHosts.get(new Random().nextInt(availableHosts.size()));
        return targetHost;
    }

    /**
     * Allocates the host with less PEs in use for a given VM.
     *
     * @param vm {@inheritDoc}
     * @return {@inheritDoc}
     * @pre $none
     * @post $none
     */
    //@Override
    /*public boolean allocateHostForVm(Vm vm) {
        List<Host> availableHosts=TAVMSUtils.findAvailableHosts(getHostList(),vm,new ArrayList<>());
        if(availableHosts.size()==0){
            return false;
        }
        return allocateHostForVm(vm,availableHosts.get(new Random().nextInt(availableHosts.size())));
    }*/
}
