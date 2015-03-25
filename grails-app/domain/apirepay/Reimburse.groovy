package apirepay

class Reimburse {
	
	String title
	String description
	Double	total
	Date	projectDate
	Date	sentDate
	Date	statusDate
	Boolean	isSent
	Boolean	isDeleted
	Integer	status
	String	sentTo
	ShiroUser user
	
	static hasMany = [reimburseDetails : ReimburseDetail]
    static constraints = {
		projectDate(nullable :true)
		sentDate (nullable:true)
		statusDate (nullable:true)
		
    }
}
