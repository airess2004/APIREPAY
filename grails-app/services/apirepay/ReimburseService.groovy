package apirepay

import grails.transaction.Transactional
import java.text.SimpleDateFormat

@Transactional
class ReimburseService {
	ReimburseValidationService reimburseValidationService

	def serviceMethod() {

	}

	def getList(def user,Integer offset,Integer max,String sortBy,String Order)
	{
		return Reimburse.findAllByIsDeletedAndUser(false,user, [max: max, offset: offset ,sort: sortBy, order: Order])
	}
	
	def getListFrom(def object,def user2)
	{
		Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(object.dateCreated)
		return Reimburse.findAll{dateCreated >= date && isDeleted == false && user == user2}
	}
	
	def getList(def object,def user,Integer offset,Integer max,String sortBy,String Order)
	{
		if (object == null || object.col == null || object.col == "")
		{
			return Reimburse.findAllByIsDeletedAndUser(false,user, [max: max, offset: offset ,sort: sortBy, order: Order])
		}
		else
		{
			return Reimburse.findAll("from Reimburse as b where b.${object.col} = ${object.val} and b.isDeleted = false and b.user.id = ${user.id}",
					[max: max, offset: offset ,sort: sortBy, order: Order])
		}
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
		
		object.isSent = false
		object.isDeleted = false
		object.total = 0
		object.status = 0
		object.sentTo = ""
		object.title = object.title
		object.description = object.description
		object.user = object.user
		if (object.projectDate != null)
		{
			object.projectDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(object.projectDate)
		}
		object = reimburseValidationService.createObjectValidation(object as Reimburse)
		if (object.errors.getErrorCount() == 0)
		{
			object = object.save()
		}
		return object
	}

	def updateObject(def object){
		def valObject = Reimburse.read(object.id)
		valObject.title = object.title
		valObject.description = object.description
		if (object.projectDate != null)
		{
			valObject.projectDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(object.projectDate)
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
