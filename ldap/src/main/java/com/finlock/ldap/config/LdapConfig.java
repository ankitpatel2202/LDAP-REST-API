package com.finlock.ldap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class LdapConfig {

    @Value("${ldap.base.dn:ou=users,dc=security,dc=ssokerberos,dc=org}")
    private String baseDn;

    @Value("${ldap.context.object.classes:top,person,organizationalPerson,inetOrgPerson}")
    private String objectClassesStr;

    @Value("${ldap.kdc.realm:SSOKERBEROS.ORG}")
    private String realm;

    @Value("${ldap.modifiable.attrs:sn,cn,mail,userPassword}")
    private String ldapModifiableAttr;

    public String getBaseDn() {
        return baseDn;
    }

    public String[] getObjectClasses() {
        return objectClassesStr.split(",");
    }

    public String getRealm() {
        return realm;
    }

    @Bean
    public List<String> ldapModifiableAttrList(){
        if(!StringUtils.hasLength(ldapModifiableAttr)){
            return new ArrayList<>();
        }
        String[] split = ldapModifiableAttr.split(",");
        System.out.println("Modifiable ldap attribute list:  " + ldapModifiableAttr);
        return Arrays.asList(split);
    }
}
