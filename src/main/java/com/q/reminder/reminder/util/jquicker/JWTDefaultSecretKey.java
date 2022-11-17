package com.q.reminder.reminder.util.jquicker;

/**
 * JQuicker的密钥默认枚举
 */
public enum JWTDefaultSecretKey{
    //默认密钥
    DEFAULT_SECRET_KEY1("self_define_secret_key_about_the_project"),
    //默认密钥2
    DEFAULT_SECRET_KEY2("init_define_secret_key_about_project_self"),
    //HS256密钥
    HS256_SECRET_KEY("default_key@JQuicker-HS256-for-the-secret"),
    //HS384
    HS384_SECRET_KEY("default_key@JQuicker-HS384-for-the-secret:dj$#546e;'wiu-67-q$#$u4546adoia484@#$#45fhdf%$adiayhifhqihfiqgf184396tf8ihisdah4564seafhr97735h43v13416c$%^$%3v4c6#$^$%&^5741v545^&%^&dHDF^Hrej45uy%%^BBV%asg3v43vwc1443cx5c3406354&@&(&212ajkdghwrjk125353b2j453bit34598#$%#$%gjr3ho2y540vyc6v4cx645430y65c46545ex0qewfxy14fuy239yt4gw@#$#2_+93874kxjer76ckw;28::random-over-for-this-security-policy"),
    //HS512
    HS512_SECRET_KEY("default_key@JQuicker-HS512-for-the-secret");
    private String value;

    JWTDefaultSecretKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
