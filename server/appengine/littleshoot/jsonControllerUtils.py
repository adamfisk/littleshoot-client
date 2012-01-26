
from django.http import HttpResponse
from django.http import HttpResponseNotFound
from django.http import HttpResponseBadRequest


import logging
#import logging.config
#logging.config.fileConfig('logging.conf')

"""

    public static void writeResponse(final HttpServletRequest request,
        final HttpServletResponse response, final String data) 
        throws IOException
        {
        final String responseString;
        final String functionName = request.getParameter("callback");
        if (StringUtils.isBlank(functionName))
            {
            m_log.debug("No response function, sending raw JSON");
            responseString = data;
            }
        else
            {
            responseString = functionName+"("+data+");";
            }
        m_log.trace("Function: "+functionName);
        
        response.setContentType("application/json");
        
        final OutputStream os = response.getOutputStream();
        
        m_log.debug("Writing javascript callback.");
        os.write(responseString.getBytes("UTF-8"));
        os.flush();
        }
"""

def writeResponse(request, data):
    logging.info('Writing data %s to response', data)
    functionName = request.REQUEST.get('callback')
    
    if functionName is None:
        #return HttpResponseBadRequest('No callback specified')
    
        responseString = data
        mimeType = 'application/json'
    
    else:
        responseString = functionName
        responseString += '('
        responseString += data
        responseString += ')'
        mimeType = 'text/javascript'
    
    
    logging.info('Writing response: %s', responseString)
    return HttpResponse(responseString, mimetype=mimeType)
