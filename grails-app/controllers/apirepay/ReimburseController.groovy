package apirepay

import java.net.Authenticator.RequestorType;

import grails.rest.RestfulController
import grails.converters.JSON

class ReimburseController extends RestfulController {
	def reimburseService
	def restService
	def userService
	def index() { }

	def show = {
	

	}


	def save = {
		if (restService.getUsernameForToken(request.JSON.token)!= null)
		{
			println restService.getUsernameForToken(request.JSON.token)
			def User = userService.getObjectByName(restService.getUsernameForToken(request.JSON.token))
			println User
			switch(request.JSON.method)
			{
				case "get":
					if(params.id && Reimburse.exists(params.id)){
						def newJson = [
							model: Reimburse.findById(params.id),
							error: null
						]
						render newJson as JSON
					}else{
						def newJson = [
							model: reimburseService.getList(User,Integer.parseInt(request.JSON.offset)
								,Integer.parseInt(request.JSON.max),request.JSON.sortBy,request.JSON.order).toArray(),
							error: null
						]
						render  newJson as JSON
					}
					break

				case "create":
					def object = [
						title:request.JSON.model.title,
						description:request.JSON.model.description,
						projectDate:request.JSON.model.projectDate.toString(),
						user:User
					]
					object = reimburseService.createObject(object)
					def newJson = [
						model: object,
						error: null
					]
					render newJson as JSON
					break

				case "update":
					def object = [
						id:request.JSON.model.id,
						title:request.JSON.model.title,
						description:request.JSON.model.description,
						projectDate:request.JSON.model.projectDate,
						user:User
					]
					object = reimburseService.updateObject(object)
					def newJson = [
						model: object,
						error: null
					]
					render newJson as JSON
					break

				case "delete":
					def object = [
						id:request.JSON.model.id
					]
					object = reimburseService.softDeleteObject(object)
					def newJson = [
						model: object,
						error: null
					]
					render newJson as JSON
					break
			}
		}
		else
		{
			def newJson = [
				error: "Invalid Token"
			]
			render newJson
		}


	}

}


