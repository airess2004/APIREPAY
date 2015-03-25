package apirepay

import grails.transaction.Transactional
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.shiro.subject.Subject
import apirepay.ShiroUser

@Transactional
class UserValidatorService {

    def serviceMethod() {

    }
	
	def vName(){
			
	}
	
	def nameNotNull(def object){
		
			if (object.username == null || object.username.trim() == "")
			{
				object.errors.rejectValue('username','null','UserName tidak boleh kosong')
			}
			return object
		}
	
	def nameMustUnique(def object){
		def uniq = ShiroUser.findByUsernameAndIsDeleted(object.username,false)
		print uniq
		print object
		if (uniq != null)
		{
			if (uniq.id != object.id)
			{
			object.errors.rejectValue('username','null','UserName harus unik')
			}
		}
		return object
	}
		
	def passNotNull(def object){
		
			if (object.passwordHash == null || object.passwordHash.trim() == new Sha256Hash("").toHex())
			{
				object.errors.rejectValue('passwordHash','null','Password tidak boleh kosong')
			}
			return object
		}
	
	def confirmPassCorrect(def object, confirmpass){
		
			if (object.passwordHash != confirmpass)
			{
				object.errors.rejectValue(null,'','Konfirmasi Password tidak sama')
			}
			return object
		}
	
	def oldPassCorrect(def object, oldpass){
			Subject currentUser = SecurityUtils.getSubject();
			def user = ShiroUser.findByUsername(currentUser.getPrincipal())
			if (oldpass != user.passwordHash)
			{
				object.errors.rejectValue(null,'','Old Password salah')
			}
			return object
		}
	
	def createObjectValidation(def object){
		object = nameNotNull(object)
		if (object.errors.hasErrors()) return object
		object = passNotNull(object)
		return object
	}
	
	def updateObjectValidation(def object){
		object = createObjectValidation(object)
		return object
	}
	
	def updatePasswordObjectValidation(def object, oldpass, confirmpass){
		object = createObjectValidation(object)
		if (object.errors.hasErrors()) return object
		object = confirmPassCorrect(object, confirmpass)
		if (object.errors.hasErrors()) return object
		object = oldPassCorrect(object, oldpass)
		return object
	}
}
