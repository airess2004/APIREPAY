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
						def rows = reimburseService.getList(request.JSON.filter,User,request.JSON.offset,
								request.JSON.max,request.JSON.sortBy,request.JSON.order)
						
						def newJson = [
							model: rows.models.toArray(),
							error: null,
							totalRows : rows.totalRows,
						]
						render  newJson as JSON
					}
					break

				case "getFrom":
					if(params.id && Reimburse.exists(params.id)){
						def newJson = [
							model: Reimburse.findById(params.id),
							error: null
						]
						render newJson as JSON
					}else{
						def newJson = [
							model: reimburseService.getListFrom(request.JSON.model,User),
							error: null
						]
						render  newJson as JSON
					}
					break


				case "create":
					def object = [
						idx:request.JSON.model.idx,
						title:request.JSON.model.title,
						description:request.JSON.model.description,
						projectDate:request.JSON.model.projectDate,//.toString(),
						lastUpdate:request.JSON.model.lastUpdate,
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
						idx:request.JSON.model.idx,
						title:request.JSON.model.title,
						description:request.JSON.model.description,
						projectDate:request.JSON.model.projectDate,
						lastUpdate:request.JSON.model.lastUpdate,
						isDone:request.JSON.model.isDone,
						isDeleted:request.JSON.model.isDeleted,
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

				case "send":

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


