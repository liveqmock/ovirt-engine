package org.ovirt.engine.core.bll;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.ovirt.engine.core.utils.MockConfigRule.mockConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.ovirt.engine.core.common.action.ImportVmParameters;
import org.ovirt.engine.core.common.businessentities.DisplayType;
import org.ovirt.engine.core.common.businessentities.OvfEntityData;
import org.ovirt.engine.core.common.businessentities.StorageDomain;
import org.ovirt.engine.core.common.businessentities.StorageDomainStatus;
import org.ovirt.engine.core.common.businessentities.StorageDomainType;
import org.ovirt.engine.core.common.businessentities.VDSGroup;
import org.ovirt.engine.core.common.config.ConfigValues;
import org.ovirt.engine.core.common.errors.VdcBllMessages;
import org.ovirt.engine.core.common.osinfo.OsRepository;
import org.ovirt.engine.core.common.utils.SimpleDependecyInjector;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.compat.Version;
import org.ovirt.engine.core.dao.UnregisteredOVFDataDAO;
import org.ovirt.engine.core.utils.MockConfigRule;

@RunWith(MockitoJUnitRunner.class)
public class ImportVMFromConfigurationCommandTest {
    private Guid vmId;
    private Guid storageDomainId;
    private Guid storagePoolId;
    private Guid clusterId;
    private static final String VM_OVF_XML_DATA = "src/test/resources/vmOvfData.xml";
    private String xmlOvfData;
    private VDSGroup vdsGroup;

    private ImportVmFromConfigurationCommand<ImportVmParameters> cmd;

    @ClassRule
    public static MockConfigRule mcr = new MockConfigRule(
            mockConfig(ConfigValues.VirtIoScsiEnabled, Version.v3_2.toString(), false)
            );

    @Mock
    private OsRepository osRepository;

    @Mock
    private UnregisteredOVFDataDAO unregisteredOVFDataDao;

    @Before
    public void setUp() throws IOException {
        vmId = Guid.newGuid();
        storageDomainId = Guid.newGuid();
        storagePoolId = Guid.newGuid();
        clusterId = Guid.newGuid();

        // init the injector with the osRepository instance
        SimpleDependecyInjector.getInstance().bind(OsRepository.class, osRepository);
        final int osId = 0;
        Map<Integer, Map<Version, List<DisplayType>>> displayTypeMap = new HashMap<>();
        displayTypeMap.put(osId, new HashMap<Version, List<DisplayType>>());
        displayTypeMap.get(osId).put(null, Arrays.asList(DisplayType.qxl));
        when(osRepository.getDisplayTypes()).thenReturn(displayTypeMap);
        mockVdsGroup();
        setXmlOvfData();
    }

    private void setXmlOvfData() throws IOException {
        xmlOvfData = new String(Files.readAllBytes(Paths.get(VM_OVF_XML_DATA)));
    }

    @Test
    public void testPositiveImportVmFromConfiguration() {
        initCommand(getOvfEntityData());
        doReturn(createStorageDomain()).when(cmd).getStorageDomain();
        doReturn(Boolean.TRUE).when(cmd).canDoActionAfterCloneVm(anyMap());
        doReturn(Boolean.TRUE).when(cmd).canDoActionBeforeCloneVm(anyMap());
        CanDoActionTestUtils.runAndAssertCanDoActionSuccess(cmd);
    }

    @Test
    public void testImportVMFromConfigurationWhenStorageDomainIsInMaintenance() {
        initCommand(getOvfEntityData());
        StorageDomain storageDomain = createStorageDomain();
        storageDomain.setStatus(StorageDomainStatus.Maintenance);
        doReturn(storageDomain).when(cmd).getStorageDomain();
        CanDoActionTestUtils.runAndAssertCanDoActionFailure(cmd,
                VdcBllMessages.ACTION_TYPE_FAILED_STORAGE_DOMAIN_STATUS_ILLEGAL2);
    }

    @Test
    public void testImportVMFromConfigurationWhenStorageDomainIsInactive() {
        initCommand(getOvfEntityData());
        StorageDomain storageDomain = createStorageDomain();
        storageDomain.setStatus(StorageDomainStatus.Inactive);
        doReturn(storageDomain).when(cmd).getStorageDomain();
        CanDoActionTestUtils.runAndAssertCanDoActionFailure(cmd,
                VdcBllMessages.ACTION_TYPE_FAILED_STORAGE_DOMAIN_STATUS_ILLEGAL2);
    }

    @Test
    public void testImportVMFromConfigurationWhenVMDoesNotExists() {
        initCommand(null);
        CanDoActionTestUtils.runAndAssertCanDoActionFailure(cmd, VdcBllMessages.ACTION_TYPE_FAILED_UNSUPPORTED_OVF);
    }

    @Test
    public void testImportVMFromConfigurationXMLCouldNotGetParsed() {
        OvfEntityData ovfEntity = getOvfEntityData();
        ovfEntity.setOvfData("This is not a valid XML");
        initCommand(ovfEntity);
        when(unregisteredOVFDataDao.getByEntityIdAndStorageDomain(vmId, storageDomainId)).thenReturn(ovfEntity);
        CanDoActionTestUtils.runAndAssertCanDoActionFailure(cmd,
                VdcBllMessages.ACTION_TYPE_FAILED_OVF_CONFIGURATION_NOT_SUPPORTED);
    }

    private ImportVmParameters createParametersWhenImagesExistOnTargetStorageDomain() {
        ImportVmParameters params = new ImportVmParameters();
        params.setContainerId(vmId);
        params.setStorageDomainId(storageDomainId);
        params.setVdsGroupId(clusterId);
        params.setImagesExistOnTargetStorageDomain(true);
        return params;
    }

    private void initCommand(OvfEntityData resultOvfEntityData) {
        ImportVmParameters parameters = createParametersWhenImagesExistOnTargetStorageDomain();
        initUnregisteredOVFData(resultOvfEntityData);
        cmd = spy(new ImportVmFromConfigurationCommand<ImportVmParameters>(parameters) {
            // Overridden here and not during spying, since it's called in the constructor
            @SuppressWarnings("synthetic-access")
            @Override
            protected UnregisteredOVFDataDAO getUnregisteredOVFDataDao() {
                return unregisteredOVFDataDao;
            }

            public VDSGroup getVdsGroup() {
                return vdsGroup;
            }
        });
    }

    private void initUnregisteredOVFData(OvfEntityData resultOvfEntityData) {
        when(unregisteredOVFDataDao.getByEntityIdAndStorageDomain(vmId, storageDomainId)).thenReturn(resultOvfEntityData);
    }

    private OvfEntityData getOvfEntityData() {
        OvfEntityData ovfEntity = new OvfEntityData();
        ovfEntity.setEntityId(vmId);
        ovfEntity.setEntityName("Some VM");
        ovfEntity.setOvfData(xmlOvfData);
        return ovfEntity;
    }

    private void mockVdsGroup() {
        vdsGroup = new VDSGroup();
        vdsGroup.setId(clusterId);
        vdsGroup.setStoragePoolId(storagePoolId);
    }

    protected StorageDomain createStorageDomain() {
        StorageDomain sd = new StorageDomain();
        sd.setId(storageDomainId);
        sd.setStorageDomainType(StorageDomainType.Data);
        sd.setStatus(StorageDomainStatus.Active);
        sd.setStoragePoolId(storagePoolId);
        return sd;
    }
}
