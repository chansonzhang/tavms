import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/1.
 */
public class ResourceRequirementTypeManager {

    /**
     * @TODO: modify this to arrarlist
     */
    private ResourceType[] applicationTypes;
    private List<ResourceType> kernelTypes;
    private int cloudletTypeNumber;

    public ResourceType[] getApplicationTypes() {
        return applicationTypes;
    }

    public void setApplicationTypes(ResourceType[] applicationTypes) {
        this.applicationTypes = applicationTypes;
    }

    public int getCloudletTypeNumber() {
        return cloudletTypeNumber;
    }

    public void setCloudletTypeNumber(int cloudletTypeNumber) {
        this.cloudletTypeNumber = cloudletTypeNumber;
    }

    public List<ResourceType> getKernelTypes() {
        return kernelTypes;
    }

    public void setKernelTypes(List<ResourceType> kernelTypes) {
        this.kernelTypes = kernelTypes;
    }

    public ResourceRequirementTypeManager() {
        setCloudletTypeNumber(8);
        applicationTypes = new ResourceType[cloudletTypeNumber];
        ResourceType resourceType0 = new ResourceType(new LoadGenerator1(0,200), new LoadGenerator1(0,256),new LoadGenerator1(0,4000), 0);
        ResourceType resourceType1 = new ResourceType(new LoadGenerator2(0,200), new LoadGenerator2(0,256), new LoadGenerator2(4000, 20000), 1);
        ResourceType resourceType2 = new ResourceType(new LoadGenerator3(0,200), new LoadGenerator3(256, 1024),new LoadGenerator3(0,4000), 2);
        ResourceType resourceType3 = new ResourceType(new LoadGenerator4(0,200), new LoadGenerator4(256, 1024), new LoadGenerator4(4000, 20000), 3);
        ResourceType resourceType4 = new ResourceType(new LoadGenerator5(200, 1000), new LoadGenerator5(0,256), new LoadGenerator5(0,4000), 4);
        ResourceType resourceType5 = new ResourceType(new LoadGenerator6(200, 1000), new LoadGenerator6(0,256), new LoadGenerator6(4000, 20000), 5);
        ResourceType resourceType6 = new ResourceType(new LoadGenerator7(200, 1000), new LoadGenerator7(256, 1024), new LoadGenerator7(0,4000), 6);
        ResourceType resourceType7 = new ResourceType(new LoadGenerator8(200, 1000), new LoadGenerator8(256, 1024), new LoadGenerator8(4000, 20000), 7);
        applicationTypes[0] = resourceType0;
        applicationTypes[1] = resourceType1;
        applicationTypes[2] = resourceType2;
        applicationTypes[3] = resourceType3;
        applicationTypes[4] = resourceType4;
        applicationTypes[5] = resourceType5;
        applicationTypes[6] = resourceType6;
        applicationTypes[7] = resourceType7;

        kernelTypes = new ArrayList<>();
        LoadGenerator mipsGeneratorKernel = new LoadGenerator1(200, 200);
        LoadGenerator ramGeneratorKernel = new LoadGenerator1(512, 512);
        LoadGenerator bwGeneratorKernel = new LoadGenerator1(1000, 1000);
        ResourceType kernelType0 = new ResourceType(mipsGeneratorKernel, ramGeneratorKernel, bwGeneratorKernel, 8);
        kernelTypes.add(kernelType0);
    }
}
