package apirepay

import grails.test.spock.IntegrationSpec

class ReimburseIntegrationSpec extends IntegrationSpec {
	def reimburseService
	def reimburseDetailService
	
    def setup() {
    }

    def cleanup() {
    }

    void "Test create Reimburse"() {
		setup:'setup new reimburse data'
		def reimburse = new Reimburse()
		reimburse.title = "new Title"
		reimburse.description = "new description"
		
		when:'create is called'
		reimburse =  reimburseService.createObject(reimburse)
		
		then: 'check has no error'
		reimburse.hasErrors() == false
		reimburse.isDeleted == false
		reimburse.isSent == false
		reimburse.total == 0 
		reimburse.status == 0
    }
	
	void "Test update Reimburse"() {
		setup:'setup new reimburse data'
		def reimburse = new Reimburse()
		reimburse.title = "new Title"
		reimburse.description = "new description"
		reimburse =  reimburseService.createObject(reimburse)
		
		and :'setup new reimburse update data'
		def reimburse2 = new Reimburse()
		reimburse2.id = reimburse.id
		reimburse2.title = "new Update Title"
		reimburse2.description = "new Update description"
		
		when:'update is called'
		reimburse =  reimburseService.updateObject(reimburse2)
		
		then: 'check has no error'
		reimburse.hasErrors() == false
		reimburse.title == reimburse2.title
		reimburse.description == reimburse2.description
	}
	
	void "Test softDelete Reimburse"() {
		setup:'setup new reimburse data'
		def reimburse = new Reimburse()
		reimburse.title = "new Title"
		reimburse.description = "new description"
		reimburse =  reimburseService.createObject(reimburse)
		
		when:'delete is called'
		reimburse =  reimburseService.softDeleteObject(reimburse)
		
		then: 'check has no error'
		reimburse.hasErrors() == false
		reimburse.isDeleted == true
	}
	
	void "Test create ReimburseDetail"() {
		setup:'setup new reimburse data'
		def reimburse = new Reimburse()
		reimburse.title = "new Title"
		reimburse.description = "new description"
		reimburse =  reimburseService.createObject(reimburse)
		
		and :'setup new reimburse detail data'
		def reimburseDetail = new ReimburseDetail()
		reimburseDetail.reimburse = reimburse
		reimburseDetail.name = "new Title Detail"
		reimburseDetail.description = "new description detail"
		reimburseDetail.amount = 10000
		reimburseDetail.receiptDate = new Date(2015,3,24)
		
		and :'setup new reimburse detail data2'
		def reimburseDetail2 = new ReimburseDetail()
		reimburseDetail2.reimburse = reimburse
		reimburseDetail2.name = "new Title Detail"
		reimburseDetail2.description = "new description detail"
		reimburseDetail2.amount = 10000
		reimburseDetail2.receiptDate = new Date(2015,3,24)
		
		when:'create is called'
		reimburseDetail =  reimburseDetailService.createObject(reimburseDetail)
		reimburseDetail =  reimburseDetailService.createObject(reimburseDetail2)
		
		then: 'check has no error'
		reimburseDetail.hasErrors() == false
		reimburseDetail.isDeleted == false
		reimburseDetail.reimburse.total == 20000
	}
	
	
}
