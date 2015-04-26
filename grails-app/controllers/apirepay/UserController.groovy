package apirepay

import java.net.Authenticator.RequestorType;
import java.security.*
import java.text.SimpleDateFormat
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import groovy.time.TimeCategory

import grails.rest.RestfulController
import grails.converters.JSON

class UserController extends RestfulController {
	def userService
	def restService
	def index() { }
	def secret = "87817823e0d9b77b59fabac093f2aaf2e0233edb";
	def expFormat = "yyyy/MM/dd HH:mm:ss+00:00";
	def transParams = [
			auth: [
				// 'expires' should be placed after 'key' in the final params JSON string to prevent generating a wrong signature
				expires: '', // "yyyy/MM/dd HH:mm:ss+00:00"
				key: '86603f20804911e4a9905fd9992d44bc',
			],
			template_id: '429d0ac0d1ed11e48e8cd335307ba78c',
			notify_url: '',
			fields: [
					customFormField : true,
				],
		];
	def INVALID_TOKEN = "Invalid Token";

	private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	private String calculateHMAC(String secret, String data) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(),    HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			String result = new BigInteger(1, rawHmac).toString(16).padLeft(40, '0');
			//result = javax.xml.bind.DatatypeConverter.printHexBinary(rawHmac);
			return result;
		} catch (GeneralSecurityException e) {
			//LOG.warn("Unexpected error while creating hash: " + e.getMessage(),    e);
			//throw new IllegalArgumentException();
			println "Unexpected error while creating hash: " + e.getMessage();
		}
	}
	
	// Simple ISO Converter https://gist.github.com/kdabir/6bfe265d2f3c2f9b438b
	private String DateToISOString(Date date) {
		return new Date().format("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", TimeZone.getTimeZone("UTC"));
	}
	
	private Date ISOStringToDate(String dateString) {
		return Date.parse("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", dateString);
	}
	 
	private String utcFormat(def date) {
		SimpleDateFormat f = new SimpleDateFormat(expFormat);
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		return f.format(date);
	}
	
	def show = {
		//		if(params.ids && Reimburse.exists(params.id)){
		//			render Reimburse.findById(params.id) as JSON
		//		}else{
		//			render userService.getList(1,50,"id","desc").toArray() as JSON
		//		}
	}


	def save = {
		switch(request.JSON.method)
		{
			case "login":
			def err = null;
			String hash = "";
			String paramstr = null;
			def userobj;
			def object = null;
			try
			{
				def expDate = new Date()
				use(TimeCategory) {
					expDate = (expDate + 2.hours) //.format(expFormat)
				}
				transParams.auth.expires = utcFormat(expDate)
				paramstr = (transParams as JSON).toString();
				hash = calculateHMAC(secret, paramstr)
				object = userService.SignIn(request.JSON.model.username, request.JSON.model.passwordHash,false)
				userobj = ShiroUser.find{ username == String.valueOf(request.JSON.model.username).trim().toUpperCase() }
				
			} catch(Exception e) {
				err = e.message;
			}
			def newJson = [
				timeStamp : DateToISOString(new Date()),
				token: object,
				hash : hash,
				param : paramstr,
				model : [fullname : userobj?.fullname] ,
				error: err
			]
			render newJson as JSON
			break
			
			
			case "getHash":
			if (restService.getUsernameForToken(request.JSON.token)!= null)
			{
					def err = null;
					String hash = "";
					try
					{
						hash = calculateHMAC(secret, (request.JSON.model as JSON).toString())
					} catch(Exception e) {
						err = e.message;
					}
					def result = [
						timeStamp : DateToISOString(new Date()),
						error : err,
						hash : hash,
					]
					render result as JSON;
			}
			else
			{
				def newJson = [
					error: INVALID_TOKEN
				]
				render newJson as JSON
			}
			break
			
			case "create":
				def error
				def object = [
					fullname:request.JSON.model.fullname,
					username:request.JSON.model.username,
					passwordHash:request.JSON.model.passwordHash,
				]
				try {
					object = userService.createObject(object)
					if (object.hasErrors()) {
						object.errors.allErrors.each {
							 error = it.defaultMessage
						}
					}
				} catch(Exception e) {
					 error = e.message // TODO : get error message from object when possible instead of long expection message
				}
				def newJson = [
					model: object,
					error: error
				]
				render newJson as JSON
			break

			case "update":
			if (restService.getUsernameForToken(request.JSON.token)!= null)
			{
			def object = [
				id:request.JSON.model.id,
				username:request.JSON.model.username,
				passwordHash:request.JSON.model.passwordHash,
			]
			object = userService.updatePasswordObject(object,request.JSON.model.oldpass,
			request.JSON.model.confirmpass)
			render object
			}
			else
			{
				def newJson = [
					error: INVALID_TOKEN
				]
				render
			}
			break

			case "delete":
			if (restService.getUsernameForToken(request.JSON.token)!= null)
			{
			def object = [
				id:newObject.id
			]
			object = userService.softDeleteObject(object)
			render object
			}
			else
			{
				def newJson = [
					error: INVALID_TOKEN
				]
				render
			}
			break
			
			case "logout":
			def username = restService.getUsernameForToken(request.JSON.token)
			if (username != null)
			{
				def object = userService.signOut(request.JSON.model.username, username )
				def newJson = [
					error: object
				]
				render newJson as JSON
			}
			else
			{
				def newJson = [
					error: null //INVALID_TOKEN
				]
				render newJson as JSON
			}
			break
		}

	}

}


