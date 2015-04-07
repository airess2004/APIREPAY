package apirepay

import grails.transaction.Transactional
import java.security.MessageDigest



import org.apache.shiro.subject.support.DefaultSubjectContext
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

@Transactional
class UserService {
	UserValidatorService userValidatorService = new UserValidatorService()
	RestService restService = new RestService()
    def serviceMethod() {

    }
	
	def getObjectById(def object){
		return ShiroUser.get(object)
	}
	
	def getObjectByName(def object){
		return ShiroUser.findByUsernameAndIsDeleted(String.valueOf(object).toUpperCase(),false)
	}
	
	def getList(){
		return ShiroUser.getAll()
	}
	
	def createObject(object){
		ShiroUser newObject = new ShiroUser()
		newObject.fullname = String.valueOf(object.fullname).trim().toUpperCase()
		newObject.username = String.valueOf(object.username).trim().toUpperCase()
		newObject.passwordHash = new Sha256Hash(object.passwordHash).toHex()
		newObject.isDeleted = false
		object = userValidatorService.createObjectValidation(newObject)
		if (object.errors.getErrorCount() == 0)
		{
			newObject.save()
			object = newObject
		} 
		return newObject
	}
	
	def updateObject(def object){
		def newObject = ShiroUser.read(object.id)
		newObject.fullname = String.valueOf(object.fullname).trim().toUpperCase()
		newObject.username = String.valueOf(object.username).trim().toUpperCase()
		newObject.passwordHash = new Sha256Hash(object.passwordHash).toHex()
		object = userValidatorService.updateObjectValidation(newObject)
		if (object.errors.getErrorCount() == 0)
		{
			newObject.save()
			object = newObject
		}
		else newObject.discard()
		return object
	}
	
	def updatePasswordObject(def object, oldpass, confirmpass){
		def newObject = ShiroUser.read(object.id)
		object.passwordHash = new Sha256Hash(object.passwordHash).toHex()
		object = userValidatorService.updatePasswordObjectValidation(object as ShiroUser, new Sha256Hash(oldpass).toHex(), new Sha256Hash(confirmpass).toHex())
		if (object.errors.getErrorCount() == 0)
		{
			newObject.passwordHash = object.passwordHash
			newObject.save()
			object = newObject
		} 
		//else object.discard()
		return object
	}
	
	def softDeleteObject(def object){
		def newObject = ShiroUser.get(object.id)
		newObject.isDeleted = true
		newObject.save()
	}
	
	public def SignIn(String username, String password, boolean rememberMe = false)
	{
		Subject currentUser = SecurityUtils.getSubject();
		if ( !currentUser.isAuthenticated() ) {
			def authToken = new UsernamePasswordToken(String.valueOf(username).trim().toUpperCase(), password) //new Sha256Hash(password).toHex()
			authToken.rememberMe = rememberMe
			try{
				currentUser.login(authToken)
				}
			catch (AuthenticationException ex){
				// Authentication failed, so display the appropriate message
				// on the login page.
				log.info "Authentication failure for user '${username}'."
				throw new Exception("Authentication Failure for user '${username}'.")
				//return null
			}
			catch (Exception ex){
				// Authentication failed, so display the appropriate message
				// on the login page.
				log.info "Authentication Exception for user '${username}'."
				throw new Exception("Authentication Exception for user '${username}'.")
				//return null
			}
		}
		def token = generateAndSaveTokenForUser(String.valueOf(username).trim().toUpperCase(), currentUser)
		return token
	}
	
	public def signOut(String username,String username2)
	{
		try{
			if (String.valueOf(username).trim().toUpperCase() == String.valueOf(username2).trim().toUpperCase())
			{
				restService.deleteToken(username2)
			}
			else
			{
				return "Invalid User"
			}
			return null
		}
		catch (Exception ex){
			return ex.message
		}
	}
	
	private generateAndSaveTokenForUser(username, request) {
		def token = null

		//generate unique token
		while (!token) {
			//generate token
			token = restService.generateToken(username, request)

			//check token uniqueness
			def tokenUser = restService.getUsernameForToken(token)
			if (tokenUser && tokenUser != username) {
				//token is not unique. Generate token again
				token = null
			}
		}

		//save token
		restService.saveToken(token, username)

		return token
	}

//	private getRestService() {
//		if (!this.@restService) {
//			this.@restService = SpringUtils.getBean("restService")
//		}
//
//		return this.@restService
//	}
}
