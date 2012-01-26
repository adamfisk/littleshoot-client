###############################################################################
# Building the Mozilla plugin
###############################################################################

MOSTLYCLEANFILES = 
CLEANFILES = $(BUILT_SOURCES)
EXTRA_DIST = $(DIST_sources) install.js nplittleshoot.rc.in littleshoot.r

SOURCES_mozilla_common = \
	vlcshell.cpp \
	vlcplugin.cpp \
	vlcplugin.h \
	control/npolibvlc.cpp \
	control/npolibvlc.h \
	control/nporuntime.cpp \
	control/nporuntime.h \
	support/classinfo.h

DIST_sources = $(SOURCES_mozilla_common) \
	windows/np_impl.cpp mac/np_impl.cpp unix/mp_impl.c

if BUILD_MOZILLA

LIBRARIES_libvlc = $(top_builddir)/src/libvlc.la \
				   $(top_builddir)/src/libvlccore.la

if HAVE_WIN32

# Under Win32, Mozilla plugins need to be named NP******.DLL, but under Unix
# the common naming scheme is lib******plugin.so. Also, we need npwin.cpp
# under Win32 and npunix.c under Unix.
#
lib_LTLIBRARIES = nplittleshoot.la

SOURCES_support = windows/np_impl.cpp
CPPFLAGS_mozilla_EXTRA = -DXP_WIN -DXP_WIN32
LDFLAGS_mozilla_EXTRA = -no-undefined -Wl,--kill-at -Wl,$(DATA_nplittleshoot_rc)

NPLITTLESHOOT_LA_SOURCES = $(SOURCES_mozilla_common) $(SOURCES_support)
NPLITTLESHOOT_LA_CFLAGS = `$(LittleShoot_CONFIG) --cflags mozilla` $(CPPFLAGS_mozilla_extra)
NPLITTLESHOOT_LA_CXXFLAGS = `$(LittleShoot_CONFIG) --cxxflags mozilla` $(CPPFLAGS_mozilla_extra)
NPLITTLESHOOT_LA_DEPENDENCIES = $(DATA_nplittleshoot_rc) $(LIBRARIES_libvlc)
NPLITTLESHOOT_LA_LDFLAGS = `$(LittleShoot_CONFIG) --ldflags mozilla` -module -avoid-version \
				   $(LDFLAGS_mozilla_EXTRA)
NPLITTLESHOOT_LA_LIBADD = `$(LittleShoot_CONFIG) -libs mozilla` $(LIBRARIES_libvlc)

DATA_nplittleshoot_rc = $(noinst_nplittleshoot_rc_DATA)
noinst_nplittleshoot_rc_DATA = nplittleshoot_rc.$(OBJEXT)
noinst_nplittleshoot_rcdir =

nplittleshoot_rc.$(OBJEXT): nplittleshoot_rc.rc
	$(WINDRES) --include-dir $(srcdir) -i $< -o $@

else
if HAVE_DARWIN

#
# MacOS X
#

lib_LTLIBRARIES = nplittleshoot.la

SOURCES_support = mac/np_impl.cpp
CPPFLAGS_mozilla_EXTRA = -I. -I$(top_builddir) -I$(srcdir)/../include -c \
	-F/System/Library/Frameworks/CoreFoundation.framework $(moz_CFLAGS) \
	-I/Developer/Headers/FlatCarbon -fno-common -fpascal-strings \
	-Wmost -Wno-four-char-constants -Wno-unknown-pragmas -DXP_MACOSX=1 \
	-DNO_X11=1 -DUSE_SYSTEM_CONSOLE=1 -pipe -fmessage-length=0 \
	-include mozilla-config.h
LDFLAGS_mozilla_EXTRA = -no-undefined -bundle -Wl,-read_only_relocs -Wl,suppress \
	-Wl,-headerpad_max_install_names -shrext $(LIBEXT) -Wl,-framework,Carbon -Wl,-framework,System

NPLITTLESHOOT_LA_SOURCES = $(SOURCES_mozilla_common) $(SOURCES_support)
NPLITTLESHOOT_LA_CFLAGS = `$(LittleShoot_CONFIG) --cflags mozilla` $(CPPFLAGS_mozilla_EXTRA)
NPLITTLESHOOT_LA_CXXFLAGS = `$(LittleShoot_CONFIG) --cxxflags mozilla` $(CPPFLAGS_mozilla_EXTRA) 
NPLITTLESHOOT_LA_DEPENDENCIES = $(LIBRARIES_libvlc)
NPLITTLESHOOT_LA_LDFLAGS = `$(LittleShoot_CONFIG) --ldflags mozilla` -module -avoid-version \
				   $(LDFLAGS_mozilla_EXTRA)
NPLITTLESHOOT_LA_LIBADD = `$(LittleShoot_CONFIG) -libs mozilla` $(LIBRARIES_libvlc)


noinst_DATA = nplittleshoot.rsrc LittleShoot\ Plugin.plugin
MOSTLYCLEANFILES += nplittleshoot.rsrc
CLEANFILES += LittleShoot\ Plugin.plugin

nplittleshoot.rsrc: $(srcdir)/vlc.r
	/Developer/Tools/Rez -useDF /Developer/Headers/FlatCarbon/Types.r $< -o $@

#
# Plugin uses shared libraries that are located relatively through @executable_path,
# which unfortunately references the path of the App using the Plugin, rather than the
# Plugin itself. Since this Plugin should always be installed in '/Library/Internet Plug-Ins',
# it is safer to force dylibs to locate dependents through a fixed path
#
define FIXEXECPATH
	otool -L "$$dylib" | \
	awk -v libdylib="$$dylib" ' \
	/@executable_path/ { \
	    newpath=$$1 ; \
	    sub("@executable_path","/Library/Internet Plug-Ins/LittleShoot Plugin.plugin/Contents/MacOS",newpath) ; \
	    print "install_name_tool -change \""$$1"\" \""newpath"\" \""libdylib"\"" ; \
	}' | sh -x
endef

LittleShoot\ Plugin.plugin: nplittleshoot.rsrc $(lib_LTLIBRARIES)
	rm -Rf "$@"
	$(INSTALL) -d "$@/Contents/MacOS/lib"
	$(INSTALL) -d "$@/Contents/Resources"
	$(INSTALL) nplittleshoot.rsrc "$@/Contents/Resources/LittleShoot Plugin.rsrc"
	cp -r "$(top_srcdir)/extras/package/macosx/plugin/English.lproj" "$@/Contents/Resources/"
	cp -r "$(top_builddir)/extras/package/macosx/plugin/English.lproj" "$@/Contents/Resources/"
	$(INSTALL) "$(top_builddir)/extras/package/macosx/plugin/Info.plist" "$@/Contents/Info.plist"
   ACTION="release-makefile" PRODUCT="LittleShoot Plugin.plugin" src_dir=$(srcdir) build_dir=$(top_builddir) sh $(srcdir)/projects/macosx/framework/Pre-Compile.sh

# uncomment if dependencies on XPCOM libs is sought
#	if test -d "$(MOZILLA_SDK_PATH)/lib"; then \
#	  for i in "$(MOZILLA_SDK_PATH)"/lib/*.dylib ; do \
#	    dylib="$(srcdir)/$@/Contents/MacOS/`basename $${i}`" ; \
#	    $(INSTALL) -m 644 "$${i}" "$$dylib" ; \
#	    $(FIXEXECPATH); \
#	  done ; \
#	fi

else

#
# UNIX/Others platforms
#

CPPFLAGS_mozilla_EXTRA = -DDATA_PATH=\"$(pkgdatadir)\"

nplittleshoot_LTLIBRARIES = nplittleshoot.la

nplittleshoot = nplittleshoot$(LIBEXT)
nplittleshootdir = $(libdir)/mozilla/plugins
SOURCES_support = support/npunix.c

NPLITTLESHOOT_LA_SOURCES = $(SOURCES_mozilla_common) $(SOURCES_support)
NPLITTLESHOOT_LA_CFLAGS = `$(LittleShoot_CONFIG) --cflags mozilla` $(CPPFLAGS_mozilla_EXTRA)
NPLITTLESHOOT_LA_CXXFLAGS = `$(LittleShoot_CONFIG) --cxxflags mozilla` $(CPPFLAGS_mozilla_EXTRA)
NPLITTLESHOOT_LA_LDFLAGS = `$(LittleShoot_CONFIG) --ldflags mozilla` -module -avoid-version -shrext $(LIBEXT)
NPLITTLESHOOT_LA_LIBADD = $(LIBRARIES_libvlc) `$(LittleShoot_CONFIG) -libs mozilla`
# automake gets confused by the ../..
NPLITTLESHOOT_LA_DEPENDENCIES = $(AM_LIBADD) 
endif
endif
endif

