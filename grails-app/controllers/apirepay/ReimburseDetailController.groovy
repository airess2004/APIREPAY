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
						error: null
					]
					render newJson as JSON
				}else{
					def rows = reimburseDetailService.getList(request.JSON.reimburseId,
							request.JSON.offset,request.JSON.max,
							request.JSON.sortBy,request.JSON.order)
					def newJson = [
						model: rows.models.toArray(),
						error: null,
						totalRows : rows.totalRows,
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
					lastUpdate:request.JSON.model.lastUpdate,
					reimburse: Reimburse.findById(request.JSON.model.reimburseId)
				]
				object = reimburseDetailService.createObject(object)
				def newJson = [
					model: object.model,
					error: null,
					total: object.total,
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
					lastUpdate:request.JSON.model.lastUpdate,
					isDeleted:request.JSON.model.isDeleted,
					reimburse: Reimburse.findById(request.JSON.model.reimburseId)
				]
				object = reimburseDetailService.updateObject(object)
				def newJson = [
					model: object.model,
					error: null,
					total: object.total,
				]
				render newJson as JSON
				break

				case "delete":
				def object = [
					id:request.JSON.model.id
				]
				object = reimburseDetailService.softDeleteObject(object)
				def newJson = [
					model: object.model,
					error: null,
					total: object.total,
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
