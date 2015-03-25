class UrlMappings {

	static mappings = {
		//        "/$controller/$action?/$id?(.$format)?"{
		//            constraints {
		//                // apply constraints here
		//            }
		//        }


		//		"/auth/$action?" (controller: 'auth')
		//
		//		"/rest/$controller/$id?"{
		//			action = [GET:"show", POST:"save", PUT:"update", DELETE:"remove"]
		//		  }
		//
		//		"/rest/$action" (controller: 'office')
		//		"/rest/" (controller: 'office', action: 'hall')
		//
		        "/"(view:"/index")
		        "500"(view:'/error')

//		"/$action?" (controller: 'office')
//		"/"(controller: 'office', action: 'hall')
//		"500"(view:'/error')

//		// auth controller
//		"/auth/$action?" (controller: 'auth')
//
//		// REST api
//		"/rest/$action" (controller: 'office')
//		"/rest/" (controller: 'office', action: 'hall')

		"/api/$controller/$id?"{
			action = [GET:"show", POST:"save", PUT:"update", DELETE:"remove"]
		}
	}
}
