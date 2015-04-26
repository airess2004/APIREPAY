package apirepay

import grails.converters.JSON
import grails.transaction.Transactional

import java.text.SimpleDateFormat
import java.util.Date;

@Transactional
class ReimburseService {
	ReimburseValidationService reimburseValidationService
	
	// Simple ISO Converter https://gist.github.com/kdabir/6bfe265d2f3c2f9b438b
	private String DateToISOString(Date date) {
		return new Date().format("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", TimeZone.getTimeZone("UTC"));
	}
	
	private Date ISOStringToDate(String dateString) {
		return Date.parse("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", dateString);
	}

	def serviceMethod() {

	}

	def getList(def user,Integer offset,Integer max,String sortBy,String Order)
	{
		return Reimburse.findAllByIsDeletedAndUser(false,user, [max: max, offset: offset ,sort: sortBy, order: Order])
	}
	
	def getListFrom(def object,def user2)
	{
		Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(object.dateCreated)
		return Reimburse.findAll{dateCreated >= date && isDeleted == false && user == user2}
	}
	
	def getList(def object,def user,Integer offset,Integer max,String sortBy,String Order)
	{
		def models
		def result = [models:[], totalRows:0]
		if (object == null || object.col == null || object.col == "")
		{
			models = Reimburse.findAllByIsDeletedAndUser(false,user, [sort: sortBy, order: Order])
			result.totalRows = models.size()
			result.models = models.findAll([max: max, offset: offset])
		}
		else
		{
			models = Reimburse.findAll("from Reimburse as b where b.${object.col} ${object.op} ${object.val} and b.isDeleted = false and b.user.id = ${user.id}",
					[sort: sortBy, order: Order])
			result.totalRows = models.size()
			result.models = models.findAll([max: max, offset: offset])
		}
		return result;
	}
	
	
	def calculateTotal(object){
		def valObject = Reimburse.read(object)
		Double total = 0
		for (i in valObject.reimburseDetails.findAll{ it.isDeleted == false })
		{
			total +=  i.amount
		}
		valObject.total = total
		valObject.save()
		return valObject
	}

	def createObject(def object){
		Reimburse valObject = new Reimburse()
		valObject.isDone = false
		valObject.isSent = false
		valObject.isDeleted = false
		valObject.total = 0
		valObject.status = 0
		valObject.sentTo = ""
		valObject.title = object.title
		valObject.description = object.description
		valObject.user = object.user
		valObject.idx = object.idx
		
		if (object.lastUpdate != null) {
			valObject.lastUpdate = ISOStringToDate(object.lastUpdate)
		}
		if (valObject.projectDate != null)
		{
			valObject.projectDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(object.projectDate)
		}
		valObject = reimburseValidationService.createObjectValidation(valObject as Reimburse)
		if (valObject.errors.getErrorCount() == 0)
		{
			valObject = valObject.save()
		}
		return valObject
	}

	def updateObject(def object){
		def valObject = Reimburse.read(object.id)
		valObject.title = object.title
		valObject.description = object.description
		valObject.idx = object.idx
		valObject.isDone = object.isDone
		
		if (!valObject.isDeleted) valObject.isDeleted = object.isDeleted
		
		if (object.lastUpdate != null) {
			valObject.lastUpdate = ISOStringToDate(object.lastUpdate)
		}
		if (object.projectDate != null)
		{
			valObject.projectDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(object.projectDate)
		}
		valObject = reimburseValidationService.updateObjectValidation(valObject)
		if (valObject.errors.getErrorCount() == 0)
		{
			valObject.save()
		}
		else
		{
			valObject.discard()
		}
		return valObject
	}

	def softDeleteObject(def object){
		def newObject = Reimburse.get(object.id)
		newObject = reimburseValidationService.softDeleteObjectValidation(newObject)
		if (newObject.errors.getErrorCount() == 0)
		{
			newObject.isDeleted = true
		}
		return newObject
	}



}
