## Testing

    java -cp wiremock-standalone-2.27.2.jar::handlebars-4.2.0.jar:handlebars-helpers-4.2.0.ja
    com.github.tomakehurst.wiremock.standalone.WireMockServerRunner --global-response-templating --port 9001 --verbose

* download from maven cental
    * wiremock (version 2.27+)
    * handlebars 
    
* run command and set classpath with all jars
* global-response-template allow handlebars transformation of stub data

        {
              "request": {
              "method": "GET",
              "urlPattern": "/customer/([A-Za-z0-9]+)"
          },
              "response": {
              "status": 200,
              "jsonBody": {
                    "custId" :"{{{request.pathSegments.[1]}}}",
                  "firstName": "Busayo",
                  "lastName": "Gbadamosi",
                  "enabled": " {{{ pickRandom 'true' 'false' }}} "
          },
            "headers": {
                "Content-Type": "application/json"
            } 
            }
        }







