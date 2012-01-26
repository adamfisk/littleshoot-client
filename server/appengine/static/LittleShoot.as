package {
    import flash.display.Sprite;
    import flash.events.*;
    import flash.external.ExternalInterface;
    import flash.net.*;
    import flash.utils.Timer;
    import flash.system.Security;
    
    public class LittleShoot extends Sprite
        {
        
        private var pollTimer:Timer = new Timer(4000);
        private var url:String;
        //public var loader:URLLoader;
        
        private var gotLittleShoot:Boolean;
        
        public function LittleShoot()
            {
            trace("Creating LittleShoot flash movie");
            if (ExternalInterface.available)
                {
                trace("External interface is available.");
                try 
                    {
                    ExternalInterface.addCallback("httpGet", httpGet); 
                    ExternalInterface.addCallback("poll", poll); 
                    ExternalInterface.call("onLittleShootSwf()");
                    } 
                catch (error:SecurityError) 
                    {
                    trace("A SecurityError occurred: " + error.message);
                    } 
                catch (error:Error) 
                    {
                    trace("An Error occurred: " + error.message);
                    }
                }
            else
                {
                trace("External interface is not available for this container.");
                }
            //ExternalInterface.call("onLittleShootSwf()");
            }
       
        private function debug(msg:String):void
            {
            trace(msg);
            //ExternalInterface.call("console.info('FLASH: "+msg+"')");
            }
            
        public function poll():void
            {
            debug("Polling...");
            this.url = "http://p2p2o.littleshoot.org:8107/api/client/appCheck";
            
            // Always try to load the master first.
            Security.loadPolicyFile("http://p2p2o.littleshoot.org:8107/crossdomain.xml");
            checkForLittleShoot();
            
            // Now we start a timer in case we don't find LittleShoot.  This
            // will keep checking forever.
            debug("Starting timer...");
            //this.pollTimer = new Timer(4000.0);
            this.pollTimer.addEventListener(TimerEvent.TIMER, pollTimerHandler);        
            this.pollTimer.start();
            debug("Finished starting timer...");
            }

        private function pollTimerHandler(evt:TimerEvent):void
            {
            trace("Received timer event...");
            checkForLittleShoot();   
            }
            
        private function checkForLittleShoot():void
            {
            // We need to customize the policy file URL because we'll get
            // a 404 when LittleShoot is not present, causing the browser to
            // cache it.  This forces a reload of the policy file each time.
            var pfUrl:String = 
                "http://p2p2o.littleshoot.org:8107/api/client/crossdomain.xml?rand="+String(Math.random());
            debug("About to load policy file: "+pfUrl);
            Security.loadPolicyFile(pfUrl);
            
            if (this.gotLittleShoot)
                {
                debug("Got LittleShoot...stopping timer...");
                this.pollTimer.reset();
                return;
                }
            else
                {
                // We do this even on the first run to set the baseline.
                ExternalInterface.call("onNoLittleShootFromFlash()");
                }
            var urlLoader:URLLoader = new URLLoader();
            debug(new Date() + " polling with url: "+this.url);
            configureListeners(urlLoader);    

            // We add params in the straight URL to make sure to avoid caching.
            var url:String = this.url + 
                "?noCache="+String(Math.random())+
                "&callback=onLittleShootFromFlash";
            
            var request:URLRequest = new URLRequest(url);
            var header:URLRequestHeader = new URLRequestHeader("pragma", "no-cache");
            
            // Doing anything we can to prevent caching!!
            var randHeader:URLRequestHeader = 
                new URLRequestHeader("random+String(Math.random())", String(Math.random()));
            request.requestHeaders.push(header);
            request.requestHeaders.push(randHeader);
            trace("Added random header: "+randHeader);
            //var variables:URLVariables = new URLVariables();
            //variables.callback = "onLittleShootFromFlash";
            //variables.noCache = new Date().getTime();
            
            //request.data = variables;
            
            debug("About to load URL: "+url);
            try 
                {
                urlLoader.load(request);
                debug("Finished load call...");
                }
            catch (error:Error) 
                {
                debug(new Date() + " Unable to load URL: " + error);
                }    
        }

        public function httpGet(url:String):void
            {
            debug("Calling URL: "+url);
            var loader:URLLoader = new URLLoader();
            configureListeners(loader);
            var request:URLRequest = new URLRequest(url);
            try 
                {
                loader.load(request);
                } 
            catch (error:Error) 
                {
                debug("Unable to load requested document.");
                }            
            }
                        
       private function configureListeners(dispatcher:IEventDispatcher):void {
            dispatcher.addEventListener(Event.COMPLETE, completeHandler);
            dispatcher.addEventListener(Event.OPEN, openHandler);
            dispatcher.addEventListener(ProgressEvent.PROGRESS, progressHandler);
            dispatcher.addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
            dispatcher.addEventListener(HTTPStatusEvent.HTTP_STATUS, httpStatusHandler);
            dispatcher.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
        }

        private function completeHandler(event:Event):void {
            var loader:URLLoader = URLLoader(event.target);
            debug("completeHandler: " + loader.data);
    
            var base:String = loader.data;
            var js:String = base.substring(0, base.length-1);
            debug("js: "+js);
            ExternalInterface.call(js);
            //reset();
            this.gotLittleShoot = true;
            reset(event);
        }

        private function openHandler(event:Event):void {
            debug("openHandler: " + event);
        }

        private function progressHandler(event:ProgressEvent):void {
            debug("progressHandler loaded:" + event.bytesLoaded + " total: " + event.bytesTotal);
        }

        private function securityErrorHandler(event:SecurityErrorEvent):void {
            debug("securityErrorHandler: " + event);
            //reset(event);
        }

        private function httpStatusHandler(event:HTTPStatusEvent):void {
            debug("httpStatusHandler: " + event);
            //reset(event);
        }

        private function ioErrorHandler(event:IOErrorEvent):void {
            debug("ioErrorHandler: " + event);
            reset(event);
        }    
        
        
        private function reset(event:Event):void {
            debug("Resetting");
            var loader:URLLoader = URLLoader(event.target);
            loader.close();
            //var targetLoader:URLLoader = URLLoader(evt.target);
            //loader.close();
            //loader.removeEventListener(Event.COMPLETE, onComplete);
            //loader.removeEventListener(IOErrorEvent.IO_ERROR, onIOError);
            //debug(new Date() + " Complete: " + targetLoader.data);
            //this.pollTimer.reset();    
            //this.pollTimer.start();
        }   
    }
}
