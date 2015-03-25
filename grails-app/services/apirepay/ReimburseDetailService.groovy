package apirepay

import grails.converters.JSON
import grails.transaction.Transactional

import java.text.SimpleDateFormat

@Transactional
class ReimburseDetailService {
	ReimburseService reimburseService
	ReimburseDetailValidationService reimburseDetailValidationService
	def serviceMethod() {

	}

	def getList(Long reimburseId,Integer offset,Integer max,String sortBy,String Order){
		def parent = Reimburse.findById(reimburseId)
		return ReimburseDetail.findAllByReimburseAndIsDeleted(parent,false, [max: max, offset: offset ,sort: sortBy, order: Order])
	}

	def createObject(def object){
		def newObject = new ReimburseDetail()
		newObject.isDeleted = false
		newObject.name = object.name
		newObject.description = object.description
		newObject.amount =  Double.parseDouble(object.amount)
		if (object.receiptDate != null)
		{
			newObject.receiptDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(object.receiptDate)
		}
		newObject.urlImageOri = object.urlImageOri
		newObject.urlImageMedium = object.urlImageMedium
		newObject.urlImageSmall = object.urlImageSmall
		newObject.reimburse = object.reimburse
		newObject = reimburseDetailValidationService.createObjectValidation(newObject)
		if (newObject.errors.getErrorCount() == 0)
		{
			def parent = Reimburse.findById(newObject.reimburse.id)
			
			newObject = newObject.save()
			
			parent = parent.addToReimburseDetails(newObject)
			newObject.reimburse = reimburseService.calculateTotal(parent.id)
			newObject = [model : newObject,
				total : parent.total]
		}
	
		return newObject
	}


	def updateObject(def object){
		def valObject = ReimburseDetail.read(object.id)
		valObject.name = object.title
		valObject.description = object.salesOrder
		valObject.amount = object.amount
		if (valObject.receiptDate != null)
		{
			valObject.receiptDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(object.receiptDate)
		}
		valObject.urlImageOri = object.urlImageOri
		valObject.urlImageMedium = object.urlImageMedium
		valObject.urlImageSmall = object.urlImageSmall
		valObject = reimburseDetailValidationService.updateObjectValidation(valObject)
		if (valObject.errors.getErrorCount() == 0)
		{
			valObject.save()
			reimburseService.calculateTotal(valObject.reimburse)
		}
		else
		{
			valObject.discard()
		}
		return valObject
	}

	def softDeleteObject(def object){
		def newObject = ReimburseDetail.get(object.id)
		newObject = reimburseDetailValidationService.softDeleteObjectValidation(newObject)
		if (newObject.errors.getErrorCount() == 0)
		{
			newObject.isDeleted = true
		}
		return newObject
	}



}
