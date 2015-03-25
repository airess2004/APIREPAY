package apirepay

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.subject.Subject
import org.apache.shiro.web.filter.authc.AuthenticatingFilter
import org.apache.shiro.web.util.WebUtils

import grails.converters.JSON

import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse
import org.apache.shiro.SecurityUtils

abstract class RestUsernamePasswordAuthenticationFilter extends AuthenticatingFilter {
    private RestService restService

    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        def username = getUsername(request)
        def password = getPassword(request)

        return new UsernamePasswordToken(username as String, password as String)
    }

    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //this request should be handled by onAccessDenied
        return false;
    }

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
		print "2222"
		print request.getParameter("password")
//		if (SignIn(request.getParameter("username"),request.getParameter("password"),false)){
        if (executeLogin(request, response)) {
            //ok... login/password is correct.
            def username = getUsername(request)
            def token = generateAndSaveTokenForUser(username, request)

            //return token as answer
			
            response.outputStream << token
            return false
        } else {
            //failed to authenticate. Return 401
            HttpServletResponse httpResponse = WebUtils.toHttp(response);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.writer << "Authentication failure"

            return false;
        }
    }

	protected boolean SignIn(String username, String password, boolean rememberMe = false)
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
				println "Authentication failure for user '${username}'." + ex
				log.info "Authentication failure for user '${username}'."
				return false
			}
			catch (Exception ex){
				// Authentication failed, so display the appropriate message
				// on the login page.
				println "Authentication Exception for user '${username}'."
				log.info "Authentication Exception for user '${username}'."
				return false
			}
		}
		return true
	}
	
    private generateAndSaveTokenForUser(username, request) {
        def token = null

        //generate unique token
        while (!token) {
            //generate token
            token = getRestService().generateToken(username, request)

            //check token uniqueness
            def tokenUser = getRestService().getUsernameForToken(token)
            if (tokenUser && tokenUser != username) {
                //token is not unique. Generate token again
                token = null
            }
        }

        //save token
        getRestService().saveToken(token, username)

        return token
    }

    protected abstract String getUsername(request)

    protected abstract String getPassword(request)

    private getRestService() {
        if (!this.@restService) {
            this.@restService = SpringUtils.getBean("restService")
        }

        return this.@restService
    }
	
	private getUserService() {
		if (!this.@userService) {
			this.@userService = SpringUtils.getBean("userService")
		}

		return this.@userService
	}
}
