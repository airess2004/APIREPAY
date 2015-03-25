package apirepay

import java.net.Authenticator.RequestorType;
import java.security.MessageDigest

import grails.rest.RestfulController
import grails.converters.JSON

class ReimburseDetailController extends RestfulController {
	def reimburseDetailService
	def restService
	def index() { }

	def show = {
		
	}

	def save = {
		if (restService.getUsernameForToken(request.JSON.token)!= null)
		{
			switch(request.JSON.method)
			{
				case "get":
				if(params.id && ReimburseDetail.exists(params.id)){
					def newJson = [
						model:  ReimburseDetail.findById(params.id),
						,
						error: null
					]
					render newJson as JSON
				}else{
					def newJson = [
						model: reimburseDetailService.getList(Long.parseLong(request.JSON.reimburseId),
							Integer.parseInt(request.JSON.offset),Integer.parseInt(request.JSON.max),
							request.JSON.sortBy,request.JSON.order).toArray(),
						error: null
					]
					render newJson as JSON
				}
				break
				
				case "create":
				def object = [ 
					name:request.JSON.model.name,
					description:request.JSON.model.description,
					receiptDate: request.JSON.model.receiptDate,
					urlImageOri: request.JSON.model.urlImageOri,
					urlImageMedium: request.JSON.model.urlImageMedium,
					urlImageSmall: request.JSON.model.urlImageSmall,
					amount: request.JSON.model.amount,
					reimburse: Reimburse.findById(request.JSON.model.reimburseId)
				]
				object = reimburseDetailService.createObject(object)
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
					receiptDate: request.JSON.model.receiptDate,
					urlImageOri: request.JSON.model.urlImageOri,
					urlImageMedium: request.JSON.model.urlImageMedium,
					urlImageSmall: request.JSON.model.urlImageSmall,
					amount: request.JSON.model.amount,
					reimburse: Reimburse.findById(request.JSON.model.reimburseId)
				]
				object = reimburseDetailService.updateObject(object)
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
				object = reimburseDetailService.softDeleteObject(object)
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
