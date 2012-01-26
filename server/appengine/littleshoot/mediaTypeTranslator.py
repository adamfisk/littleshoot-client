
import logging
import os

class MediaTypeTranslator():
    
    def __init__(self):
        self.types = {}
        self.addTypes(self.DOCUMENTS, 'document')
        self.addTypes(self.OSX_APPLICATIONS, 'application/mac')
        self.addTypes(self.LINUX_APPLICATIONS, 'application/linux')
        self.addTypes(self.WINDOWS_APPLICATIONS, 'application/win')
        self.addTypes(self.AUDIO, 'audio')
        self.addTypes(self.VIDEO, 'video')
        self.addTypes(self.IMAGE, 'image')
        self.addTypes(self.GENERAL_APPLICATION, 'application')
        self.addTypes(self.ARCHIVE, 'archive')
        
    
    def addTypes(self, extensions, category):
        #logging.info('Adding types....')
        for ext in extensions:
            if self.types.has_key(ext):
                logging.error('Duplicate type: %s', ext)
            else:
                self.types[ext] = category
                
    def getType(self, fileName):
        #logging.info('Getting ext for file name: %s', fileName)
        (shortname, ext) = os.path.splitext(fileName)
        # The splitext function leaves the '.'
        ext = ext.strip('.').lower()
        #logging.info('Found extension: %s', ext)
        
        
        if ext is None or len(ext) > 7:
            logging.debug('No extension: %s', ext)
            return 'unknown'
        elif not self.types.has_key(ext):
            logging.warn('Unknown extension: %s', ext)
            return 'unknown'
        else:
            return self.types.get(ext)
        
            
        
    DOCUMENTS = [
        'html', 'htm', 'xhtml', 'mht', 'mhtml', 'xml',
        'txt', 'ans', 'asc', 'diz', 'eml',
        'pdf', 'ps', 'epsf', 'dvi', 
        'rtf', 'wri', 'doc', 'mcw', 'wps',
        'xls', 'wk1', 'dif', 'csv', 'ppt', 'tsv',
        'hlp', 'chm', 'lit', 
        'tex', 'texi', 'latex', 'info', 'man',
        'wp', 'wpd', 'wp5', 'wk3', 'wk4', 'shw', 
        'sdd', 'sdw', 'sdp', 'sdc',
        'sxd', 'sxw', 'sxp', 'sxc',
        'abw', 'kwd', 'js', 'java', 'cpp', 'c', 'py', 'php', 'ruby',
        'pps', # PowerPoint show
        'dll',
        'jhtml', # Java in html
        'mmap', # mind mapping document
        'dat', # data file
        'bash',
        ]
    
    OSX_APPLICATIONS = [
        'dmg', 'pkg'
        ]
    
    LINUX_APPLICATIONS = [
        'mdb', 'sh', 'csh', 'awk', 'pl',
        'rpm', 'deb',  'z', 'zoo', 'tar', 
        'taz', 'shar', 'hqx', '7z', 
        ]
    
    
    WINDOWS_APPLICATIONS = [
        'exe', 'cab', 'msi', 'msp',
        'arj', 'ace', 
        'nsi',  # Nullsoft installer.
        ]   
    
    AUDIO = [
        'mp3', 'mpa', 'mp1', 'mpga', 'mp2', 
        'ra', 'rm', 'ram', 'rmj',
        'wma', 'wav', 'm4a', 'm4p',
        'lqt', 'ogg', 'med',
        'aif', 'aiff', 'aifc',
        'au', 'snd', 's3m', 'aud', 
        'mid', 'midi', 'rmi', 'mod', 'kar',
        'ac3', 'shn', 'fla', 'flac', 'cda', 
        'mka', 
        ]
    
    VIDEO = [
        'mpg', 'mpeg', 'mpe', 'mng', 'mpv', 'm1v',
        'vob', 'mpv2', 'mp2v', 'm2p', 'm2v', 'm4v', 'mpgv', 
        'vcd', 'mp4', 'dv', 'dvd', 'div', 'divx', 'dvx',
        'smi', 'smil', 'rv', 'rmm', 'rmvb', 
        'avi', 'asf', 'asx', 'wmv', 'qt', 'mov',
        'fli', 'flc', 'flx', 'flv', 
        'wml', 'vrml', 'swf', 'dcr', 'jve', 'nsv', 
        'mkv', 'ogm', 
        'cdg', 'srt', 'sub', 'idx', 'msmedia',
        'wvx', # This is a redirect to a wmv
        ]
    
    IMAGE = [
        'gif', 'png',
        'jpg', 'jpeg', 'jpe', 'jif', 'jiff', 'jfif',
        'tif', 'tiff', 'iff', 'lbm', 'ilbm', 'eps',
        'mac', 'drw', 'pct', 'img',
        'bmp', 'dib', 'rle', 'ico', 'ani', 'icl', 'cur',
        'emf', 'wmf', 'pcx',
        'pcd', 'tga', 'pic', 'fig',
        'psd', 'wpg', 'dcx', 'cpt', 'mic',
        'pbm', 'pnm', 'ppm', 'xbm', 'xpm', 'xwd',
        'sgi', 'fax', 'rgb', 'ras'
        ]
    
    GENERAL_APPLICATION = [
        'jar', 'jnlp', 'iso', 'bin', 
        'nrg', # Nero CD image file.
        'cue', # Another CD image file type.
        ]
    
    ARCHIVE = [
        'zip', 'sitx', 'sit', 'tgz', 'gz', 'gzip', 'bz2','rar', 'lzh','lha'
        ]
        