package apirepay

import org.apache.commons.validator.routines.EmailValidator
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

	def usernameMustEmail(def object)
	{
		EmailValidator emailValidator = EmailValidator.getInstance()
		if(emailValidator.isValid(object.username) == false)
		{
			object.errors.rejectValue('username','null','Not Valid Username (eg.repay@pnbit.com)')
		}
		
		return object
	}
	
	def nameNotNull(def object){

		if (object.username == null || object.username.trim() == "")
		{
			object.errors.rejectValue('username','null','UserName cannot empty')
		}
		return object
	}

	def nameMustUnique(def object){
		def uniq = ShiroUser.findByUsernameAndIsDeleted(object.username,false)
		if (uniq != null)
		{
			if (uniq.id != object.id)
			{
				object.errors.rejectValue('username','null','UserName already used')
			}
		}
		return object
	}

	def passNotNull(def object){

		if (object.passwordHash == null || object.passwordHash.trim() == new Sha256Hash("").toHex())
		{
			object.errors.rejectValue('passwordHash','null','Password cannot empty')
		}
		return object
	}

	def confirmPassCorrect(def object, confirmpass){

		if (object.passwordHash != confirmpass)
		{
			object.errors.rejectValue(null,'','Password does not match')
		}
		return object
	}

	def oldPassCorrect(def object, oldpass){
		Subject currentUser = SecurityUtils.getSubject();
		def user = ShiroUser.findByUsername(currentUser.getPrincipal())
		if (oldpass != user.passwordHash)
		{
			object.errors.rejectValue(null,'','Wrong old password')
		}
		return object
	}

	def createObjectValidation(def object){
		object = nameNotNull(object)
		if (object.errors.hasErrors()) return object
		object = usernameMustEmail(object)
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
