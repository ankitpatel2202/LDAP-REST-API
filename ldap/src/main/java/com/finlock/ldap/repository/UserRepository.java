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
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.Name;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Repository
public class UserRepository {
    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LdapConfig ldapConfig;

    @Autowired
    private List<String> ldapModifiableAttrList;

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
        Class<? extends User> userClass = user.getClass();
        Method[] methods = userClass.getDeclaredMethods();
        Field[] fields = userClass.getDeclaredFields();
        try {
            for (Field field : fields) {
                Attribute attrAnn = field.getAnnotation(Attribute.class);
                if(null == attrAnn) {
                    continue;
                }
                String attrName = attrAnn.name();
                for (Method method : methods) {
                    //find getters
                    if (method.getName().startsWith("set")) {
                        continue;
                    }
                    String methodName = method.getName().substring("get".length());
                    if (methodName.equalsIgnoreCase(attrName)) {
                        //Invoke method
                        Object attrVal = method.invoke(user);
                        if (attrVal != null) {
                            context.setAttributeValue(attrName, attrVal);
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e){
            System.out.println("Failed to map object.");
        }
        //Override some special fields.
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
            User user = ldapTemplate.findOne(query().base(ldapConfig.getBaseDn()).where("uid").is(uid), User.class);
            user.setUserPassword(null);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User Not Found", e);
        }
    }

    public List<User> getUsers() {
        List<User> users = ldapTemplate.find(query().base(ldapConfig.getBaseDn()).where("uid").isPresent(), User.class);
        for(User user: users){
            //reset sensitive information
            user.setUserPassword(null);
        }
        return users;
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

    private void updateModifiableAttributes(DirContextOperations context, User user){

        Method[] methods = user.getClass().getDeclaredMethods();
        try {
            for (String attrName : ldapModifiableAttrList) {
                for (Method method : methods) {
                    //find getters
                    if (method.getName().startsWith("set")) {
                        continue;
                    }
                    String methodName = method.getName().substring("get".length());
                    if (methodName.equalsIgnoreCase(attrName)) {
                        Object attrVal = method.invoke(user);
                        if(attrVal != null) {
                            context.setAttributeValue(attrName, attrVal);
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e){
            System.out.println("Failed to map object.");
        }
    }

    public void updateUsers(String uid, User user){
        User foundUser = getUser(uid);
        if(null == foundUser){
            return;
        }
        try {
            Name dn = buildDn(user);
            DirContextOperations context = ldapTemplate.lookupContext(dn);
            context.setAttributeValue("uid", uid);
            updateModifiableAttributes(context, user);
            ldapTemplate.modifyAttributes(context);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Failed to update user.", e);
        }
    }
}
