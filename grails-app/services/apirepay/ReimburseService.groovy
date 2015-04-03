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
		def newObject = new Reimburse()
		newObject.isSent = false
		newObject.isDeleted = false
		newObject.total = 0
		newObject.status = 0
		newObject.sentTo = ""
		newObject.title = object.title
		newObject.description = object.description
		newObject.user = object.user
		if (object.projectDate != null)
		{
			newObject.projectDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(object.projectDate)
		}
		newObject = reimburseValidationService.createObjectValidation(newObject as Reimburse)
		if (newObject.errors.getErrorCount() == 0)
		{
			newObject = newObject.save()
		}
		return newObject
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