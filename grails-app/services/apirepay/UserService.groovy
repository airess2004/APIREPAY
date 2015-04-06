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
		newObject.username = String.valueOf(object.username).toUpperCase()
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
		newObject.username = String.valueOf(object.username).toUpperCase()
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
		//Object userIdentity = username
//		String realmName = "ShiroDbRealm";
//		PrincipalCollection principals = new SimplePrincipalCollection(username, realmName);
//		Subject subject = new Subject.Builder().principals(principals).buildSubject();
//		ThreadContext.bind(subject)
		Subject currentUser = SecurityUtils.getSubject();
		if ( !currentUser.isAuthenticated() ) {
			def authToken = new UsernamePasswordToken(String.valueOf(username).toUpperCase(), password) //new Sha256Hash(password).toHex()
			// Support for "remember me"
			authToken.rememberMe = rememberMe
//			if (rememberMe) {
//				authToken.rememberMe = true
//			}
			
			
			// If a controller redirected to this page, redirect back
			// to it. Otherwise redirect to the root URI.
			//def targetUri = params.targetUri ?: "/"
				
			// Handle requests saved by Shiro filters.
//			SavedRequest savedRequest = WebUtils.getSavedRequest(request)
//			if (savedRequest) {
//				targetUri = savedRequest.requestURI - request.contextPath
//				if (savedRequest.queryString) targetUri = targetUri + '?' + savedRequest.queryString
//			}
				
			try{
				// Perform the actual login. An AuthenticationException
				// will be thrown if the username is unrecognised or the
				// password is incorrect.
				
				currentUser.login(authToken) //SecurityUtils.subject.login(authToken)
				//SecurityUtils.subject.getSession().setTimeout(300000)
				
//				log.info "Welcome" //"Redirecting to '${targetUri}'."
//				redirect(uri: targetUri)
//				getUI().getPage().getCurrent().getJavaScript().execute("window.location.reload();");
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
		def token = generateAndSaveTokenForUser(username, currentUser)
		return token
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
