#!/usr/bin/env bash
DOJO_VERSION=dojo

function excludeRsyncJsLib
{
  rsync --exclude .svn/ \
        --exclude test/ \
        --exclude tests/ \
        --exclude demo/ \
        --exclude demos/ \
        --exclude soria/ \
        --exclude nihilo/ \
        --exclude grid/ \
        --exclude charting/ \
        --exclude util/ \
        --exclude collections/ \
        --exclude README* \
        --exclude *.psd \
        --exclude *.uncompressed.js \
        --exclude *.commented.css \
        --exclude dijit/templates \
        --exclude dijit/form/templates \
        --exclude dijit/layout/templates \
        --exclude *silverlight* \
        --exclude gfx3d/ \
        --exclude dojo/_base/ \
        --exclude dojo/_base.js \
        --exclude dojo/build.txt \
        --exclude functional/ \
        --exclude off/ \
        --exclude presentation/ \
        --exclude sketch/ \
        --exclude storage/ \
        --exclude wire/ \
        --exclude data/ \
        --exclude dtl/ \
        --exclude dojox/xmpp/ \
        --exclude dojox/widget/ \
        --exclude dojox/layout \
        --exclude dojox/image \
        --exclude dojox/editor \
        --exclude dojox/gfx \
        --exclude dojox/form \
        -avz $1 $2 || die "Could not sync"
}

