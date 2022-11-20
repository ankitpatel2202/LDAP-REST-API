package com.finlock.ldap.repository;

import com.finlock.ldap.config.LdapConfig;
import com.finlock.ldap.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.Name;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Repository
public class UserRepository {
    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LdapConfig ldapConfig;

    public void addUser(User user) {
        try {
            DirContextAdapter context = new DirContextAdapter(buildDn(user));
            mapToContext(user, context);
            ldapTemplate.bind(context);
        } catch (NameAlreadyBoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "User Already Exists", e);
        }
    }

    protected void mapToContext(User user, DirContextOperations context) {
        context.setAttributeValues("objectclass", ldapConfig.getObjectClasses());
        context.setAttributeValue("cn", user.getCn());
        context.setAttributeValue("sn", user.getSn());
        context.setAttributeValue("uid", user.getUid());
        context.setAttributeValue("krb5PrincipalName", user.getUid() + "@" + ldapConfig.getRealm());
        context.setAttributeValue("krb5KeyVersionNumber", "0");
    }

    private Name buildDn(User user) {
        Name dn = LdapNameBuilder.newInstance(ldapConfig.getBaseDn())
                .add("uid", user.getUid())
                .build();
        return dn;
    }

    public User getUser(String uid) {
        try {
            return ldapTemplate.findOne(query().base(ldapConfig.getBaseDn()).where("uid").is(uid), User.class);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User Not Found", e);
        }
    }

    public List<User> getUsers() {
        return ldapTemplate.find(query().base(ldapConfig.getBaseDn()).where("uid").isPresent(), User.class);
    }

    public void delete(String uid) {
        try {
            User user = ldapTemplate.findOne(query().base(ldapConfig.getBaseDn()).where("uid").is(uid), User.class);
            ldapTemplate.unbind(user.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User Not Found", e);
        }
    }
}
