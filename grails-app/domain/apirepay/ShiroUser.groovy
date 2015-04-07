package apirepay

import java.util.Date;

class ShiroUser {
    String username
    String passwordHash
    String fullname
	
	boolean isDeleted
	Date dateCreated
	Date lastUpdated
	
    static hasMany = [ roles: ShiroRole, permissions: String ]

    static constraints = {
        username(nullable: false, blank: false, unique: true)
    }
}
