package apirepay

import java.util.Date;

class Reimburse {
	
	String title
	String description
	Integer idx
	Double	total
	Date	projectDate
	Date	sentDate
	Date	statusDate
	Boolean	isSent
	Boolean	isDone
	Boolean	isDeleted
	Integer	status
	String	sentTo
	Date lastUpdate //client's lastUpdate timestamp
	ShiroUser user
	Date dateCreated
	Date lastUpdated
	
	static hasMany = [reimburseDetails : ReimburseDetail]
    static constraints = {
		projectDate(nullable :true)
		sentDate (nullable:true)
		statusDate (nullable:true)
		
    }
}
