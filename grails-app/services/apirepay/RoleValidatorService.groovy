package apirepay

import grails.transaction.Transactional

@Transactional
class RoleValidatorService {

    def serviceMethod() {

    }
	
	def idNotNull(def object){
		if (object.id == null || object.id.trim() == "")
		{
			
			object.errors.rejectValue('','null','Update Error')
		}
		return object
	}
	
	def nameNotNull(def object){
	
		if (object.name == null || object.name.trim() == "")
		{
			object.errors.rejectValue('name','null','Name tidak boleh kosong')
		}
		return object 
	}
	
	def nameMustUnique(def object){
			def uniq = ShiroRole.findBySkuAndIsDeleted(object.name,false)
			print uniq
			print object
			if (uniq != null)
			{
				if (uniq.id != object.id)
				{	
				object.errors.rejectValue('name','null','Name harus unik')
				}
			}
			return object
		} 
	
	def updateObjectValidation(def object){		
		object = createObjectValidation(object)
		return object
	}
	
	
	def createObjectValidation(def object){
		object = nameNotNull(object)
		if (object.errors.hasErrors()) return object
		object = nameMustUnique(object)
		return object
	}
}
