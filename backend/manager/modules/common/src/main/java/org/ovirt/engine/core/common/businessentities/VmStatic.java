package org.ovirt.engine.core.common.businessentities;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.ovirt.engine.core.common.businessentities.OvfExportOnlyField.ExportOption;
import org.ovirt.engine.core.common.utils.ObjectUtils;
import org.ovirt.engine.core.common.validation.annotation.ValidI18NName;
import org.ovirt.engine.core.common.validation.group.CreateVm;
import org.ovirt.engine.core.common.validation.group.ImportClonedEntity;
import org.ovirt.engine.core.common.validation.group.UpdateVm;
import org.ovirt.engine.core.compat.Guid;

public class VmStatic extends VmBase {
    private static final long serialVersionUID = -2753306386502558044L;

    private Guid vmtGuid;

    private boolean initialized;

    private String originalTemplateName;

    private Guid originalTemplateGuid;

    @EditableOnVmStatusField
    @OvfExportOnlyField(exportOption = ExportOption.EXPORT_NON_IGNORED_VALUES)
    @Size(max = BusinessEntitiesDefinitions.GENERAL_MAX_SIZE)
    private String userDefinedProperties;

    @EditableOnVmStatusField
    @OvfExportOnlyField(exportOption = ExportOption.EXPORT_NON_IGNORED_VALUES)
    @Size(max = BusinessEntitiesDefinitions.GENERAL_MAX_SIZE)
    private String predefinedProperties;

    /**
     * Disk size in sectors of 512 bytes
     */
    private int diskSize;

    @EditableOnVmStatusField
    private String customProperties;

    @EditableField
    @OvfExportOnlyField(exportOption = ExportOption.EXPORT_NON_IGNORED_VALUES)
    @Size(max = BusinessEntitiesDefinitions.GENERAL_MAX_SIZE)
    private String cpuPinning;

    @EditableField
    private boolean useHostCpuFlags;

    @EditableField
    private Guid instanceTypeId;
    private Guid imageTypeId;

    @EditableField
    private boolean useLatestVersion;

    private NumaTuneMode numaTuneMode;

    private List<VmNumaNode> vNumaNodeList;

    public VmStatic() {
        setNumOfMonitors(1);
        initialized = false;
        setNiceLevel(0);
        setCpuShares(0);
        setDefaultBootSequence(BootSequence.C);
        setDefaultDisplayType(DisplayType.qxl);
        setVmType(VmType.Desktop);
        vmtGuid = Guid.Empty;
        setNumaTuneMode(NumaTuneMode.INTERLEAVE);
        vNumaNodeList = new ArrayList<VmNumaNode>();
        customProperties = "";
    }

    public VmStatic(VmStatic vmStatic) {
        this((VmBase)vmStatic);
        vmtGuid = vmStatic.getVmtGuid();
        setCustomProperties(vmStatic.getCustomProperties());
        setInitialized(vmStatic.isInitialized());
        setUseLatestVersion(vmStatic.isUseLatestVersion());
        setInstanceTypeId(vmStatic.getInstanceTypeId());
    }

    public VmStatic(VmBase vmBase) {
        super(vmBase);
    }

    public String getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(String customProperties) {
        this.customProperties = customProperties;
    }

    public String getPredefinedProperties() {
        return predefinedProperties;
    }

    public void setPredefinedProperties(String predefinedProperties) {
        this.predefinedProperties = predefinedProperties;
    }

    public String getUserDefinedProperties() {
        return userDefinedProperties;
    }

    public void setUserDefinedProperties(String userDefinedProperties) {
        this.userDefinedProperties = userDefinedProperties;
    }

    public int getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(int value) {
        diskSize = value;
    }

    public boolean isFirstRun() {
        return !isInitialized();
    }

    @Override
    @Size(min = 1, max = BusinessEntitiesDefinitions.VM_NAME_SIZE, groups = { Default.class, ImportClonedEntity.class })
    @ValidI18NName(message = "ACTION_TYPE_FAILED_NAME_MAY_NOT_CONTAIN_SPECIAL_CHARS",
            groups = { CreateVm.class, UpdateVm.class, ImportClonedEntity.class })
    public String getName() {
        return super.getName();
    }

    public Guid getVmtGuid() {
        return this.vmtGuid;
    }

    public void setVmtGuid(Guid value) {
        this.vmtGuid = value;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean value) {
        initialized = value;
    }

    @Override
    public Object getQueryableId() {
        return getId();
    }

    public String getCpuPinning() {
        return cpuPinning;
    }

    public void setCpuPinning(String cpuPinning) {
        this.cpuPinning = cpuPinning;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (initialized ? 1231 : 1237);
        result = prime * result + diskSize;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((predefinedProperties == null) ? 0 : predefinedProperties.hashCode());
        result = prime * result + ((userDefinedProperties == null) ? 0 : userDefinedProperties.hashCode());
        result = prime * result + ((vmtGuid == null) ? 0 : vmtGuid.hashCode());
        result = prime * result + (useHostCpuFlags ? 1231 : 1237);
        result = prime * result + ((instanceTypeId == null) ? 0 : instanceTypeId.hashCode());
        result = prime * result + ((imageTypeId == null) ? 0 : imageTypeId.hashCode());
        result = prime * result + ((originalTemplateGuid == null) ? 0 : originalTemplateGuid.hashCode());
        result = prime * result + ((originalTemplateName == null) ? 0 : originalTemplateName.hashCode());
        result = prime * result + (useLatestVersion ? 1249 : 1259);
        result = prime * result + ((numaTuneMode == null) ? 0 : numaTuneMode.getValue().hashCode());
        result = prime * result + ((vNumaNodeList == null) ? 0 : vNumaNodeList.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof VmStatic)) {
            return false;
        }
        VmStatic other = (VmStatic) obj;
        return (initialized == other.initialized
                && diskSize == other.diskSize
                && ObjectUtils.objectsEqual(getName(), other.getName())
                && ObjectUtils.objectsEqual(predefinedProperties, other.predefinedProperties)
                && ObjectUtils.objectsEqual(userDefinedProperties, other.userDefinedProperties)
                && ObjectUtils.objectsEqual(vmtGuid, other.vmtGuid)
                && useHostCpuFlags == other.useHostCpuFlags
                && ObjectUtils.objectsEqual(instanceTypeId, other.instanceTypeId)
                && ObjectUtils.objectsEqual(imageTypeId, other.imageTypeId)
                && ObjectUtils.objectsEqual(originalTemplateGuid, other.originalTemplateGuid)
                && ObjectUtils.objectsEqual(originalTemplateName, other.originalTemplateName)
                && useLatestVersion == other.useLatestVersion
                && ObjectUtils.objectsEqual(numaTuneMode.getValue(), other.numaTuneMode.getValue())
                && ObjectUtils.objectsEqual(vNumaNodeList, other.vNumaNodeList)
         );
    }

    public boolean isUseHostCpuFlags() {
        return useHostCpuFlags;
    }

    public void setUseHostCpuFlags(boolean useHostCpuFlags) {
        this.useHostCpuFlags = useHostCpuFlags;
    }

    @Override
    public int getMinAllocatedMem() {
        if (super.getMinAllocatedMem() > 0) {
            return super.getMinAllocatedMem();
        }
        return getMemSizeMb();
    }

    public Guid getInstanceTypeId() {
        return instanceTypeId;
    }

    public void setInstanceTypeId(Guid instanceTypeId) {
        this.instanceTypeId = instanceTypeId;
    }

    public Guid getImageTypeId() {
        return imageTypeId;
    }

    public void setImageTypeId(Guid imageTypeId) {
        this.imageTypeId = imageTypeId;
    }

    public String getOriginalTemplateName() {
        return originalTemplateName;
    }

    public void setOriginalTemplateName(String originalTemplateName) {
        this.originalTemplateName = originalTemplateName;
    }

    public Guid getOriginalTemplateGuid() {
        return originalTemplateGuid;
    }

    public void setOriginalTemplateGuid(Guid originalTemplateGuid) {
        this.originalTemplateGuid = originalTemplateGuid;
    }

    public boolean isUseLatestVersion() {
        return useLatestVersion;
    }

    public void setUseLatestVersion(boolean useLatestVersion) {
        this.useLatestVersion = useLatestVersion;
    }

    public NumaTuneMode getNumaTuneMode() {
        return numaTuneMode;
    }

    public void setNumaTuneMode(NumaTuneMode numaTuneMode) {
        this.numaTuneMode = numaTuneMode;
    }

    public List<VmNumaNode> getvNumaNodeList() {
        return vNumaNodeList;
    }

    public void setvNumaNodeList(List<VmNumaNode> vNumaNodeList) {
        this.vNumaNodeList = vNumaNodeList;
    }

}
