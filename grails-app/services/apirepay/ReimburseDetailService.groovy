package apirepay

import grails.converters.JSON
import grails.transaction.Transactional

import java.text.SimpleDateFormat
import java.util.Date;

@Transactional
class ReimburseDetailService {
	ReimburseService reimburseService
	ReimburseDetailValidationService reimburseDetailValidationService
	
	// Simple ISO Converter https://gist.github.com/kdabir/6bfe265d2f3c2f9b438b
	private String DateToISOString(Date date) {
		return new Date().format("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", TimeZone.getTimeZone("UTC"));
	}
	
	private Date ISOStringToDate(String dateString) {
		return Date.parse("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", dateString);
	}
	
	def serviceMethod() {

	}

	def getList(Long reimburseId,Integer offset,Integer max,String sortBy,String Order){
		def parent = Reimburse.findById(reimburseId)
		return ReimburseDetail.findAllByReimburseAndIsDeleted(parent,false, [max: max, offset: offset ,sort: sortBy, order: Order])
	}
	
	def getList(def object,Long reimburseId,Integer offset,Integer max,String sortBy,String Order)
	{
		def models
		def result = [models:[], totalRows:0]
		def parent = Reimburse.findById(reimburseId)
		if (object == null || object.col == null || object.col == "")
		{
			models = ReimburseDetail.findAllByReimburseAndIsDeleted(parent,false, [sort: sortBy, order: Order])
			result.totalRows = models.size()
			result.models = models.findAll([max: max, offset: offset])
		}
		else
		{
			models = ReimburseDetail.findAll("from ReimburseDetail as b where b.${object.col} ${object.op} ${object.val} and b.isDeleted = false and b.reimburse.id = ${reimburseId}",
					[sort: sortBy, order: Order])
			result.totalRows = models.size()
			result.models = models.findAll([max: max, offset: offset])
		}
		return result;
	}

	def createObject(def object){
		def newObject = new ReimburseDetail()
		newObject.isDeleted = false
		newObject.name = object.name
		newObject.description = object.description
		newObject.amount =  object.amount
		
		if (object.lastUpdate != null) {
			newObject.lastUpdate = ISOStringToDate(object.lastUpdate)
		}
		if (object.receiptDate != null)
		{
			newObject.receiptDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(object.receiptDate)
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
		
		if (!valObject.isDeleted) valObject.isDeleted = object.isDeleted
		
		if (object.lastUpdate != null) {
			valObject.lastUpdate = ISOStringToDate(object.lastUpdate)
		}
		if (valObject.receiptDate != null)
		{
			valObject.receiptDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(object.receiptDate)
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
