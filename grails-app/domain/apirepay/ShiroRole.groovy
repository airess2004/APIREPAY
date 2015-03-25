package apirepay

import java.util.Date;

class ShiroRole {
    String name

	
	boolean isDeleted
	Date dateCreated
	Date lastUpdated
	
    static hasMany = [ users: ShiroUser, permissions: String ]
    static belongsTo = ShiroUser

    static constraints = {
        name(nullable: false, blank: false, unique: true)
    }
}
