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

	def titleNotNull(def object){
		if (object.title == null || object.title == "" )
		{
			object.errors.rejectValue('title','null','Title cannot empty')
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
//		object = titleNotNull(object)
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
