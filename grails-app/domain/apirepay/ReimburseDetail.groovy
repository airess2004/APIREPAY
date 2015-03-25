package apirepay

class ReimburseDetail {
	String name
	String description 
	Double	amount
	Date receiptDate
	String	urlImageOri
	String 	urlImageSmall
	String	urlImageMedium
	Boolean	isDeleted
	Reimburse reimburse
	
//	static belongsTo = Reimburse
	
    static constraints = {
		receiptDate (nullable :true)
		urlImageOri (nullable :true)
		urlImageSmall (nullable :true)
		urlImageMedium (nullable :true)
    }
}
