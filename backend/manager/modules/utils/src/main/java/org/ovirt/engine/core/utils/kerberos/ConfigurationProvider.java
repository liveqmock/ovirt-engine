package org.ovirt.engine.core.utils.kerberos;

import static org.ovirt.engine.core.common.config.ConfigValues.AdUserId;
import static org.ovirt.engine.core.common.config.ConfigValues.AdUserName;
import static org.ovirt.engine.core.common.config.ConfigValues.AdUserPassword;
import static org.ovirt.engine.core.common.config.ConfigValues.DomainName;
import static org.ovirt.engine.core.common.config.ConfigValues.LDAPProviderTypes;
import static org.ovirt.engine.core.common.config.ConfigValues.LDAPSecurityAuthentication;
import static org.ovirt.engine.core.common.config.ConfigValues.LdapServers;

import java.io.IOException;
import java.util.EnumMap;

import org.apache.log4j.Logger;
import org.ovirt.engine.core.common.config.ConfigValues;

public class ConfigurationProvider {

    private EnumMap<ConfigValues, String> configVals = new EnumMap<ConfigValues, String>(ConfigValues.class);
    private String engineConfigExecutable;
    private String engineConfigProperties;
    private final static Logger log = Logger.getLogger(ManageDomainsDAOImpl.class);

    public ConfigurationProvider(String adUserName,
            String adUserPassword,
            String domainName,
            String ldapSecurityAuthentication,
            String ldapServers,
            String adUserId,
            String ldapProviderTypes,
            String engineConfigExecutable,
            String engineConfigProperties) {
        super();
        configVals.put(AdUserName, adUserName);
        configVals.put(AdUserPassword, adUserPassword);
        configVals.put(DomainName, domainName);
        configVals.put(LDAPSecurityAuthentication, ldapSecurityAuthentication);
        configVals.put(LdapServers, ldapServers);
        configVals.put(AdUserId, adUserId);
        configVals.put(LDAPProviderTypes, ldapProviderTypes);
        this.engineConfigExecutable = engineConfigExecutable;
        this.engineConfigProperties = engineConfigProperties;
    }

    public String getConfigValue(ConfigValues enumValue) {
        if (configVals.containsKey(enumValue)) {
            return configVals.get(enumValue);
        }
        return "";
    }

    public void setConfigValue(ConfigValues enumValue, String value, String loggingValue) throws ManageDomainsResult {

        log.info("Setting value for " + enumValue.toString() + " to " + loggingValue);

        try {
            Process engineConfigProcess =
                    Runtime.getRuntime().exec(engineConfigExecutable + " -s "
                            + enumValue.name() + "="
                            + value + " -p " + engineConfigProperties);
            int retVal = engineConfigProcess.waitFor();
            if (retVal != 0) {
                throw new ManageDomainsResult(ManageDomainsResultEnum.FAILED_SETTING_CONFIGURATION_VALUE_FOR_OPTION,
                        enumValue.name());
            }
        } catch (IOException e) {
            throw new ManageDomainsResult(ManageDomainsResultEnum.FAILED_SETTING_CONFIGURATION_VALUE_FOR_OPTION_WITH_DETAILS,
                    new String[] { enumValue.name(), e.getMessage() });
        } catch (InterruptedException e) {
            throw new ManageDomainsResult(ManageDomainsResultEnum.FAILED_SETTING_CONFIGURATION_VALUE_FOR_OPTION_WITH_DETAILS,
                    new String[] { enumValue.name(), e.getMessage() });
        }

    }
}
