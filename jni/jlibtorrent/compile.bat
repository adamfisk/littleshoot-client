
cl -I "c:\program files\java\jdk1.6.0_12\include" -I"c:\program files\java\jdk1.6.0_12\include\win32" -I"c:\boost" -I "../../libtorrent/libtorrent/include" -I "../../libtorrent/libtorrent/zlib" -I "./build/Release/Headers" /LD /MT /EHsc /W1  /TP ./src/main/cpp/jnltorrentjnilib.cpp /Fe"jlibtorrent.dll"
