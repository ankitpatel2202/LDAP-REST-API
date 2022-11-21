package com.finlock.ldap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Attribute;

import javax.naming.Name;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entry(objectClasses = { "inetOrgPerson", "organizationalPerson", "person", "top" })
public class User implements Comparable<User>{

    @Id
    @JsonIgnore
    private Name id;

    @ApiModelProperty(value = "unique id")
    @Attribute(name="uid")
    @NotBlank
    private String uid;

    @ApiModelProperty(value = "First name")
    @Attribute(name="cn")
    @NotBlank
    private String cn;

    @ApiModelProperty(value = "Last name")
    @Attribute(name="sn")
    @NotBlank
    private String sn;

    @ApiModelProperty(value = "mail")
    @Attribute(name="mail")
    @NotBlank
    private String mail;

    @ApiModelProperty(value = "userPassword")
    @Attribute(name="userPassword")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userPassword;

    @Override
    public int compareTo(User user) {
        boolean allAttributesEqual = getUid().equals(user.getUid())
                && getCn().equals(user.getCn())
                && getSn().equals(user.getSn())
                && getMail().equals(user.getMail());
        return  allAttributesEqual ? 0 : 1;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", cn='" + cn + '\'' +
                ", sn='" + sn + '\'' +
                ", mail='" + mail + '\''  +
                '}';
    }
}
