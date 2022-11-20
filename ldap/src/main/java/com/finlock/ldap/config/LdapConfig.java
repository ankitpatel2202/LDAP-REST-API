package com.finlock.ldap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdapConfig {

    @Value("${ldap.base.dn:ou=users,dc=security,dc=ssokerberos,dc=org}")
    private String baseDn;

    @Value("${ldap.context.object.classes:top,person,organizationalPerson,inetOrgPerson}")
    private String objectClassesStr;

    @Value("${ldap.kdc.realm:SSOKERBEROS.ORG}")
    private String realm;

    public String getBaseDn() {
        return baseDn;
    }

    public String[] getObjectClasses() {
        return objectClassesStr.split(",");
    }

    public String getRealm() {
        return realm;
    }
}
