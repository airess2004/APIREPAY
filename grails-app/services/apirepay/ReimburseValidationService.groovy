package apirepay

import grails.transaction.Transactional

@Transactional
class ReimburseValidationService {

    def serviceMethod() {

    }
	
	def idNotNull(def object){
		if (object.id == "" || object.id == null)
		{
			
			object.errors.rejectValue('null','null','Update Error')
		}
		return object
	}
	
	def descNotNull(def object){

		
		if (object.description == null || object.description == "" )
		{
			object.errors.rejectValue('description','null','Description tidak boleh kosong')
		}
	
		return object
	}
	
	def skuNotNull(def object){
	
		if (object.sku == null || object.sku == "")
		{
			object.errors.rejectValue('sku','null','SKU tidak boleh kosong')
		}
		return object
	}
	
	def skuMustUnique(def object){
			def uniq = Item.findBySkuAndIsDeleted(object.sku,false)
			if (uniq != null)
			{
				if (uniq.id != object.id)
				{
				object.errors.rejectValue('sku','null','SKU harus unik')
				}
			}
			return object
		}
	
	def updateObjectValidation(def object){
//		object = skuNotNull(object)
//		if (object.errors.hasErrors()) return object
//		object = skuMustUnique(object)
//		if (object.errors.hasErrors()) return object
//		object = descNotNull(object)
		return object
	}
	
	
	def createObjectValidation(def object){
//		object = skuNotNull(object)
//		if (object.errors.hasErrors()) return object
//		object = skuMustUnique(object)
//		if (object.errors.hasErrors()) return object
//		object = descNotNull(object)
		return object
	}
	
	def softDeleteObjectValidation(def object){
		//		object = skuNotNull(object)
		//		if (object.errors.hasErrors()) return object
		//		object = skuMustUnique(object)
		//		if (object.errors.hasErrors()) return object
		//		object = descNotNull(object)
				return object
	}
	
	
}
