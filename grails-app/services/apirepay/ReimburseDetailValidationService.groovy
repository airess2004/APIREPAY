package apirepay

import grails.transaction.Transactional

@Transactional
class ReimburseDetailValidationService {

	def serviceMethod() {

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
