import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/24.
 */
public class Test1 {
    /**
     * The vmlist.
     */
    private static List<Vm> vmlist;

    /**
     * The cloudlet list.
     */
    private static List<Cloudlet> cloudletList;

    public static void main(String args[]) {
        Log.printLine("Starting TAVMS...");
        try {
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
            boolean trace_flag = false; // trace events
            CloudSim.init(num_user, calendar, trace_flag);

            Datacenter datacenter_0 = createDatacenter("DataCenter_0");
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            final int vmTypeNumber = 256;
            final int vmCopyNumber = 2;
            final int vmNumber = vmTypeNumber * vmCopyNumber;
            int vmCoreNumber = 1;
            int mips = 1000; //每个PE的MIPS
            int ram = 1*1024+512;
            long bw = 20*1000;
            long size = 10 * 1000; // image size (MB)
            String vmm = "Xen"; // VMM name

            vmlist = new ArrayList<>();
            for (int vmId = 0; vmId < vmNumber; vmId++) {
                Vm vm = new VmTAVMS(vmId, brokerId, mips, vmCoreNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                vmlist.add(vm);
            }
            broker.submitVmList(vmlist);

            final int cloudletTypeNumber = 8;
            int pesNumber = 1;
            long length = 100000 * 1000; //单位MInstruction
            long fileSize = 300;
            long outputSize = 300;

            TAVMSUtils.setCloudletNumber(vmNumber * cloudletTypeNumber / 2);

            ResourceType[] resourceTypes = new ResourceRequirementTypeManager().getApplicationTypes();

            boolean[][] vmTaskAssignmentMatrix = new boolean[vmTypeNumber][cloudletTypeNumber];
            for (int i = 0; i < vmTypeNumber; i++) {
                for (int j = 0; j < cloudletTypeNumber; j++) {
                    vmTaskAssignmentMatrix[i][j] = (
                            (
                                    (i % (1 << (cloudletTypeNumber - j)))
                                            >> (cloudletTypeNumber - j - 1)
                            ) != 0
                    );
                }
            }

            for (int i = 0; i < vmTypeNumber; i++) {
                for (int j = 0; j < cloudletTypeNumber; j++) {
                    System.out.print(vmTaskAssignmentMatrix[i][j] ? 1 : 0 + " ");
                }
                System.out.println();
            }


            UtilizationModel utilizationModel = new UtilizationModelFull();
            cloudletList = new ArrayList<Cloudlet>();
            Map<Integer, Integer> vmOfCloutlet = new HashMap<>(); //key为cloudletId,value为vmId
            int cloudletId = 0;
            for (int i = 0; i < vmTypeNumber; i++) {
                for (int j = 0; j < cloudletTypeNumber; j++) {
                    if (vmTaskAssignmentMatrix[i][j]) {
                        for (int c = 0; c < vmCopyNumber; c++) {
                            vmOfCloutlet.put(cloudletId, i + c * vmTypeNumber);
                            Cloudlet cloudlet =
                                    new CloudletWithResType(cloudletId++, length, pesNumber, fileSize,
                                            outputSize, utilizationModel, utilizationModel,
                                            utilizationModel, resourceTypes[j]);
                            cloudlet.setUserId(brokerId);
                            cloudletList.add(cloudlet);
                        }
                    }
                }
            }
            broker.submitCloudletList(cloudletList);


            //将任务放入指定的虚拟机中
            for (cloudletId = 0; cloudletId < cloudletList.size(); cloudletId++) {
                try {
                    broker.bindCloudletToVm(cloudletId, vmOfCloutlet.get(cloudletId));
                } catch (NullPointerException e) {
                    System.out.println("NullPointerException caused by: cloudletId:" + cloudletId + " vmId:" + vmOfCloutlet.get(cloudletId));
                }
            }

            //用于计算初始状态，应该为虚拟机分配多少资源
            Map<Vm, Map<String, Double>> initialResOfVm = new HashMap<>();
            for (Cloudlet cloudlet : cloudletList) {
                CloudletWithResType cloudletWithResType = (CloudletWithResType) cloudlet;
                Vm vm = vmlist.get(vmOfCloutlet.get(cloudletWithResType.getCloudletId()));
                if (initialResOfVm.get(vm) == null) {
                    initialResOfVm.put(vm, new HashMap<>());
                }
                if (initialResOfVm.get(vm).get("cpu") == null) {
                    initialResOfVm.get(vm).put("cpu", 0.0);
                }
                if (initialResOfVm.get(vm).get("ram") == null) {
                    initialResOfVm.get(vm).put("ram", 0.0);
                }
                if (initialResOfVm.get(vm).get("bw") == null) {
                    initialResOfVm.get(vm).put("bw", 0.0);
                }
                double oldCpu = (double) initialResOfVm.get(vm).get("cpu");
                double oldRam = (double) initialResOfVm.get(vm).get("ram");
                double oldBw = (double) initialResOfVm.get(vm).get("bw");

                ResourceType resourceType = cloudletWithResType.getResourceType();
                initialResOfVm.get(vm).put("cpu", oldCpu + resourceType.getMIPS());
                initialResOfVm.get(vm).put("ram", oldRam + resourceType.getRam());
                initialResOfVm.get(vm).put("bw", oldBw + resourceType.getBw());
            }

            /*//resize the vm along the above calculation
            for (Vm vm : vmlist) {
                Map<String, Double> resOfThisVm = initialResOfVm.get(vm);
                if (resOfThisVm != null) {
                    ((VmTAVMS) vm).setNumberOfPes((int)((resOfThisVm.get("cpu")/ mips))+1);
                    vm.setRam(resOfThisVm.get("ram").intValue() + 10);
                    vm.setBw(resOfThisVm.get("bw").intValue() + 10);
                }
            }*/

            // Sixth step: Starts the simulation
            CloudSim.startSimulation();

            CloudSim.stopSimulation();

            //Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);

            Log.printLine("CloudSimExample1 finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }

    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     * @return the datacenter
     */
    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<Host>();

        int coreNumber = 32;
        int mips = 1000;
        int ram = 32 * 1024; // 单位MB
        long bw = 1000 * 1000; //单位Kbps
        long storage = 1000 * 1000; //单位MB
        int hostNumber = 32; //主机数量

        for (int hostId = 0; hostId < hostNumber; hostId++) {
            List<Pe> peList = new ArrayList<Pe>();
            for (int coreId = 0; coreId < coreNumber; coreId++) {
                peList.add(new Pe(coreId, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
            }
            Host host = new HostTAVMS(
                    hostId,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peList,
                    new VmSchedulerTimeShared(peList)
            );
            hostList.add(host);
        }

        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
        // devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        Datacenter datacenter = null;
        try {
            datacenter = new DatacenterTAVMS(name, characteristics, new VMAllocationPolicyRandom(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBrokerTAVMS("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

}
